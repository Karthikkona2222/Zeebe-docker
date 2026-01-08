package com.aaseya.AIS.dto;

import java.util.List;
import java.util.Map;

public class EntityInspectionReportDTO {
    private Map<String, Long > casesByInspectionType;
    private Map<String, Long > casesByStatus;
    private Map<String, Long > casesBySource;
	public Map<String, Long> getCasesByInspectionType() {
		return casesByInspectionType;
	}
	public void setCasesByInspectionType(Map<String, Long> casesByInspectionType) {
		this.casesByInspectionType = casesByInspectionType;
	}
	public Map<String, Long> getCasesByStatus() {
		return casesByStatus;
	}
	public void setCasesByStatus(Map<String, Long> casesByStatus) {
		this.casesByStatus = casesByStatus;
	}
	public Map<String, Long> getCasesBySource() {
		return casesBySource;
	}
	public void setCasesBySource(Map<String, Long> casesBySource) {
		this.casesBySource = casesBySource;
	}


}
