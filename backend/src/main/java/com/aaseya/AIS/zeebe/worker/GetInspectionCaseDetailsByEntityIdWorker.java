package com.aaseya.AIS.zeebe.worker;
	 
	import com.aaseya.AIS.Model.InspectionCase;
	import com.aaseya.AIS.dao.InspectionCaseDAO; // Import your custom DAO interface
	import io.camunda.zeebe.client.api.response.ActivatedJob;
	import io.camunda.zeebe.client.api.worker.JobClient;
	import io.camunda.zeebe.spring.client.annotation.JobWorker;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;
	 
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import java.util.stream.Collectors;
	 
	@Service
	public class GetInspectionCaseDetailsByEntityIdWorker {
	 
	    private static final Logger logger = LoggerFactory.getLogger(GetInspectionCaseDetailsByEntityIdWorker.class);
	 
	    @Autowired
	    private InspectionCaseDAO inspectionCaseDAO; // Autowire your custom DAO interface
	 
	    @JobWorker(type = "get-inspection-cases-by-entity-id", autoComplete = true)
	    public void getInspectionCaseDetailsByEntityId(final JobClient client, final ActivatedJob job) {
	System.out.println("kart");
	        Map<String, Object> variables = job.getVariablesAsMap();
	        String entityId = null;
	 
	        try {
	            Object entityIdObj = variables.get("entityId");
	            if (entityIdObj instanceof String) {
	                entityId = (String) entityIdObj;
	            } else {
	                throw new IllegalArgumentException("Invalid 'entityId' type, expected String.");
	            }
	 
	            logger.info("Attempting to retrieve inspection cases for entity ID: {}", entityId);
	 
	            List<InspectionCase> inspectionCases = inspectionCaseDAO.findByEntityId(entityId);
	 
	            if (inspectionCases.isEmpty()) {
	                logger.warn("No inspection cases found for entity ID: {}", entityId);
	                variables.put("inspectionCasesFound", false);
	                variables.put("listOfInspectionCases", List.of());
	            } else {
	                logger.info("Found {} inspection case(s) for entity ID {}.", inspectionCases.size(), entityId);
	                variables.put("inspectionCasesFound", true);
	 
	                List<Map<String, Object>> simplifiedInspectionCases = inspectionCases.stream()
	                    .map(this::mapInspectionCaseToMap)
	                    .collect(Collectors.toList());
	 
	                variables.put("listOfInspectionCases", simplifiedInspectionCases);
	            }
	 
	            client.newCompleteCommand(job.getKey())
	                  .variables(variables)
	                  .send()
	                  .join();
	 
	        } catch (Exception e) {
	            logger.error("Failed to process entity ID {}: {}", entityId, e.getMessage(), e);
	 
	            client.newFailCommand(job.getKey())
	                  .retries(0)
	                  .errorMessage("Failed to retrieve inspection details for entity ID: " + e.getMessage())
	                  .send()
	                  .join();
	        }
	    }
	 
	    private Map<String, Object> mapInspectionCaseToMap(InspectionCase inspectionCase) {
	        Map<String, Object> resultMap = new HashMap<>();
	        resultMap.put("inspectionID", inspectionCase.getInspectionID());
	        resultMap.put("status", inspectionCase.getStatus());
	        resultMap.put("inspectionType", inspectionCase.getInspectionType());
	        resultMap.put("dateOfInspection", inspectionCase.getDateOfInspection());
	        resultMap.put("inspectorID", inspectionCase.getInspectorID());
	        resultMap.put("dueDate", inspectionCase.getDueDate() != null ? inspectionCase.getDueDate().toString() : null);
	        resultMap.put("inspector_source", inspectionCase.getInspector_source());
	        resultMap.put("reason", inspectionCase.getReason());
	        resultMap.put("reviewerID", inspectionCase.getReviewerID());
	        resultMap.put("approverID", inspectionCase.getApproverID());
	        return resultMap;
	    }
	    
	}


