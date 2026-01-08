package com.aaseya.AIS.dto;

import java.util.List;

public class InspectionEscalationDTO {
    
    private Long escalationId;
    private Long inspectionId;
    private String escalationMessage;
    private List<String> userIds; // Assuming you want to send a list of user IDs associated with this escalation

    public InspectionEscalationDTO() {
		// TODO Auto-generated constructor stub
	}

	public InspectionEscalationDTO(Long inspectionId2, String escalationMessage2, List<Long> userIds2) {
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public Long getEscalationId() {
        return escalationId;
    }

    public void setEscalationId(Long escalationId) {
        this.escalationId = escalationId;
    }

    public Long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Long inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getEscalationMessage() {
        return escalationMessage;
    }

    public void setEscalationMessage(String escalationMessage) {
        this.escalationMessage = escalationMessage;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

	@Override
	public String toString() {
		return "InspectionEscalationDTO [escalationId=" + escalationId + ", inspectionId=" + inspectionId
				+ ", escalationMessage=" + escalationMessage + ", userIds=" + userIds + "]";
	}
    
    
}
