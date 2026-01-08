package com.aaseya.AIS.dto;


public class HealthChecklistAnswersDTO {
    private String answer;
    private String attachment;
    private String comment;
    private String correctiveAction;
 
    private Long categoryId;
    private String categoryName;
 
    private Long checklistId;
    private String checklistDescription;
 
    private Long claimId;
 
	public String getAnswer() {
		return answer;
	}
 
	public void setAnswer(String answer) {
		this.answer = answer;
	}
 
	public String getAttachment() {
		return attachment;
	}
 
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
 
	public String getComment() {
		return comment;
	}
 
	public void setComment(String comment) {
		this.comment = comment;
	}
 
	public String getCorrectiveAction() {
		return correctiveAction;
	}
 
	public void setCorrectiveAction(String correctiveAction) {
		this.correctiveAction = correctiveAction;
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
 
	public Long getChecklistId() {
		return checklistId;
	}
 
	public void setChecklistId(Long checklistId) {
		this.checklistId = checklistId;
	}
 
	public String getChecklistDescription() {
		return checklistDescription;
	}
 
	public void setChecklistDescription(String checklistDescription) {
		this.checklistDescription = checklistDescription;
	}
 
	public Long getClaimId() {
		return claimId;
	}
 
	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}
 
  
}
