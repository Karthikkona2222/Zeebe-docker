package com.aaseya.AIS.dto;
public class StatusDTO {
 
	private long inspectionID;
	private String inspector_source;
    private String status;
    private String dateOfInspection;
    private String entityid;
    private String name;
    private String location;
    private String size;
    private String representative_name;
    private String representative_phoneno;
    private String subSegment;
    private String segment;
	public long getInspectionID() {
		return inspectionID;
	}
	public void setInspectionID(long inspectionID) {
		this.inspectionID = inspectionID;
	}
	public String getInspector_source() {
		return inspector_source;
	}
	public void setInspector_source(String inspector_source) {
		this.inspector_source = inspector_source;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDateOfInspection() {
		return dateOfInspection;
	}
	public void setDateOfInspection(String dateOfInspection) {
		this.dateOfInspection = dateOfInspection;
	}
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getRepresentative_name() {
		return representative_name;
	}
	public void setRepresentative_name(String representative_name) {
		this.representative_name = representative_name;
	}
	public String getRepresentative_phoneno() {
		return representative_phoneno;
	}
	public void setRepresentative_phoneno(String representative_phoneno) {
		this.representative_phoneno = representative_phoneno;
	}
	public String getSubSegment() {
		return subSegment;
	}
	public void setSubSegment(String subSegment) {
		this.subSegment = subSegment;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	
}
 