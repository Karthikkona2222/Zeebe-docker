package com.aaseya.AIS.dto;

import java.util.List;

public class TempCheckInspDTO {
	private String template_name;
	private long ins_type_id;
	private List<Long> checklist_ids;
	private String version;
	private boolean isActive;
	private String action;
	private long template_id;

	public String getTemplate_name() {
		return template_name;
	}

	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}

	public long getIns_type_id() {
		return ins_type_id;
	}

	public void setIns_type_id(long ins_type_id) {
		this.ins_type_id = ins_type_id;
	}

	public List<Long> getChecklist_ids() {
		return checklist_ids;
	}

	public void setChecklist_ids(List<Long> checklist_ids) {
		this.checklist_ids = checklist_ids;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;

	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(long template_id) {
		this.template_id = template_id;
	}

	@Override
	public String toString() {
		return "TempCheckInspDTO [template_name=" + template_name + ", ins_type_id=" + ins_type_id + ", checklist_ids="
				+ checklist_ids + ", version=" + version + ", isActive=" + isActive + ", action=" + action
				+ ", template_id=" + template_id + "]";
	}

}