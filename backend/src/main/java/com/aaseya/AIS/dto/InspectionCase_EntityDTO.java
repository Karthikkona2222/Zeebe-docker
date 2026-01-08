package com.aaseya.AIS.dto;
 
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
 
import com.aaseya.AIS.Model.Segment;
import com.aaseya.AIS.Model.SubSegment;
 
import jakarta.persistence.Column;
 
public class InspectionCase_EntityDTO {
 
	private long inspectionID;
	private String inspector_source;
    private String status;
    private String dateOfInspection;
    private String entityid;
    private String name;
    private String location;
    private String inspection_type;
    private String representative_email;
    private String reference_case;
    private String efforts;
    private String reason;
    private String assigned_inspector;
    private String size;
    private String representative_name;
    private String representative_phoneno;
    private String subSegment;
    private String segment;
    private LocalDate due_date;
    private boolean is_preinspection;
    private boolean is_preinspection_submitted;
    private String caseCreationType;
    private Long groupId;
    private Long leadId;
    
    private Integer riskScore;      
    private String recommendation; 
    
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getCaseCreationType() {
		return caseCreationType;
	}
	public void setCaseCreationType(String caseCreationType) {
		this.caseCreationType = caseCreationType;
	}
	public LocalDate getDue_date() {
		return due_date;
	}
	public void setDue_date(LocalDate localDate) {
		this.due_date = localDate;
	}
	public String getInspection_type() {
		return inspection_type;
	}
	public void setInspection_type(String inspection_type) {
		this.inspection_type = inspection_type;
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
	public String getAssigned_inspector() {
		return assigned_inspector;
	}
	public void setAssigned_inspector(String assigned_inspector) {
		this.assigned_inspector = assigned_inspector;
	}
	public long getInspectionID() {
		return inspectionID;
	}
	public void setInspectionID(long inspectionID) {
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
	public String getDateOfInspection() {
		return dateOfInspection;
	}
	public void setDateOfInspection(String dateOfInspection) {
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
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
	public boolean isIs_preinspection() {
		return is_preinspection;
	}
	public void setIs_preinspection(boolean is_preinspection) {
		this.is_preinspection = is_preinspection;
	}
	public boolean isIs_preinspection_submitted() {
		return is_preinspection_submitted;
	}
	public void setIs_preinspection_submitted(boolean is_preinspection_submitted) {
		this.is_preinspection_submitted = is_preinspection_submitted;
	}
	public Long getLeadId() {
		return leadId;
	}
	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}
	
	public Integer getRiskScore() {
	    return riskScore;
	}
	public void setRiskScore(Integer riskScore) {
	    this.riskScore = riskScore;
	}
	
	public String getRecommendation() {
	    return recommendation;
	}
	public void setRecommendation(String recommendation) {
	    this.recommendation = recommendation;
	}
	
}

