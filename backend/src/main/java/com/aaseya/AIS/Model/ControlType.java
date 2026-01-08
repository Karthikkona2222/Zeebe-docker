package com.aaseya.AIS.Model;
 
import jakarta.persistence.*;
import java.util.Set;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
 
@Entity
@Table(name = "ControlType")
public class ControlType {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "controlTypeId")
    private long controlTypeId;
 
    @Column(name = "controlTypeName")
    private String controlTypeName;
    
    @Column(name = "is_active")
    @JsonProperty("is_active") // Ensures the JSON response contains "is_active"
    private boolean isActive;
 
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore // Ignores this field in JSON response
    @JoinTable(
        name = "ControlType_InspectionType", // Join table name
        joinColumns = @JoinColumn(name = "controlTypeId"), // Join column for ControlType
        inverseJoinColumns = @JoinColumn(name = "ins_type_id") // Join column for Inspection_Type
    )
    private Set<Inspection_Type> inspectionTypes;
 
    public long getControlTypeId() {
        return controlTypeId;
    }
 
    public void setControlTypeId(long controlTypeId) {
        this.controlTypeId = controlTypeId;
    }
 
    public String getControlTypeName() {
        return controlTypeName;
    }
 
    public void setControlTypeName(String controlTypeName) {
        this.controlTypeName = controlTypeName;
    }
 
    public Set<Inspection_Type> getInspectionTypes() {
        return inspectionTypes;
    }
 
    public void setInspectionTypes(Set<Inspection_Type> inspectionTypes) {
        this.inspectionTypes = inspectionTypes;
    }
 
    @JsonProperty("is_active") // Explicitly map the getter to "is_active"
    public boolean getIsActive() {
        return isActive;
    }
 
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
 
    @Override
    public String toString() {
        return "ControlType [controlTypeId=" + controlTypeId + ", controlTypeName=" + controlTypeName + ", is_active="
                + isActive + "]";
    }
}