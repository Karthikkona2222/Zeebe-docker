package com.aaseya.AIS.dto;

public class ZoneDTO {

    private String zoneId;
    private String name;
    private String description;
    private String location;
    private String coordinates;
    private boolean isDefaultZone;

    // Constructor with all fields
    public ZoneDTO(String zoneId, String name, String description, String location, String coordinates, boolean isDefaultZone) {
        this.zoneId = zoneId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.coordinates = coordinates;
        this.isDefaultZone = isDefaultZone;
    }

    // Constructor with zoneId, name, and defaultZone only
    public ZoneDTO(String zoneId, String name, boolean isDefaultZone) {
        this.zoneId = zoneId;
        this.name = name;
        this.description = ""; // Default empty value
        this.location = "";    // Default empty value
        this.coordinates = ""; // Default empty value
       this.isDefaultZone = isDefaultZone;
    }

    // Constructor with only zoneId and name
    public ZoneDTO(String zoneId, String name) {
        this.zoneId = zoneId;
        this.name = name;
//        this.description = ""; // Default empty value
//        this.location = "";    // Default empty value
//        this.coordinates = ""; // Default empty value
//        this.isDefaultZone = false; // Default value
    }

    // Getters and Setters
    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isDefaultZone() {
        return isDefaultZone;
    }

    public void setDefaultZone(boolean isDefaultZone) {
        this.isDefaultZone = isDefaultZone;
    }

    @Override
    public String toString() {
        return "ZoneDTO [zoneId=" + zoneId + ", name=" + name + ", description=" + description + ", location="
                + location + ", coordinates=" + coordinates + ", isDefaultZone=" + isDefaultZone + "]";
    }
}
