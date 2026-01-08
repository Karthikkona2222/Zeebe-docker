package com.aaseya.AIS.dto;
 
import java.util.List;
import java.util.Set;

import com.aaseya.AIS.Model.PreInspectionChecklist;
import com.fasterxml.jackson.annotation.JsonIgnore;
 
public class PreInspectionChecklistDTO {
   
    private Set<PreInspectionChecklist> preInspectionChecklists;
    private Set<String> customPreInspectionChecklists;
    private String name;
    private Long checklistId;
    
    private long insTypeId;
    private List<String> requirementNames;
    private List<Long>   existingRequirement;
    private String action;
    private boolean isActive; 
    private String inspectionType;


 
   

	public Set<PreInspectionChecklist> getPreInspectionChecklists() {
		return preInspectionChecklists;
	}

	public void setPreInspectionChecklists(Set<PreInspectionChecklist> preInspectionChecklists) {
		this.preInspectionChecklists = preInspectionChecklists;
	}

	public Set<String> getCustomPreInspectionChecklists() {
		return customPreInspectionChecklists;
	}

	public void setCustomPreInspectionChecklists(Set<String> customPreInspectionChecklists) {
		this.customPreInspectionChecklists = customPreInspectionChecklists;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	
	public Long getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(Long checklistId) {
		this.checklistId = checklistId;
	}

	public long getInsTypeId() {
		return insTypeId;
	}

	public void setInsTypeId(long insTypeId) {
		this.insTypeId = insTypeId;
	}

	public List<String> getRequirementNames() {
		return requirementNames;
	}

	public void setRequirementNames(List<String> requirementNames) {
		this.requirementNames = requirementNames;
	}

	public List<Long> getExistingRequirement() {
		return existingRequirement;
	}

	public void setExistingRequirement(List<Long> existingRequirement) {
		this.existingRequirement = existingRequirement;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}
	
	
}
 
