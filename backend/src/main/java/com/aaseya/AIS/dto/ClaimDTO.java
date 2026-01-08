package com.aaseya.AIS.dto;

public class ClaimDTO {
    private Long claimId;
    private String policyId;
    private String claimantName;
    private String contactNumber;
    private String coverageEffectiveDate;
    private String expirationDate;
 
    private String inspectorReport;
    private String reviewerReport;
    private String approverReport;
 
    private String CoverageStartDate;
    private String CoverageEndDate;
    private String RecommendedAction;
    private String AmountStatus;
 
    // Getters & Setters
    public Long getClaimId() {
        return claimId;
    }
    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }
    public String getPolicyId() {
        return policyId;
    }
    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }
    public String getClaimantName() {
        return claimantName;
    }
    public String getAmountStatus() {
		return AmountStatus;
	}
	public void setAmountStatus(String amountStatus) {
		AmountStatus = amountStatus;
	}
	public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public String getCoverageEffectiveDate() {
        return coverageEffectiveDate;
    }
    public void setCoverageEffectiveDate(String coverageEffectiveDate) {
        this.coverageEffectiveDate = coverageEffectiveDate;
    }
    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getInspectorReport() {
        return inspectorReport;
    }
    public void setInspectorReport(String inspectorReport) {
        this.inspectorReport = inspectorReport;
    }
    public String getRecommendedAction() {
		return RecommendedAction;
	}
	public void setRecommendedAction(String recommendedAction) {
		RecommendedAction = recommendedAction;
	}
	public String getReviewerReport() {
        return reviewerReport;
    }
    public void setReviewerReport(String reviewerReport) {
        this.reviewerReport = reviewerReport;
    }
    public String getApproverReport() {
        return approverReport;
    }
    public void setApproverReport(String approverReport) {
        this.approverReport = approverReport;
    }
	public String getCoverageStartDate() {
		return CoverageStartDate;
	}
	public void setCoverageStartDate(String coverageStartDate) {
		CoverageStartDate = coverageStartDate;
	}
	public String getCoverageEndDate() {
		return CoverageEndDate;
	}
	public void setCoverageEndDate(String coverageEndDate) {
		CoverageEndDate = coverageEndDate;
	}
    
}
