package com.aaseya.AIS.dto;


import java.util.List;

public class SkillRequestDTO {
	
	    private Long skillId; // For edit action
	    private String skillName;
	    private List<Long> inspectionTypeIds;
	    private List<Long> userIds;
	    private String action; // "add" or "edit"
	    
	  
	 
		public Long getSkillId() {
			return skillId;
		}
		public void setSkillId(Long skillId) {
			this.skillId = skillId;
		}
		public String getSkillName() {
			return skillName;
		}
		public void setSkillName(String skillName) {
			this.skillName = skillName;
		}
		public List<Long> getInspectionTypeIds() {
			return inspectionTypeIds;
		}
		public void setInspectionTypeIds(List<Long> inspectionTypeIds) {
			this.inspectionTypeIds = inspectionTypeIds;
		}
		public List<Long> getUserIds() {
			return userIds;
		}
		public void setUserIds(List<Long> userIds) {
			this.userIds = userIds;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		
	
		}

	    
	

