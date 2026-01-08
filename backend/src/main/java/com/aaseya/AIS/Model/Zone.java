package com.aaseya.AIS.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Zone")
public class Zone {

	@Id
	@Column(name = "zoneId")
	@GeneratedValue(generator = ZoneIDGenerator.GENERATOR_NAME)
	@GenericGenerator(name = ZoneIDGenerator.GENERATOR_NAME, strategy = "com.aaseya.AIS.Model.ZoneIDGenerator", parameters = {
			@Parameter(name = ZoneIDGenerator.GENERATOR_NAME, value = "ZN") })
	private String zoneId;

	@Column
	private String name;

	@Column
	private String description;

	@Column
	private String location;

	@Column
	private String coordinates;

	@Column
	private Boolean isDefaultZone;

	@JsonIgnore
	@ManyToMany(mappedBy = "zones", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<NewEntity> newEntities;

	@JsonIgnore
	@ManyToMany(mappedBy = "zones", fetch = FetchType.EAGER)
	private List<Users> users = new ArrayList<>();
	
	@Column(name = "is_active")
	private boolean isActive;
	
	// Getters and Setters

	public String getZoneId() {
		return zoneId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, zoneId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Zone other = (Zone) obj;
		return Objects.equals(name, other.name) && Objects.equals(zoneId, other.zoneId);
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public Boolean getIsDefaultZone() {
		return isDefaultZone;
	}

	public void setIsDefaultZone(Boolean isDefaultZone) {
		this.isDefaultZone = isDefaultZone;
	}

	public List<NewEntity> getNewEntities() {
		return newEntities;
	}

	public void setNewEntities(List<NewEntity> newEntities) {
		this.newEntities = newEntities;
	}

	public List<Users> getUsers() {
		return users;
	}

	public void setUsers(List<Users> users) {
		this.users = users;
	}
	
	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Zone [zoneId=" + zoneId + ", name=" + name + ", description=" + description + ", location=" + location
				+ ", coordinates=" + coordinates + ", isDefaultZone=" + isDefaultZone + ", newEntities=" + newEntities
				+ ", users=" + users + ", isActive=" + isActive + "]";
	}
}
