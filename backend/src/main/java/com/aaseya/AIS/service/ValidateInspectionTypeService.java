package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.ControlTypeDAO;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.IDPSummaryDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.Model.IDPSummary;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.service.InspectionRiskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ValidateInspectionTypeService {

    @Autowired
    private InspectionTypeDAO inspectionTypeDAO;

    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private ControlTypeDAO controlTypeDAO;

    @Autowired
    private InspectionRiskService inspectionRiskService;

    @Autowired
    private IDPSummaryDAO idpSummaryDAO;

    private final ObjectMapper mapper = new ObjectMapper();

    // =====================================================================================
    // EXACT SAME LOGIC AS ORIGINAL - just moved here (non-transactional)
    // =====================================================================================
    public List<Map<String, Object>> accumulateIdpOutputs(Map<String, Object> variables) {
        List<Map<String, Object>> idpOutputs = new ArrayList<>();

        try {
            Object existing = variables.get("idpOutputs");

            if (existing instanceof List<?>) {
                for (Object val : (List<?>) existing) {
                    if (val instanceof Map<?, ?> map) idpOutputs.add((Map<String, Object>) map);
                    else if (val instanceof String json)
                        idpOutputs.add(mapper.readValue(json, Map.class));
                }
            } else if (existing instanceof String json) {
                idpOutputs = mapper.readValue(json, List.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return idpOutputs;
    }

    // =====================================================================================
    // EXACT SAME LOGIC AS ORIGINAL performValidation() - now @Transactional + idempotent
    // =====================================================================================
    @Transactional
    public Map<String, Object> performValidation(List<Map<String, Object>> idpOutputs, Long processInstanceKey) {
        try {

            boolean allInspectionTypesNull =
                    idpOutputs.stream()
                            .map(j -> (String) j.getOrDefault("inspectionType", j.get("InspectionType")))
                            .allMatch(v -> v == null || v.isBlank());

            if (allInspectionTypesNull)
                return failure("All documents are missing inspection type information.", "Missing inspection type");

            Map<String, Object> merged = mergeJsonsIntelligently(idpOutputs);

            String inspectionType = (String) merged.get("inspectionType");
            String location = (String) merged.get("location");
            String entityName = (String) merged.get("entityName");

            if (inspectionType == null || inspectionType.isBlank() ||
                    location == null || location.isBlank() ||
                    entityName == null || entityName.isBlank()) {

                return failure("Required data missing: 'Inspection Type', 'Location', 'Entity Name'.",
                        "Incomplete data");
            }

            if (!inspectionTypeDAO.isInspectionTypeExists(inspectionType))
                return failure("Inspection type '" + inspectionType + "' not found in system.",
                        "Invalid inspection type");

            Inspection_Type inspection = inspectionTypeDAO.getInspectionTypesByName(inspectionType);

            boolean locationValid = false;
            if (inspection != null && inspection.getNewEntities() != null) {
                for (NewEntity ent : inspection.getNewEntities()) {
                    String loc = (ent.getAddress() != null)
                            ? ent.getAddress()
                            : ent.getFacility();

                    if (loc != null && loc.equalsIgnoreCase(location)) {
                        locationValid = true;
                        break;
                    }
                }
            }

            if (!locationValid)
                return failure("Location '" + location + "' does not match any mapped entity.",
                        "Location mismatch");

            // Entity lookup or creation
            NewEntity existingEntity = entityDAO.getEntityByName(entityName);
            String entityId;
            boolean isNewEntity = false;

            if (existingEntity == null) {
                NewEntity ne = new NewEntity();
                ne.setName(entityName);
                ne.setAddress(location);

                entityId = entityDAO.saveEntity(ne);
                isNewEntity = true;

            } else {
                entityId = existingEntity.getEntityid();
            }

            if (inspection != null) {
                try {
                    inspectionTypeDAO.linkEntityWithInspectionType(
                            inspection.getIns_type_id(), entityId);
                } catch (Exception ex) {
                    return failure("Entity–inspectionType mapping failed.", "Mapping error");
                }
            }

            // ControlType
            String controlTypeDoc = (String) merged.get("controlType");
            String finalControlType =
                    (controlTypeDoc != null && !controlTypeDoc.isBlank())
                            ? controlTypeDoc.trim()
                            : controlTypeDAO.getControlTypeByInspectionType(inspectionType);

            // Normalize numbers
            merged.put("Severity", normalizeNumeric(merged.get("Severity")));
            merged.put("Likelihood", normalizeNumeric(merged.get("Likelihood")));
            merged.put("complianceGap", normalizeNumeric(merged.get("complianceGap")));
            merged.put("historicalRisk", normalizeNumeric(merged.get("historicalRisk")));

            // Final JSON
            Map<String, Object> finalJson = new LinkedHashMap<>();

            finalJson.put("inspectionType", inspectionType);
            finalJson.put("location", location);
            finalJson.put("entityName", entityName);
            finalJson.put("entityId", entityId);
            finalJson.put("isNewEntity", isNewEntity);

            finalJson.put("equipmentName", merged.get("equipmentName"));
            finalJson.put("equipmentId", merged.get("equipmentId"));
            finalJson.put("equipmentType", merged.get("equipmentType"));

            finalJson.put("Severity", merged.get("Severity"));
            finalJson.put("Likelihood", merged.get("Likelihood"));
            finalJson.put("complianceGap", merged.get("complianceGap"));
            finalJson.put("historicalRisk", merged.get("historicalRisk"));

            finalJson.put("certificateNumber", merged.get("certificateNumber"));
            finalJson.put("remarksOrDefectsFound", merged.get("remarksOrDefectsFound"));
            finalJson.put("lastInspectionDate", merged.get("lastInspectionDate"));

            finalJson.put("controlType", finalControlType);

            // Risk Score + Risk Level
            try {
                Map<String, Object> riskInput = Map.of(
                        "Severity", merged.get("Severity"),
                        "Likelihood", merged.get("Likelihood"),
                        "complianceGap", merged.get("complianceGap"),
                        "historicalRisk", merged.get("historicalRisk")
                );

                Map<String, Object> riskResult =
                        inspectionRiskService.generateRiskSummary(riskInput);

                finalJson.put("riskScore", riskResult.get("riskScore"));
                finalJson.put("riskLevel", riskResult.get("riskLevel"));

            } catch (Exception ex) {
                finalJson.put("riskScore", 0);
                finalJson.put("riskLevel", "Low");
            }

            // Status
            String docStatus =
                    merged.containsKey("Status") && merged.get("Status") != null
                            ? merged.get("Status").toString()
                            : "New";

            finalJson.put("Status", docStatus);
            finalJson.put("outputStatus", "success");

            finalJson.put("processInstanceKey", processInstanceKey);

            // =====================================================================================
            // ⭐⭐ IDP SUMMARY SAVE - NOW IDEMPOTENT ⭐⭐
            // =====================================================================================
            IDPSummary summary = idpSummaryDAO.findByProcessInstanceKey(processInstanceKey);
            if (summary == null) {
                summary = new IDPSummary();
                summary.setProcessInstanceKey(processInstanceKey);
            }

            summary.setInspectionType(inspection);
            summary.setEntity(existingEntity);
            summary.setEntityId(entityId);
            summary.setIsNewEntity(isNewEntity);
            summary.setLocation(location);

            summary.setEquipmentId((String) finalJson.get("equipmentId"));
            summary.setEquipmentType((String) finalJson.get("equipmentType"));

            summary.setSeverity((Integer) finalJson.get("Severity"));
            summary.setLikelihood((Integer) finalJson.get("Likelihood"));
            summary.setComplianceGap((Integer) finalJson.get("complianceGap"));
            summary.setHistoricalRisk((Integer) finalJson.get("historicalRisk"));

            summary.setCertificateNumber((String) finalJson.get("certificateNumber"));
            summary.setRemarksOrDefectsFound((String) finalJson.get("remarksOrDefectsFound"));

            if (finalJson.get("lastInspectionDate") != null) {
                summary.setLastInspectionDate(
                        LocalDate.parse(finalJson.get("lastInspectionDate").toString()));
            }

            summary.setRiskScore((Integer) finalJson.get("riskScore"));
            summary.setRiskLevel((String) finalJson.get("riskLevel"));

            summary.setStatus((String) finalJson.get("Status"));
            summary.setOutputStatus((String) finalJson.get("outputStatus"));

            summary.setMergedJson(mapper.writeValueAsString(finalJson));

            idpSummaryDAO.save(summary);  // Now handles both INSERT + UPDATE

            // =====================================================================================

            Map<String, Object> response = new HashMap<>();
            response.put("mergedJson", finalJson);
            response.put("validationStatus", "success");
            return response;

        } catch (Exception e) {
            // CRITICAL: Let exceptions bubble up so Zeebe retries the job
            throw new RuntimeException("Validation failed: " + e.getMessage(), e);
        }
    }

    // =====================================================================================
    // ALL HELPER METHODS - EXACT SAME AS ORIGINAL
    // =====================================================================================
    private Map<String, Object> failure(String msg, String cause) {
        Map<String, Object> fail = new HashMap<>();
        fail.put("validationStatus", "failure");
        fail.put("ErrorDescription", msg);
        fail.put("previousOutcome", cause);
        return fail;
    }

    private Integer normalizeNumeric(Object value) {
        try {
            if (value == null) return 0;
            String s = value.toString().trim();
            if (s.isEmpty() || s.equalsIgnoreCase("null")) return 0;
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private Map<String, Object> mergeJsonsIntelligently(List<Map<String, Object>> list) {
        Map<String, Object> merged = new LinkedHashMap<>();
        String inspectionType = null;

        for (Map<String, Object> json : list) {

            Object type = json.getOrDefault("inspectionType", json.get("InspectionType"));
            if (inspectionType == null && type != null) {
                inspectionType = String.valueOf(type);
                merged.put("inspectionType", inspectionType);
            }

            Object jsonOutput = json.getOrDefault("jsonOutput", json.get("JsonOutput"));
            if (jsonOutput instanceof Map<?, ?> inner) {
                for (Map.Entry<?, ?> e : inner.entrySet()) {
                    String key = normalizeKey(e.getKey().toString());
                    Object val = e.getValue();
                    if (val != null) merged.putIfAbsent(key, val);
                }
            }

            for (Map.Entry<String, Object> e : json.entrySet()) {
                String key = normalizeKey(e.getKey());
                Object val = e.getValue();

                if (key.equals("jsonoutput")) continue;
                if (val != null) merged.putIfAbsent(key, val);
            }
        }

        return merged;
    }

    private String normalizeKey(String key) {
        key = key.trim().toLowerCase();

        return switch (key) {
            case "entity name", "entity", "entity_name", "issued to" -> "entityName";
            case "location", "loc", "place", "address" -> "location";
            case "certificate no", "certificateno", "certificate_no" -> "certificateNumber";
            case "remarks", "remark", "defectsfound" -> "remarksOrDefectsFound";
            case "severity" -> "Severity";
            case "likelihood" -> "Likelihood";
            case "compliance gap", "compliancegap", "compliance" -> "complianceGap";
            case "historical risk", "historicalrisk" -> "historicalRisk";
            case "inspection type", "inspectiontype" -> "inspectionType";
            case "equipment name", "equipment_name" -> "equipmentName";
            case "equipment id", "equipmentid" -> "equipmentId";
            case "equipment type", "equipmenttype" -> "equipmentType";
            case "last serviced", "last inspection date" -> "lastInspectionDate";
            case "control type", "controltype" -> "controlType";
            case "status" -> "Status";
            default -> key;
        };
    }
}
