package com.aaseya.AIS.dto;
 
import java.util.List;
 
public class EntityDTO {
 
	private String entityid;
	private String name;
	private String address;
	private String location;
	private String facility;
	private String building;
	private String type;
	private List<String> Segment;
	private List<String> subSegment;
	private String representative_name;
	private String representative_email;
	private String representative_id;
	private String representative_phoneno;
	private String floor;
	private String isActive;
	private String isAddress;
	private String zonename;
	private String countryname;
	private String size;
	public String getEntityid() {
		return entityid;
	}
	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getFacility() {
		return facility;
	}
	public void setFacility(String facility) {
		this.facility = facility;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getSegment() {
		return Segment;
	}
	public void setSegment(List<String> segment) {
		Segment = segment;
	}
	public List<String> getSubSegment() {
		return subSegment;
	}
	public void setSubSegment(List<String> subSegment) {
		this.subSegment = subSegment;
	}
	public String getRepresentative_name() {
		return representative_name;
	}
	public void setRepresentative_name(String representative_name) {
		this.representative_name = representative_name;
	}
	public String getRepresentative_email() {
		return representative_email;
	}
	public void setRepresentative_email(String representative_email) {
		this.representative_email = representative_email;
	}
	public String getRepresentative_id() {
		return representative_id;
	}
	public void setRepresentative_id(String representative_id) {
		this.representative_id = representative_id;
	}
	public String getRepresentative_phoneno() {
		return representative_phoneno;
	}
	public void setRepresentative_phoneno(String representative_phoneno) {
		this.representative_phoneno = representative_phoneno;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getIsAddress() {
		return isAddress;
	}
	public void setIsAddress(String isAddress) {
		this.isAddress = isAddress;
	}
	public String getZonename() {
		return zonename;
	}
	public void setZonename(String zonename) {
		this.zonename = zonename;
	}
	public String getCountryname() {
		return countryname;
	}
	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	
	 // Constructor for minimal fields
    public EntityDTO(String entityid, String name, String address) {
        this.entityid = entityid;
        this.name = name;
        this.address = address;
    }
	public EntityDTO() {
		
	}
	
}