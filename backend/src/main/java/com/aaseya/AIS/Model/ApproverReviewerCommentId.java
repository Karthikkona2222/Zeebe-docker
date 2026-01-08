package com.aaseya.AIS.Model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ApproverReviewerCommentId implements Serializable {
    
    @Column(name = "inspectionID")
    private long inspectionID;
    
    @Column(name = "categoryID")
    private long categoryID;

    // Default constructor
    public ApproverReviewerCommentId() {}

    // Parameterized constructor
    public ApproverReviewerCommentId(long inspectionID, long categoryID) {
        this.inspectionID = inspectionID;
        this.categoryID = categoryID;
    }

    // Getters and setters
    public long getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(long inspectionID) {
        this.inspectionID = inspectionID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }

    // Override equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApproverReviewerCommentId that = (ApproverReviewerCommentId) o;

        if (inspectionID != that.inspectionID) return false;
        return categoryID == that.categoryID;
    }

    @Override
    public int hashCode() {
        int result = (int) (inspectionID ^ (inspectionID >>> 32));
        result = 31 * result + (int) (categoryID ^ (categoryID >>> 32));
        return result;
    }
}