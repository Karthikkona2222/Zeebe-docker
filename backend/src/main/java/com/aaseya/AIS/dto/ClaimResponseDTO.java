package com.aaseya.AIS.dto;

import java.util.List;

public class ClaimResponseDTO {
    private ClaimDTO claim;
    private Long categoryId;
    private String categoryName;
    private List<HealthChecklistAnswersDTO> checklistAnswers;
	public ClaimDTO getClaim() {
		return claim;
	}
	public void setClaim(ClaimDTO claim) {
		this.claim = claim;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public List<HealthChecklistAnswersDTO> getChecklistAnswers() {
		return checklistAnswers;
	}
	public void setChecklistAnswers(List<HealthChecklistAnswersDTO> checklistAnswers) {
		this.checklistAnswers = checklistAnswers;
	}
 
   
}