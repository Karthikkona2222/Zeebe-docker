package com.aaseya.AIS.dto;

import java.time.LocalTime;
import java.util.List;

public class InspectionTypeAdminSkillDTO {
	  private String name;
	    private String threshold;
	    private String isActive;
	    private LocalTime high;
	    private LocalTime medium;
	    private LocalTime low;
	    private String entitySize;
	    private long controlTypeId;
	    private String  controlTypeName;

	    public long getControlTypeId() {
			return controlTypeId;
		}

		public void setControlTypeId(long controlTypeId) {
			this.controlTypeId = controlTypeId;
		}

		public String getControlTypeName() {
			return controlTypeName;
		}

		public void setControlTypeName(String controlTypeName) {
			this.controlTypeName = controlTypeName;
		}

		// List of skills with skill names
	    private List<SkillDTO> skills;

		
		
		

		public String getName() {
			return name;
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

		public List<SkillDTO> getSkills() {
			return skills;
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

		public void setSkills(List<SkillDTO> skills) {
			this.skills = skills;
		}

}
