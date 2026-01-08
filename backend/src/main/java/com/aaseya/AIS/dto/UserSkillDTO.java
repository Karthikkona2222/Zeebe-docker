package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.List;

public class UserSkillDTO {
    private Long skillId;
    private String skill;
    private LocalDate startDate;
    private LocalDate expiryDate;
    
   


	public Long getSkillId() {
		return skillId;
	}
	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}

	

    
}
