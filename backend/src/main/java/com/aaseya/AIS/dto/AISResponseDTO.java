package com.aaseya.AIS.dto;

public class AISResponseDTO {
    private String status;
    private String businessKey;
    private String message;
    private long inspectionTypeId;
    private String SapNotificationID;


    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getInspectionTypeId() {
		return inspectionTypeId;
	}

	public void setInspectionTypeId(long inspectionTypeId) {
		this.inspectionTypeId = inspectionTypeId;
	}

	public String getSapNotificationID() {
		return SapNotificationID;
	}

	public void setSapNotificationID(String sapNotificationID) {
		SapNotificationID = sapNotificationID;
	}

	@Override
	public String toString() {
		return "AISResponseDTO [status=" + status + ", businessKey=" + businessKey + ", message=" + message
				+ ", inspectionTypeId=" + inspectionTypeId + ", SapNotificationID=" + SapNotificationID + "]";
	}

	
}
