package com.aaseya.AIS.dto;

import java.util.List;

public class InspectionTypeEntityDTO {

    private String inspectionTypeId;
    private List<String> entityIds; // List of entity IDs to map

    public String getInspectionTypeId() {
        return inspectionTypeId;
    }

    public void setInspectionTypeId(String inspectionTypeId) {
        this.inspectionTypeId = inspectionTypeId;
    }

    public List<String> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<String> entityIds) {
        this.entityIds = entityIds;
    }
}
