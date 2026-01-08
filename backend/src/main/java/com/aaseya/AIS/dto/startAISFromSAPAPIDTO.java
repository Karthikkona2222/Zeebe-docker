package com.aaseya.AIS.dto;

import com.aaseya.AIS.Model.NewEntity;
 
public class startAISFromSAPAPIDTO {
 
	private String reason;
 
	private String sapNotificationID;
	private String source;
	private EntityInfo entityInfo;
 
	public String getReason() {
		return reason;
	}
 
	public void setReason(String reason) {
		this.reason = reason;
	}
 
	public String getSapNotificationID() {
		return sapNotificationID;
	}
 
	public void setSapNotificationID(String sapNotificationID) {
		this.sapNotificationID = sapNotificationID;
	}
 
	public String getSource() {
		return source;
	}
 
	public void setSource(String source) {
		this.source = source;
	}
 
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
 
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}
 
	@Override
	public String toString() {
		return "startAISFromSAPAPIDTO [reason=" + reason + ", sapNotificationID=" + sapNotificationID + ", source="
				+ source + ", entityInfo=" + entityInfo + "]";
	}
 
}