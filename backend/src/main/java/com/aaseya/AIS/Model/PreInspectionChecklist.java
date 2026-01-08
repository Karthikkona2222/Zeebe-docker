package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pre_inspection_checklist")
public class PreInspectionChecklist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "answer_type")
	private String answerType;

	@Transient
	private String selected_answer;
	
	@Column(name = "Comments")
	private String Comments;
	
	@Column(name = "is_active", nullable = false)
    private boolean isActive;
	@JsonIgnore
	@ManyToMany(mappedBy = "preInspectionChecklists", fetch = FetchType.LAZY)
	private Set<InspectionCase> inspectionCases;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JsonIgnore
//	@JoinTable(name = "pre_inspection_checklist_inspection_type", joinColumns = @JoinColumn(name = "pre_inspection_checklist_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "ins_type_id", referencedColumnName = "ins_type_id"))
//	private Set<Inspection_Type> inspectionTypes;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ins_type_id", nullable = true)
	private Inspection_Type inspectionType;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
	    name = "pre_inspection_checklist_requirements",
	    joinColumns = @JoinColumn(name = "pre_inspection_checklist_id", referencedColumnName = "id"),
	    inverseJoinColumns = @JoinColumn(name = "requirement_id", referencedColumnName = "requirementId")
	)
	private Set<Requirements> requirements;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}

	public String getSelected_answer() {
		return selected_answer;
	}

	public void setSelected_answer(String selected_answer) {
		this.selected_answer = selected_answer;
	}
//
//	public Set<Inspection_Type> getInspectionTypes() {
//		return inspectionTypes;
//	}
//
//	public void setInspectionTypes(Set<Inspection_Type> inspectionTypes) {
//		this.inspectionTypes = inspectionTypes;
//	}
	

	public String getComments() {
		return Comments;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setComments(String comments) {
		Comments = comments;
	}

	public Set<InspectionCase> getInspectionCases() {
		return inspectionCases;
	}

	public void setInspectionCases(Set<InspectionCase> inspectionCases) {
		this.inspectionCases = inspectionCases;
	}

	public Inspection_Type getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(Inspection_Type inspectionType) {
		this.inspectionType = inspectionType;
	}

	public Set<Requirements> getRequirements() {
		return requirements;
	}

	public void setRequirements(Set<Requirements> requirements) {
		this.requirements = requirements;
	}
	
	
	
}
