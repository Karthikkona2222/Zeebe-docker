package com.aaseya.AIS.dto;
import static org.junit.jupiter.api.Assertions.assertEquals;
 
import org.junit.jupiter.api.Test;
 
public class ReportRequestDTOTest {
 
    @Test
    public void testOverview() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setOverview("Overview Content");
        assertEquals("Overview Content", dto.getOverview());
    }
 
    @Test
    public void testObservations() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setObservations("Observation Content");
        assertEquals("Observation Content", dto.getObservations());
    }
 
    @Test
    public void testRiskScore() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setRiskScore("High");
        assertEquals("High", dto.getRiskScore());
    }
 
    @Test
    public void testRecommendations() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setRecommendations("Follow safety measures");
        assertEquals("Follow safety measures", dto.getRecommendations());
    }
 
    @Test
    public void testSummary() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setSummary("Summary Content");
        assertEquals("Summary Content", dto.getSummary());
    }
 
    @Test
    public void testMeasurements() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setMeasurements("Measurement Data");
        assertEquals("Measurement Data", dto.getMeasurements());
    }
 
    @Test
    public void testKeyRisks() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setKeyRisks("Fire Risk");
        assertEquals("Fire Risk", dto.getKeyRisks());
    }
}
 
 
 