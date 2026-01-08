package com.aaseya.AIS.dto;

import java.util.List;

import com.aaseya.AIS.Model.Inspection_SLA;

public class InspectionTypeRequestDTO {

	private String action; // "save" or "edit"

	// private String name;

	private InspectionTypePrimaryDetailsDTO inspectionTypePrimaryDetails;

	private InspectionTypeSLADTO inspectionTypeSLA;

	private InspectionTypeEntityDTO inspectionTypeEntity;

	private PeriodicityDTO periodicity; // <-- ADD THIS LINE

	private List<Inspection_SLA> inspectionSLA;

	private List<EntityDetailsDTO> entityDetailsDTOs;
	
	private List<PeriodicityDetailsDTO> periodicityDetailsDTO;
	
	
	private List<PeriodicityDTO> periodicityDetails;
 
	
 
    private TemplateDTO templateDTO;
    
    private ChecklistDTO checklistDTO;
 
    private Long selectedTemplateId;
    private String selectedTemplateName;
 
	public InspectionTypePrimaryDetailsDTO getInspectionTypePrimaryDetails() {

		return inspectionTypePrimaryDetails;

	}

	public void setInspectionTypePrimaryDetails(InspectionTypePrimaryDetailsDTO inspectionTypePrimaryDetails) {

		this.inspectionTypePrimaryDetails = inspectionTypePrimaryDetails;

	}

	public InspectionTypeSLADTO getInspectionTypeSLA() {

		return inspectionTypeSLA;

	}

	public void setInspectionTypeSLA(InspectionTypeSLADTO inspectionTypeSLA) {

		this.inspectionTypeSLA = inspectionTypeSLA;

	}

	public InspectionTypeEntityDTO getInspectionTypeEntity() {

		return inspectionTypeEntity;

	}

	public void setInspectionTypeEntity(InspectionTypeEntityDTO inspectionTypeEntity) {

		this.inspectionTypeEntity = inspectionTypeEntity;

	}

	public String getAction() {

		return action;

	}

	public void setAction(String action) {

		this.action = action;

	}

	public List<Inspection_SLA> getInspectionSLA() {

		return inspectionSLA;

	}

	public void setInspectionSLA(List<Inspection_SLA> inspectionSLA) {

		this.inspectionSLA = inspectionSLA;

	}

	public List<EntityDetailsDTO> getEntityDetailsDTOs() {

		return entityDetailsDTOs;

	}

	public void setEntityDetailsDTOs(List<EntityDetailsDTO> entityDetailsDTOs) {

		this.entityDetailsDTOs = entityDetailsDTOs;

	}

	public PeriodicityDTO getPeriodicity() {
		return periodicity;
	}
 
	public void setPeriodicity(PeriodicityDTO periodicities) {
		this.periodicity = periodicities;
	}
 
	
	
 
	public List<PeriodicityDetailsDTO> getPeriodicityDetailsDTO() {
		return periodicityDetailsDTO;
	}
 
	public void setPeriodicityDetailsDTO(List<PeriodicityDetailsDTO> periodicityDetailsDTO) {
		this.periodicityDetailsDTO = periodicityDetailsDTO;
	}
 
	public List<PeriodicityDTO> getPeriodicityDetails() {
		return periodicityDetails;
	}
 
	public void setPeriodicityDetails(List<PeriodicityDTO> periodicityDetails2) {
		this.periodicityDetails = periodicityDetails2;
	}
 
	public TemplateDTO getTemplateDTO() {
		return templateDTO;
	}
 
	public void setTemplateDTO(TemplateDTO templateDTO) {
		this.templateDTO = templateDTO;
	}
 
	public ChecklistDTO getChecklistDTO() {
		return checklistDTO;
	}
 
	public void setChecklistDTO(ChecklistDTO checklistDTO) {
		this.checklistDTO = checklistDTO;
	}

	public Long getSelectedTemplateId() {
		return selectedTemplateId;
	}

	public void setSelectedTemplateId(Long selectedTemplateId) {
		this.selectedTemplateId = selectedTemplateId;
	}

	public String getSelectedTemplateName() {
		return selectedTemplateName;
	}

	public void setSelectedTemplateName(String selectedTemplateName) {
		this.selectedTemplateName = selectedTemplateName;
	}
	
 
 
}
