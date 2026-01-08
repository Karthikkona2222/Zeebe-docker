package com.aaseya.AIS.dto;

import java.util.Map;

public class StatusCountResponseDTO {
    private Map<String, Long> statusCounts;
    private Long totalCount;

    
 // To get individual status names and its count and total count of status for particular createdby// 
    
    public StatusCountResponseDTO(Map<String, Long> statusCounts, Long totalCount) {
        this.statusCounts = statusCounts;
        this.totalCount = totalCount;
    }

    public Map<String, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
