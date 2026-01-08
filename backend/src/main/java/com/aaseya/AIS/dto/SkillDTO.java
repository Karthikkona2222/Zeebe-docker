package com.aaseya.AIS.dto;

import java.util.List;

public class SkillDTO {
    private long skillId;
    private String skill;
    private boolean isActive;
    private List<Long> ins_type_id;
    private List<String> inspectionTypeNames;

    // Getters and Setters
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

  

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<Long> getIns_type_id() {
		return ins_type_id;
	}

	public void setIns_type_id(List<Long> ins_type_id) {
		this.ins_type_id = ins_type_id;
	}	

	public List<String> getInspectionTypeNames() {
		return inspectionTypeNames;
	}

	public void setInspectionTypeNames(List<String> inspectionTypeNames) {
		this.inspectionTypeNames = inspectionTypeNames;
	}
	
	public SkillDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SkillDTO(long skillId, String skill) {
		super();
		this.skillId = skillId;
		this.skill = skill;
	}
    
    
}
