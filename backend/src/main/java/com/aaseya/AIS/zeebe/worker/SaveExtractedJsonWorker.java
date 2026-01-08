package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.service.ClaimCaseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SaveExtractedJsonWorker {

    @Autowired
    private ClaimCaseService claimCaseService;

    @Autowired
    private ZeebeClient zeebeClient;

    private final Logger logger = LoggerFactory.getLogger(SaveExtractedJsonWorker.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ✅ JobWorker to save discharge summary & businessKey (claimId) into DB
     */
    @JobWorker(type = "extract_discharge_summary", autoComplete = true)
    public void handleDischargeSummary(final ActivatedJob job) {
        try {
            Map<String, Object> variables = job.getVariablesAsMap();
            Long claimId = job.getProcessInstanceKey();

            String dischargeSummaryJson = (variables.containsKey("DischargeSummary"))
                    ? variables.get("DischargeSummary").toString()
                    : null;

            if (dischargeSummaryJson == null) {
                throw new RuntimeException("DischargeSummary variable not found for claimId: " + claimId);
            }

            String validationResultJson = (variables.containsKey("ValidationDataResult"))
                    ? variables.get("ValidationDataResult").toString()
                    : null;

            claimCaseService.saveExtractedData(claimId, dischargeSummaryJson, validationResultJson);
            logger.info("✅ DischargeSummary & ValidationResult saved for ClaimId: {}", claimId);

        } catch (Exception e) {
            throw new RuntimeException("❌ Error while saving Discharge Summary: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ Debug worker - print Zeebe job variables
     */
    @JobWorker(type = "debuggingpurpose", autoComplete = true)
    public void debuggingpurpose(final ActivatedJob job, final JobClient client) {
        try {
            Map<String, Object> variables = job.getVariablesAsMap();
            Long claimId = job.getProcessInstanceKey();

            logger.info("🔍 Debugging variables for processInstanceKey: {}", claimId);
            variables.forEach((key, value) -> logger.info("Key: {}, Value: {}", key, value));

        } catch (Exception e) {
            throw new RuntimeException("Error during debugging: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ Prepares and stores extracted output results from ADP job safely
     */
    @JobWorker(type = "prepareTheOutPut", autoComplete = true)
    public void prepareTheOutputNeeded(final ActivatedJob job) {
        Long processInstanceKey = job.getProcessInstanceKey();
        Long elementId = job.getElementInstanceKey();

        try {
            logger.info("🟦 [prepareTheOutPut] Started for processInstanceKey={}, elementId={}", processInstanceKey, elementId);

            Map<String, Object> variables = job.getVariablesAsMap();

            // 🔹 1. Extract latest idpResult / result
            Object resultObject = variables.get("idpResult");
            if (resultObject == null) resultObject = variables.get("result");

            JsonNode extractedFields = null;
            if (resultObject != null) {
                JsonNode resultNode = objectMapper.valueToTree(resultObject);
                extractedFields = resultNode.get("extractedFields");
            }

            if (extractedFields == null || extractedFields.isNull()) {
                logger.warn("⚠️ No extractedFields found for elementId={}", elementId);
                zeebeClient.newSetVariablesCommand(processInstanceKey)
                        .variables(Map.of(
                                "status", "failure",
                                "ErrorDescription", "No extracted fields found for the ADP job output."))
                        .send()
                        .join();
                return;
            }

            // 🔹 2. Convert JsonNode → Map for Zeebe compatibility
            Map<String, Object> currentOutput = objectMapper.convertValue(extractedFields, Map.class);

            // 🔹 3. Retrieve previously accumulated idpOutputs from this scope
            List<Map<String, Object>> mergedOutputs = new ArrayList<>();

            Object existingOutputs = variables.get("idpOutputs");
            if (existingOutputs instanceof List<?>) {
                for (Object obj : (List<?>) existingOutputs) {
                    if (obj instanceof Map<?, ?> map) {
                        mergedOutputs.add((Map<String, Object>) map);
                    }
                }
            }

            // 🔹 4. Append this new extraction
            mergedOutputs.add(currentOutput);

            // 🔹 5. Prepare update variables
            Map<String, Object> outputVars = new HashMap<>();
            outputVars.put("idpOutputs", mergedOutputs);
            outputVars.put("outputResults_" + elementId, currentOutput);

            logger.info("✅ Merged {} outputs so far for processInstanceKey={}", mergedOutputs.size(), processInstanceKey);

            // 🔹 6. Push merged results back to process root
            zeebeClient.newSetVariablesCommand(processInstanceKey)
                    .variables(outputVars)
                    .send()
                    .join();

            logger.info("✅ Output successfully merged and stored for elementId={}", elementId);

        } catch (Exception e) {
            logger.error("❌ Error while preparing the output from the ADP job: {}", e.getMessage(), e);
            zeebeClient.newSetVariablesCommand(job.getProcessInstanceKey())
                    .variables(Map.of(
                            "status", "failure",
                            "ErrorDescription", "Error while merging outputs: " + e.getMessage()))
                    .send()
                    .join();
        }
    }
}