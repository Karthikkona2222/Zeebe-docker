package com.aaseya.AIS.dto;

import java.util.List;

import com.aaseya.AIS.Model.Approver_Reviewer_Comment;
import com.aaseya.AIS.Model.InspectionChecklistandAnswers;

public class SaveInspectionDTO {

	private List<InspectionChecklistandAnswers> inspectionChecklistandAnswers;
	private List<Approver_Reviewer_Comment> approver_Reviewer_Comment;
	private String inspectorComment;
	private String reviewerComment;
	private String approverComment;
	 

	public List<InspectionChecklistandAnswers> getInspectionChecklistandAnswers() {
		return inspectionChecklistandAnswers;
	}

	public void setInspectionChecklistandAnswers(List<InspectionChecklistandAnswers> inspectionChecklistandAnswers) {
		this.inspectionChecklistandAnswers = inspectionChecklistandAnswers;
	}

	public List<Approver_Reviewer_Comment> getApprover_Reviewer_Comment() {
		return approver_Reviewer_Comment;
	}

	public void setApprover_Reviewer_Comment(List<Approver_Reviewer_Comment> approver_Reviewer_Comment) {
		this.approver_Reviewer_Comment = approver_Reviewer_Comment;
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

	@Override
	public String toString() {
		return "SaveInspectionDTO [inspectionChecklistandAnswers=" + inspectionChecklistandAnswers
				+ ", approver_Reviewer_Comment=" + approver_Reviewer_Comment + ", inspectorComment=" + inspectorComment
				+ ", reviewerComment=" + reviewerComment + ", approverComment=" + approverComment + "]";
	}

	


	

}
