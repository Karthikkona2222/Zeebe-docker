package com.aaseya.AIS.dto;
 
import java.util.List;
 
public class EntityDetailsDTO {
 
    private String entityId;
    private String name;
    private String floor;
    private String address;
    private String facility;
    private String countryName;
    private String representative_phoneno;
    private String representative_name;
    private String representative_email;
    private List<ZoneDTO> zones;
    private List<InspectionTypeDTO> inspectionTypes;
    private List<String> zoneIds;
    private List<Long> inspectionTypeIds;
    private String action;
    private List<PeriodicityDTO> periodicity;
 
    // Getters and Setters
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
 
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
 
    public String getFacility() {
        return facility;
    }
 
    public void setFacility(String facility) {
        this.facility = facility;
    }
 
    public String getCountryName() {
        return countryName;
    }
 
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
 
    public String getRepresentative_phoneno() {
        return representative_phoneno;
    }
 
    public void setRepresentative_phoneno(String representative_phoneno) {
        this.representative_phoneno = representative_phoneno;
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
 
    public List<ZoneDTO> getZones() {
        return zones;
    }
 
    public void setZones(List<ZoneDTO> zones) {
        this.zones = zones;
    }
 
    public List<InspectionTypeDTO> getInspectionTypes() {
        return inspectionTypes;
    }
 
    public void setInspectionTypes(List<InspectionTypeDTO> inspectionTypes) {
        this.inspectionTypes = inspectionTypes;
    }
 
    public List<String> getZoneIds() {
        return zoneIds;
    }
 
    public void setZoneIds(List<String> zoneIds) {
        this.zoneIds = zoneIds;
    }
 
    public List<Long> getInspectionTypeIds() {
        return inspectionTypeIds;
    }
 
    public void setInspectionTypeIds(List<Long> inspectionTypeIds) {
        this.inspectionTypeIds = inspectionTypeIds;
    }
 
    public String getAction() {
        return action;
    }
 
    public void setAction(String action) {
        this.action = action;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
    
    public List<PeriodicityDTO> getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(List<PeriodicityDTO> periodicity) {
		this.periodicity = periodicity;
	}

	// Default constructor
    public EntityDetailsDTO() {
        super();
    }
 
    // Parameterized constructor
    public EntityDetailsDTO(String entityId, String name, String floor, String address, String facility,
            String representativePhoneNo, String representativeName, String representativeEmail,
            List<ZoneDTO> zones, List<InspectionTypeDTO> inspectionTypes) {
        this.entityId = entityId;
        this.name = name;
        this.floor = floor;
        this.address = address;
        this.facility = facility;
        this.representative_phoneno = representativePhoneNo;
        this.representative_name = representativeName;
        this.representative_email = representativeEmail;
        this.zones = zones;
        this.inspectionTypes = inspectionTypes;
    }
}
 