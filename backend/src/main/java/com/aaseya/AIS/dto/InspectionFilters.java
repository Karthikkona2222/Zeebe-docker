package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public class InspectionFilters {

	private String inspectorId;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)	
	private LocalDate fromDate;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)	
	private LocalDate toDate;
	private List<String> inspectionTypeIds;
	private List<String> inspectionSource;
	
	private String processType;
	
	public LocalDate getFromDate() {
		return fromDate;
	}
	public String getInspectorId() {
		return inspectorId;
	}
	public void setInspectorId(String inspectorId) {
		this.inspectorId = inspectorId;
	}
	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}
	public LocalDate getToDate() {
		return toDate;
	}
	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}
	public List<String> getInspectionTypeIds() {
		return inspectionTypeIds;
	}
	public void setInspectionTypeIds(List<String> inspectionTypeIds) {
		this.inspectionTypeIds = inspectionTypeIds;
	}
	public List<String> getInspectionSource() {
		return inspectionSource;
	}
	public void setInspectionSource(List<String> inspectionSource) {
		this.inspectionSource = inspectionSource;
	}
	
	public String getProcessType() {
        return processType;
    }
    public void setProcessType(String processType) {
        this.processType = processType;
    }
	
	
}
