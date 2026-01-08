package com.aaseya.AIS.dto;
 
public class EntitiesInspectionTypeDTO {
 
    private String entityid;
    private String name;
 
    
    public EntitiesInspectionTypeDTO() {
    }
 
    
    public EntitiesInspectionTypeDTO(String entityid, String name) {
        this.entityid = entityid;
        this.name = name;
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
 
   
}