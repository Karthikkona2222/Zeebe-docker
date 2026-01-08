package com.aaseya.AIS.dto;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.PreInspectionChecklist;
@Component
public class StartAISRequestDTO {
	private String inspectionType;
	private String dateOfInspection;
	private String reason;
	private String createdBy;
	private Long templateId;
	private String entityId;
	private String status;
	private String inspectionSource;
	private boolean addEntity;
	private NewEntity newEntity;
	private List<PreInspectionChecklist> pre_Inspection_Checklists;
	private List<String> custom_pre_inspection_checklist;
	private LocalDate createdDate;
	private boolean is_preinspection;
	private String businessKey;
    private String dateOfReInspection;
    private String SAPNotificationID;
	private String Source;
	 private Long controlTypeId;
	 private String ProcessType;


	
	public Long getControlTypeId() {
		return controlTypeId;
	}
	public void setControlTypeId(Long controlTypeId) {
		this.controlTypeId = controlTypeId;
	}
	public String getInspectionType() {
		return inspectionType;
	}
	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}
	public String getDateOfInspection() {
		return dateOfInspection;
	}
	public void setDateOfInspection(String dateOfInspection) {
		this.dateOfInspection = dateOfInspection;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInspectionSource() {
		return inspectionSource;
	}
	public void setInspectionSource(String inspectionSource) {
		this.inspectionSource = inspectionSource;
	}
	public boolean isAddEntity() {
		return addEntity;
	}
	public void setAddEntity(boolean addEntity) {
		this.addEntity = addEntity;
	}
	public NewEntity getNewEntity() {
		return newEntity;
	}
	public void setNewEntity(NewEntity newEntity) {
		this.newEntity = newEntity;
	}
	public List<PreInspectionChecklist> getPre_Inspection_Checklists() {
		return pre_Inspection_Checklists;
	}
	public void setPre_Inspection_Checklists(List<PreInspectionChecklist> pre_Inspection_Checklists) {
		this.pre_Inspection_Checklists = pre_Inspection_Checklists;
	}
	public List<String> getCustom_pre_inspection_checklist() {
		return custom_pre_inspection_checklist;
	}
	public void setCustom_pre_inspection_checklist(List<String> custom_pre_inspection_checklist) {
		this.custom_pre_inspection_checklist = custom_pre_inspection_checklist;
	}	
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
	
	public boolean isIs_preinspection() {
		return is_preinspection;
	}
	public void setIs_preinspection(boolean is_preinspection) {
		this.is_preinspection = is_preinspection;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getDateOfReInspection() {
		return dateOfReInspection;
	}
	public void setDateOfReInspection(String dateOfReInspection) {
		this.dateOfReInspection = dateOfReInspection;
	}
	
	public String getSAPNotificationID() {
		return SAPNotificationID;
	}
	public void setSAPNotificationID(String sAPNotificationID) {
		SAPNotificationID = sAPNotificationID;
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public final String getProcessType() {
		return ProcessType;
	}
	public final void setProcessType(String processType) {
		ProcessType = processType;
	}
	@Override
	public String toString() {
		return "StartAISRequestDTO [inspectionType=" + inspectionType + ", dateOfInspection=" + dateOfInspection
				+ ", reason=" + reason + ", createdBy=" + createdBy + ", templateId=" + templateId + ", entityId="
				+ entityId + ", status=" + status + ", inspectionSource=" + inspectionSource + ", addEntity="
				+ addEntity + ", newEntity=" + newEntity + ", pre_Inspection_Checklists=" + pre_Inspection_Checklists
				+ ", custom_pre_inspection_checklist=" + custom_pre_inspection_checklist + ", createdDate="
				+ createdDate + ", is_preinspection=" + is_preinspection + ", businessKey=" + businessKey
				+ ", dateOfReInspection=" + dateOfReInspection + ", SAPNotificationID=" + SAPNotificationID
				+ ", Source=" + Source + "]";
	}
	
	
}
