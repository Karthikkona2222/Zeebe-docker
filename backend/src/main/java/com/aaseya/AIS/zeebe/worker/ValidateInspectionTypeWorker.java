package com.aaseya.AIS.zeebe.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aaseya.AIS.service.ValidateInspectionTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class ValidateInspectionTypeWorker {

    @Autowired
    private ValidateInspectionTypeService validateService;

    @Autowired
    private ZeebeClient zeebeClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @JobWorker(type = "validateInspectionSK", autoComplete = true)
    public Map<String, Object> handleValidateInspectionType(final ActivatedJob job) {

        Map<String, Object> variables = job.getVariablesAsMap();

        // 1️⃣ Accumulate outputs (pure memory operation - NO DB)
        List<Map<String, Object>> idpOutputs = validateService.accumulateIdpOutputs(variables);

        // 2️⃣ Delegate ALL validation + DB logic to service
        Map<String, Object> validation = validateService.performValidation(idpOutputs, job.getProcessInstanceKey());

        // 3️⃣ Add outputs back and set riskScore (workflow variables)
        validation.put("idpOutputs", idpOutputs);
        
        Map<String, Object> mergedJson = (Map<String, Object>) validation.get("mergedJson");
        if (mergedJson != null && mergedJson.get("riskScore") != null) {
            validation.put("riskScore", mergedJson.get("riskScore"));
        }

        // 4️⃣ Update workflow variables
        zeebeClient
                .newSetVariablesCommand(job.getProcessInstanceKey())
                .variables(validation)
                .send()
                .join();

        return validation;
    }
}
