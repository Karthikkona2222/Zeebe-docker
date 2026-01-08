package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "health_care_checklist_category")
public class HealthCareChecklistCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    // One category can have many checklist items
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HealthCareChecklistItem> checklistItems;

    // Getters and setters
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
    public List<HealthCareChecklistItem> getChecklistItems() {
        return checklistItems;
    }
    public void setChecklistItems(List<HealthCareChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }
	@Override
	public String toString() {
		return "HealthCareChecklistCategory [categoryId=" + categoryId + ", categoryName=" + categoryName
				+ ", checklistItems=" + checklistItems + "]";
	}
    
}
