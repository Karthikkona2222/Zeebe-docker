package com.aaseya.AIS.dto;
 
public class TemplateSummaryDTO {
    private long template_id;
    private String template_name;
    private String version;
    private boolean active;
 
    // Getters and Setters
    public long getTemplate_id() {
        return template_id;
    }
    public void setTemplate_id(long template_id) {
        this.template_id = template_id;
    }
 
    public String getTemplate_name() {
        return template_name;
    }
    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }
 
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
 
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
 
 