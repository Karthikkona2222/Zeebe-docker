package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.service.OperateService;
import com.aaseya.AIS.service.SaveInspectionCaseService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SaveInspectionCaseDetailsWorker {

    private final OperateService operateService;
    private final SaveInspectionCaseService saveService;

    public SaveInspectionCaseDetailsWorker(OperateService operateService,
                                          SaveInspectionCaseService saveService) {
        this.operateService = operateService;
        this.saveService = saveService;
    }

    @JobWorker(type = "SaveInspectionCaseSK", autoComplete = true)
    public Map<String, Object> handle(ActivatedJob job) {

        long adpProcessKey = job.getProcessInstanceKey();
        System.out.println("🔵 ADP subprocess Instance Key = " + adpProcessKey);

        Map<String, Object> vars = job.getVariablesAsMap();

        Long businessKey;
        if (vars.containsKey("BusinessKey")) {
            businessKey = Long.valueOf(vars.get("BusinessKey").toString());
            System.out.println("🟢 Using existing BusinessKey (AIS Key) = " + businessKey);
        } else {
            businessKey = resolveAisProcessKey(adpProcessKey);
            System.out.println("🟢 Resolved BusinessKey from AIS parent = " + businessKey);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> mergedJson = (Map<String, Object>) vars.get("mergedJson");
        if (mergedJson == null) {
            throw new RuntimeException("mergedJson not found in variables");
        }

        Map<String, Object> output = new HashMap<>();
        return saveService.saveInspectionCaseAndUpdateSummary(adpProcessKey, businessKey, mergedJson, output);
    }

    private Long resolveAisProcessKey(long currentProcessKey) {
        Map<String, Object> instanceDetails =
                operateService.getProcessInstanceDetails(currentProcessKey);

        System.out.println("🔍 Operate Instance Details for key " + currentProcessKey + ":");
        if (instanceDetails != null) {
            instanceDetails.forEach((k, v) -> System.out.println("   " + k + " = " + v));
        }

        if (instanceDetails == null || instanceDetails.isEmpty()) {
            throw new RuntimeException("❌ Operate returned empty details for " + currentProcessKey);
        }

        Object parentKey =
                instanceDetails.get("parentInstanceKey") != null ? instanceDetails.get("parentInstanceKey")
              : instanceDetails.get("parentProcessInstanceKey") != null ? instanceDetails.get("parentProcessInstanceKey")
              : instanceDetails.get("parentKey") != null ? instanceDetails.get("parentKey")
              : null;

        if (parentKey == null) {
            throw new RuntimeException("❌ No parentInstanceKey found for subprocess: " + currentProcessKey +
                                       ". BusinessKey cannot be determined.");
        }

        Long parent = Long.valueOf(parentKey.toString());

        System.out.println("🟢 AIS Parent ProcessInstanceKey Resolved = " + parent);

        return parent;
    }
}
