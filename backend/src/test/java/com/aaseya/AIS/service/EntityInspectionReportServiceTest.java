package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.InspectionReportDAO;
import com.aaseya.AIS.dto.EntityInspectionReportDTO;
import com.aaseya.AIS.dto.EntityRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EntityInspectionReportServiceTest {

    @Mock
    private InspectionReportDAO inspectionReportDAO;

    @InjectMocks
    private EntityInspectionReportService entityInspectionReportService;

    @Test
    void testGetInspectionReportByEntityAndDate() {
        // Arrange
        EntityRequestDTO requestDTO = new EntityRequestDTO();
        requestDTO.setEntityId("1001");
        requestDTO.setStartdate("2024-01-01");
        requestDTO.setEnddate("2024-12-31");
        requestDTO.setProcessType("ADP");

        EntityInspectionReportDTO expectedResponse = new EntityInspectionReportDTO();
        expectedResponse.setCasesByInspectionType(Map.of("ADP", 5L));

        when(inspectionReportDAO.getInspectionReportByEntityAndDate(requestDTO))
                .thenReturn(expectedResponse);

        // Act
        EntityInspectionReportDTO actualResponse =
                entityInspectionReportService.getInspectionReportByEntityAndDate(requestDTO);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(inspectionReportDAO, times(1))
                .getInspectionReportByEntityAndDate(requestDTO);
    }
}
