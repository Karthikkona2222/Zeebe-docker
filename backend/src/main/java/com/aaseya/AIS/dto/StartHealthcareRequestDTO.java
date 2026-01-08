package com.aaseya.AIS.dto;

import java.util.List;

public class StartHealthcareRequestDTO {

    private String policyId;
    private String claimType;
    private String policyType;
    
    // This will be populated in the controller, not from the JSON
    private List<CSRDocumentDTO> documents;

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
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

	public List<CSRDocumentDTO> getDocuments() {
		return documents;
	}

	public void setDocuments(List<CSRDocumentDTO> documents) {
		this.documents = documents;
	}
}