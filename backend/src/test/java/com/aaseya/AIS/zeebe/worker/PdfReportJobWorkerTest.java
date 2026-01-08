package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.service.PdfReportService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfReportJobWorkerTest {

    @Mock
    private PdfReportService pdfReportService;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private PdfReportJobWorker pdfReportJobWorker;

    private Map<String, Object> variables;

    @BeforeEach
    void setUp() {
        variables = new HashMap<>();
    }

    // ---------- generatePdf() ----------

    @Test
    void generatePdf_success() {
        Map<String, Object> response = Map.of("pdfReportBase64", "base64data");

        when(activatedJob.getVariablesAsMap()).thenReturn(variables);
        when(pdfReportService.generatePdfOnly(variables)).thenReturn(response);

        Map<String, Object> result = pdfReportJobWorker.generatePdf(activatedJob);

        assertEquals(response, result);
        verify(pdfReportService, times(1)).generatePdfOnly(variables);
    }

    // ---------- savePdf() ----------

    @Test
    void savePdf_success_withInspectionCaseID() {
        variables.put("inspectionCaseID", 123L);
        variables.put("pdfReportBase64", "base64data");

        when(activatedJob.getVariablesAsMap()).thenReturn(variables);

        assertDoesNotThrow(() -> pdfReportJobWorker.savePdf(activatedJob));

        verify(pdfReportService, times(1))
                .savePdfFromBase64(123L, "base64data");
    }

    @Test
    void savePdf_success_withBusinessKey() {
        variables.put("BusinessKey", "456");
        variables.put("pdfReportBase64", "base64data");

        when(activatedJob.getVariablesAsMap()).thenReturn(variables);

        assertDoesNotThrow(() -> pdfReportJobWorker.savePdf(activatedJob));

        verify(pdfReportService, times(1))
                .savePdfFromBase64(456L, "base64data");
    }

    @Test
    void savePdf_missingInspectionId_throwsException() {
        variables.put("pdfReportBase64", "base64data");
        when(activatedJob.getVariablesAsMap()).thenReturn(variables);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> pdfReportJobWorker.savePdf(activatedJob)
        );

        assertEquals("No inspection id found in variables", ex.getMessage());
        verify(pdfReportService, never()).savePdfFromBase64(anyLong(), anyString());
    }

    @Test
    void savePdf_missingPdfBase64_throwsException() {
        variables.put("inspectionCaseID", 123L);
        when(activatedJob.getVariablesAsMap()).thenReturn(variables);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> pdfReportJobWorker.savePdf(activatedJob)
        );

        assertEquals("pdfReportBase64 not found", ex.getMessage());
        verify(pdfReportService, never()).savePdfFromBase64(anyLong(), anyString());
    }
}
