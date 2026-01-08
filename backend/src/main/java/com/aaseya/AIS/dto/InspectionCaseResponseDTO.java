package com.aaseya.AIS.dto;

import java.time.LocalDate;

public class InspectionCaseResponseDTO {

    private Long inspectionID;
    private String inspector_source;
    private String status;
    private LocalDate dateOfInspection;
    private String entityid;
    private String name;
    private String inspection_type;
    private Long inspection_type_Id;
    private String createdBy;
    private LocalDate createdDate;

    private String representative_email;
    private String reference_case;
    private String efforts;
    private String reason;
    private String size;

    private String representative_name;
    private String representative_phoneno;
    private String subSegment;
    private String segment;
    private LocalDate due_date;

    private Boolean is_preinspection;
    private Boolean is_preinspection_submitted;

    // Schedule details
    private ScheduleDetailsDTO scheduleDetails;

    // Getters and Setters
    public Long getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(Long inspectionID) {
        this.inspectionID = inspectionID;
    }

    public String getInspector_source() {
        return inspector_source;
    }

    public void setInspector_source(String inspector_source) {
        this.inspector_source = inspector_source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDateOfInspection() {
        return dateOfInspection;
    }

    public void setDateOfInspection(LocalDate dateOfInspection) {
        this.dateOfInspection = dateOfInspection;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public Long getInspection_type_Id() {
        return inspection_type_Id;
    }

    public void setInspection_type_Id(Long inspection_type_Id) {
        this.inspection_type_Id = inspection_type_Id;
    }

    public String getRepresentative_email() {
        return representative_email;
    }

    public void setRepresentative_email(String representative_email) {
        this.representative_email = representative_email;
    }

    public String getReference_case() {
        return reference_case;
    }

    public void setReference_case(String reference_case) {
        this.reference_case = reference_case;
    }

    public String getEfforts() {
        return efforts;
    }

    public void setEfforts(String efforts) {
        this.efforts = efforts;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getRepresentative_name() {
        return representative_name;
    }

    public void setRepresentative_name(String representative_name) {
        this.representative_name = representative_name;
    }

    public String getRepresentative_phoneno() {
        return representative_phoneno;
    }

    public void setRepresentative_phoneno(String representative_phoneno) {
        this.representative_phoneno = representative_phoneno;
    }

    public String getSubSegment() {
        return subSegment;
    }

    public void setSubSegment(String subSegment) {
        this.subSegment = subSegment;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public LocalDate getDue_date() {
        return due_date;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }

    public Boolean getIs_preinspection() {
        return is_preinspection;
    }

    public void setIs_preinspection(Boolean is_preinspection) {
        this.is_preinspection = is_preinspection;
    }

    public Boolean getIs_preinspection_submitted() {
        return is_preinspection_submitted;
    }

    public void setIs_preinspection_submitted(Boolean is_preinspection_submitted) {
        this.is_preinspection_submitted = is_preinspection_submitted;
    }

    public ScheduleDetailsDTO getScheduleDetails() {
        return scheduleDetails;
    }

    public void setScheduleDetails(ScheduleDetailsDTO scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
	
	
    
    
}
