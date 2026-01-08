package com.aaseya.AIS.Model;



import java.io.Serializable;
import jakarta.persistence.*;

@Embeddable
public class InspectionMappingId implements Serializable {

    @Column(name = "inspection_id", nullable = false)
    private Long inspectionID;

    @Column(name = "checklist_cat_id", nullable = false)
    private Long checklistCatID;

    // Default constructor
    public InspectionMappingId() {}

    // Parameterized constructor
    public InspectionMappingId(Long inspectionID, Long checklistCatID) {
        this.inspectionID = inspectionID;
        this.checklistCatID = checklistCatID;
    }

    // Getters and Setters
    public Long getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(Long inspectionID) {
        this.inspectionID = inspectionID;
    }

    public Long getChecklistCatID() {
        return checklistCatID;
    }

    public void setChecklistCatID(Long checklistCatID) {
        this.checklistCatID = checklistCatID;
    }

    // Override equals & hashCode for proper composite key handling
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InspectionMappingId that = (InspectionMappingId) obj;
        return inspectionID.equals(that.inspectionID) && checklistCatID.equals(that.checklistCatID);
    }

    @Override
    public int hashCode() {
        return inspectionID.hashCode() + checklistCatID.hashCode();
    }
}
