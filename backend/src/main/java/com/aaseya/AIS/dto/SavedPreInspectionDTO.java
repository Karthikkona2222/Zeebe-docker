package com.aaseya.AIS.dto;

import java.util.Set;

import com.aaseya.AIS.Model.PreInspectionChecklist;

public class SavedPreInspectionDTO {
	
	private Set<PreInspectionChecklist> preInspectionChecklist;
	private Boolean is_PreInspection;
	private Boolean is_PreInspection_Submitted;
	public Set<PreInspectionChecklist> getPreInspectionChecklist() {
		return preInspectionChecklist;
	}
	public void setPreInspectionChecklist(Set<PreInspectionChecklist> preInspectionChecklist) {
		this.preInspectionChecklist = preInspectionChecklist;
	}
	public Boolean getIs_PreInspection() {
		return is_PreInspection;
	}
	public void setIs_PreInspection(Boolean is_PreInspection) {
		this.is_PreInspection = is_PreInspection;
	}
	public Boolean getIs_PreInspection_Submitted() {
		return is_PreInspection_Submitted;
	}
	public void setIs_PreInspection_Submitted(Boolean is_PreInspection_Submitted) {
		this.is_PreInspection_Submitted = is_PreInspection_Submitted;
	}

}
