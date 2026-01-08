package com.aaseya.AIS.dto;

import java.util.List;

public class InspectionTypeSkillDTO {
    private long ins_type_id;
    private String name;
    private String threshold;
    private String isActive;
    private int high;
    private int medium;
    private int low;
    private String entitySize;
    private List<SkillDTO> skills;
    private List<UsersDTO> users;

    // Getters and Setters
    public long getIns_type_id() {
        return ins_type_id;
    }

    public void setIns_type_id(long ins_type_id) {
        this.ins_type_id = ins_type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public String getEntitySize() {
        return entitySize;
    }

    public void setEntitySize(String entitySize) {
        this.entitySize = entitySize;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

	public List<UsersDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UsersDTO> users) {
		this.users = users;
	}
}
