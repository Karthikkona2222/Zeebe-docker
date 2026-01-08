package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.InspectionReportDAO;
import com.aaseya.AIS.dto.EntityInspectionCasesReportResponseDTO;
import com.aaseya.AIS.dto.EntityRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EntityInspectionReportCasesTest {

    @Mock
    private InspectionReportDAO inspectionReportDAO;

    @InjectMocks
    private EntityInspectionReportService entityInspectionReportService;

    @Test
    void testGetInspectionCases() {
        EntityRequestDTO requestDTO = new EntityRequestDTO();
        requestDTO.setEntityId("1001");
        requestDTO.setProcessType("ADP");

        EntityInspectionCasesReportResponseDTO dto = new EntityInspectionCasesReportResponseDTO();
        dto.setCaseId(1L);
        dto.setInspectionType("ADP");
        dto.setStatus("Completed");
        dto.setInspectionDate("2024-12-01");

        when(inspectionReportDAO.getInspectionCases(requestDTO))
                .thenReturn(List.of(dto));

        List<EntityInspectionCasesReportResponseDTO> result =
                entityInspectionReportService.getInspectionCases(requestDTO);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCaseId());
        assertEquals("ADP", result.get(0).getInspectionType());

        verify(inspectionReportDAO, times(1)).getInspectionCases(requestDTO);
    }
}
