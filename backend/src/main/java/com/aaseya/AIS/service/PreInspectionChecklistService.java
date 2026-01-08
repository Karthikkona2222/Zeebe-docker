package com.aaseya.AIS.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.PreInspectionChecklist;
import com.aaseya.AIS.Model.Requirements;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dao.PreInspectionChecklistDAO;
import com.aaseya.AIS.dao.RequirementsDAO;
import com.aaseya.AIS.dto.InspectionTypeDTO;
import com.aaseya.AIS.dto.PreInspectionChecklistDTO;
import com.aaseya.AIS.dto.PreInspectionChecklistResponseDTO;
import com.aaseya.AIS.dto.RequirementDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class PreInspectionChecklistService {

	@Autowired
	private PreInspectionChecklistDAO preInspectionChecklistDAO;

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;
	
	@Autowired
    private PreInspectionChecklistDAO checklistDAO;

    @Autowired
    private RequirementsDAO requirementsDAO;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired      
    private  InspectionTypeDAO inspectionTypeDAO;
 
    

	public List<PreInspectionChecklist> getPreInspectionChecklist(String name) {
		return preInspectionChecklistDAO.getPreInspectionChecklist(name);
	}

	public PreInspectionChecklistDTO getPreInspectionChecklistDTO(long inspection_case_id) {
		
		PreInspectionChecklistDTO dto = new PreInspectionChecklistDTO();

		Set<PreInspectionChecklist> preInspectionChecklists = preInspectionChecklistDAO
				.getPreInspectionChecklist(inspection_case_id);
		dto.setPreInspectionChecklists(preInspectionChecklists);

		String customChecklist = inspectionCaseDAO.getCustomPreInspectionChecklist(inspection_case_id);
		if (customChecklist != null && !customChecklist.isBlank()) {
			Set<String> customChecklists = new HashSet<>(Arrays.asList(customChecklist.split(",")));
			dto.setCustomPreInspectionChecklists(customChecklists);
		}
		
		// Handle comments if needed
        for (PreInspectionChecklist checklist : preInspectionChecklists) {
            if (checklist.getComments() != null) {
                System.out.println("Comment: " + checklist.getComments());
            }
        }
		return dto;
	}
	
	@Transactional
	public PreInspectionChecklist saveOrUpdateChecklist(PreInspectionChecklistDTO checklistDTO) {
		PreInspectionChecklist checklist;
 
		if ("edit".equalsIgnoreCase(checklistDTO.getAction()) && checklistDTO.getChecklistId() != null) {
			checklist = checklistDAO.findChecklistById(checklistDTO.getChecklistId());
			if (checklist == null) {
				throw new RuntimeException("Checklist not found with ID: " + checklistDTO.getChecklistId());
			}
		} else {
			checklist = new PreInspectionChecklist();
		}
 
		// Set the name
		checklist.setName(checklistDTO.getName());
 
		// Map InspectionType through the relationship
		if (checklistDTO.getInsTypeId() != 0) {
	        Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeByInspectionId(checklistDTO.getInsTypeId());
	        if (inspectionType == null) {
	            throw new RuntimeException("Inspection Type not found with ID: " + checklistDTO.getInsTypeId());
	        }
            System.out.print(inspectionType + "Fetch the inspectiontype");
	        checklist.setInspectionType(inspectionType);
	        inspectionType.getPreInspectionChecklist().add(checklist);
	    }
 
		Set<Requirements> requirementsSet = new HashSet<>();
		if (checklistDTO.getRequirementNames() != null) {
			for (String reqName : checklistDTO.getRequirementNames()) {
				Requirements existingRequirement = requirementsDAO.findRequirementByName(reqName);
				if (existingRequirement != null) {
					requirementsSet.add(existingRequirement);
				} else {
					Requirements requirement = new Requirements();
					requirement.setName(reqName);
					requirementsDAO.saveRequirement(requirement);
					requirementsSet.add(requirement);
				}
			}
		}
 
		if (checklistDTO.getExistingRequirement() != null) {
			for (Long reqId : checklistDTO.getExistingRequirement()) {
				Requirements existingReq = entityManager.find(Requirements.class, reqId);
				if (existingReq != null) {
					requirementsSet.add(existingReq);
				}
			}
		}
 
		checklist.setRequirements(requirementsSet);
 
		checklist.setIsActive(true);
 
		if ("edit".equalsIgnoreCase(checklistDTO.getAction())) {
			return checklistDAO.updateChecklist(checklist);
		} else {
			return checklistDAO.saveChecklist(checklist);
		}
	}
	
	///GetAllPreInspectionChecklists///

	public Page<PreInspectionChecklistDTO> getAllPreInspectionChecklists(int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    
	    // Fetch paginated data from DAO
	    Page<PreInspectionChecklist> checklists = preInspectionChecklistDAO.getAllPreInspectionChecklists(pageable);
	    
	    // Convert entities to DTOs
	    return checklists.map(checklist -> {
	        PreInspectionChecklistDTO dto = new PreInspectionChecklistDTO();
	        dto.setChecklistId(checklist.getId());
	        dto.setName(checklist.getName());
	        dto.setIsActive(checklist.getIsActive());
	        
	        if (checklist.getInspectionType() != null) {
	            dto.setInspectionType(checklist.getInspectionType().getName());
	        }
	        
	        return dto;
	    });
	}

		//GetAllPreInspectionChecklists///


//////////////////////////////////////Get the preinspection checklist by id/////////////////
		
		
					public PreInspectionChecklistResponseDTO getChecklistById(long checklistId) {
					PreInspectionChecklist checklist = preInspectionChecklistDAO.findChecklistById(checklistId);
					if (checklist == null) {
					throw new RuntimeException("Checklist not found with ID: " + checklistId);
					}
					
					PreInspectionChecklistResponseDTO responseDTO = new PreInspectionChecklistResponseDTO();
					responseDTO.setChecklistId(checklist.getId());
					responseDTO.setName(checklist.getName());
					
					
					
					if (checklist.getInspectionType() != null) {
					Inspection_Type inspectionType = checklist.getInspectionType();
					InspectionTypeDTO inspectionTypeDTO = new InspectionTypeDTO();
					inspectionTypeDTO.setIns_type_id(inspectionType.getIns_type_id());
					inspectionTypeDTO.setName(inspectionType.getName());
					responseDTO.setInspectionType(inspectionTypeDTO); // Set a single InspectionTypeDTO
					}
					
					// Set Requirements
					List<RequirementDTO> requirements = new ArrayList<>();
					for (Requirements req : checklist.getRequirements()) {
					RequirementDTO reqDTO = new RequirementDTO();
					reqDTO.setId(req.getRequirementId());
					reqDTO.setName(req.getName());
					requirements.add(reqDTO);
					}
					responseDTO.setRequirements(requirements);
					
					return responseDTO;
					}



			
}
