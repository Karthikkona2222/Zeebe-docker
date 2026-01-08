package com.aaseya.AIS.zeebe.worker;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.Segment;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;
import com.aaseya.AIS.dto.SegmentDTO;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Service
public class GetInspectionCaseDetailsWorker {

    private static final Logger logger = LoggerFactory.getLogger(GetInspectionCaseDetailsWorker.class);

    @Autowired
    private InspectionCaseDAO inspectionCaseDAO; // Use DAO to fetch inspection case details

    @JobWorker(type = "get-inspection-case-details", autoComplete = true)
    public void getInspectionCaseDetails(final JobClient client, final ActivatedJob job) {

        // Extract process variables from the job
        Map<String, Object> variables = job.getVariablesAsMap();

        // Extract the inspection case ID from the process variables
        String AISBusinessKey = variables.get("AISBusinessKey").toString();
        Long inspectionCaseId = Long.parseLong(AISBusinessKey.substring(3));

        

       
        try {
            // Fetch inspection case details using the InspectionCaseDAO
            InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionCaseId);
            if (inspectionCase == null) {
                throw new RuntimeException("Inspection Case not found for ID: " + inspectionCaseId);
            }

            // Log the inspection case details
            logger.info("Inspection Case Details for inspectionCaseId {}: {}", inspectionCaseId, inspectionCase);

            // Prepare summarization input (if needed)
            String summarizationInput = "{\"inputs\": \"" + 
            		  "The inspection case has been created. " + 
            		  "Inspection ID: " + inspectionCase.getInspectionID() + ". " + 
            		  "Inspector Source: " + inspectionCase.getInspector_source() + ". " + 
            		  "Status: " + inspectionCase.getStatus() + ". " + 
            		  "Date of Inspection: " + inspectionCase.getDateOfInspection() + ". " + 
            		  "Entity ID: " + inspectionCase.getEntityID() + ". " + 
            		  "Inspection Type: " + inspectionCase.getInspectionType() + ". " + 
            		  "Reason: " + inspectionCase.getReason() + ". " + 
            		  "Due Date: " + inspectionCase.getDueDate() + ". " + 
            		  "Pre-inspection: " + (inspectionCase.getIs_preinspection() ? "Yes" : "No") + ". " + 
            		  "Pre-inspection Submitted: " + (inspectionCase.getIs_preinspection_submitted() ? "Yes" : "No") + 
            		  ".\"}";

            // Set the inspection case details back in process variables
            variables.put("summarizationInput", summarizationInput);

            // Complete the job with updated variables
            client.newCompleteCommand(job.getKey())
                  .variables(variables)
                  .send()
                  .join();

        } catch (Exception e) {
            logger.error("Failed to retrieve inspection details for inspectionCaseId {}: {}", inspectionCaseId, e.getMessage());
            variables.put("error", "Failed to retrieve inspection details: " + e.getMessage());

            // Mark job as failed
            client.newFailCommand(job.getKey())
                  .retries(0)
                  .errorMessage("Failed to retrieve inspection details.")
                  .send()
                  .join();
        }
    }

  
}
