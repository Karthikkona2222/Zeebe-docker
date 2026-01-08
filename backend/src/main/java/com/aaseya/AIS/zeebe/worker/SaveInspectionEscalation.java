package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.dao.InspectionEscalationDAO;
import com.aaseya.AIS.dto.InspectionEscalationDTO;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.FailJobResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SaveInspectionEscalation {

    @Autowired
    private InspectionEscalationDAO inspectionEscalationDAO; // DAO for database operations

    @JobWorker(type = "save-escalationdetail", autoComplete = true) // JobWorker for BPMN task of type 'save-escalation'
    public void handleJob(JobClient client, ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap(); // Fetch variables from workflow

        try {
            // Extract variables from the workflow context
        	String aISBusinessKey = (String) variables.get("AISBusinessKey");
            Long inspectionId = Long.parseLong(aISBusinessKey.substring(3));
            String userId = (String) variables.get("userIds");
            String escalationMessage = (String) variables.get("escalationMessage");
           

            // Create DTO object to pass to DAO
            InspectionEscalationDTO escalationDTO = new InspectionEscalationDTO();
            escalationDTO.setInspectionId(inspectionId);
            List<String> userIds = Collections.singletonList(userId);
            escalationDTO.setUserIds(userIds);
            escalationDTO.setEscalationMessage(escalationMessage);
           

            // Save the escalation using the DAO
            inspectionEscalationDAO.saveInspectionEscalation(escalationDTO);

            // Successfully complete the job
            client.newCompleteCommand(job.getKey()).send().join();
            
        } catch (Exception e) {
            // Log the error and fail the job if any issue occurs
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                  .retries(0) // Set retry count to 0 to avoid retries
                  .errorMessage("Failed to save escalation: " + e.getMessage())
                  .send()
                  .join();
        }
    }
}
