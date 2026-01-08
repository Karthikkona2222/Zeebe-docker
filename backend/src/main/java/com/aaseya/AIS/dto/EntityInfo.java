package com.aaseya.AIS.dto;
 
public class EntityInfo {
	
	private String entityName;
    private String entityID;
    private String address;
    private String representativeEmail;
    private String zone_id;
    private String zoneName;
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getEntityID() {
		return entityID;
	}
	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRepresentativeEmail() {
		return representativeEmail;
	}
	public void setRepresentativeEmail(String representativeEmail) {
		this.representativeEmail = representativeEmail;
	}
	
	public String getZone_id() {
		return zone_id;
	}
	public void setZone_id(String zone_id) {
		this.zone_id = zone_id;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	@Override
	public String toString() {
		return "EntityInfo [entityName=" + entityName + ", entityID=" + entityID + ", address=" + address
				+ ", representativeEmail=" + representativeEmail + ", zone_id=" + zone_id + ", zoneName=" + zoneName
				+ "]";
	}
    
 
}