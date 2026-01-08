package com.aaseya.AIS.dto;

public class EntityInformationDTO {
 
	private String entityId;
	private String floor;
	private String name;
	private String address;
	private String location;
	private String building;
	
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
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
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	@Override
	public String toString() {
		return "EntityInformationDTO [floor=" + floor + ", name=" + name + ", address=" + address + ", location="
				+ location + ", building=" + building + ", getFloor()=" + getFloor() + ", getName()=" + getName()
				+ ", getAddress()=" + getAddress() + ", getLocation()=" + getLocation() + ", getBuilding()="
				+ getBuilding() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
 
}