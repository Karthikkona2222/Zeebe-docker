package com.aaseya.AIS.dto;

import java.util.List;

public class SubmitReviewDTO {

	private String inspectionId;
	private List<ReviewChecklistDTO> reviewChecklist;
	private String overallComment;

	public String getInspectionId() {
		return inspectionId;
	}

	public void setInspectionId(String inspectionId) {
		this.inspectionId = inspectionId;
	}

	public List<ReviewChecklistDTO> getReviewChecklist() {
		return reviewChecklist;
	}

	public void setReviewChecklist(List<ReviewChecklistDTO> reviewChecklist) {
		this.reviewChecklist = reviewChecklist;
	}

	public String getOverallComment() {
		return overallComment;
	}

	public void setOverallComment(String overallComment) {
		this.overallComment = overallComment;
	}

	@Override
	public String toString() {
		return "SubmitReviewDTO [inspectionId=" + inspectionId + ", reviewChecklist=" + reviewChecklist
				+ ", overallComment=" + overallComment + "]";
	}

}
