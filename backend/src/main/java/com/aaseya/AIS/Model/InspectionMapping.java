package com.aaseya.AIS.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "inspection_mapping")
public class InspectionMapping {

    @EmbeddedId
    private InspectionMappingId id;

    @Column(name = "inspector_id", nullable = false)
    private String inspectorID;

    // Default constructor
    public InspectionMapping() {}

    // Parameterized constructor
    public InspectionMapping(InspectionMappingId id, String inspectorID) {
        this.id = id;
        this.inspectorID = inspectorID;
    }

    // Getters and Setters
    public InspectionMappingId getId() {
        return id;
    }

    public void setId(InspectionMappingId id) {
        this.id = id;
    }

    public String getInspectorID() {
        return inspectorID;
    }

    public void setInspectorID(String inspectorID) {
        this.inspectorID = inspectorID;
    }

    @Override
    public String toString() {
        return "InspectionMapping [id=" + id + ", inspectorID=" + inspectorID + "]";
    }
}
