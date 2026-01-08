package com.aaseya.AIS.service;

import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class InspectionRiskServiceTest {

    private final InspectionRiskService service = new InspectionRiskService();

    private Map<String, Object> input(int s, int lh, int c, int h) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Severity", s);
        map.put("Likelihood", lh);
        map.put("complianceGap", c);
        map.put("historicalRisk", h);
        return map;
    }

    // TC-RISK-01
    @Test
    void testLowRiskLevel() {
        Map<String, Object> result = service.generateRiskSummary(input(10, 10, 10, 5)); // Score = 35
        assertEquals(35, result.get("riskScore"));
        assertEquals("Low", result.get("riskLevel"));
    }

    // TC-RISK-02
    @Test
    void testMediumRiskLevel() {
        Map<String, Object> result = service.generateRiskSummary(input(20, 20, 20, 5)); // Score = 65
        assertEquals(65, result.get("riskScore"));
        assertEquals("Medium", result.get("riskLevel"));
    }

    // TC-RISK-03
    @Test
    void testHighRiskLevel() {
        Map<String, Object> result = service.generateRiskSummary(input(30, 25, 25, 10)); // Score = 90
        assertEquals(90, result.get("riskScore"));
        assertEquals("High", result.get("riskLevel"));
    }

    // TC-RISK-04
    @Test
    void testRiskScoreCalculation() {
        Map<String, Object> result = service.generateRiskSummary(input(15, 15, 10, 10));
        assertEquals(50, result.get("riskScore"));
    }

    // TC-RISK-05
    @Test
    void testGenerateRiskSummaryWrapper() {
        Map<String, Object> map = input(10, 20, 5, 15);
        Map<String, Object> result = service.generateRiskSummary(map);
        assertEquals(50, result.get("riskScore"));
        assertEquals("Medium", result.get("riskLevel"));
    }

    // TC-RISK-06
    @Test
    void testMissingValuesDefaultToZero() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Severity", null);
        map.put("Likelihood", null);
        map.put("complianceGap", null);
        map.put("historicalRisk", null);

        Map<String, Object> result = service.generateRiskSummary(map);

        assertEquals(0, result.get("riskScore"));
        assertEquals("Low", result.get("riskLevel"));
    }

    // TC-RISK-07
    @Test
    void testMergedJsonOverride() {
        Map<String, Object> root = new LinkedHashMap<>();
        Map<String, Object> merged = input(40, 20, 10, 5); // Score = 75 => Medium

        root.put("mergedJson", merged);

        Map<String, Object> result = service.generateRiskSummary(root);

        assertEquals(75, result.get("riskScore"));
        assertEquals("Medium", result.get("riskLevel"));
    }
}
