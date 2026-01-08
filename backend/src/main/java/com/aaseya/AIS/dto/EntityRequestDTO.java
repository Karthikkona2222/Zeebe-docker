package com.aaseya.AIS.dto;

public class EntityRequestDTO {
	

	private String startdate;
	private String enddate;
	private String entityId;
	
	private String processType;
	
	
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getProcessType() {
        return processType;
    }
    public void setProcessType(String processType) {
        this.processType = processType;
    }

}
