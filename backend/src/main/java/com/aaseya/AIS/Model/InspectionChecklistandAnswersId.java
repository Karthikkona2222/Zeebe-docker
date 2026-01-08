package com.aaseya.AIS.Model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class InspectionChecklistandAnswersId implements Serializable {

    @Column(name = "inspectionID")
    private long inspectionID;

    @Column(name = "categoryID")
    private long categoryID;

    @Column(name = "checklistID")
    private long checklistID;

    // Default constructor
    public InspectionChecklistandAnswersId() {}

    // Parameterized constructor
    public InspectionChecklistandAnswersId(long inspectionID, long categoryID, long checklistID) {
        this.inspectionID = inspectionID;
        this.categoryID = categoryID;
        this.checklistID = checklistID;
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

    public long getChecklistID() {
        return checklistID;
    }

    public void setChecklistID(long checklistID) {
        this.checklistID = checklistID;
    }
    

	@Override
	public int hashCode() {
		return Objects.hash(categoryID, checklistID, inspectionID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InspectionChecklistandAnswersId other = (InspectionChecklistandAnswersId) obj;
		return categoryID == other.categoryID && checklistID == other.checklistID && inspectionID == other.inspectionID;
	}

	@Override
	public String toString() {
		return "InspectionChecklistandAnswersId [inspectionID=" + inspectionID + ", categoryID=" + categoryID
				+ ", checklistID=" + checklistID + "]";
	}
}
