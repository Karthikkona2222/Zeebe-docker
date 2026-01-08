package com.aaseya.AIS.Model;

import jakarta.persistence.*;
 
@Entity
@Table(name = "pdf_report")
public class PdfReport {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pdf_report_id")
    private Long id;
 
    @OneToOne
    @JoinColumn(
        name = "inspection_id",
        referencedColumnName = "inspectionID",
        nullable = false
    )
    private InspectionCase inspectionCase;
 
    // ✅ RAW BYTE STORAGE (BYTEA)
    @Column(name = "pdf_data", nullable = false)
    private byte[] pdfData;
 
    public Long getId() {
        return id;
    }
 
    public InspectionCase getInspectionCase() {
        return inspectionCase;
    }
 
    public void setInspectionCase(InspectionCase inspectionCase) {
        this.inspectionCase = inspectionCase;
    }
 
    public byte[] getPdfData() {
        return pdfData;
    }
 
    public void setPdfData(byte[] pdfData) {
        this.pdfData = pdfData;
    }
}
 
 