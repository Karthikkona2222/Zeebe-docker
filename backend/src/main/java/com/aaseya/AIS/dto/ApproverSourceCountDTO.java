package com.aaseya.AIS.dto;
 
import java.util.Map;
 
public class ApproverSourceCountDTO {
    private String approverSource;
    private Long approverSourceCount;
    private Map<String, Long> statusCounts;
 
    public String getApproverSource() {
        return approverSource;
    }
 
    public void setApproverSource(String approverSource) {
        this.approverSource = approverSource;
    }
 
    public Long getApproverSourceCount() {
        return approverSourceCount;
    }
 
    public void setApproverSourceCount(Long approverSourceCount) {
        this.approverSourceCount = approverSourceCount;
    }
 
    public Map<String, Long> getStatusCounts() {
        return statusCounts;
    }
 
    public void setStatusCounts(Map<String, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }
 
    @Override
    public String toString() {
        return "ApproverSourceCountDTO [approverSource=" + approverSource + ", approverSourceCount=" + approverSourceCount + ", statusCounts=" + statusCounts + "]";
    }
}
 
