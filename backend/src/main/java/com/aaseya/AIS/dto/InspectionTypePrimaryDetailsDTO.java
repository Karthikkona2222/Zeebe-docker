package com.aaseya.AIS.dto;

import java.time.LocalTime;
import java.util.List;

public class InspectionTypePrimaryDetailsDTO {

    private String name;
    private long ins_type_id; // ID for edit action
    private String action; // Action type (save or edit)
    private String threshold;
    private long controlTypeId;
    private String controlTypeName;
    private String isActive;
    private LocalTime high;
    private LocalTime medium;
    private LocalTime low;
    private String entitySize;

    public long getControlTypeId() {
		return controlTypeId;
	}

	public void setControlTypeId(long controlTypeId) {
		this.controlTypeId = controlTypeId;
	}

	// List of new skills to be added
    private List<String> newSkills;

    // List of existing skill IDs to be mapped
    private List<String> existingSkills;
    
    private List<SkillDTO> skills;

    public String getControlTypeName() {
		return controlTypeName;
	}

	public void setControlTypeName(String controlTypeName) {
		this.controlTypeName = controlTypeName;
	}

	public String getName() {
        return name;
    }

    public long getIns_type_id() {
        return ins_type_id;
    }

    public String getAction() {
        return action;
    }

    public void setIns_type_id(long ins_type_id) {
        this.ins_type_id = ins_type_id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getThreshold() {
        return threshold;
    }

    public String getIsActive() {
        return isActive;
    }

    public LocalTime getHigh() {
        return high;
    }

    public LocalTime getMedium() {
        return medium;
    }

    public LocalTime getLow() {
        return low;
    }

    public String getEntitySize() {
        return entitySize;
    }

    public List<String> getNewSkills() {
        return newSkills;
    }

    public List<String> getExistingSkills() {
        return existingSkills;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public void setHigh(LocalTime high) {
        this.high = high;
    }

    public void setMedium(LocalTime medium) {
        this.medium = medium;
    }

    public void setLow(LocalTime low) {
        this.low = low;
    }

    public void setEntitySize(String entitySize) {
        this.entitySize = entitySize;
    }

    public void setNewSkills(List<String> newSkills) {
        this.newSkills = newSkills;
    }

    public void setExistingSkills(List<String> existingSkills) {
        this.existingSkills = existingSkills;
    }

	public List<SkillDTO> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillDTO> skills) {
		this.skills = skills;
	}
}