package com.aaseya.AIS.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionReport;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionReportDAO;

import jakarta.transaction.Transactional;

@Service
public class SummaryGeneratorService {

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;

	@Autowired
	private InspectionReportDAO inspectionReportDAO;

	@Transactional
	public void generateAndStoreReport(Long inspectionId) {
		// Generate PDF Report
		byte[] pdfReport = generatePdfReport("This is an inspection report for inspection ID: " + inspectionId);

		// Create and save InspectionReport entity
		InspectionReport inspectionReport = new InspectionReport();
		inspectionReport.setInspectionId(inspectionId);
		inspectionReport.setReport(pdfReport);
		inspectionReportDAO.saveInspectionReport(inspectionReport);
	}

	// Method to generate summary report as a PDF file
	public String summaryGenerator(Long id) {
		try {
			// Fetching the inspection case from the DAO using the provided ID
			InspectionCase inspectionCase = inspectionCaseDAO.findById(id);

			// Define the file path for the PDF to be generated
			String sourcePath = "C:\\Users\\Kalanithi.Kennady\\Desktop\\PDF";
			String fileName = "inspection_" + inspectionCase.getInspectionID() + ".pdf";
			String filePath = sourcePath + fileName;
			File outputFile = new File(filePath);

			// Initialize the PDF writer, document, and page
			try (PDDocument document = new PDDocument()) {
				PDPage page = new PDPage(); // Create a new PDPage instance
				document.addPage(page); // Add the page to the document

				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
					// Create fonts for regular and bold text
					contentStream.beginText();
					contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
					float pageWidth = page.getMediaBox().getWidth();
					float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Inspection Summary Report") / 1000
							* 18;
					float centerX = (pageWidth - textWidth) / 2;

					// Title text
					contentStream.newLineAtOffset(centerX, 800);
					contentStream.showText("Inspection Summary Report");
					contentStream.endText();

					// Add inspection details
					contentStream.beginText();
					contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
					contentStream.newLineAtOffset(50, 755);
					contentStream.showText("Inspection Details");
					contentStream.endText();

					contentStream.beginText();
					contentStream.setFont(PDType1Font.HELVETICA, 12);
					contentStream.newLineAtOffset(50, 730);
					contentStream.showText("Inspection ID: " + inspectionCase.getInspectionID());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Entity: " + inspectionCase.getEntity().getName());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Inspector Source: " + inspectionCase.getInspector_source());

					contentStream.newLineAtOffset(0, -15);

					contentStream.showText("Date of Inspection: " + inspectionCase.getDateOfInspection());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Reason: " + inspectionCase.getReason());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Inspector Comments: "
							+ (inspectionCase.getInspector_comments() != null ? inspectionCase.getInspector_comments()
									: ""));
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Approver Comments: "
							+ (inspectionCase.getApprover_comments() != null ? inspectionCase.getApprover_comments()
									: ""));
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Reviewer Comments: "
							+ (inspectionCase.getReviewer_comments() != null ? inspectionCase.getReviewer_comments()
									: ""));
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Created By: " + inspectionCase.getCreatedBy());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Inspector ID: " + inspectionCase.getInspectorID());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Reviewer ID: "
							+ (inspectionCase.getReviewerID() != null ? inspectionCase.getReviewerID() : ""));
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Approver ID: " + inspectionCase.getApproverID());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Inspection Type: " + inspectionCase.getInspectionType());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Template ID: " + inspectionCase.getTemplate_id());
					contentStream.newLineAtOffset(0, -15);
					contentStream.showText("Due Date: " + inspectionCase.getDueDate());
					contentStream.endText();

				}

				// Save the PDF document
				document.save(outputFile);
			}

			System.out.println("PDF file generated: " + filePath);
			return filePath; // Return the file path for the generated PDF

		} catch (IOException e) {
			System.out.println("Error while generating PDF: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	// Helper method to generate PDF as byte array
	private byte[] generatePdfReport(String content) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				PDDocument document = new PDDocument()) {

			PDPage page = new PDPage(); // Create a new PDPage instance
			document.addPage(page);

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
				contentStream.newLineAtOffset(100, 700);
				contentStream.showText(content);
				contentStream.endText();
			}

			document.save(byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Error generating PDF report", e);
		}
	}
}
