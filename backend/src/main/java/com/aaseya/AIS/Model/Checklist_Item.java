package com.aaseya.AIS.Model;
 
import java.util.List;
import java.util.Set;
 

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
 
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.Table;
import jakarta.persistence.Transient;
 
@Entity
@Table(name = "Checklist_Item")
public class Checklist_Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "checklist_id")
	private long checklist_id;
	@Column(name = "checklist_name")
	private String checklist_name;
	@Column(name = "severity")
	private String severity;
	@Column(name = "weightage")
	private int weightage;
	@Column(name = "answer_type")
	private String answer_type;
	@Column(name = "isActive")
	private Boolean isActive;
	@Column(name = "pre_defined_answer_type")
	private String pre_defined_answer_type;
 
	// Added for the getSubmittedInspection
	@Transient
	@JsonSerialize
	private String selected_answer;
	@Transient
	@JsonSerialize
	private List<String> selected_corrective_action;
	@Transient
	@JsonSerialize
	private String comment;
	
	@Transient
	@JsonSerialize
	private List<String> attachments;
 
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "checklist_items")
//	@JsonBackReference
	private Set<CorrectiveAction> correctiveactions;
 
	public Set<CorrectiveAction> getCorrectiveactions() {
		return correctiveactions;
	}
 
	public void setCorrectiveactions(Set<CorrectiveAction> correctiveactions) {
		this.correctiveactions = correctiveactions;
	}
 
	public List<Checklist_Category> getChecklist_categorys() {
		return checklist_categorys;
	}
 
	public void setChecklist_categorys(List<Checklist_Category> checklist_categorys) {
		this.checklist_categorys = checklist_categorys;
	}
 
	@ManyToMany(mappedBy = "checklist_items", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Checklist_Category> checklist_categorys;
 
	public long getChecklist_id() {
		return checklist_id;
	}
 
	public void setChecklist_id(long checklist_id) {
		this.checklist_id = checklist_id;
	}
 
	public String getChecklist_name() {
		return checklist_name;
	}
 
	public void setChecklist_name(String checklist_name) {
		this.checklist_name = checklist_name;
	}
 
	public String getSeverity() {
		return severity;
	}
 
	public void setSeverity(String severity) {
		this.severity = severity;
	}
 
	public int getWeightage() {
		return weightage;
	}
 
	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}
 
	public String getAnswer_type() {
		return answer_type;
	}
 
	public void setAnswer_type(String answer_type) {
		this.answer_type = answer_type;
	}
 
	public Boolean isActive() {
		return isActive;
	}
 
	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}
 
	public String getSelected_answer() {
		return selected_answer;
	}
 
	public void setSelected_answer(String selected_answer) {
		this.selected_answer = selected_answer;
	}
 
	public List<String> getSelected_corrective_action() {
		return selected_corrective_action;
	}
 
	public void setSelected_corrective_action(List<String> selected_corrective_action) {
		this.selected_corrective_action = selected_corrective_action;
	}
 
	public String getComment() {
		return comment;
	}
 
	public void setComment(String comment) {
		this.comment = comment;
	}
	
 
	public String getPre_defined_answer_type() {
		return pre_defined_answer_type;
	}

	public void setPre_defined_answer_type(String pre_defined_answer_type) {
		this.pre_defined_answer_type = pre_defined_answer_type;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "Checklist_Item [checklist_id=" + checklist_id + ", checklist_name=" + checklist_name + ", answer_type="
				+ answer_type + ", selected_answer=" + selected_answer + ", selected_corrective_action="
				+ selected_corrective_action + ", comment=" + comment + ", correctiveactions=" + correctiveactions
				+ "]";
	}
 
}