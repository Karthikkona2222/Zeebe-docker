package com.aaseya.AIS.zeebe.worker;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dao.InspectionCaseDAO;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class RouteInspectionJobWorker {

    private static final Logger logger = LoggerFactory.getLogger(RouteInspectionJobWorker.class);

    private static final String COMPLIANCE_EMAIL = "ComplianceOfficer@gmail.com";

    @Autowired
    private InspectionCaseDAO inspectionCaseDAO;

    @Autowired
    private ZeebeClient zeebeClient;


    @JobWorker(type = "route-inspection-job-SK", autoComplete = true)
    public void handleRouteInspection(final ActivatedJob job, final JobClient client) {

        try {
            /*
             * 1. Read the process business key correctly
             *    (NOT from variables, but from job metadata)
             */
        	Object bkObj = job.getVariablesAsMap().get("BusinessKey");

        	if (bkObj == null) {
        	    throw new RuntimeException("BusinessKey variable is missing from process variables.");
        	}

        	String businessKey = bkObj.toString();
        	logger.info("BusinessKey received: {}", businessKey);

        	


            // 2. Extract only digits → inspectionId
            Long inspectionId = Long.parseLong(businessKey.replaceAll("\\D+", ""));
            logger.info("Extracted inspectionId: {}", inspectionId);

            // 3. Fetch existing InspectionCase
            InspectionCase inspectionCase = inspectionCaseDAO
                    .getInspectionCaseById(inspectionId)
                    .orElseThrow(() ->
                            new RuntimeException("Inspection case not found for ID: " + inspectionId)
                    );

            // 4. Update only compliance email
            inspectionCase.setComplianceID(COMPLIANCE_EMAIL);
            inspectionCaseDAO.updateInspectionCase(inspectionCase);

            logger.info("InspectionCase {} updated with complianceID={}",
                    inspectionId, COMPLIANCE_EMAIL);

            // 5. Send process variables back to Camunda
            zeebeClient
                    .newSetVariablesCommand(job.getProcessInstanceKey())
                    .variables(
                            Map.of(
                                    "routeStatus", "OK",
                                    "routedTo", "Compliance",
                                    "complianceEmail", COMPLIANCE_EMAIL
                            )
                    )
                    .send()
                    .join();

            logger.info("Process variables updated for instance {}", job.getProcessInstanceKey());

        } catch (Exception ex) {
            logger.error("Error in route-inspection-job: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
}
