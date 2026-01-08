package com.aaseya.AIS.zeebe.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class EntityReviewWorker {

    @Autowired
    private ZeebeClient zeebeClient;

    @JobWorker(type = "check-entity-review", autoComplete = true)
    public void handle(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String reviewRequired = ((String) variables.getOrDefault("isEntityReviewRequired", "Yes"))
             .trim().replace(".", "").toLowerCase();

        
        List<String> activeElements = new ArrayList<String>();

        if ("Yes".equalsIgnoreCase(reviewRequired)) {
            // Full list of tasks
            activeElements = Arrays.asList(
                "Activity_Reinspection",
                "Activity_DetectViolationPatterns",
                "Activity_InspectionCaseSummary"
            );
        } else {
            // Partial list of tasks
            activeElements = Arrays.asList(
                "Activity_Reinspection",
                "Activity_InspectionCaseSummary"
            );
        }

        Map<String, Object> newVars = new HashMap<>();
        newVars.put("activeElements", activeElements); // Required variable name for ad-hoc

        zeebeClient.newSetVariablesCommand(job.getProcessInstanceKey())
            .variables(newVars)
            .send()
            .join();
    }
}
