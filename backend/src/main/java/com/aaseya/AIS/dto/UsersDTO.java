package com.aaseya.AIS.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsersDTO {
	private long userID;
	private String userId;
	private String userName;
	private String password;
	private String emailID;
	private String countryName;
	private String phoneNumber;
	private String role;
	private String photo;
	private String registrationID;
	private List<String> zoneIds;
	private List<String> skillNames;
	private List<String> skills;
	private List<UserSkillDTO> skill;
	private String status;
	private List<String> zones;
	private boolean isActive;

	// Getters and Setters

	public String getUserName() {
		return userName;
	}

	public String getRegistrationID() {
		return registrationID;
	}

	public void setRegistrationID(String registrationID) {
		this.registrationID = registrationID;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(List<String> skillNames) {
		this.skillNames = skillNames;
	}

	public List<String> getZoneIds() {
		return zoneIds;
	}

	public void setZoneIds(List<String> zoneIds) {
		this.zoneIds = zoneIds;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// Constructors

	public List<String> getZones() {
		return zones;
	}

	public void setZones(List<String> zones) {
		this.zones = zones;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public List<UserSkillDTO> getSkill() {
		return skill;
	}

	public void setSkill(List<UserSkillDTO> skill) {
		this.skill = skill;
	}
	
	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public UsersDTO() {
		// Default constructor
	}

	public UsersDTO(long userID, String userName, String password, String emailID, String countryName,
			String phoneNumber, String role, String photo, List<String> zoneIds, List<String> skillNames,
			List<String> skills, String status, List<String> zones) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.password = password;
		this.emailID = emailID;
		this.countryName = countryName;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.photo = photo;
		this.zoneIds = zoneIds;
		this.skillNames = skillNames;
		this.skills = skills;
		this.status = status;
		this.zones = zones;
	}

//	public UsersDTO(long userID2, String userName2, String password2, String emailID2, String countryName2,
//			String phoneNumber2, String role2, List<String> collect, Object object, String status2) {
//		// TODO Auto-generated constructor stub
//	}
	public UsersDTO(long userID, String userId, String userName, String emailID, String phoneNumber, String role,
			boolean isActive) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.userId = userId;
		this.emailID = emailID;
		this.phoneNumber = phoneNumber;
		this.role = role;
		//this.status = status;
		this.isActive = isActive;
	}

	public UsersDTO(long userID, String userName, String emailID, String phoneNumber, String role) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.emailID = emailID;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	@Override
	public String toString() {
		return "UsersDTO [ userName=" + userName + ", password=" + password + ", emailID=" + emailID + ", countryName="
				+ countryName + ", phoneNumber=" + phoneNumber + ", role=" + role + ", photo=" + photo + ", zoneIds="
				+ zoneIds + ", skillNames=" + skillNames + ", skills=" + skills + ", status=" + status + ", zones="
				+ zones + "]";
	}

}
