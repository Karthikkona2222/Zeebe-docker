package com.aaseya.AIS.dto;

import java.util.List;

public class GetAllSkillDTO {
	 private Long userID;
	    private String userName;
	    private String role;
	    private String emailId;
	    private List<String> skills;

	    // Constructor
	    

		public GetAllSkillDTO(Long userID, String userName, String role, String emailId, List<String> skills) {
			super();
			this.userID = userID;
			this.userName = userName;
			this.role = role;
			this.emailId = emailId;
			this.skills = skills;
		}

		public Long getUserID() {
			return userID;
		}

		public void setUserID(Long userID) {
			this.userID = userID;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public List<String> getSkills() {
			return skills;
		}

		public void setSkills(List<String> skills) {
			this.skills = skills;
		}

		public String getEmailId() {
			return emailId;
		}

		public void setEmailId(String emailId) {
			this.emailId = emailId;
		}
		
		

	}
