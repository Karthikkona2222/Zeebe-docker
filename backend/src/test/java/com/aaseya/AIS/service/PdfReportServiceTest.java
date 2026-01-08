package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.PdfReport;
import com.aaseya.AIS.dao.PdfReportDAO;
import com.aaseya.AIS.dto.ReportRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfReportServiceTest {

    @InjectMocks
    private PdfReportService pdfReportService;

    @Mock
    private PdfReportDAO pdfReportDAO;

    @Mock
    private EntityManager entityManager;

    @Spy
    private ObjectMapper objectMapper;

    private Map<String, Object> vars;

    @BeforeEach
    void setup() {
        vars = new HashMap<>();
    }

    // =====================================================
    // generatePdfOnly()
    // =====================================================

    @Test
    void generatePdfOnly_success() {
        vars.put("inspectionId", 101L);
        vars.put("riskScore", 5);

        Map<String, Object> result =
                pdfReportService.generatePdfOnly(vars);

        assertNotNull(result);
        assertEquals(101L, result.get("inspectionId"));
        assertNotNull(result.get("pdfReportBase64"));
    }

    // =====================================================
    // savePdfFromBase64()
    // =====================================================

    @Test
    void savePdfFromBase64_insertNewPdf() {
        Long inspectionId = 1L;
        String base64 = Base64.getEncoder().encodeToString("pdf".getBytes());

        InspectionCase inspectionCase = new InspectionCase();

        when(entityManager.find(InspectionCase.class, inspectionId))
                .thenReturn(inspectionCase);

        when(pdfReportDAO.getByInspectionId(inspectionId))
                .thenReturn(null);

        pdfReportService.savePdfFromBase64(inspectionId, base64);

        verify(pdfReportDAO, times(1)).save(any(PdfReport.class));
        verify(entityManager, times(1)).flush();
    }

    @Test
    void savePdfFromBase64_updateExistingPdf() {
        Long inspectionId = 2L;
        String base64 = Base64.getEncoder().encodeToString("pdf".getBytes());

        InspectionCase inspectionCase = new InspectionCase();
        PdfReport existing = new PdfReport();

        when(entityManager.find(InspectionCase.class, inspectionId))
                .thenReturn(inspectionCase);

        when(pdfReportDAO.getByInspectionId(inspectionId))
                .thenReturn(existing);

        pdfReportService.savePdfFromBase64(inspectionId, base64);

        verify(pdfReportDAO, never()).save(any());
        verify(entityManager, times(1)).flush();
    }

    @Test
    void savePdfFromBase64_inspectionCaseNotFound() {
        when(entityManager.find(InspectionCase.class, 99L))
                .thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> pdfReportService.savePdfFromBase64(99L, "abc")
        );

        assertTrue(ex.getMessage().contains("InspectionCase not found"));
    }

    // =====================================================
    // generatePdf()
    // =====================================================

    @Test
    void generatePdf_success() {
        ReportRequestDTO dto = new ReportRequestDTO();
        dto.setOverview("overview");
        dto.setObservations("obs");
        dto.setRiskScore("5");
        dto.setSummary("summary");
        dto.setKeyRisks("risk1");
        dto.setRecommendations("rec");

        byte[] pdf = pdfReportService.generatePdf(dto);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    // =====================================================
    // buildReportDTO() via generatePdfOnly()
    // =====================================================

    @Test
    void buildReportDTO_withOpenAiJsonResponse() {
        Map<String, Object> message = Map.of(
                "content", """
                {
                  "summary": "AI summary",
                  "keyRisks": ["Risk A", "Risk B"]
                }
                """
        );

        Map<String, Object> choice = Map.of("message", message);

        vars.put("inspectionId", 10L);
        vars.put("openAiResponse", Map.of("choices", List.of(choice)));
        vars.put("riskScore", "7");

        Map<String, Object> result =
                pdfReportService.generatePdfOnly(vars);

        assertNotNull(result.get("pdfReportBase64"));
    }

    @Test
    void buildReportDTO_plainTextAiResponse() {
        Map<String, Object> message = Map.of(
                "content", "This is plain AI text"
        );

        Map<String, Object> choice = Map.of("message", message);

        vars.put("inspectionId", 11L);
        vars.put("openAiResponse", Map.of("choices", List.of(choice)));

        Map<String, Object> result =
                pdfReportService.generatePdfOnly(vars);

        assertNotNull(result.get("pdfReportBase64"));
    }

    // =====================================================
    // resolveInspectionId()
    // =====================================================

    @Test
    void resolveInspectionId_fromBusinessKey() {
        vars.put("businessKey", "12345");

        Map<String, Object> result =
                pdfReportService.generatePdfOnly(vars);

        assertEquals(12345L, result.get("inspectionId"));
    }
}
