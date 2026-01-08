package com.aaseya.AIS.dto;

import java.util.List;

public class UpdateIsActiveDTO {
	
	private String inputType;  // Name of the table or model
    private String action;     // Action to perform, either "Activate" or "Deactivate"
    private List<Object> ids;  // List of IDs that can be Integer, Long, or String

    // Default constructor
    public UpdateIsActiveDTO() {}

    // Constructor with parameters
    public UpdateIsActiveDTO(String inputType, String action, List<Object> ids) {
        this.inputType = inputType;
        this.action = action;
        this.ids = ids;
    }

    // Getters and Setters
    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Object> getIds() {
        return ids;
    }

    public void setIds(List<Object> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "UpdateIsActiveDTO{" +
                "inputType='" + inputType + '\'' +
                ", action='" + action + '\'' +
                ", ids=" + ids +
                '}';
    }

}
