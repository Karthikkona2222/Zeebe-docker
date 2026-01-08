package com.aaseya.AIS.dto;
 
import org.junit.jupiter.api.Test;
 
import java.time.LocalDate;
 
import static org.junit.jupiter.api.Assertions.*;
 
class InspectionCase_EntityDTOTest {
 
    @Test
    void testSettersAndGetters() {
 
        InspectionCase_EntityDTO dto = new InspectionCase_EntityDTO();
 
        // Primitive fields
        dto.setInspectionID(1001L);
        dto.setInspector_source("SRC1");
        dto.setStatus("COMPLETED");
        dto.setDateOfInspection("2024-01-10");
        dto.setEntityid("ABC Corp");
        dto.setName("Entity Name");
        dto.setLocation("Bangalore");
        dto.setInspection_type("ADP");
        dto.setRepresentative_email("test@email.com");
        dto.setReference_case("CASE001");
        dto.setEfforts("HIGH");
        dto.setReason("Verification");
        dto.setAssigned_inspector("Inspector A");
        dto.setSize("LARGE");
        dto.setRepresentative_name("John Doe");
        dto.setRepresentative_phoneno("9876543210");
        dto.setSubSegment("SubSeg01");
        dto.setSegment("Seg01");
 
        LocalDate due = LocalDate.of(2024, 5, 20);
        dto.setDue_date(due);
 
        dto.setIs_preinspection(true);
        dto.setIs_preinspection_submitted(false);
        dto.setCaseCreationType("AUTO");
        dto.setGroupId(11L);
        dto.setLeadId(22L);
 
        // Risk fields
        dto.setRiskScore(85);
        dto.setRecommendation("Maintain ASAP");
 
        // Assertions
        assertEquals(1001L, dto.getInspectionID());
        assertEquals("SRC1", dto.getInspector_source());
        assertEquals("COMPLETED", dto.getStatus());
        assertEquals("2024-01-10", dto.getDateOfInspection());
        assertEquals("ABC Corp", dto.getEntityid());
        assertEquals("Entity Name", dto.getName());
        assertEquals("Bangalore", dto.getLocation());
        assertEquals("ADP", dto.getInspection_type());
        assertEquals("test@email.com", dto.getRepresentative_email());
        assertEquals("CASE001", dto.getReference_case());
        assertEquals("HIGH", dto.getEfforts());
        assertEquals("Verification", dto.getReason());
        assertEquals("Inspector A", dto.getAssigned_inspector());
        assertEquals("LARGE", dto.getSize());
        assertEquals("John Doe", dto.getRepresentative_name());
        assertEquals("9876543210", dto.getRepresentative_phoneno());
        assertEquals("SubSeg01", dto.getSubSegment());
        assertEquals("Seg01", dto.getSegment());
        assertEquals(due, dto.getDue_date());
        assertTrue(dto.isIs_preinspection());
        assertFalse(dto.isIs_preinspection_submitted());
        assertEquals("AUTO", dto.getCaseCreationType());
        assertEquals(11L, dto.getGroupId());
        assertEquals(22L, dto.getLeadId());
 
        // Risk checks
        assertEquals(85, dto.getRiskScore());
        assertEquals("Maintain ASAP", dto.getRecommendation());
    }
 
    @Test
    void testDefaultValues() {
        InspectionCase_EntityDTO dto = new InspectionCase_EntityDTO();
 
        assertNull(dto.getDateOfInspection());
        assertNull(dto.getEntityid());
        assertNull(dto.getRiskScore());
        assertNull(dto.getRecommendation());
    }
}