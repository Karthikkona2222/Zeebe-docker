package com.aaseya.AIS.dto;
 
import java.time.LocalTime;
import java.util.List;
 
import com.aaseya.AIS.Model.Inspection_Type;
 
public class InspectionTypeDTO {
    private long ins_type_id;
    private String name;
    private String threshold;
    private String isActive;
    private LocalTime high;
    private LocalTime medium;
    private LocalTime low;
    private String entitySize;
    private List<String> skills;
    private List<UsersDTO> users;
    private List<EntityDetailsDTO> entityDetails;
    
    private List<String> newEntities;
	
	public InspectionTypeDTO() {
		// TODO Auto-generated constructor stub
	}
	public long getIns_type_id() {
		return ins_type_id;
	}
	public void setIns_type_id(long ins_type_id) {
		this.ins_type_id = ins_type_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public LocalTime getHigh() {
		return high;
	}
	public void setHigh(LocalTime localTime) {
		this.high = localTime;
	}
	public LocalTime getMedium() {
		return medium;
	}
	public void setMedium(LocalTime localTime) {
		this.medium = localTime;
	}
	public LocalTime getLow() {
		return low;
	}
	public void setLow(LocalTime localTime) {
		this.low = localTime;
	}
	public String getEntitySize() {
		return entitySize;
	}
	public void setEntitySize(String entitySize) {
		this.entitySize = entitySize;
	}
	public static List<Inspection_Type> getAllInspection_Types() {
		 return null;
	}
	public List<String> getSkills() {
		return skills;
	}
	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	public List<UsersDTO> getUsers() {
		return users;
	}
	public void setUsers(List<UsersDTO> users) {
		this.users = users;
	}
	
	
	  public List<String> getNewEntities() {
		return newEntities;
	}
	public void setNewEntities(List<String> newEntities) {
		this.newEntities = newEntities;
	}
	public List<EntityDetailsDTO> getEntityDetails() {
		return entityDetails;
	}
	public void setEntityDetails(List<EntityDetailsDTO> entityDetails) {
		this.entityDetails = entityDetails;
	}
	public InspectionTypeDTO(long ins_type_id, String name) {
	        this.ins_type_id = ins_type_id;
	        this.name = name;
	    }
 
}