package com.aaseya.AIS.dto;

public class InspectionTypeIdDTO extends AISResponseDTO {
    private long inspectionTypeId;

    // Getter and Setter for inspectionTypeId
    public long getInspectionTypeId() {
        return inspectionTypeId;
    }

    public void setInspectionTypeId(long inspectionTypeId) {
        this.inspectionTypeId = inspectionTypeId;
    }

    @Override
    public String toString() {
        return "InspectionTypeDTO [inspectionTypeId=" + inspectionTypeId + ", status=" + getStatus() + 
               ", businessKey=" + getBusinessKey() + ", message=" + getMessage() + "]";
    }
}
