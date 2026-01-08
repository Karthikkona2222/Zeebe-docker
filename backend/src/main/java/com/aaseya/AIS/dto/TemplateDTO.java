package com.aaseya.AIS.dto;
 
public class TemplateDTO {
	private String template_name;
	private String effective_from;
	private String version;
	private long template_id;
 
	private boolean isActive;
	
	public long getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(long template_id) {
		this.template_id = template_id;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTemplate_name() {
		return template_name;
	}
	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}
	public String getEffective_from() {
		return effective_from;
	}
	public void setEffective_from(String effective_from) {
		this.effective_from = effective_from;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion_from(String version) {
		this.version = version;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
 
}
 
 
