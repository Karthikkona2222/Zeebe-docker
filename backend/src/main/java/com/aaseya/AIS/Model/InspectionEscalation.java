package com.aaseya.AIS.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "InspectionEscalation")
public class InspectionEscalation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate escalationId
	@Column(name = "escalationId")
	private Long escalationId;

	@Column(name = "inspectionId")
	private Long inspectionId;

	@Column(name = "escalationMessage")
	private String escalationMessage;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_inspection_escalation", // Join table
        joinColumns = @JoinColumn(name = "escalation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
	private List<Users> users;

	// Getters and Setters
	public Long getEscalationId() {
		return escalationId;
	}

	public void setEscalationId(Long escalationId) {
		this.escalationId = escalationId;
	}

	public Long getInspectionId() {
		return inspectionId;
	}

	public void setInspectionId(Long inspectionId) {
		this.inspectionId = inspectionId;
	}

	public String getEscalationMessage() {
		return escalationMessage;
	}

	public void setEscalationMessage(String escalationMessage) {
		this.escalationMessage = escalationMessage;
	}

	public List<Users> getUsers() {
		return users;
	}

	public void setUsers(List<Users> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "InspectionEscalation [escalationId=" + escalationId + ", inspectionId=" + inspectionId
				+ ", escalationMessage=" + escalationMessage + "]";
	}

}
