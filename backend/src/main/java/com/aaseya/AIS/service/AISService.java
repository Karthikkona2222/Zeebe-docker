package com.aaseya.AIS.service;
 
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.PreInspectionChecklist;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.ZoneDAO;
import com.aaseya.AIS.dto.StartAISRequestDTO;
import com.aaseya.AIS.dto.startAISFromSAPAPIDTO;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

 
@Service
public class AISService {
 
	@Autowired
	private ZeebeClient zeebeClient;
	
	@Autowired
	private ZoneDAO zoneDAO;
	
	@Autowired
	private EntityDAO entityDAO;
	
	@Value("${developer.id}")
	String developerId;


	
 
	public String startAISProcess(StartAISRequestDTO startAISRequestDTO, String processType) {

		String businessKey = "";
		Map<String, Object> map = new HashMap<>();
		map.put("inspectionType", startAISRequestDTO.getInspectionType());
		map.put("dateOfInspection", startAISRequestDTO.getDateOfInspection());
		map.put("reason", startAISRequestDTO.getReason());
		map.put("createdBy", startAISRequestDTO.getCreatedBy());
		map.put("templateId", startAISRequestDTO.getTemplateId());
		map.put("entityId", startAISRequestDTO.getEntityId());
		map.put("Status", startAISRequestDTO.getStatus());
		map.put("preInspectionChecklists", startAISRequestDTO.getPre_Inspection_Checklists());
		map.put("custom_pre_inspection_checklist", startAISRequestDTO.getCustom_pre_inspection_checklist());
		map.put("is_preinspection", startAISRequestDTO.isIs_preinspection());
		
		map.put("processType", processType);
		map.put("developerId", developerId);

		

		ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand().bpmnProcessId("AISProcessV3SK")
				.latestVersion().variables(map).send().join();
		System.out.println(processInstanceEvent.getProcessInstanceKey());
		businessKey = "AIS" + processInstanceEvent.getProcessInstanceKey();
		map.put("AISBusinessKey", businessKey);
		zeebeClient.newSetVariablesCommand(processInstanceEvent.getProcessInstanceKey()).variables(map).send().join();
		return businessKey;

	}

//	 public String startAISProcess(StartAISRequestDTO startAISRequestDTO, String SAPNotificationID, String Source) {
//	        String businessKey = "";
//	        Map<String, Object> map = new HashMap<>();
//	        map.put("inspectionType", startAISRequestDTO.getInspectionType());
//	        map.put("dateOfInspection", startAISRequestDTO.getDateOfInspection());
//	        map.put("reason", startAISRequestDTO.getReason());
//	        map.put("createdBy", startAISRequestDTO.getCreatedBy());
//	        map.put("templateId", startAISRequestDTO.getTemplateId());
//	        map.put("entityId", startAISRequestDTO.getEntityId());
//	        map.put("Status", startAISRequestDTO.getStatus());
//	        map.put("preInspectionChecklists", startAISRequestDTO.getPre_Inspection_Checklists());
//	        map.put("custom_pre_inspection_checklist", startAISRequestDTO.getCustom_pre_inspection_checklist());
//	        map.put("is_preinspection", startAISRequestDTO.isIs_preinspection());
//	        map.put("SAPNotificationID", startAISRequestDTO.getSAPNotificationID());
//	        map.put("Source", startAISRequestDTO.getSource());
//	        System.out.println(startAISRequestDTO);
//
//	        // Check if the entity exists in the database using DAO
//	        NewEntity existingEntity = entityDAO.getEntityByEntityId(startAISRequestDTO.getEntityId());
//	        if (existingEntity == null) {
//	            // If entity doesn't exist, save it to the database
//	            NewEntity newEntity = new NewEntity();
//	            newEntity.setEntityid(startAISRequestDTO.getNewEntity().getEntityid());
//	            System.out.println(startAISRequestDTO.getEntityId());
//	            newEntity.setName(startAISRequestDTO.getNewEntity().getName());
//	            newEntity.setAddress(startAISRequestDTO.getNewEntity().getAddress());
//	            newEntity.setRepresentativeEmail(startAISRequestDTO.getNewEntity().getRepresentativeEmail());
//	            newEntity.setZoneName(startAISRequestDTO.getNewEntity().getZoneName());
//	            if (startAISRequestDTO.getNewEntity().getZone() != null) {
//	                String zoneId = startAISRequestDTO.getNewEntity().getZone().getZoneId(); // Get the zone ID as a String
//	                Zone zone = zoneDAO.getZoneById(zoneId); 
//	                if (zone != null) {
//	                    newEntity.setZone(zone); 
//	                } else {
//	                    System.out.println("Zone not found for ID: " + zoneId);
//	                }
//	            }
//
//	            System.out.println(newEntity+"testing");
//	            entityDAO.saveEntity(newEntity); 
//	        }
//
//	        // Start the process in Zeebe
//	        ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
//	                .bpmnProcessId("AISProcessV3")
//	                .latestVersion()
//	                .variables(map)
//	                .send()
//	                .join();
//	        
//	        // Generate the business key
//	        businessKey = "AIS" + processInstanceEvent.getProcessInstanceKey();
//	        map.put("AISBusinessKey", businessKey);
//	        
//	        // Set variables back to the process
//	        zeebeClient.newSetVariablesCommand(processInstanceEvent.getProcessInstanceKey())
//	                .variables(map)
//	                .send()
//	                .join();
//	        boolean result = inspectionCaseService.saveInspectionCase(startAISRequestDTO, businessKey);
//	        
//	        return businessKey;
//	    }
//
//	 public String processStartAIS(startAISFromSAPAPIDTO fromSAPAPIDTO) {
//	        // Create an object of StartAISRequestDTO
//	        StartAISRequestDTO startAISRequestDTO = new StartAISRequestDTO();
//	 
//	        // Map fields from startAISFromSAPAPIDTO to StartAISRequestDTO
//	        startAISRequestDTO.setInspectionType("Radiation");
//	        startAISRequestDTO.setReason(fromSAPAPIDTO.getReason());
//	        startAISRequestDTO.setCreatedBy("santino@aaseya.com");
//	        startAISRequestDTO.setTemplateId(3L);
//	        startAISRequestDTO.setDateOfInspection(LocalDate.now().toString());
//	        startAISRequestDTO.setStatus("new");
//	        startAISRequestDTO.setInspectionSource("Adhoc");
//	        startAISRequestDTO.setCreatedDate(LocalDate.now());
//	        startAISRequestDTO.setSAPNotificationID(fromSAPAPIDTO.getSapNotificationID());
//	        startAISRequestDTO.setSource(fromSAPAPIDTO.getSource());
//	        System.out.println(startAISRequestDTO + "checking");
//	        // Map EntityInfo fields from startAISFromSAPAPIDTO to StartAISRequestDTO
//	        
//	        if (fromSAPAPIDTO.getEntityInfo() != null) {
//	            NewEntity newEntity = new NewEntity();
//	 
//	            // Set properties from fromSAPAPIDTO.getEntityInfo() to NewEntity
//	            if (fromSAPAPIDTO.getEntityInfo().getEntityName() != null) {
//	                newEntity.setName(fromSAPAPIDTO.getEntityInfo().getEntityName());
//	            }
//	 
//	            if (fromSAPAPIDTO.getEntityInfo().getEntityID() != null) {
//	                newEntity.setEntityid(fromSAPAPIDTO.getEntityInfo().getEntityID());
//	                startAISRequestDTO.setEntityId(fromSAPAPIDTO.getEntityInfo().getEntityID());
//	                System.out.println(fromSAPAPIDTO.getEntityInfo().getEntityID());
//	            }
//	 
//	            if (fromSAPAPIDTO.getEntityInfo().getAddress() != null) {
//	                newEntity.setAddress(fromSAPAPIDTO.getEntityInfo().getAddress());
//	            }
//	         if (fromSAPAPIDTO.getEntityInfo().getRepresentativeEmail() != null) {
//	                newEntity.setRepresentativeEmail(fromSAPAPIDTO.getEntityInfo().getRepresentativeEmail());
//	            }
//	 
//	            if (fromSAPAPIDTO.getEntityInfo().getZoneName() != null) {
//	                newEntity.setZoneName(fromSAPAPIDTO.getEntityInfo().getZoneName());
//	            }
//	            
//	            if (fromSAPAPIDTO.getEntityInfo().getZone_id() != null) {
//	                Zone zone = zoneDAO.getZoneByIdforZones(fromSAPAPIDTO.getEntityInfo().getZone_id()); 
//	                if (zone != null) {
//	                    newEntity.setZone(zone);
//	                }
//	            }
//	 
//	            // Assign NewEntity to StartAISRequestDTO
//	            startAISRequestDTO.setNewEntity(newEntity);
//	            
//	        }
//	        
//	        
//	 System.out.println(fromSAPAPIDTO +"testsapdetails");
//	        // Call startAISProcess and return the business key
//	        return startAISProcess(startAISRequestDTO,fromSAPAPIDTO.getSapNotificationID(),fromSAPAPIDTO.getSource());
//	    }

	
	    
	
	////Corelation message to run chatgpt connector 
	 public String correlateChatGPTMessage(String businessKey, String prompt) {
		    // Define necessary variables for message correlation
		    Map<String, Object> variables = new HashMap<>();
		    variables.put("AISBusinessKey", businessKey);
		    variables.put("prompt", prompt);

		    // Correlate the message with Zeebe using the businessKey and prompt
//		    zeebeClient.newPublishMessageCommand()
//		            .messageName("StartChatGPT")
//		            .correlationKey(businessKey)
//		            .variables(variables)
//		            .send()
//		            .join();  // Wait for the message correlation to complete

		    // Retrieve the process instance variables (including the ChatGPT response)
		    ProcessInstanceResult result = zeebeClient.newCreateInstanceCommand()
		            .bpmnProcessId("AIS_Copilot") // Replace with your BPMN process ID
		            .latestVersion()
		            .variables(variables)
		            .withResult() // Fetch the process variables
		            .send()
		            .join();

		    // Extract the ChatGPT response from the result variables
		    String chatGPTResponse = result.getVariablesAsMap().get("copilot_ans").toString(); // Replace with actual variable name

		    System.out.println("ChatGPT response received: " + chatGPTResponse);

		    return chatGPTResponse;
		}
}
