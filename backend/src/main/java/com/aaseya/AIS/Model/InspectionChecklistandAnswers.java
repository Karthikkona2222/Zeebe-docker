package com.aaseya.AIS.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "InspectionChecklistandAnswers")
public class InspectionChecklistandAnswers {

	@EmbeddedId
	private InspectionChecklistandAnswersId id;

	@Column(name = "selected_answer")
	private String selected_answer;

	
	@Column(name = "comment")
	private String comment;

	@Transient
	private List<String> corrective_actions;
	

	
	
	@Column(name = "selected_corr_action")
	private String selected_corr_action;

	@ManyToOne(fetch = FetchType.EAGER)
	// @MapsId("inspectionID")
	@JoinColumn(name = "inspectionID", nullable = false, insertable = false, updatable = false)
	@JsonIgnore
	private InspectionCase inspectionCase;
	
	

	@Transient
	private List<String> attachment;
	
	 @OneToMany(mappedBy = "inspectionChecklistandAnswers", cascade = CascadeType.ALL, orphanRemoval = true)
	    private final List<InspectionAttachments> attachments = new ArrayList<>();

	// Getters and setters
	public InspectionChecklistandAnswersId getId() {
		return id;
	}

	public void setId(InspectionChecklistandAnswersId id) {
		this.id = id;
	}

	public String getSelected_answer() {
		return selected_answer;
	}

	public void setSelected_answer(String selected_answer) {
		this.selected_answer = selected_answer;
	}


	public List<InspectionAttachments> getAttachments() {
		return attachments;
	}

	 public void addAttachment(InspectionAttachments attachment) {
	        attachments.add(attachment);
	        attachment.setInspectionChecklistandAnswers(this); // Set the back reference
	    }

	    public void removeAttachment(InspectionAttachments attachment) {
	        attachments.remove(attachment);
	        attachment.setInspectionChecklistandAnswers(null); // Clear the back reference
	    }

	    public void setAttachments(List<InspectionAttachments> attachments) {
	        this.attachments.clear(); // Clear existing attachments
	        for (InspectionAttachments attachment : attachments) {
	            addAttachment(attachment); // Use the add method to maintain the relationship
	        }
	    }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	public List<String> getCorrective_actions() {
		return corrective_actions;
	}

	public void setCorrective_actions(List<String> corrective_actions) {
		this.corrective_actions = corrective_actions;
	}

	public String getSelected_corr_action() {
		return selected_corr_action;
	}

	public void setSelected_corr_action(String selected_corr_action) {
		this.selected_corr_action = selected_corr_action;
	}

	public InspectionCase getInspectionCase() {
		return inspectionCase;
	}

	public void setInspectionCase(InspectionCase inspectionCase) {
		this.inspectionCase = inspectionCase;
	}

	public List<String> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<String> attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
	    return "InspectionChecklistandAnswers [id=" + id 
	            + ", selected_answer=" + selected_answer 
	            + ", comment=" + comment 
	            + ", selected_corr_action=" + selected_corr_action 
	            + "]";
	}


	
}
