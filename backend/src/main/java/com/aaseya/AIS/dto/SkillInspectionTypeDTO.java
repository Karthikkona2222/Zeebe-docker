package com.aaseya.AIS.dto;

import java.util.List;

//@Author Vijay
public class SkillInspectionTypeDTO {

	private long skillId;
	private String skill;
	private List<String> inspectiontype;
	private boolean isActive;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public long getSkillId() {
		return skillId;
	}

	public void setSkillId(long skillId) {
		this.skillId = skillId;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public List<String> getInspectiontype() {
		return inspectiontype;
	}

	public void setInspectiontype(List<String> inspectiontype) {
		this.inspectiontype = inspectiontype;
	}

}
