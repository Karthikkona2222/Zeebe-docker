package com.aaseya.AIS.Model;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class Users {
 
    @Id
    @GeneratedValue
    @Column(name = "userID")
    private long userID;
 
    @Column(name = "user_name")
    private String userName;
 
    @Column(name = "password")
    private String password;
 
    @Column(name = "emailID")
    private String emailID;
 
    @Column(name = "country_name")
    private String countryName;
 
    @Column(name = "phone_number")
    private String phoneNumber;
 
    @Column(name = "role")
    private String role;
 
    @Column(name = "status")
    private String status;
    
    @Column(name = "registrationID")
    private String registrationID;
    
    
    @Column(name = "photo", columnDefinition = "BYTEA")
    private byte[] photo;    
  
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_zone",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private Set<Zone> zones = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_skill",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skill;
    
    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
   	private
   	List<InspectionEscalation> inspectionEscalations;
    
    @Column(name = "is_active")
	private boolean isActive;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id")
    private Pool pool;

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

 
    // Getters and Setters
 
    public long getUserID() {
        return userID;
    }
 
    public void setUserID(long userID) {
        this.userID = userID;
    }
 
    public String getUserName() {
        return userName;
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
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public Set<Zone> getZones() {
        return zones;
    }
 
    public void setZones(Set<Zone> zones) {
        this.zones = zones;
    }
 
    public List<Skill> getSkill() {
        return skill;
    }
 
    public void setSkill(List<Skill> skill) {
        this.skill = skill;
    }
    
    public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public List<InspectionEscalation> getInspectionEscalations() {
		return inspectionEscalations;
	}

	public void setInspectionEscalations(List<InspectionEscalation> inspectionEscalations) {
		this.inspectionEscalations = inspectionEscalations;
	}

	public String getRegistrationID() {
		return registrationID;
	}

	public void setRegistrationID(String registrationID) {
		this.registrationID = registrationID;
	}
	
	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Users [userID=" + userID + ", userName=" + userName + ", emailID=" + emailID + ", phoneNumber="
				+ phoneNumber + ", role=" + role + "]";
	}

}