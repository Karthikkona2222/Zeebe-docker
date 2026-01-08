package com.aaseya.AIS.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "health_care_checklist_item")
public class HealthCareChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklistid")
    private Long checklistId;

    @Column(name = "checklist_description")
    private String checklistDescription;
    
    @Column(name = "answer_type")
    private String answertype;

    // Many checklist items belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "category_id", nullable = false)
    private HealthCareChecklistCategory category;

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

	public HealthCareChecklistCategory getCategory() {
		return category;
	}

	public void setCategory(HealthCareChecklistCategory category) {
		this.category = category;
	}

	public String getAnswertype() {
		return answertype;
	}

	public void setAnswertype(String answertype) {
		this.answertype = answertype;
	}

	@Override
	public String toString() {
		return "HealthCareChecklistItem [checklistId=" + checklistId + ", checklistDescription=" + checklistDescription
				+ ", answertype=" + answertype + ", category=" + category + "]";
	}

    
}
