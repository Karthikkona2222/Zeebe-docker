package com.aaseya.AIS.dto;
import java.time.LocalDate;
import java.util.List;
 
public class CreateInspectionPlanRequestDTO {
    private String inspectionPlanName;
    private String reasonForInspectionPlan;
    private String description;
    private String inspectorType;
    private LocalDate dateOfInspection;
    private List<SelectedEntityDTO> selectedEntities;
    private String createdBy;
    private String inspectionPlanId;
    private String action; // "add" or "edit"

 
    // Getters and Setters
    public String getInspectionPlanName() {
        return inspectionPlanName;
    }
 
    public String getCreatedBy() {
		return createdBy;
	}
 
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
 
	public void setInspectionPlanName(String inspectionPlanName) {
        this.inspectionPlanName = inspectionPlanName;
    }
 
    public String getReasonForInspectionPlan() {
        return reasonForInspectionPlan;
    }
 
    public void setReasonForInspectionPlan(String reasonForInspectionPlan) {
        this.reasonForInspectionPlan = reasonForInspectionPlan;
    }
 
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public String getInspectorType() {
        return inspectorType;
    }
 
    public void setInspectorType(String inspectorType) {
        this.inspectorType = inspectorType;
    }
 
    public LocalDate getDateOfInspection() {
        return dateOfInspection;
    }
 
    public void setDateOfInspection(LocalDate dateOfInspection) {
        this.dateOfInspection = dateOfInspection;
    }
 
    public List<SelectedEntityDTO> getSelectedEntities() {
        return selectedEntities;
    }
 
    public void setSelectedEntities(List<SelectedEntityDTO> selectedEntities) {
        this.selectedEntities = selectedEntities;
    }

	

	public String getInspectionPlanId() {
		return inspectionPlanId;
	}

	public void setInspectionPlanId(String inspectionPlanId) {
		this.inspectionPlanId = inspectionPlanId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}