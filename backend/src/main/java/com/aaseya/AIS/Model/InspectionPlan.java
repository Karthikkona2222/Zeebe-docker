package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "InspectionPlan")
public class InspectionPlan {

	@Id
	@Column(name = "inspectionPlanId")
	@GeneratedValue(generator = InspectionPlanIdGenerator.GENERATOR_NAME)
	@GenericGenerator(name = InspectionPlanIdGenerator.GENERATOR_NAME, strategy = "com.aaseya.AIS.Model.InspectionPlanIdGenerator", parameters = {
			@Parameter(name = InspectionPlanIdGenerator.PREFIX_PARAM, value = "INS_PLN") })
	private String inspectionPlanId;

	@Column(name = "inspectionPlanName")
	private String inspectionPlanName;

	@Column(name = "reasonForInspectionPlan")
	private String reasonForInspectionPlan;

	@Column(name = "description")
	private String description;

	@Column(name = "inspectorType")
	private String inspectorType;
	
	@Column(name = "status")
	private String status;

	@JsonIgnore
	@OneToMany(mappedBy = "inspectionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<InspectionCase> inspectionCases = new HashSet<>();

	// Getters and Setters
	public String getInspectionPlanId() {
		return inspectionPlanId;
	}

	public void setInspectionPlanId(String inspectionPlanId) {
		this.inspectionPlanId = inspectionPlanId;
	}

	public String getInspectionPlanName() {
		return inspectionPlanName;
	}

	public void setInspectionPlanName(String inspectionPlanName) {
		this.inspectionPlanName = inspectionPlanName;
	}

	public String getReasonForInspectionPlan() {
		return reasonForInspectionPlan;
	}

	public void setReasonForInspectionPlan(String reasonForInspectionPlan) {
		this.reasonForInspectionPlan = reasonForInspectionPlan;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInspectorType() {
		return inspectorType;
	}

	public void setInspectorType(String inspectorType) {
		this.inspectorType = inspectorType;
	}

	public Set<InspectionCase> getInspectionCases() {
		return inspectionCases;
	}

	public void setInspectionCases(Set<InspectionCase> inspectionCases) {
		this.inspectionCases = inspectionCases;
	}
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "InspectionPlan [inspectionPlanId=" + inspectionPlanId + ", inspectionPlanName=" + inspectionPlanName
				+ ", reasonForInspectionPlan=" + reasonForInspectionPlan + ", description=" + description
				+ ", inspectorType=" + inspectorType + ", status=" + status + ", inspectionCases=" + inspectionCases
				+ "]";
	}
}