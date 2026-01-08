package com.aaseya.AIS.dto;

	
public class AssignRequestDTO {
    private String assignTo; // Inspector ID
    private Long checklistCatId; // Checklist Category ID
	public String getAssignTo() {
		return assignTo;
	}
	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}
	public Long getChecklistCatId() {
		return checklistCatId;
	}
	public void setChecklistCatId(Long checklistCatId) {
		this.checklistCatId = checklistCatId;
	}


}
