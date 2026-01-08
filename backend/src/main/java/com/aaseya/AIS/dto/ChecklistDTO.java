package com.aaseya.AIS.dto;

import java.util.List;

import com.aaseya.AIS.Model.Checklist_Item;

public class ChecklistDTO {

	private long categoryId;
	private String categoryName;
	private List<Checklist_Item> checklistItem;
	private String reviewerComment;
	private String approverComment;
	private String inspectorID;
	private List<String> inspectionids;
	
	 public ChecklistDTO() {}

	    public ChecklistDTO(long categoryId, String categoryName, String inspectorID, List<Checklist_Item> checklistItems) {
	        this.categoryId = categoryId;
	        this.categoryName = categoryName;
	        this.inspectorID = inspectorID;
	        //this.inspectionID = inspectionID;
	        this.checklistItem = checklistItems;
	    }
	
 public List<String> getInspectionids() {
		return inspectionids;
	}

	public void setInspectionids(List<String> inspectionids) {
		this.inspectionids = inspectionids;
	}
	

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<Checklist_Item> getChecklistItem() {
		return checklistItem;
	}

	public void setChecklistItem(List<Checklist_Item> checklistItem) {
		this.checklistItem = checklistItem;
	}

	public String getReviewerComment() {
		return reviewerComment;
	}

	public void setReviewerComment(String reviewerComment) {
		this.reviewerComment = reviewerComment;
	}

	public String getApproverComment() {
		return approverComment;
	}

	public void setApproverComment(String approverComment) {
		this.approverComment = approverComment;
	}
	
	public String getInspectorID() {
        return inspectorID;
    }

    public void setInspectorID(String inspectorID) {
        this.inspectorID = inspectorID;
    }

	@Override
	public String toString() {
		return "ChecklistDTO [categoryId=" + categoryId + ", categoryName=" + categoryName + ", checklistItem="
				+ checklistItem + ", inspectorID=" + inspectorID + "]";
	}

}
