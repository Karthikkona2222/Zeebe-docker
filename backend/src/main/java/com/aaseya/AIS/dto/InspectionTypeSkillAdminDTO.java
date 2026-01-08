package com.aaseya.AIS.dto;

import java.util.List;

public class InspectionTypeSkillAdminDTO {
    private long insTypeId;
    private String name;
    private Boolean isActive; // Represented as String (true/false)
    private List<String> skills; // List of skill names as strings

    // Getters and setters
    public long getInsTypeId() {
        return insTypeId;
    }

    public void setInsTypeId(long insTypeId) {
        this.insTypeId = insTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   

    public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
