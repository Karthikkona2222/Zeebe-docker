package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PolicyDetails")
public class PolicyDetails {

    @Id
    @Column(name = "policy_id")
    private String policyId;

    @Column(name = "customer_name")
    private String customerName;
    
    @Column (name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "social_security_number")
    private String socialSecurityNumber;

    @Column(name = "policy_type")
    private String policyType; // e.g., "Reimbursement"

    @Column(name = "coverage_start_date")
    private LocalDate coverageStartDate;

    @Column(name = "coverage_end_date")
    private LocalDate coverageEndDate; // Expiration date

    @Column(name = "premium_amount")
    private Double premiumAmount;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "insurance_plan_name")
    private String insurancePlanName;

    @Column(name = "created_timestamp", updatable = false)
    private LocalDateTime createdTimestamp;

    // Default constructor
    public PolicyDetails() {}

    // Auto-set creation timestamp
    @PrePersist
    protected void onCreate() {
        this.createdTimestamp = LocalDateTime.now();
    }
    
    @OneToMany(mappedBy = "policyDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private java.util.List<ClaimRequestDocuments> claimRequestDocuments;
    
    @OneToMany(mappedBy = "policyDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClaimCase> claimCases;

    // Getters & Setters
    public java.util.List<ClaimRequestDocuments> getClaimRequestDocuments() {
        return claimRequestDocuments;
    }

    public void setClaimRequestDocuments(java.util.List<ClaimRequestDocuments> claimRequestDocuments) {
        this.claimRequestDocuments = claimRequestDocuments;
    }
    
    public List<ClaimCase> getClaimCases() {
        return claimCases;
    }

    public void setClaimCases(List<ClaimCase> claimCases) {
        this.claimCases = claimCases;
    }


    // Getters & Setters
    public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}


    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSocialSecurityNumber() { return socialSecurityNumber; }
    public void setSocialSecurityNumber(String socialSecurityNumber) { this.socialSecurityNumber = socialSecurityNumber; }

    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }

    public LocalDate getCoverageStartDate() { return coverageStartDate; }
    public void setCoverageStartDate(LocalDate coverageStartDate) { this.coverageStartDate = coverageStartDate; }

    public LocalDate getCoverageEndDate() { return coverageEndDate; }
    public void setCoverageEndDate(LocalDate coverageEndDate) { this.coverageEndDate = coverageEndDate; }

    public Double getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(Double premiumAmount) { this.premiumAmount = premiumAmount; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getInsurancePlanName() { return insurancePlanName; }
    public void setInsurancePlanName(String insurancePlanName) { this.insurancePlanName = insurancePlanName; }

    public LocalDateTime getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(LocalDateTime createdTimestamp) { this.createdTimestamp = createdTimestamp; }
    
    

    public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPolicyId() {
		return policyId;
	}

	@Override
	public String toString() {
		return "PolicyDetails [policyId=" + policyId + ", customerName=" + customerName + ", gender=" + gender
				+ ", dateOfBirth=" + dateOfBirth + ", address=" + address + ", phoneNumber=" + phoneNumber + ", email="
				+ email + ", socialSecurityNumber=" + socialSecurityNumber + ", policyType=" + policyType
				+ ", coverageStartDate=" + coverageStartDate + ", coverageEndDate=" + coverageEndDate
				+ ", premiumAmount=" + premiumAmount + ", medicalHistory=" + medicalHistory + ", insurancePlanName="
				+ insurancePlanName + ", createdTimestamp=" + createdTimestamp + ", claimRequestDocuments="
				+ claimRequestDocuments + "]";
	}
}
