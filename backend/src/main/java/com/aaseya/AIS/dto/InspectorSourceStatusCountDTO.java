package com.aaseya.AIS.dto;

import java.util.Map;

public class InspectorSourceStatusCountDTO {
    private String inspectorSource;
    private Long inspectorSourceCount;
    private Map<String, Long> statusCounts;

    // To get count and names of status, Inspector source based on createBy//
    
    public InspectorSourceStatusCountDTO(String inspectorSource, Long inspectorSourceCount, Map<String, Long> statusCounts) {
        this.inspectorSource = inspectorSource;
        this.inspectorSourceCount = inspectorSourceCount;
        this.statusCounts = statusCounts;
    }

    public String getInspectorSource() {
        return inspectorSource;
    }

    public void setInspectorSource(String inspectorSource) {
        this.inspectorSource = inspectorSource;
    }

    public Long getInspectorSourceCount() {
        return inspectorSourceCount;
    }

    public void setInspectorSourceCount(Long inspectorSourceCount) {
        this.inspectorSourceCount = inspectorSourceCount;
    }

    public Map<String, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }
}
