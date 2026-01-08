package com.aaseya.AIS.dto;

import java.util.Map;

public class CaseSummaryDTO {

    private long openCases;
    private long pendingCases;
    private long closedCases;
    private Map<String, Long> casesByStatus;
    private Map<String, Long> casesByinspector_source;

    // Default Constructor
    public CaseSummaryDTO() {}

	public long getOpenCases() {
		return openCases;
	}

	public void setOpenCases(long openCases) {
		this.openCases = openCases;
	}

	public long getPendingCases() {
		return pendingCases;
	}

	public void setPendingCases(long pendingCases) {
		this.pendingCases = pendingCases;
	}

	public long getClosedCases() {
		return closedCases;
	}

	public void setClosedCases(long closedCases) {
		this.closedCases = closedCases;
	}

	public Map<String, Long> getCasesByStatus() {
		return casesByStatus;
	}

	public void setCasesByStatus(Map<String, Long> casesByStatus) {
		this.casesByStatus = casesByStatus;
	}

	public Map<String, Long> getCasesByinspector_source() {
		return casesByinspector_source;
	}

	public void setCasesByinspector_source(Map<String, Long> casesByinspector_source) {
		this.casesByinspector_source = casesByinspector_source;
	}

	public CaseSummaryDTO(long openCases, long pendingCases, long closedCases, Map<String, Long> casesByStatus,
			Map<String, Long> casesByinspector_source) {
		super();
		this.openCases = openCases;
		this.pendingCases = pendingCases;
		this.closedCases = closedCases;
		this.casesByStatus = casesByStatus;
		this.casesByinspector_source = casesByinspector_source;
	}
	

}