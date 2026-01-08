package com.aaseya.AIS.service;
 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.dmn.engine.*;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.springframework.stereotype.Service;
 
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
 
@Service
public class InspectionRiskService {
 
    private final ObjectMapper mapper = new ObjectMapper();
 
    // Controller currently calls generateRiskSummary(Map<String,Object>)
    // This wrapper converts the Map -> JsonNode and reuses buildRiskSummary(JsonNode)
    @SuppressWarnings("unchecked")
    public Map<String, Object> generateRiskSummary(Map<String, Object> inputMap) {
        JsonNode node = mapper.convertValue(inputMap, JsonNode.class);
        return buildRiskSummary(node);
    }
 
    /**
     * Main implementation: reads fields from inputJson (or inputJson.mergedJson),
     * computes riskScore, evaluates DMN inline (if present), and returns final map.
     *
     * Requirements:
     * - DMN file path: src/main/resources/dmn/RiskActionDecision.dmn
     * - Decision id (in DMN) should be "RiskActionDecision" OR change decisionId below
     *
     * Add Camunda DMN dependency in pom.xml if you want DMN evaluation:
     * <dependency>
     *   <groupId>org.camunda.bpm.dmn</groupId>
     *   <artifactId>camunda-engine-dmn</artifactId>
     *   <version>7.21.0</version> <!-- or your Camunda 7.x version -->
     * </dependency>
     */
    public Map<String, Object> buildRiskSummary(JsonNode inputJson) {
        Map<String, Object> finalJson = new LinkedHashMap<>();
 
        // Use mergedJson if present; otherwise use root node
        JsonNode mergedNode = inputJson.has("mergedJson") ? inputJson.get("mergedJson") : inputJson;
 
        // copy fields (numbers preserved as numbers)
        mergedNode.fieldNames().forEachRemaining(field -> {
            JsonNode node = mergedNode.get(field);
            Object value = node.isNull() ? null : (node.isNumber() ? node.numberValue() : node.asText());
            finalJson.put(field, value);
        });
 
        // Normalise equipment keys (some sources used equipmentId/equipmentID)
        if (!finalJson.containsKey("equipmentID") && finalJson.containsKey("equipmentId")) {
            finalJson.put("equipmentID", finalJson.get("equipmentId"));
        }
        finalJson.putIfAbsent("equipmentName", finalJson.get("equipmentName"));
        finalJson.putIfAbsent("equipmentType", finalJson.get("equipmentType"));
 
        // extract numeric values (case-sensitive keys used in your mergedJson)
        int severity = toInt(finalJson.get("Severity"));
        int likelihood = toInt(finalJson.get("Likelihood"));
        int complianceGap = toInt(finalJson.get("complianceGap"));
        int historicalRisk = toInt(finalJson.get("historicalRisk"));
 
        int riskScore = severity + likelihood + complianceGap + historicalRisk;
 
        // === Inline DMN evaluation ===
        String decisionId = "RiskActionDecision";
        String dmnPath = "/dmn/RiskActionDecision.dmn";
        String riskLevel = "Unknown";
        String recommendation = null;
 
        InputStream dmnStream = null;
        DmnEngine dmnEngine = null;
        try {
            dmnStream = getClass().getResourceAsStream(dmnPath);
            if (dmnStream != null) {
                dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
                // parse decision using decisionId
                DmnDecision decision = dmnEngine.parseDecision(decisionId, dmnStream);
 
                Map<String, Object> variables = Map.of("riskScore", riskScore);
 
                DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
 
                if (result != null && !result.isEmpty()) {
                    Map<String, Object> entries = result.getSingleResult().getEntryMap();
                    Object lvl = entries.get("riskLevel");
                    Object rec = entries.get("recommendation");
                    if (lvl != null) riskLevel = lvl.toString();
                    if (rec != null) recommendation = rec.toString();
                } else {
                    // no match: fallback to in-code thresholds
                    if (riskScore >= 80) riskLevel = "High";
                    else if (riskScore >= 50) riskLevel = "Medium";
                    else riskLevel = "Low";
                }
            } else {
                // DMN not found -> fallback to in-code thresholds
                if (riskScore >= 80) riskLevel = "High";
                else if (riskScore >= 50) riskLevel = "Medium";
                else riskLevel = "Low";
            }
        } catch (Exception e) {
            // log and fallback
            e.printStackTrace();
            if (riskScore >= 80) riskLevel = "High";
            else if (riskScore >= 50) riskLevel = "Medium";
            else riskLevel = "Low";
            recommendation = "Error evaluating DMN: " + e.getMessage();
        } finally {
            // Camunda DMN engine doesn't require explicit close; clear references
            if (dmnStream != null) {
                try { dmnStream.close(); } catch (Exception ignored) {}
            }
            dmnEngine = null;
        }
 
        // Add computed values
        finalJson.put("riskScore", riskScore);
        finalJson.put("riskLevel", riskLevel);
        if (recommendation != null && !recommendation.isBlank()) finalJson.put("recommendation", recommendation);
 
        return finalJson;
    }
 
    private int toInt(Object val) {
        try {
            if (val == null) return 0;
            String s = val.toString().trim();
            if (s.isEmpty() || s.equalsIgnoreCase("null")) return 0;
            return Integer.parseInt(s.replaceAll("[^0-9-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
 
 