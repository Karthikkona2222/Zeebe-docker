package com.aaseya.AIS.dto;

import java.time.LocalDateTime;

public class ClaimCaseDTO {
    private Long claimId;
    private String claimType;
    private String policyType;
    private LocalDateTime createdTimestamp;
    private PolicyDetailsDTO policyDetails;
    private String dischargeSummary; // if needed
    private String claimCaseDetails; // if needed
    private String status;
	public Long getClaimId() {
		return claimId;
	}
	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}
	public String getClaimType() {
		return claimType;
	}
	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}
	public String getPolicyType() {
		return policyType;
	}
	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public PolicyDetailsDTO getPolicyDetails() {
		return policyDetails;
	}
	public void setPolicyDetails(PolicyDetailsDTO policyDetails) {
		this.policyDetails = policyDetails;
	}
	public String getDischargeSummary() {
		return dischargeSummary;
	}
	public void setDischargeSummary(String dischargeSummary) {
		this.dischargeSummary = dischargeSummary;
	}
	public String getClaimCaseDetails() {
		return claimCaseDetails;
	}
	public void setClaimCaseDetails(String claimCaseDetails) {
		this.claimCaseDetails = claimCaseDetails;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
	
    
}
