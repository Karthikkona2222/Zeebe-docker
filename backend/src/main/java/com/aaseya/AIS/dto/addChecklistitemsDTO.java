package com.aaseya.AIS.dto;

import java.util.ArrayList;
import java.util.List;

public class addChecklistitemsDTO {
    private Long id;
    private String checklistName;
    private String severity;
    private int weightage;
    private String answerType;
    private String predefinedAnswerTypes;
    private List<Long> correctiveActions;
    private List<String> newCorrectiveActions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChecklistName() {
        return checklistName;
    }

    public void setChecklistName(String checklistName) {
        this.checklistName = checklistName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getPredefinedAnswerTypes() {
        return predefinedAnswerTypes;
    }

    public void setPredefinedAnswerTypes(String predefinedAnswerTypes) {
        this.predefinedAnswerTypes = predefinedAnswerTypes;
    }

    public List<Long> getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(List<Long> correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

	public List<String> getNewCorrectiveActions() {
		return newCorrectiveActions;
	}

	public void setNewCorrectiveActions(List<String> newCorrectiveActions) {
		this.newCorrectiveActions = newCorrectiveActions;
	}

	

	
	}
    

