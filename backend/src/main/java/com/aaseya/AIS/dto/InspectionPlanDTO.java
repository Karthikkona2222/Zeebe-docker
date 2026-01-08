package com.aaseya.AIS.dto;

public class InspectionPlanDTO {

	    private String inspectionPlanId;
	    private String inspectionPlanName;
	    private String reason;
	    private String status;
		
	    public String getInspectionPlanId() {
			return inspectionPlanId;
		}
		public void setInspectionPlanId(String inspectionPlanId) {
			this.inspectionPlanId = inspectionPlanId;
		}
		public String getInspectionPlanName() {
			return inspectionPlanName;
		}
		public void setInspectionPlanName(String inspectionPlanName) {
			this.inspectionPlanName = inspectionPlanName;
		}
		public String getReason() {
			return reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public InspectionPlanDTO(String inspectionPlanId, String inspectionPlanName, String reason, String status) {
			super();
			this.inspectionPlanId = inspectionPlanId;
			this.inspectionPlanName = inspectionPlanName;
			this.reason = reason;
			this.status = status;
		}
		public InspectionPlanDTO() {
			super();
		}
	    
	    

}