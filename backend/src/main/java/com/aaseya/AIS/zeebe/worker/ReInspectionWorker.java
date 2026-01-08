package com.aaseya.AIS.zeebe.worker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.dto.StartAISRequestDTO;
import com.aaseya.AIS.service.InspectionCaseService;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Service
public class ReInspectionWorker {

	@Autowired
	private ZeebeClient zeebeClient;

	@Autowired
	private InspectionCaseService inspectionCaseService;

	@JobWorker(type = "re-inspection")
	public void handleReInspection(final JobClient client, final ActivatedJob job) {
	    // Retrieve variables from the job
	    Map<String, Object> variables = job.getVariablesAsMap();

	    // Extract necessary data from variables
	    String inspectionType = (String) variables.get("inspectionType");
	    String reason = (String) variables.get("reason");
	    String createdBy = (String) variables.get("createdBy");
	    Object templateIdValue = variables.get("templateId");
	    String entityId = (String) variables.get("entityId");
	    String status = (String) variables.get("Status");

	    // Handle templateId safely
	    Long templateId = null;
	    if (templateIdValue instanceof Number) {
	        templateId = ((Number) templateIdValue).longValue();
	    }

	    // Create DTO or entity to save data in the database
	    StartAISRequestDTO startAISRequestDTO = new StartAISRequestDTO();
	    startAISRequestDTO.setInspectionType(inspectionType);

	    // Set today's date as the Date of Inspection
	    LocalDate today = LocalDate.now(); // Get current date
	    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    startAISRequestDTO.setDateOfInspection(today.format(dateFormat));

	    startAISRequestDTO.setReason(reason);
	    startAISRequestDTO.setCreatedBy(createdBy);
	    startAISRequestDTO.setTemplateId(templateId);  // Safe casting
	    startAISRequestDTO.setEntityId(entityId);
	    startAISRequestDTO.setStatus(status);

	    // Create a map for process variables
	    Map<String, Object> map = new HashMap<>();
	    if (startAISRequestDTO.getPre_Inspection_Checklists() != null) {
	        map.put("preInspectionChecklists", startAISRequestDTO.getPre_Inspection_Checklists());
	    }
	    if (startAISRequestDTO.getCustom_pre_inspection_checklist() != null) {
	        map.put("custom_pre_inspection_checklist", startAISRequestDTO.getCustom_pre_inspection_checklist());
	    }
	    map.put("is_preinspection", startAISRequestDTO.isIs_preinspection());
	    map.put("inspectionType", startAISRequestDTO.getInspectionType());
	    map.put("dateOfInspection", startAISRequestDTO.getDateOfInspection());
	    map.put("reason", startAISRequestDTO.getReason());
	    map.put("createdBy", startAISRequestDTO.getCreatedBy());
	    map.put("templateId", startAISRequestDTO.getTemplateId());
	    map.put("entityId", startAISRequestDTO.getEntityId());
	    map.put("Status", startAISRequestDTO.getStatus());

	    // Start the re-inspection process in BPMN and get the process instance key
	    ProcessInstanceEvent processInstanceEvent = zeebeClient
	        .newCreateInstanceCommand()
	        .bpmnProcessId("AISProcessV3")  // This should be your BPMN process id for re-inspection
	        .latestVersion()
	        .variables(map)
	        .send()
	        .join();

	    // Generate the new business key starting with "AIS"
	    String newBusinessKey = "AIS" + processInstanceEvent.getProcessInstanceKey();

	    // Update the process variables to include the new business key
	    map.put("AISBusinessKey", newBusinessKey);

	    zeebeClient
	        .newSetVariablesCommand(processInstanceEvent.getProcessInstanceKey())
	        .variables(map)
	        .send()
	        .join();

	    // Save the inspection case in the database with the new business key as inspectionID
	    startAISRequestDTO.setBusinessKey(newBusinessKey);  // Set the new business key

	    // Log the information before saving
	    System.out.println("Preparing to save re-inspection case with the following details:");
	    System.out.println("Inspection Type: " + startAISRequestDTO.getInspectionType());
	    System.out.println("Generated Business Key: " + newBusinessKey);
	    System.out.println("Date of Inspection: " + startAISRequestDTO.getDateOfInspection());
	    System.out.println("Reason: " + startAISRequestDTO.getReason());
	    System.out.println("Created By: " + startAISRequestDTO.getCreatedBy());
	    System.out.println("Template ID: " + startAISRequestDTO.getTemplateId());
	    System.out.println("Entity ID: " + startAISRequestDTO.getEntityId());
	    System.out.println("Status: " + startAISRequestDTO.getStatus());

	    // Call the service method to save the case
	    boolean saveResult = inspectionCaseService.saveInspectionCase(startAISRequestDTO, newBusinessKey);
	    
	    // Log the result of the save operation
	    if (saveResult) {
	        System.out.println("Inspection case saved successfully.");
	    } else {
	        System.out.println("Failed to save the inspection case.");
	    }

	    // Complete the job in Zeebe
	    client.newCompleteCommand(job.getKey()).send().join();
	}

}
