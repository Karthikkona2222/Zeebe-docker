package com.aaseya.AIS.zeebe.worker;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
import com.aaseya.AIS.service.SummaryGeneratorService;
 
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
 
@Component
public class SummaryReportWorker {
	private static final Logger logger = LoggerFactory.getLogger(SummaryReportWorker.class);
 
	@Autowired
	private SummaryGeneratorService summaryGeneratorService;
 
	@JobWorker(type = "generate-report")
	public void handleGeneratePdfReport(final JobClient client, final ActivatedJob job) {
		try {
			String aisBusinessKey = (String) job.getVariablesAsMap().get("AISBusinessKey");
			Long inspectionId = Long.parseLong(aisBusinessKey.substring(3));
 
			// Call the service to generate and store the PDF report
			summaryGeneratorService.generateAndStoreReport(inspectionId);
 
			// Complete the job
			client.newCompleteCommand(job.getKey()).send().join();
		} catch (Exception e) {
			logger.error("Error generating PDF report: {}", e.getMessage());
			client.newFailCommand(job.getKey()).retries(0)
					.errorMessage("Error generating PDF report: " + e.getMessage()).send().join();
		}
	}
}
 
 