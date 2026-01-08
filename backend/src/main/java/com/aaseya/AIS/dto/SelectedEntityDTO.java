package com.aaseya.AIS.dto;

public class SelectedEntityDTO {
	private String entityId;
	private String entityType;
	private long controlTypeId;
	private long inspectionID;
	private String inspectionTypeName;
	private Long templateId;
	private Long groupId;
	private Long leadId;
	private String inspectorId;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public long getControlTypeId() {
		return controlTypeId;
	}

	public void setControlTypeId(long controlTypeId) {
		this.controlTypeId = controlTypeId;
	}

	public long getInspectionID() {
		return inspectionID;
	}

	public void setInspectionID(long inspectionID) {
		this.inspectionID = inspectionID;
	}

	public String getInspectionTypeName() {
		return inspectionTypeName;
	}

	public void setInspectionTypeName(String inspectionTypeName) {
		this.inspectionTypeName = inspectionTypeName;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getLeadId() {
		return leadId;
	}

	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}

	public String getInspectorId() {
		return inspectorId;
	}

	public void setInspectorId(String inspectorId) {
		this.inspectorId = inspectorId;
	}

}