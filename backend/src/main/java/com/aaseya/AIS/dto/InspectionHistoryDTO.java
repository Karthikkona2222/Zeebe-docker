package com.aaseya.AIS.dto;

import java.util.Map;

public class InspectionHistoryDTO {

	private long casesCount;
	private long entitiesCount;
	private long percentageCompleted;
	
	private Map<String, Long> inspectionByEntities;
	private Map<String, Long> inspectionByInspectionType;
	private Map<String, Long> inspectionByStatus;
	private Map<String, Long> inspectionBySource;	
	
	public long getCasesCount() {
		return casesCount;
	}

	public void setCasesCount(long casesCount) {
		this.casesCount = casesCount;
	}

	public long getEntitiesCount() {
		return entitiesCount;
	}

	public void setEntitiesCount(long entitiesCount) {
		this.entitiesCount = entitiesCount;
	}

	public long getPercentageCompleted() {
		return percentageCompleted;
	}

	public void setPercentageCompleted(long percentageCompleted) {
		this.percentageCompleted = percentageCompleted;
	}

	public Map<String, Long> getInspectionByEntities() {
		return inspectionByEntities;
	}

	public void setInspectionByEntities(Map<String, Long> inspectionByEntities) {
		this.inspectionByEntities = inspectionByEntities;
	}

	public Map<String, Long> getInspectionByInspectionType() {
		return inspectionByInspectionType;
	}

	public void setInspectionByInspectionType(Map<String, Long> inspectionByInspectionType) {
		this.inspectionByInspectionType = inspectionByInspectionType;
	}

	public Map<String, Long> getInspectionByStatus() {
		return inspectionByStatus;
	}

	public void setInspectionByStatus(Map<String, Long> inspectionByStatus) {
		this.inspectionByStatus = inspectionByStatus;
	}

	public Map<String, Long> getInspectionBySource() {
		return inspectionBySource;
	}

	public void setInspectionBySource(Map<String, Long> inspectionBySource) {
		this.inspectionBySource = inspectionBySource;
	}
	
	
}
