package com.aaseya.AIS.dto;

import java.util.List;

public class SegmentDTO {

	private String name;
	private String description;
	private String isActive;
	private List<String> subSegment;
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
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public List<String> getSubSegment() {
		return subSegment;
	}
	public void setSubSegment(List<String> subSegment) {
		this.subSegment = subSegment;
	}
	
	
}
