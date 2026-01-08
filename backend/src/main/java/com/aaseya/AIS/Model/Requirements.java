package com.aaseya.AIS.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "requirements")
public class Requirements {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "requirementId")
	private Long requirementId;

	@Column(name = "name", nullable = false)
	private String name;

	public Long getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(Long requirementId) {
		this.requirementId = requirementId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}