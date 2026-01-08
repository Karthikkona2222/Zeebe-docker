package com.aaseya.AIS.dto;
 
import java.util.List;
 
public class PreInspectionChecklistResponseDTO {
    private long checklistId;
    private String name;
    private String answerType;
    private String selectedAnswer;
    private String comments;
    
    
    private InspectionTypeDTO inspectionType; // Change to InspectionTypeDTO
    private List<RequirementDTO> requirements;
 
    // Getters and Setters
 
    public long getChecklistId() {
        return checklistId;
    }
 
    public void setChecklistId(long checklistId) {
        this.checklistId = checklistId;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getAnswerType() {
        return answerType;
    }
 
    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }
 
    public String getSelectedAnswer() {
        return selectedAnswer;
    }
 
    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
 
    public String getComments() {
        return comments;
    }
 
    public void setComments(String comments) {
        this.comments = comments;
    }
 
    
 
    public InspectionTypeDTO getInspectionType() {
		return inspectionType;
	}
 
	public void setInspectionType(InspectionTypeDTO inspectionType) {
		this.inspectionType = inspectionType;
	}
 
	public List<RequirementDTO> getRequirements() {
        return requirements;
    }
 
    public void setRequirements(List<RequirementDTO> requirements) {
        this.requirements = requirements;
    }
 
	
}