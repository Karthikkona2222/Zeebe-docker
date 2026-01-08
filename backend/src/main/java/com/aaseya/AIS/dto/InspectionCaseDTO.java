package com.aaseya.AIS.dto;
 
import java.time.LocalDate;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
 
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class InspectionCaseDTO {
    @JsonProperty("Case_ID")
    private long inspectionID;
    @JsonProperty("Assigned_To")
    private String inspectorID;
    @JsonProperty("Inspection_Type")
    private String inspectionType;
    @JsonProperty("Inspection_Date")
    private String dateOfInspection;
    @JsonProperty("Inspected_By")
    private String createdBy;
    private String status;
    @JsonProperty("Approved_By")
    private String approverID;
    private String entityName;
    private String inspector_source;
    private LocalDate start_date;
    private LocalDate end_date;
    @JsonProperty("Entity_Name")
   private String entity_name;
    @JsonProperty("location")
    private String address;
    private LocalDate dueStartDate;
    private LocalDate dueEndDate;
 
    public InspectionCaseDTO(long inspectionID, String inspectorID, String inspectionType, String dateOfInspection,
            String createdBy, String status, String approverID, String inspector_source, LocalDate start_date,
            LocalDate end_date, String entity_name, String building, String floor, String facility,
            String address) {
        this.inspectionID = inspectionID;
        this.inspectorID = inspectorID;
        this.inspectionType = inspectionType;
        this.dateOfInspection = dateOfInspection;
        this.createdBy = createdBy;
        this.status = status;
        this.approverID = approverID;
        this.inspector_source = inspector_source;
        this.start_date = start_date;
        this.end_date = end_date;
        this.entity_name = entity_name;
        this.address = concatenateAddress(building, floor, facility, address);
    }
 
    private String concatenateAddress(String building, String floor, String facility, String address) {
        StringBuilder concatenatedAddress = new StringBuilder();
        if (building != null && !building.isEmpty()) {
            concatenatedAddress.append(building).append(", ");
        }
        if (floor != null && !floor.isEmpty()) {
            concatenatedAddress.append(floor).append(", ");
        }
        if (facility != null && !facility.isEmpty()) {
            concatenatedAddress.append(facility).append(", ");
        }
        if (address != null && !address.isEmpty()) {
            concatenatedAddress.append(address);
        }
        String result = concatenatedAddress.toString().trim();
        return result.isEmpty() ? null : result;
    }
 
	public long getInspectionID() {
		return inspectionID;
	}
 
	public void setInspectionID(long inspectionID) {
		this.inspectionID = inspectionID;
	}
 
	public String getInspectorID() {
		return inspectorID;
	}
 
	public void setInspectorID(String inspectorID) {
		this.inspectorID = inspectorID;
	}
 
	public String getInspectionType() {
		return inspectionType;
	}
 
	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}
 
	public String getDateOfInspection() {
		return dateOfInspection;
	}
 
	public void setDateOfInspection(String dateOfInspection) {
		this.dateOfInspection = dateOfInspection;
	}
 
	public String getCreatedBy() {
		return createdBy;
	}
 
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
 
	public String getStatus() {
		return status;
	}
 
	public void setStatus(String status) {
		this.status = status;
	}
 
	public String getApproverID() {
		return approverID;
	}
 
	public void setApproverID(String approverID) {
		this.approverID = approverID;
	}
 
	public String getEntityName() {
		return entityName;
	}
 
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
 
	public String getInspector_source() {
		return inspector_source;
	}
 
	public void setInspector_source(String inspector_source) {
		this.inspector_source = inspector_source;
	}
 
	public LocalDate getStart_date() {
		return start_date;
	}
 
	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}
 
	public LocalDate getEnd_date() {
		return end_date;
	}
 
	public void setEnd_date(LocalDate end_date) {
		this.end_date = end_date;
	}
 
	public String getEntity_name() {
		return entity_name;
	}
 
	public void setEntity_name(String entity_name) {
		this.entity_name = entity_name;
	}
 
	public String getAddress() {
		return address;
	}
 
	public void setAddress(String address) {
		this.address = address;
	}
 
	public LocalDate getDueStartDate() {
		return dueStartDate;
	}
 
	public void setDueStartDate(LocalDate dueStartDate) {
		this.dueStartDate = dueStartDate;
	}
 
	public LocalDate getDueEndDate() {
		return dueEndDate;
	}
 
	public void setDueEndDate(LocalDate dueEndDate) {
		this.dueEndDate = dueEndDate;
	}
 
    
 
}