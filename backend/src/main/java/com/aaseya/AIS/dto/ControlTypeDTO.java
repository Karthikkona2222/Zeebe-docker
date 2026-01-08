package com.aaseya.AIS.dto;

import java.util.List;

public class ControlTypeDTO {
    private String controlTypeName;
    private List<Long> inspectionTypeIds; // List of inspection type IDs to map
    private String action; // New field to specify the action (add/edit)
    private Long controlTypeId; // New field for the ControlType ID (for editing)

    public String getControlTypeName() {
        return controlTypeName;
    }

    public void setControlTypeName(String controlTypeName) {
        this.controlTypeName = controlTypeName;
    }

    public List<Long> getInspectionTypeIds() {
        return inspectionTypeIds;
    }

    public void setInspectionTypeIds(List<Long> inspectionTypeIds) {
        this.inspectionTypeIds = inspectionTypeIds;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getControlTypeId() {
        return controlTypeId;
    }

    public void setControlTypeId(Long controlTypeId) {
        this.controlTypeId = controlTypeId;
    }

    @Override
    public String toString() {
        return "ControlTypeDTO [controlTypeName=" + controlTypeName + ", inspectionTypeIds=" + inspectionTypeIds +
                ", action=" + action + ", controlTypeId=" + controlTypeId + "]";
    }
}