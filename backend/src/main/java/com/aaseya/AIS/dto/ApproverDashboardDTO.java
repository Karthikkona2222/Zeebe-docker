package com.aaseya.AIS.dto;
 
public class ApproverDashboardDTO {
    private String status;
    private long count;
 
    public ApproverDashboardDTO(String status, long count) {
        this.status = status;
        this.count = count;
    }
 
	public String getStatus() {
		return status;
	}
 
	public void setStatus(String status) {
		this.status = status;
	}
 
	public long getCount() {
		return count;
	}
 
	public void setCount(long count) {
		this.count = count;
	}
 
	@Override
	public String toString() {
		return "ApproverDashboardDTO [status=" + status + ", count=" + count + "]";
	}
 
    
}
 
