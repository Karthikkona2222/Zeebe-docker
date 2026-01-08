package com.aaseya.AIS.dto;

import java.time.LocalTime;

public class InspectionCaseDetailsDTO {
	private long caseId;

	private String inspectionType;

	private String entityID;
	private String entityName;
	private String inspectorSource;
	private String size;
	private String representativeName;
	private String segmentName;
	private String subSegmentName;
	private String phoneNumber;
	private String emailID;
	private String location;
	private LocalTime efforts;

	// Constructor for projection
	public InspectionCaseDetailsDTO(long caseId, String entityID, String entityName, String inspectionType,
			String inspectorSource, String size, String representativeName, String segmentName, String subSegmentName,
			String phoneNumber, String emailID, String location, LocalTime high, LocalTime low, LocalTime medium) {
		this.caseId = caseId; // Initialize caseId
		this.entityID = entityID;
		this.entityName = entityName; // Initialize entityName
		this.inspectionType = inspectionType;
		this.inspectorSource = inspectorSource;
		this.size = size;
		this.representativeName = representativeName;
		this.segmentName = segmentName;
		this.subSegmentName = subSegmentName;
		this.phoneNumber = phoneNumber;
		this.emailID = emailID;
		this.location = location;
		this.efforts = high;
		this.efforts = low;
		this.efforts = medium;
	}

	public long getCaseId() {
		return caseId;
	}

	public void setCaseId(long caseId) {
		this.caseId = caseId;
	}

	public String getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}

	public String getEntityID() {
		return entityID;
	}

	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getInspectorSource() {
		return inspectorSource;
	}

	public void setInspectorSource(String inspectorSource) {
		this.inspectorSource = inspectorSource;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public String getSubSegmentName() {
		return subSegmentName;
	}

	public void setSubSegmentName(String subSegmentName) {
		this.subSegmentName = subSegmentName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalTime getEfforts() {
		return efforts;
	}

	public void setEfforts(LocalTime efforts) {
		this.efforts = efforts;
	}

	// Getters and Setters for all fields
	

	
}
