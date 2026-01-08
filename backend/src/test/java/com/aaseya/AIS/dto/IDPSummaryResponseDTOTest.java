package com.aaseya.AIS.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IDPSummaryResponseDTOTest {

    @Test
    void testDTOInitializationAndGetters() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        IDPSummaryResponseDTO dto = new IDPSummaryResponseDTO(
                1001L, "EntityA", "Transformer", "EQ-999",
                90, "High", date, "Electrical",
                "INS100", "All ok", "Replace fuse", "ADP"
        );

        assertEquals(1001L, dto.getinspectionID());
        assertEquals("EntityA", dto.getEntityName());
        assertEquals("Transformer", dto.getEquipmentType());
        assertEquals("EQ-999", dto.getEquipmentId());
        assertEquals(90, dto.getRiskScore());
        assertEquals("High", dto.getRiskLevel());
        assertEquals(date, dto.getLastInspectionDate());
        assertEquals("Electrical", dto.getInspectionType());
        assertEquals("INS100", dto.getInspectorID());
        assertEquals("All ok", dto.getObservations());
       // assertEquals("Replace fuse", dto.getRecomendations());
        assertEquals("ADP", dto.getInspectorSource());
    }
}
