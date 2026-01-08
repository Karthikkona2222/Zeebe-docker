package com.aaseya.AIS.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@Table(name = "correctiveaction")
public class CorrectiveAction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "corrective_action_id")
	private long corrective_action_id;

	@Column(name = "corrective_action_name")
	private String corrective_action_name;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Checklist_Item.class)
	@JoinTable(name = "CorrectiveAction_ChecklistItem", joinColumns = @JoinColumn(name = "corrective_action_id", referencedColumnName = "corrective_action_id"), inverseJoinColumns = @JoinColumn(name = "checklist_id", referencedColumnName = "checklist_id"))
	@JsonIgnore
	private Set<Checklist_Item> checklist_items = new HashSet<>(); // Initialize to avoid null

	// Getters and Setters
	public long getCorrective_action_id() {
		return corrective_action_id;
	}

	public void setCorrective_action_id(long corrective_action_id) {
		this.corrective_action_id = corrective_action_id;
	}

	public String getCorrective_action_name() {
		return corrective_action_name;
	}

	public void setCorrective_action_name(String corrective_action_name) {
		this.corrective_action_name = corrective_action_name;
	}

	public Set<Checklist_Item> getChecklist_items() {
		return checklist_items;
	}

	public void setChecklist_items(Set<Checklist_Item> checklist_items) {
		this.checklist_items = checklist_items;
	}

	
}
