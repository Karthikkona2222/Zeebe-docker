package com.aaseya.AIS.dto;
 
import java.util.List;
 
public class ChecklistCategoryDTO {
	
	  private String checklist_category_name;
	  private int category_threshold_local;
	  private int category_weightage_local;
	  private long checklist_cat_id;
	  private List<Long> checklist_ids;
	  private List<ChecklistItemDTO> checklist_items;
	  
	public String getChecklist_category_name() {
		return checklist_category_name;
	}
	public void setChecklist_category_name(String checklist_category_name) {
		this.checklist_category_name = checklist_category_name;
	}
	
	
	public int getCategory_threshold_local() {
		return category_threshold_local;
	}
	public void setCategory_threshold_local(int category_threshold_local) {
		this.category_threshold_local = category_threshold_local;
	}
	public int getCategory_weightage_local() {
		return category_weightage_local;
	}
	public void setCategory_weightage_local(int category_weightage_local) {
		this.category_weightage_local = category_weightage_local;
	}
	public List<Long> getChecklist_ids() {
		return checklist_ids;
	}
	public void setChecklist_ids(List<Long> checklist_ids) {
		this.checklist_ids = checklist_ids;
	}
	
	public long getChecklist_cat_id() {
		return checklist_cat_id;
	}
	public void setChecklist_cat_id(long checklist_cat_id) {
		this.checklist_cat_id = checklist_cat_id;
	}
	public List<ChecklistItemDTO> getChecklist_items() {
		return checklist_items;
	}
	public void setChecklist_items(List<ChecklistItemDTO> checklist_items) {
		this.checklist_items = checklist_items;
	}
	
	
	@Override
	public String toString() {
		return "ChecklistCategoryDTO [checklist_category_name=" + checklist_category_name
				+ ", category_threshold_local=" + category_threshold_local + ", category_weightage_local="
				+ category_weightage_local + ", checklist_cat_id=" + checklist_cat_id + ", checklist_ids="
				+ checklist_ids + ", checklist_items=" + checklist_items + "]";
	}
 
}
 
