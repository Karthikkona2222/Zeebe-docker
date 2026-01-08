package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClaimCaseResponseDTO {
    private Long claimId;
    private String claimType;
    private String createdBy;
    private LocalDate dueDate;
    private String status;
    private LocalDateTime createdTimestamp;
    private PolicyDetailsDTO policyDetails; // nested object

    // Getters and setters
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public PolicyDetailsDTO getPolicyDetails() { return policyDetails; }
    public void setPolicyDetails(PolicyDetailsDTO policyDetails) { this.policyDetails = policyDetails; }
	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
    
    
}
