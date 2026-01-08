package com.aaseya.AIS.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Approver_Reviewer_Comment")
public class Approver_Reviewer_Comment {

    @EmbeddedId
    private ApproverReviewerCommentId id;

    @Column(name = "reviewerComment")
    private String reviewerComment;
    
    @Column(name = "approverComment")
    private String approverComment;

    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("inspectionID")
    @JoinColumn(name = "inspectionID", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private InspectionCase inspectionCase;

    // Getters and setters
    public ApproverReviewerCommentId getId() {
        return id;
    }

    public void setId(ApproverReviewerCommentId id) {
        this.id = id;
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

    public InspectionCase getInspectionCase() {
        return inspectionCase;
    }

    public void setInspectionCase(InspectionCase inspectionCase) {
        this.inspectionCase = inspectionCase;
    }

    @Override
    public String toString() {
        return "Approver_Reviewer_Comment [id=" + id + ", reviewerComment=" + reviewerComment 
                + ", approverComment=" + approverComment + "]";
    }
}