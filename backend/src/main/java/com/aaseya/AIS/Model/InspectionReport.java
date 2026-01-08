package com.aaseya.AIS.Model;

import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "InspectionReport")
public class InspectionReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reportid")
    private Long reportId;

    @Column(name = "inspectionid")
    private Long inspectionId;

    @Column(name = "report", columnDefinition = "BYTEA")
    private byte[] report;

    // Getters and setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Long inspectionId) {
        this.inspectionId = inspectionId;
    }

    public byte[] getReport() {
        return report;
    }

    public void setReport(byte[] report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "InspectionReport [reportId=" + reportId + ", inspectionId=" + inspectionId + ", report="
                + Arrays.toString(report) + "]";
    }
}
