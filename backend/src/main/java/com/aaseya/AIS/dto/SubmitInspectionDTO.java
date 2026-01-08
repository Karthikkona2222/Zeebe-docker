package com.aaseya.AIS.dto;

import java.util.List;

public class SubmitInspectionDTO {

	private String inspectionId;
	private List<ChecklistDTO> checklist;
	private String inspectorComment;
	private String reviewerComment;
	private String approverComment;
	private Long leadId;
	private Long groupId;

	public String getInspectionId() {
		return inspectionId;
	}

	public void setInspectionId(String inspectionId) {
		this.inspectionId = inspectionId;
	}

	public List<ChecklistDTO> getChecklist() {
		return checklist;
	}

	public void setChecklist(List<ChecklistDTO> checklist) {
		this.checklist = checklist;
	}

	public String getInspectorComment() {
		return inspectorComment;
	}

	public void setInspectorComment(String inspectorComment) {
		this.inspectorComment = inspectorComment;
	}

	public String getReviewerComment() {
		return reviewerComment;
	}

	public void setReviewerComment(String reviewerComment) {
		this.reviewerComment = reviewerComment;
	}

	public String getApproverComment() {
		return approverComment;
	}

	public void setApproverComment(String approverComment) {
		this.approverComment = approverComment;
	}

	public Long getLeadId() {
		return leadId;
	}

	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "SubmitInspectionDTO [inspectionId=" + inspectionId + ", checklist=" + checklist + ", inspectorComment="
				+ inspectorComment + ", reviewerComment=" + reviewerComment + ", approverComment=" + approverComment
				+ "]";
	}

}
