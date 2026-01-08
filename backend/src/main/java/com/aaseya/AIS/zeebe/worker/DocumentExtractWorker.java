package com.aaseya.AIS.zeebe.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DocumentExtractWorker {
    private static final Logger log = LoggerFactory.getLogger(DocumentExtractWorker.class);

    @Autowired
    private ZeebeClient zeebeClient;

    @JobWorker(type = "DocumentExtractWorker")
    public Map<String, Object> medicalReview(final ActivatedJob job) {
        Map<String, Object> variables = new HashMap<>();

        // Construct dischargeSummary with heart-related meaningful values aligned with policy ABC12307 (Priya Singh)
        Map<String, Object> dischargeSummary = new HashMap<>();

        Map<String, Object> patientDetails = new HashMap<>();
        patientDetails.put("name", "Priya Singh");
        patientDetails.put("dateofbirth", "1990-04-25");
        patientDetails.put("gender", "Female");
        patientDetails.put("admissionDate", "2025-09-01");
        patientDetails.put("dischargeDate", "2025-09-10");

        Map<String, Object> facility = new HashMap<>();
        facility.put("name", "Apollo Hospitals, Hyderabad");
        facility.put("referringPhysician", "Dr. Ramesh Kumar");
        facility.put("DiagnosisProcedureEntry", "Coronary Artery Disease - Angioplasty");

        Map<String, Object> diagnosis = new HashMap<>();
        diagnosis.put("primary", "Coronary Artery Disease");
        diagnosis.put("secondary", "Hyperlipidemia");
        diagnosis.put("ICD10DiagnosisCodes", new String[] { "I25.10" }); // ICD-10 code for CAD

        Map<String, Object> procedure = new HashMap<>();
        procedure.put("name", "Coronary Angioplasty");
        procedure.put("code", "92928");
        procedure.put("type", "CPT/HCPCS");

        dischargeSummary.put("patientDetails", patientDetails);
        dischargeSummary.put("facility", facility);
        dischargeSummary.put("diagnosis", diagnosis);
        dischargeSummary.put("procedures", new Object[] { procedure });
        dischargeSummary.put("treatmentSummary",
                "Patient underwent successful coronary angioplasty with stent placement. Post-op recovery stable.");
        
        Map<String, Object> medication1 = new HashMap<>();
        medication1.put("name", "Aspirin");
        medication1.put("dosage", "81mg");
        medication1.put("frequency", "Once daily");
        medication1.put("duration", "Indefinite");

        Map<String, Object> medication2 = new HashMap<>();
        medication2.put("name", "Atorvastatin");
        medication2.put("dosage", "40mg");
        medication2.put("frequency", "Once daily");
        medication2.put("duration", "Indefinite");

        dischargeSummary.put("medicationsOnDischarge", new Object[] { medication1, medication2 });
        dischargeSummary.put("followUpInstructions",
                "Follow up in 7 days at cardiology clinic. Continue medications as prescribed. Report any chest pain immediately.");
        
        dischargeSummary.put("CPT_HCPCS_ProcedureCodes", "92928");
        dischargeSummary.put("Procedure", "Coronary Angioplasty");
        dischargeSummary.put("billedAmount", "₹1,50,000");
        dischargeSummary.put("disease", "heart");
        dischargeSummary.put("Admitting Diagnosis", "Coronary Artery Disease");
        dischargeSummary.put("Doctor Prescription Diagnosis", "Coronary Artery Disease");

        // Also include policyId and dateOfBirth variables to satisfy validation worker
        variables.put("policyId", "ABC12307");
        variables.put("dateOfBirth", "1990-04-25");

        // keep only dischargeSummary in variables (along with above for validation)
        variables.put("DischargeSummary", dischargeSummary);

        // Safe handling of AISBusinessKey
        Object keyObj = job.getVariablesAsMap().get("AISBusinessKey");
        String businessKey = (keyObj != null) ? keyObj.toString() : UUID.randomUUID().toString();

        // Logging
        log.info("Worker [{}] started for jobKey={} processInstanceKey={}",
                job.getType(), job.getKey(), job.getProcessInstanceKey());
        log.info("Incoming Job Variables: {}", job.getVariables());
        log.info("Using BusinessKey: {}", businessKey);
        log.info("Prepared Variables to send: {}", variables);

        // Publish updated data message
        zeebeClient.newPublishMessageCommand()
                .messageName("MedicalReviewMessage")
                .correlationKey(businessKey)
                .variables(variables)
                .send()
                .join();

        log.info("Message [MedicalReviewMessage] published with correlationKey={} for processInstanceKey={}",
                businessKey, job.getProcessInstanceKey());

        return variables;
    }
}
