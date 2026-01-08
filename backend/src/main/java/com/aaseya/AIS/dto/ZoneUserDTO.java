package com.aaseya.AIS.dto;
 
import java.util.List;
 
public class ZoneUserDTO {
 
    private String zoneId;
    private String zoneName;
    private String zoneDescription;
    private String zoneLocation;
    private String zoneCoordinates;
    private Boolean isDefaultZone;
    private List<UsersDTO> users;
 
    // Getters and Setters
 
    // Constructor
    public ZoneUserDTO(String zoneId, String zoneName, String zoneDescription, String zoneLocation, String zoneCoordinates, Boolean isDefaultZone, List<UsersDTO> users) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.zoneDescription = zoneDescription;
        this.zoneLocation = zoneLocation;
        this.zoneCoordinates = zoneCoordinates;
        this.isDefaultZone = isDefaultZone;
        this.users = users;
    }
 
	public String getZoneId() {
		return zoneId;
	}
 
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
 
	public String getZoneName() {
		return zoneName;
	}
 
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
 
	public String getZoneDescription() {
		return zoneDescription;
	}
 
	public void setZoneDescription(String zoneDescription) {
		this.zoneDescription = zoneDescription;
	}
 
	public String getZoneLocation() {
		return zoneLocation;
	}
 
	public void setZoneLocation(String zoneLocation) {
		this.zoneLocation = zoneLocation;
	}
 
	public String getZoneCoordinates() {
		return zoneCoordinates;
	}
 
	public void setZoneCoordinates(String zoneCoordinates) {
		this.zoneCoordinates = zoneCoordinates;
	}
 
	public Boolean getIsDefaultZone() {
		return isDefaultZone;
	}
 
	public void setIsDefaultZone(Boolean isDefaultZone) {
		this.isDefaultZone = isDefaultZone;
	}
 
	public List<UsersDTO> getUsers() {
		return users;
	}
 
	public void setUsers(List<UsersDTO> users) {
		this.users = users;
	}
 
	@Override
	public String toString() {
		return "ZoneUserDTO [zoneId=" + zoneId + ", zoneName=" + zoneName + ", zoneDescription=" + zoneDescription
				+ ", zoneLocation=" + zoneLocation + ", zoneCoordinates=" + zoneCoordinates + ", isDefaultZone="
				+ isDefaultZone + ", users=" + users + "]";
	}
 
   
}