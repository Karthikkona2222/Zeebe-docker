package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PolicyDetailsDTO {
    private String policyId;
    private String customerName;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private String email;
    private String policyType;
    private LocalDate coverageStartDate;
    private LocalDate coverageEndDate;
    private Double premiumAmount;
    private String medicalHistory;
    private String insurancePlanName;
    private LocalDateTime createdTimestamp;
    private String socialSecurityNumber;
    
    // getters, setters, constructors
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getAddress() {
		return address;
	}
	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPolicyType() {
		return policyType;
	}
	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
	public LocalDate getCoverageStartDate() {
		return coverageStartDate;
	}
	public void setCoverageStartDate(LocalDate coverageStartDate) {
		this.coverageStartDate = coverageStartDate;
	}
	public LocalDate getCoverageEndDate() {
		return coverageEndDate;
	}
	public void setCoverageEndDate(LocalDate coverageEndDate) {
		this.coverageEndDate = coverageEndDate;
	}
	public Double getPremiumAmount() {
		return premiumAmount;
	}
	public void setPremiumAmount(Double premiumAmount) {
		this.premiumAmount = premiumAmount;
	}
	public String getMedicalHistory() {
		return medicalHistory;
	}
	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}
	public String getInsurancePlanName() {
		return insurancePlanName;
	}
	public void setInsurancePlanName(String insurancePlanName) {
		this.insurancePlanName = insurancePlanName;
	}
	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
    
    
}
