package com.aaseya.AIS.dto;

import java.util.Date;

public class SkillsDTO {
    private long skillId;
    private String skill;
    private boolean isActive;
    private Date startDate;
    private Date expiryDate;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

	public SkillsDTO(long skillId, String skill) {
		super();
		this.skillId = skillId;
		this.skill = skill;
	}

	public SkillsDTO(long skillId, String skill, boolean isActive, Date startDate, Date expiryDate) {
		super();
		this.skillId = skillId;
		this.skill = skill;
		this.isActive = isActive;
		this.startDate = startDate;
		this.expiryDate = expiryDate;
	}
    
    
}
