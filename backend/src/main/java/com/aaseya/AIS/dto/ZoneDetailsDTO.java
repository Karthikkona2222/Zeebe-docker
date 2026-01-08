package com.aaseya.AIS.dto;
 
public class ZoneDetailsDTO {
	
	
	private String name;    
	private String description;    
    private int coordinates;
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
	public int getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(int coordinates) {
		this.coordinates = coordinates;
	}
	@Override
	public String toString() {
		return "ZoneDetailsDTO [name=" + name + ", description=" + description + ", coordinates=" + coordinates + "]";
	}
    
    
 
}