package com.aaseya.AIS.dto;
 
public class ChecklistCategoriesDTO {
    private Long checklistCatId;
    private String checklistCategoryName;
    private Boolean isActive;
 
    public ChecklistCategoriesDTO(Long checklistCatId, String checklistCategoryName, Boolean isActive) {
        this.checklistCatId = checklistCatId;
        this.checklistCategoryName = checklistCategoryName;
        this.isActive = isActive;
    }
 
	public Long getChecklistCatId() {
		return checklistCatId;
	}
 
	public void setChecklistCatId(Long checklistCatId) {
		this.checklistCatId = checklistCatId;
	}
 
	public String getChecklistCategoryName() {
		return checklistCategoryName;
	}
 
	public void setChecklistCategoryName(String checklistCategoryName) {
		this.checklistCategoryName = checklistCategoryName;
	}
 
	public Boolean getIsActive() {
		return isActive;
	}
 
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
 
    
}
 
