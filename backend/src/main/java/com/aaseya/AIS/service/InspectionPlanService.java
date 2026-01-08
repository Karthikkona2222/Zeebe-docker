package com.aaseya.AIS.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionPlan;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Template;
import com.aaseya.AIS.Model.UserGroup;
import com.aaseya.AIS.dao.ControlTypeDAO;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionPlanDAO;
import com.aaseya.AIS.dao.NewEntityDAO;
import com.aaseya.AIS.dao.TemplateDAO;
import com.aaseya.AIS.dao.UserGroupDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.CreateInspectionPlanRequestDTO;
import com.aaseya.AIS.dto.InspectionPlanDTO;
import com.aaseya.AIS.dto.SelectedEntityDTO;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class InspectionPlanService {

	@Autowired
	private InspectionPlanDAO inspectionPlanDAO;

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;

	@Autowired
	private NewEntityDAO newEntityDAO; // Inject the custom DAO

	@Autowired
	private ZeebeClient zeebeClient;

	@Autowired
	private TaskListService taskListService;
	
	@Autowired
	private ControlTypeDAO controlTypeDAO;

	@Autowired
	private TemplateDAO templateDAO;

	@Autowired
	private UserGroupDAO userGroupDAO;

	@Autowired
	private UsersDAO usersDAO;

	private static final Logger logger = LoggerFactory.getLogger(InspectionPlanService.class);

	public Page<InspectionPlanDTO> getAllInspectionPlans(Pageable pageable) {
		Page<InspectionPlan> inspectionPlans = inspectionPlanDAO.getAllInspectionPlans(pageable);

		// Convert InspectionPlan entities to DTOs
		return inspectionPlans.map(this::convertToDTO);
	}

	private InspectionPlanDTO convertToDTO(InspectionPlan inspectionPlan) {
		return new InspectionPlanDTO(inspectionPlan.getInspectionPlanId(), inspectionPlan.getInspectionPlanName(),
				inspectionPlan.getReasonForInspectionPlan(), inspectionPlan.getStatus());
	}

	@Transactional
	public Map<String, Object> createInspectionPlan(InspectionPlan inspectionPlan,
			List<SelectedEntityDTO> selectedEntities, LocalDate dateOfInspection, String createdBy) {

		// Check if the Inspection Plan Name already exists
		if (inspectionPlanDAO.existsByInspectionPlanName(inspectionPlan.getInspectionPlanName())) {
			throw new IllegalArgumentException(
					"Inspection Plan with the same name already exists: " + inspectionPlan.getInspectionPlanName());
		}
		// Validate all selected entities
		for (SelectedEntityDTO entity : selectedEntities) {
			validateEntityFields(entity); // Perform validation
		}

		inspectionPlanDAO.save(inspectionPlan); // Save the inspection plan

		Long businessKey = null;
		for (SelectedEntityDTO entity : selectedEntities) {
			logger.info("Processing entity with ID: {}", entity.getEntityId());

			// Fetch the NewEntity object
			NewEntity newEntity = newEntityDAO.findById(entity.getEntityId());
			if (newEntity == null) {
				logger.warn("Entity not found with ID: {}. Skipping.", entity.getEntityId());
				continue;
			}

			// Create process variables
			Map<String, Object> variables = new HashMap<>();
			variables.put("inspectionPlanId", inspectionPlan.getInspectionPlanId());
			variables.put("entityId", entity.getEntityId());
			variables.put("createdBy", createdBy);
			variables.put("Status", "pending");
			variables.put("DateOfInspection", dateOfInspection.toString());
			variables.put("InspectionPlan", inspectionPlan.getInspectionPlanName());
			variables.put("Inspector_source", "Adhoc");
			variables.put("InspectionType", entity.getInspectionTypeName());
			variables.put("InspectionPlanName", inspectionPlan.getInspectionPlanName());
			variables.put("ReasonForInspectionPlan", inspectionPlan.getReasonForInspectionPlan());
			variables.put("Description", inspectionPlan.getDescription());
//			variables.put("InspectorType", inspectionPlan.getInspectorType());

			// Start Zeebe process
			ProcessInstanceEvent processInstance = zeebeClient.newCreateInstanceCommand()
					.bpmnProcessId("AISProcessV3").latestVersion().variables(variables).send().join();
			businessKey = processInstance.getProcessInstanceKey();
			logger.info("Generated Business Key: {}", businessKey);

			// Set AISBusinessKey variable
			Map<String, Object> businessKeyMap = new HashMap<>();
			businessKeyMap.put("AISBusinessKey", "AIS" + businessKey);
			zeebeClient.newSetVariablesCommand(businessKey).variables(businessKeyMap).send().join();

			// Assign inspector or lead
			String taskId = taskListService.getActiveTaskID(businessKey.toString());
			if (entity.getLeadId() != null && entity.getGroupId() != null) {
				// Assign lead and group
				taskListService.assignTask(businessKey.toString(), taskId, null);
				zeebeClient.newSetVariablesCommand(businessKey)
						.variables(Map.of("leadId", entity.getLeadId(), "groupId", entity.getGroupId())).send().join();
			} else if (entity.getLeadId() == null && entity.getGroupId() == null && entity.getInspectorId() != null) {
				// Assign individual inspector
				taskListService.assignTask(businessKey.toString(), taskId, entity.getInspectorId());
				zeebeClient.newSetVariablesCommand(businessKey)
						.variables(Map.of("inspectorId", entity.getInspectorId())).send().join();
			}

			// Create and save the InspectionCase
			InspectionCase inspectionCase = new InspectionCase();
			inspectionCase.setInspectionID(businessKey);
			inspectionCase.setInspectionPlan(inspectionPlan);
			inspectionCase.setDateOfInspection(dateOfInspection.toString());
			inspectionCase.setEntity(newEntity);
			inspectionCase.setControlTypeId(entity.getControlTypeId());
			inspectionCase.setTemplate_id(entity.getTemplateId());
			inspectionCase.setGroupId(entity.getGroupId());
			long leadId = Optional.ofNullable(entity.getLeadId()).orElse(0L);
			inspectionCase.setLeadId(leadId);
			inspectionCase.setInspectionType(entity.getInspectionTypeName());
			inspectionCase.setInspector_source("Adhoc");
			inspectionCase.setStatus("pending");
			inspectionCase.setCreatedBy(createdBy);
			inspectionCase.setInspectorID(entity.getInspectorId());

			// Set case creation type
			if (entity.getLeadId() != null && entity.getGroupId() != null) {
				inspectionCase.setCaseCreationType("plan");
			} else {
				inspectionCase.setCaseCreationType("individual");
			}

			// Set due date and created date
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(inspectionCase.getDateOfInspection(), dateFormat);
			try {
				inspectionCase.setDueDate(date.plusDays(2));
			} catch (Exception e) {
				logger.error("Error setting due date: {}", e.getMessage());
			}
			inspectionCase.setCreatedDate(LocalDate.now());
			logger.info("Saving InspectionCase: {}", inspectionCase);
			inspectionCaseDAO.save(inspectionCase);
		}

		// Construct response map
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Inspection Plan Created Successfully");
		response.put("status", "success");
		response.put("inspectionPlanId", inspectionPlan.getInspectionPlanId());
		return response;
	}
	
	public CreateInspectionPlanRequestDTO getInspectionPlanWithCasesById(String inspectionPlanId) {
        InspectionPlan inspectionPlan = inspectionPlanDAO.findByIdWithCases(inspectionPlanId);
        if (inspectionPlan == null) {
            return null; // or throw an exception
        }

        CreateInspectionPlanRequestDTO responseDTO = new CreateInspectionPlanRequestDTO();
        responseDTO.setInspectionPlanName(inspectionPlan.getInspectionPlanName());
        responseDTO.setReasonForInspectionPlan(inspectionPlan.getReasonForInspectionPlan());
        responseDTO.setDescription(inspectionPlan.getDescription());
        responseDTO.setInspectorType(inspectionPlan.getInspectorType());
        
        List<InspectionCase> inspectionCases = new ArrayList<>(inspectionPlan.getInspectionCases());
        
        if (!inspectionCases.isEmpty()) {
            InspectionCase firstInspectionCase = inspectionCases.get(0);
            responseDTO.setCreatedBy(firstInspectionCase.getCreatedBy());
            responseDTO.setDateOfInspection(LocalDate.parse(firstInspectionCase.getDateOfInspection()));
        }

        List<SelectedEntityDTO> selectedEntities = new ArrayList<>();
        boolean hasPendingStatus = false;

        for (InspectionCase inspectionCase : inspectionPlan.getInspectionCases()) {
            if ("pending".equalsIgnoreCase(inspectionCase.getStatus())) {
                hasPendingStatus = true;

                SelectedEntityDTO entityDTO = new SelectedEntityDTO();
                entityDTO.setEntityId(inspectionCase.getEntity().getEntityid());
                entityDTO.setControlTypeId(inspectionCase.getControlTypeId());
                entityDTO.setInspectionID(inspectionCase.getInspectionID());
                entityDTO.setInspectionTypeName(inspectionCase.getInspectionType());
                entityDTO.setTemplateId(inspectionCase.getTemplate_id());
                entityDTO.setGroupId(inspectionCase.getGroupId());
                entityDTO.setLeadId(inspectionCase.getLeadId());
                entityDTO.setInspectorId(inspectionCase.getInspectorID()); 

                selectedEntities.add(entityDTO);
            }
        }

        
        if (hasPendingStatus) {
            responseDTO.setSelectedEntities(selectedEntities);
        } else {
            responseDTO.setSelectedEntities(new ArrayList<>());
        }

        return responseDTO;
    }
	
	
	@Transactional
	public Map<String, Object> editInspectionPlan(CreateInspectionPlanRequestDTO requestDTO) {
		Map<String, Object> response = new HashMap<>();
		logger.info("Editing Inspection Plan with ID: {}", requestDTO.getInspectionPlanId());

		// Fetch the existing inspection plan
		InspectionPlan existingPlan = inspectionPlanDAO.findById(requestDTO.getInspectionPlanId());
		if (existingPlan == null) {
			logger.error("Inspection Plan not found with ID: {}", requestDTO.getInspectionPlanId());
			throw new EntityNotFoundException("Inspection Plan not found with ID: " + requestDTO.getInspectionPlanId());
		}

		// Check if the new name is already taken by another inspection plan
		boolean nameExists = inspectionPlanDAO.existsByInspectionPlanName(requestDTO.getInspectionPlanName());
		if (nameExists && !existingPlan.getInspectionPlanName().equals(requestDTO.getInspectionPlanName())) {
			response.put("status", "error");
			response.put("message",
					"Inspection Plan with the same name already exists: " + requestDTO.getInspectionPlanName());
			return response;
		}

		// Validate all selected entities
		for (SelectedEntityDTO entity : requestDTO.getSelectedEntities()) {
			validateEntityFields(entity); // Perform validation
		}

		// Update inspection plan fields
		existingPlan.setInspectionPlanName(requestDTO.getInspectionPlanName());
		existingPlan.setReasonForInspectionPlan(requestDTO.getReasonForInspectionPlan());
		existingPlan.setDescription(requestDTO.getDescription());
		existingPlan.setInspectorType(requestDTO.getInspectorType());

		// Save the updated inspection plan
		inspectionPlanDAO.save(existingPlan);
		logger.info("Updated Inspection Plan: {}", existingPlan.getInspectionPlanId());

		// Fetch existing inspection cases for the inspection plan
		List<InspectionCase> inspectionCases = inspectionCaseDAO.findByInspectionPlan(existingPlan);

		// Validate size mismatch between inspection cases and selected entities
		if (inspectionCases.size() != requestDTO.getSelectedEntities().size()) {
			throw new IllegalArgumentException(
					"Mismatch between existing inspection cases and selected entities in request.");
		}

		// Iterate over selected entities and update inspection cases by maintaining
		// position
		for (int i = 0; i < requestDTO.getSelectedEntities().size(); i++) {
			SelectedEntityDTO entityDTO = requestDTO.getSelectedEntities().get(i);
			InspectionCase inspectionCase = inspectionCases.get(i); // Maintain correct mapping by index

			// Check if entityId has changed, update entity if necessary
			if (!inspectionCase.getEntity().getEntityid().equals(entityDTO.getEntityId())) {
				NewEntity newEntity = newEntityDAO.findById(entityDTO.getEntityId());
				if (newEntity == null) {
					throw new EntityNotFoundException("Entity not found with ID: " + entityDTO.getEntityId());
				}
				inspectionCase.setEntity(newEntity);
			}

			// Update other fields
			inspectionCase.setControlTypeId(entityDTO.getControlTypeId());
			inspectionCase.setTemplate_id(entityDTO.getTemplateId());
			inspectionCase.setGroupId(entityDTO.getGroupId());
			inspectionCase.setInspectionType(entityDTO.getInspectionTypeName());
			inspectionCase.setLeadId(Optional.ofNullable(entityDTO.getLeadId()).orElse(0L));
			inspectionCase.setInspectorID(entityDTO.getInspectorId());
			inspectionCase.setCreatedBy(requestDTO.getCreatedBy());
			inspectionCase.setDateOfInspection(requestDTO.getDateOfInspection().toString());
			
			
			// Set case creation type dynamically
	        if (entityDTO.getLeadId() != null && entityDTO.getGroupId() != null) {
	            inspectionCase.setCaseCreationType("plan");
	        } else if (entityDTO.getInspectorId() != null) {
	            inspectionCase.setCaseCreationType("individual");
	        } else {
	            throw new IllegalArgumentException("Invalid configuration for entity ID: " + entityDTO.getEntityId());
	        }

			// Save updated inspection case
			inspectionCaseDAO.save(inspectionCase);
			logger.info("Updated InspectionCase for entity at index {} with ID: {}", i, entityDTO.getEntityId());

			// Update Camunda process variables
			Map<String, Object> variables = new HashMap<>();
			variables.put("inspectionPlanId", existingPlan.getInspectionPlanId());
			variables.put("entityId", inspectionCase.getEntity().getEntityid());
			variables.put("createdBy", requestDTO.getCreatedBy());
			variables.put("Status", "pending");
			variables.put("DateOfInspection", requestDTO.getDateOfInspection().toString());
			variables.put("InspectionPlan", existingPlan.getInspectionPlanName());
			variables.put("Inspector_source", "Adhoc");
			variables.put("InspectionType", inspectionCase.getInspectionType());
			variables.put("InspectionPlanName", existingPlan.getInspectionPlanName());
			variables.put("ReasonForInspectionPlan", existingPlan.getReasonForInspectionPlan());
			variables.put("Description", existingPlan.getDescription());
//			variables.put("InspectorType", existingPlan.getInspectorType());
			variables.put("inspectorId", inspectionCase.getInspectorID());
			variables.put("groupId", inspectionCase.getGroupId());
			variables.put("leadId", inspectionCase.getLeadId());

			// Update Camunda process instance variables
			zeebeClient.newSetVariablesCommand(inspectionCase.getInspectionID()).variables(variables).send().join();
			logger.info("Updated Camunda variables for InspectionCase ID: {}", inspectionCase.getInspectionID());
		}

		// Construct and return response
		response.put("status", "success");
		response.put("message", "Inspection Plan Updated Successfully");
		response.put("inspectionPlanId", existingPlan.getInspectionPlanId());
		return response;
	}

	public boolean existsByInspectionPlanName(String inspectionPlanName) {
		return inspectionPlanDAO.existsByInspectionPlanName(inspectionPlanName);
	}

	/**
	 * Validates that the entity fields exist in their respective tables.
	 * 
	 * @param entity The SelectedEntityDTO containing the fields to validate.
	 */
	private void validateEntityFields(SelectedEntityDTO entity) {
		// Validate Entity ID
		if (entity.getEntityId() == null || entity.getEntityId().isEmpty()) {
			throw new IllegalArgumentException("Entity ID is required and cannot be null or empty.");
		}
		if (!newEntityDAO.existsById(entity.getEntityId())) {
			throw new EntityNotFoundException("Entity ID does not exist: " + entity.getEntityId());
		}

		ControlType controlType = controlTypeDAO.findById(entity.getControlTypeId());
		if (controlType == null) {
			throw new EntityNotFoundException("Control Type ID does not exist: " + entity.getControlTypeId());
		}

		// Validate Template ID
		if (entity.getTemplateId() == null) {
			throw new IllegalArgumentException("Template ID is required and cannot be null.");
		}
		try {
			Template template = templateDAO.getTemplateById(entity.getTemplateId());
			if (template == null) {
				throw new EntityNotFoundException("Template ID does not exist: " + entity.getTemplateId());
			}
		} catch (EntityNotFoundException e) {
			throw new EntityNotFoundException("Template ID does not exist: " + entity.getTemplateId());
		}

		// Validate Group ID only if it is provided
		if (entity.getGroupId() != null) {
			UserGroup userGroup = userGroupDAO.findById(entity.getGroupId());
			if (userGroup == null) {
				throw new EntityNotFoundException("Group ID does not exist: " + entity.getGroupId());
			}
		}

		// No validation for Lead ID or Inspector ID
	}
	

}