package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.Model.CodeRangeCategory;
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.dao.CodeRangeCategoryDAO;
import com.aaseya.AIS.dao.PolicyDetailsDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MedicalValidationWorker {

    @Autowired
    private PolicyDetailsDAO policyDetailsDAO;

    @Autowired
    private CodeRangeCategoryDAO codeRangeCategoryDAO;

    @Autowired
    private ZeebeClient zeebeClient;

    private static final Logger log = LoggerFactory.getLogger(MedicalValidationWorker.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @JobWorker(type = "MedicalValidationWorker")
    @Transactional
    public Map<String, Object> validate(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Map<String, Object> validationResult = new HashMap<>();
        List<String> failedValidations = new ArrayList<>();

        // NEW: collector for structured, claimant-friendly issues (added; does not replace existing fields)
        List<Map<String, Object>> validationIssues = new ArrayList<>();
        List<String> claimantMessages = new ArrayList<>();

        log.info("Starting medical validation for job key {}", job.getKey());
        try {
            String policyId = (String) variables.get("policyId");
            PolicyDetails policy = policyDetailsDAO.findById(policyId);
            if (policy == null) {
                String msg = "Policy not found for provided Policy ID: " + policyId;
                log.error(msg);

                // Add claimant-facing issue for hard failure
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "policy");
                issue.put("status", "FAIL");
                issue.put("claimantMessage", "The policy information could not be found for the submitted Policy ID. Please verify the Policy ID and resubmit.");
                issue.put("recommendedAction", "Review the Policy ID and provide the correct identifier or contact support for assistance.");
                issue.put("details", Collections.singletonMap("policyId", policyId));
                validationIssues.add(issue);
                claimantMessages.add((String) issue.get("claimantMessage"));

                throw new RuntimeException(msg);
            }
            log.info("Policy found for id {}: Customer {}", policyId, policy.getCustomerName());

            // =========================
            // 1. Customer Name Check
            // =========================
            String dbCustomerName = policy.getCustomerName();
            Map<String, Object> dischargeSummary = (Map<String, Object>) variables.get("DischargeSummary");
            String dischargeName = null;
            if (dischargeSummary != null) {
                Map<String, Object> patientDetails = (Map<String, Object>) dischargeSummary.get("patientDetails");
                if (patientDetails != null) {
                    dischargeName = (String) patientDetails.get("name");
                }
            }
            boolean nameValid = dbCustomerName != null
                    && dbCustomerName.equalsIgnoreCase(dischargeName);

            Map<String, Object> nameCheck = new HashMap<>();
            nameCheck.put("dbName", dbCustomerName);
            nameCheck.put("dischargeName", dischargeName);
            nameCheck.put("valid", nameValid);
            validationResult.put("customerNameCheck", nameCheck);

            // NEW: claimant-facing issue block
            {
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("dbName", dbCustomerName);
                details.put("dischargeName", dischargeName);
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "customerName");
                issue.put("status", nameValid ? "PASS" : "FAIL");
                String msg = nameValid
                        ? "The claimant name matches the policy records."
                        : "The claimant name in the discharge summary differs from the name on the policy.";
                String action = nameValid
                        ? "No action is needed."
                        : "Please confirm the correct claimant name and provide supporting identification, if required.";
                issue.put("claimantMessage", msg);
                issue.put("recommendedAction", action);
                issue.put("details", details);
                validationIssues.add(issue);
                claimantMessages.add(msg);
            }

            if (!nameValid) {
                failedValidations.add("Customer name mismatch: The name in the database and discharge summary document do not match. Please verify the claimant's name.");
                log.warn("Name mismatch: DB='{}', DischargeSummary='{}'", dbCustomerName, dischargeName);
            } else {
                log.info("Customer name validated successfully.");
            }

            // =========================
            // 2. Date of Birth Check
            // =========================
            String expectedDob = policy.getDateOfBirth() != null ? policy.getDateOfBirth().toString() : null;
            String dobVar = null;
            if (variables.get("dateOfBirth") != null) {
                try {
                    LocalDate parsedDob = LocalDate.parse(
                            variables.get("dateOfBirth").toString().replace("/", "-").replace(".", "-"),
                            formatter);
                    dobVar = parsedDob.toString();
                } catch (Exception e) {
                    dobVar = variables.get("dateOfBirth").toString();
                    log.warn("DateOfBirth parsing issue for process variable, using raw value: {}", dobVar);
                }
            }
            String dischargeDob = null;
            Map<String, Object> dischargeSummaryDOB = (Map<String, Object>) variables.get("DischargeSummary");
            if (dischargeSummaryDOB != null) {
                Map<String, Object> patientDetails = (Map<String, Object>) dischargeSummaryDOB.get("patientDetails");
                if (patientDetails != null) {
                    Object dobFromSummaryObj = null;
                    for (String key : patientDetails.keySet()) {
                        if ("dateofbirth".equalsIgnoreCase(key)) {
                            dobFromSummaryObj = patientDetails.get(key);
                            break;
                        }
                    }
                    if (dobFromSummaryObj != null) {
                        try {
                            LocalDate parsedDischargeDob = LocalDate.parse(dobFromSummaryObj.toString(), formatter);
                            dischargeDob = parsedDischargeDob.toString();
                        } catch (Exception e) {
                            dischargeDob = dobFromSummaryObj.toString();
                            log.warn("DateOfBirth parsing issue for DischargeSummary patientDetails, using raw value: {}", dischargeDob);
                        }
                    }
                }
            }
            boolean dobValid = expectedDob != null && dischargeDob != null
                    && expectedDob.equals(dobVar) && expectedDob.equals(dischargeDob);

            Map<String, Object> dobCheck = new HashMap<>();
            dobCheck.put("expected", expectedDob);
            dobCheck.put("dateOfBirthVar", dobVar);
            dobCheck.put("dischargeSummaryDob", dischargeDob);
            dobCheck.put("valid", dobValid);
            validationResult.put("dobCheck", dobCheck);

            // NEW: claimant-facing issue block
            {
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("policyDob", expectedDob);
                details.put("submittedDob", dobVar);
                details.put("dischargeDob", dischargeDob);
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "dateOfBirth");
                issue.put("status", dobValid ? "PASS" : "FAIL");
                String msg = dobValid
                        ? "The date of birth matches the policy records."
                        : "The date of birth provided does not match the policy records.";
                String action = dobValid
                        ? "No action is needed."
                        : "Please provide the correct date of birth and a government-issued ID copy to verify.";
                issue.put("claimantMessage", msg);
                issue.put("recommendedAction", action);
                issue.put("details", details);
                validationIssues.add(issue);
                claimantMessages.add(msg);
            }

            if (!dobValid) {
                failedValidations.add("Date of Birth mismatch: The date of birth provided in the process variable, discharge summary, or database do not all match. Please verify the claimant's DOB.");
                log.warn("DOB mismatch: DB='{}', ProcessVar='{}', DischargeSummary='{}'", expectedDob, dobVar, dischargeDob);
            } else {
                log.info("Date of Birth validated successfully across DB, process variable, and discharge summary.");
            }

            // =========================
            // 3. Admission and Discharge Dates Validation
            // =========================
            LocalDate admissionDate = null;
            LocalDate dischargeDate = null;
            if (dischargeSummaryDOB != null) {
                Map<String, Object> patientDetails = (Map<String, Object>) dischargeSummaryDOB.get("patientDetails");
                if (patientDetails != null) {
                    try {
                        if (patientDetails.get("admissionDate") != null) {
                            admissionDate = LocalDate.parse(patientDetails.get("admissionDate").toString(), formatter);
                        }
                        if (patientDetails.get("dischargeDate") != null) {
                            dischargeDate = LocalDate.parse(patientDetails.get("dischargeDate").toString(), formatter);
                        }
                    } catch (Exception e) {
                        failedValidations.add("Invalid admission or discharge date format in Discharge Summary document. Please provide valid dates.");
                        log.warn("Invalid date format in DischargeSummary.patientDetails", e);
                    }
                }
            }
            LocalDate coverageStart = policy.getCoverageStartDate();
            LocalDate coverageEnd = policy.getCoverageEndDate();

            Map<String, Object> admissionCheck = new HashMap<>();
            admissionCheck.put("expectedStart", coverageStart);
            admissionCheck.put("expectedEnd", coverageEnd);
            admissionCheck.put("actual", admissionDate);
            boolean admissionInRange = admissionDate != null && coverageStart != null && coverageEnd != null
                    && !admissionDate.isBefore(coverageStart) && !admissionDate.isAfter(coverageEnd);
            admissionCheck.put("valid", admissionInRange);
            validationResult.put("admissionDateCheck", admissionCheck);

            // NEW: claimant-facing issue for admission date
            {
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("coverageStart", coverageStart);
                details.put("coverageEnd", coverageEnd);
                details.put("admissionDate", admissionDate);
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "admissionDate");
                issue.put("status", admissionInRange ? "PASS" : "FAIL");
                String msg = admissionInRange
                        ? "The admission date is within the policy coverage period."
                        : "The admission date is outside the policy coverage period.";
                String action = admissionInRange
                        ? "No action is needed."
                        : "Please review the treatment dates or share documents clarifying coverage eligibility.";
                issue.put("claimantMessage", msg);
                issue.put("recommendedAction", action);
                issue.put("details", details);
                validationIssues.add(issue);
                claimantMessages.add(msg);
            }

            if (!admissionInRange) {
                failedValidations.add("Admission date out of coverage: The admission date is outside your policy coverage period.");
                log.warn("Admission date {} out of range [{} - {}]", admissionDate, coverageStart, coverageEnd);
            } else {
                log.info("Admission date is within coverage range.");
            }

            Map<String, Object> dischargeCheck = new HashMap<>();
            dischargeCheck.put("expectedStart", coverageStart);
            dischargeCheck.put("expectedEnd", coverageEnd);
            dischargeCheck.put("actual", dischargeDate);
            boolean dischargeInRange = dischargeDate != null && coverageStart != null && coverageEnd != null
                    && !dischargeDate.isBefore(coverageStart) && !dischargeDate.isAfter(coverageEnd);
            dischargeCheck.put("valid", dischargeInRange);
            validationResult.put("dischargeDateCheck", dischargeCheck);

            // NEW: claimant-facing issue for discharge date and sequence
            {
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("coverageStart", coverageStart);
                details.put("coverageEnd", coverageEnd);
                details.put("dischargeDate", dischargeDate);
                boolean sequenceOk = !(admissionDate != null && dischargeDate != null && dischargeDate.isBefore(admissionDate));
                boolean pass = dischargeInRange && sequenceOk;

                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "dischargeDate");
                issue.put("status", pass ? "PASS" : "FAIL");

                String msg;
                String action;
                if (!sequenceOk) {
                    msg = "The discharge date occurs before the admission date.";
                    action = "Please correct the admission and discharge dates and provide supporting documents.";
                } else if (!dischargeInRange) {
                    msg = "The discharge date is outside the policy coverage period.";
                    action = "Please review the treatment dates or share documents clarifying coverage eligibility.";
                } else {
                    msg = "The discharge date is within the policy coverage period.";
                    action = "No action is needed.";
                }
                issue.put("claimantMessage", msg);
                issue.put("recommendedAction", action);
                issue.put("details", details);
                validationIssues.add(issue);
                claimantMessages.add(msg);

                if (!sequenceOk) {
                    failedValidations.add("Admission/Discharge sequence error: Discharge date cannot be before admission date.");
                    log.warn("Discharge date {} is before admission date {}", dischargeDate, admissionDate);
                }
            }

            // =========================
            // 4. ICD Code Validation
            // =========================
            List<String> icdCodes = new ArrayList<>();
            try {
                if (dischargeSummaryDOB != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.convertValue(dischargeSummaryDOB, JsonNode.class);
                    JsonNode icdArray = root.path("diagnosis").path("ICD10DiagnosisCodes");
                    if (icdArray.isArray()) {
                        for (JsonNode codeNode : icdArray) {
                            icdCodes.add(codeNode.asText());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error extracting ICD-10 codes from DischargeSummary JSON", e);
            }
            if (icdCodes.isEmpty()) {
                failedValidations.add("Missing ICD-10 codes: No diagnosis ICD-10 codes were found in the Discharge Summary. Kindly provide them.");
                log.warn("No ICD-10 codes found in DischargeSummary.");
            }
            List<CodeRangeCategory> codeRanges = null;
            try {
                codeRanges = codeRangeCategoryDAO.findAll();
                log.info("Fetched {} code ranges for ICD validation.", codeRanges.size());
            } catch (Exception e) {
                log.error("Error fetching ICD code ranges from the database.", e);
            }
            if (codeRanges == null || codeRanges.isEmpty()) {
                failedValidations.add("System configuration issue: No ICD-10 code ranges are currently configured. Please contact support.");
                log.error("No ICD-10 code ranges found in DB.");
            }
            List<Map<String, Object>> icdCheckList = new ArrayList<>();
            for (String code : icdCodes) {
                String codeNorm = code.trim();
                String normalizedCode = normalizeIcdCode(codeNorm);
                boolean valid = false;
                for (CodeRangeCategory range : codeRanges) {
                    String rangeDef = range.getCodeRange();
                    if (rangeDef == null || rangeDef.trim().isEmpty())
                        continue;
                    rangeDef = rangeDef.trim().toUpperCase()
                            .replaceAll("[\\s]+", "")
                            .replaceAll("[\\-–—]+", "-");
                    if (rangeDef.contains("-")) {
                        String[] parts = rangeDef.split("-", 2);
                        if (parts.length != 2) continue;
                        String startNormalized = normalizeIcdCode(parts[0].trim());
                        String endNormalized = normalizeIcdCode(parts[1].trim());
                        if (!startNormalized.isEmpty() && !endNormalized.isEmpty()) {
                            if (normalizedCode.compareTo(startNormalized) >= 0
                                    && normalizedCode.compareTo(endNormalized) <= 0) {
                                valid = true;
                                break;
                            }
                        }
                    } else {
                        String singleNormalized = normalizeIcdCode(rangeDef);
                        if (normalizedCode.equals(singleNormalized)
                                || singleNormalized.startsWith(normalizedCode)
                                || normalizedCode.startsWith(singleNormalized)) {
                            valid = true;
                            break;
                        }
                    }
                }
                Map<String, Object> codeCheck = new HashMap<>();
                codeCheck.put("code", codeNorm);
                codeCheck.put("valid", valid ? "PASS" : "FAIL");
                icdCheckList.add(codeCheck);
                if (!valid) {
                    failedValidations.add("Invalid ICD-10 code '" + codeNorm + "': The code is not recognized as valid within the approved ranges.");
                    log.warn("ICD code validation failed for code {}", codeNorm);
                } else {
                    log.info("ICD code {} validated successfully.", codeNorm);
                }
            }
            validationResult.put("ICD10Check", icdCheckList);

            // NEW: claimant-facing issue for ICD codes
            {
                boolean allCodesPass = true;
                for (Map<String, Object> c : icdCheckList) {
                    if (!"PASS".equals(c.get("valid"))) {
                        allCodesPass = false;
                        break;
                    }
                }
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("codes", icdCheckList);
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "icdCodes");
                boolean configOk = codeRanges != null && !codeRanges.isEmpty();
                boolean inputOk = !icdCodes.isEmpty();
                boolean pass = allCodesPass && configOk && inputOk;
                issue.put("status", pass ? "PASS" : "FAIL");
                String msg;
                String action;
                if (!inputOk) {
                    msg = "Diagnosis codes (ICD-10) are missing from the submitted documents.";
                    action = "Please provide the ICD-10 diagnosis codes as shown in the discharge summary.";
                } else if (!configOk) {
                    msg = "We are currently unable to validate diagnosis codes due to a system configuration issue.";
                    action = "No action required from your side; our team will address this and notify if any additional information is needed.";
                } else if (!allCodesPass) {
                    msg = "One or more diagnosis codes are not within the accepted ranges.";
                    action = "Please provide valid ICD-10 diagnosis codes as reflected in the discharge summary or a revised summary.";
                } else {
                    msg = "The diagnosis codes are accepted.";
                    action = "No action is needed.";
                }
                issue.put("claimantMessage", msg);
                issue.put("recommendedAction", action);
                issue.put("details", details);
                validationIssues.add(issue);
                claimantMessages.add(msg);
            }

            // =========================
            // 5. Required Documents Validation
            // =========================
            Object documentsObj = variables.get("documents");
            Set<String> requiredDocs = new LinkedHashSet<>(Arrays.asList("prescription", "discharge_summary", "summary"));
            Set<String> foundDocs = new LinkedHashSet<>();
            List<Map<String, Object>> documentChecks = new ArrayList<>();
            if (documentsObj instanceof List<?>) {
                for (Object docObj : (List<?>) documentsObj) {
                    if (!(docObj instanceof Map)) continue;
                    Map<String, Object> docMap = (Map<String, Object>) docObj;
                    String docType = docMap.get("documentType") != null
                            ? docMap.get("documentType").toString().toLowerCase(Locale.ROOT).trim()
                            : "";
                    int dotIndex = docType.lastIndexOf('.');
                    String baseName = (dotIndex > 0) ? docType.substring(0, dotIndex) : docType;
                    String normalized = baseName.replace('_', ' ')
                            .replace('-', ' ')
                            .replaceAll("\\s+", " ")
                            .trim();
                    boolean matched = false;
                    if (normalized.contains("discharge summary")) {
                        foundDocs.add("discharge_summary");
                        matched = true;
                    } else if (normalized.contains("summary")) {
                        foundDocs.add("summary");
                        matched = true;
                    }
                    if (normalized.contains("prescription")) {
                        foundDocs.add("prescription");
                        matched = true;
                    }
                    Map<String, Object> docCheck = new HashMap<>();
                    docCheck.put("documentType", docType);
                    docCheck.put("valid", matched);
                    documentChecks.add(docCheck);
                }
            }
            validationResult.put("documentTypeCheck", documentChecks);
            validationResult.put("foundDocuments", new ArrayList<>(foundDocs));

            // NEW: claimant-facing issue for documents
            {
                Set<String> missingDocs = new LinkedHashSet<>(requiredDocs);
                missingDocs.removeAll(foundDocs);
                Map<String, Object> details = new LinkedHashMap<>();
                details.put("required", new ArrayList<>(requiredDocs));
                details.put("received", new ArrayList<>(foundDocs));
                details.put("missing", new ArrayList<>(missingDocs));
                boolean docsPass = missingDocs.isEmpty();
                Map<String, Object> issue = new LinkedHashMap<>();
                issue.put("type", "documents");
                issue.put("status", docsPass ? "PASS" : "FAIL");
                String msg = docsPass
                        ? "All required documents have been received."
                        : "Some required documents are missing: " + String.join(", ", missingDocs) + ".";
                String action = docsPass
                        ? "No action is needed."
                        : "Please upload the missing document(s) listed to proceed with the claim review.";
                issue.put("claimantMessage", msg);
                issue.put("recommendedAction", action);
                issue.put("details", details);
                validationIssues.add(issue);
                claimantMessages.add(msg);

                if (!docsPass) {
                    failedValidations.add("Missing required documents: Please submit the following documents - "
                            + String.join(", ", missingDocs) + ".");
                    log.warn("Missing documents detected: {}", missingDocs);
                } else {
                    log.info("All required documents found.");
                }
            }

            // =========================
            // 6. Final Validation Result and Publishing
            // =========================
            
            boolean allPassed = failedValidations.isEmpty();
            validationResult.put("ValidationStatus", allPassed ? "PASS" : "FAIL");
            validationResult.put("ResultMessage",
                    allPassed ? "All validations passed successfully." : String.join("; ", failedValidations));

            // NEW: attach structured issues and claimant message list without changing existing names
            validationResult.put("ValidationIssues", validationIssues);
            validationResult.put("ClaimantMessages", claimantMessages);

            variables.put("ValidationDataResult", validationResult);
            variables.put("ValidationStatus", validationResult.get("ValidationStatus"));

            log.info("Validation complete. Status: {}, Messages: {}",
                    validationResult.get("ValidationStatus"), validationResult.get("ResultMessage"));

            String businessKey = variables.getOrDefault("AISBusinessKey", UUID.randomUUID().toString()).toString();
            zeebeClient.newPublishMessageCommand()
                    .messageName("MedicalValidationMessage")
                    .correlationKey(businessKey)
                    .variables(variables)
                    .send()
                    .join();

        } catch (Exception e) {
            log.error("Exception during medical validation", e);
            validationResult.put("ValidationStatus", "FAIL");
            validationResult.put("ResultMessage", "Exception occurred during validation: " + e.getMessage());

            // Ensure issues/messages are present for email even on exception
            if (!validationResult.containsKey("ValidationIssues")) {
                validationResult.put("ValidationIssues", new ArrayList<Map<String, Object>>());
            }
            if (!validationResult.containsKey("ClaimantMessages")) {
                validationResult.put("ClaimantMessages", new ArrayList<String>());
            }

            variables.put("ValidationDataResult", validationResult);
        }

        log.info("Validation job finished with status: {}",
                validationResult.getOrDefault("ValidationStatus", "UNKNOWN"));
        return variables;
    }

    private String normalizeIcdCode(String code) {
        if (code == null || code.trim().isEmpty()) return "";

        String cleaned = code.trim().toUpperCase();

        cleaned = cleaned.replaceAll("[\\s/\\-–—]", "");

        if (cleaned.length() == 0) return "";

        char letter = cleaned.charAt(0);
        String numPart = cleaned.substring(1);

        if (!numPart.matches("\\d{2}(\\.\\d{1,2})?")) {
            log.warn("Invalid ICD code format: {}", code);
            return "";
        }

        return letter + numPart;
    }
}
