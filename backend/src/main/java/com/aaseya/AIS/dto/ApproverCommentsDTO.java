package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.List;

import com.aaseya.AIS.Model.Approver_Reviewer_Comment;

public class ApproverCommentsDTO {
	
//////////////////////Save and Submit the inspection for approver///////////////////
	
    private String inspectionId;
    private String inspectionStage;
    private List<Approver_Reviewer_Comment> approverReviewerComments;
    private String overallComment;
    private String action;
    private String recommendedAction;
    private LocalDate dateOfFollowUp;
    private LocalDate dateOfReinspection;

    public String getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(String inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getInspectionStage() {
		return inspectionStage;
	}

	public void setInspectionStage(String inspectionStage) {
		this.inspectionStage = inspectionStage;
	}

	public String getRecommendedAction() {
		return recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}

	public LocalDate getDateOfFollowUp() {
		return dateOfFollowUp;
	}

	public void setDateOfFollowUp(LocalDate dateOfFollowUp) {
		this.dateOfFollowUp = dateOfFollowUp;
	}

	

	public LocalDate getDateOfReinspection() {
		return dateOfReinspection;
	}

	public void setDateOfReinspection(LocalDate dateOfReinspection) {
		this.dateOfReinspection = dateOfReinspection;
	}

	public List<Approver_Reviewer_Comment> getApproverReviewerComments() {
        return approverReviewerComments;
    }

    public void setApproverReviewerComments(List<Approver_Reviewer_Comment> approverReviewerComments) {
        this.approverReviewerComments = approverReviewerComments;
    }

    public String getOverallComment() {
        return overallComment;
    }

    public void setOverallComment(String overallComment) {
        this.overallComment = overallComment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
	public String toString() {
		return "ApproverCommentsDTO [inspectionId=" + inspectionId + ", approverReviewerComments="
				+ approverReviewerComments + ", overallComment=" + overallComment + ", action=" + action
				+ ", recommendedAction=" + recommendedAction + ", dateOfFollowUp=" + dateOfFollowUp
				+ ", dateOfReinspection=" + dateOfReinspection + "]";
	}

}