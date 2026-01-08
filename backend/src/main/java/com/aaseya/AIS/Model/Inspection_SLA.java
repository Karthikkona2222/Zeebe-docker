package com.aaseya.AIS.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Inspection_SLA")
public class Inspection_SLA {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private long Id;

	@Column
	private int inspectorGoal;

	@Column
	private int inspectorDeadline;

	@Column
	private int reviewerGoal;

	@Column
	private int reviewerDeadline;

	@Column
	private int approverGoal;

	@Column
	private int approverDeadline;

	@Column
	private int caseGoal;

	@Column
	private int caseDeadline;
	
	 
    @Column(nullable = false)
    private String entitySize; 

    @JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ins_type_id", nullable = false)
	private Inspection_Type inspectionType;

	public Inspection_SLA() {
	}

	// Getters and setters
	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public int getInspectorGoal() {
		return inspectorGoal;
	}

	public void setInspectorGoal(int inspectorGoal) {
		this.inspectorGoal = inspectorGoal;
	}

	public int getInspectorDeadline() {
		return inspectorDeadline;
	}

	public void setInspectorDeadline(int inspectorDeadline) {
		this.inspectorDeadline = inspectorDeadline;
	}

	public int getReviewerGoal() {
		return reviewerGoal;
	}

	public String getEntitySize() {
		return entitySize;
	}

	public void setEntitySize(String entitySize) {
		this.entitySize = entitySize;
	}

	public void setReviewerGoal(int reviewerGoal) {
		this.reviewerGoal = reviewerGoal;
	}

	public int getReviewerDeadline() {
		return reviewerDeadline;
	}

	public void setReviewerDeadline(int reviewerDeadline) {
		this.reviewerDeadline = reviewerDeadline;
	}

	public int getApproverGoal() {
		return approverGoal;
	}

	public void setApproverGoal(int approverGoal) {
		this.approverGoal = approverGoal;
	}

	public int getApproverDeadline() {
		return approverDeadline;
	}

	public void setApproverDeadline(int approverDeadline) {
		this.approverDeadline = approverDeadline;
	}

	public int getCaseGoal() {
		return caseGoal;
	}

	public void setCaseGoal(int caseGoal) {
		this.caseGoal = caseGoal;
	}

	public int getCaseDeadline() {
		return caseDeadline;
	}

	public void setCaseDeadline(int caseDeadline) {
		this.caseDeadline = caseDeadline;
	}

	public Inspection_Type getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(Inspection_Type inspectionType) {
		this.inspectionType = inspectionType;
	}

	@Override
	public String toString() {
		return "Inspection_SLA [Id=" + Id + ", inspectorGoal=" + inspectorGoal + ", inspectorDeadline="
				+ inspectorDeadline + ", reviewerGoal=" + reviewerGoal + ", reviewerDeadline=" + reviewerDeadline
				+ ", approverGoal=" + approverGoal + ", approverDeadline=" + approverDeadline + ", caseGoal=" + caseGoal
				+ ", caseDeadline=" + caseDeadline + "]";
	}
}