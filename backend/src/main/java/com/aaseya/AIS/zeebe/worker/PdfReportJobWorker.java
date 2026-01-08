package com.aaseya.AIS.zeebe.worker;
import com.aaseya.AIS.service.PdfReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;
 
import java.util.Map;
 
@Component
public class PdfReportJobWorker {

    private final PdfReportService pdfReportService;

    public PdfReportJobWorker(PdfReportService pdfReportService) {
        this.pdfReportService = pdfReportService;
    }

    // 🔹 Worker 1 — GENERATE PDF ONLY
    @JobWorker(type = "PDFReportSK")
    public Map<String, Object> generatePdf(final ActivatedJob job) {
        return pdfReportService.generatePdfOnly(job.getVariablesAsMap());
    }

    // 🔹 Worker 2 — SAVE PDF ONLY
    @JobWorker(type = "PDFReportSAVESK")
    public void savePdf(final ActivatedJob job) {

        Map<String, Object> vars = job.getVariablesAsMap();

        Object inspectionIdObj =
                vars.get("inspectionCaseID") != null ? vars.get("inspectionCaseID") :
                vars.get("BusinessKey") != null ? vars.get("BusinessKey") :
                null;

        if (inspectionIdObj == null) {
            throw new RuntimeException("No inspection id found in variables");
        }

        Long inspectionId = Long.parseLong(inspectionIdObj.toString());

        Object pdfBase64Obj = vars.get("pdfReportBase64");
        if (pdfBase64Obj == null) {
            throw new RuntimeException("pdfReportBase64 not found");
        }

        pdfReportService.savePdfFromBase64(inspectionId, pdfBase64Obj.toString());
    }

}

 
 