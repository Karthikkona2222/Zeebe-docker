package com.aaseya.AIS.Model;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "healthcare_checklistand_answers")
public class HealthcareChecklistandAnswers {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // ✅ Mapping to Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private HealthCareChecklistCategory category;
 
    // ✅ Mapping to Checklist Item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    private HealthCareChecklistItem checklistItem;
 
    private String answer; // Yes / No / NA
 
    @Column(length = 1000)
    private String comment;
 
    private String correctiveAction;
    private String attachment; // file path or url
 
    // ✅ Mapping to ClaimCase
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    private ClaimCase claimCase;
 
    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
 
    public HealthCareChecklistCategory getCategory() {
        return category;
    }
    public void setCategory(HealthCareChecklistCategory category) {
        this.category = category;
    }
 
    public HealthCareChecklistItem getChecklistItem() {
        return checklistItem;
    }
    public void setChecklistItem(HealthCareChecklistItem checklistItem) {
        this.checklistItem = checklistItem;
    }
 
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
 
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
 
    public String getCorrectiveAction() {
        return correctiveAction;
    }
    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }
 
    public String getAttachment() {
        return attachment;
    }
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
 
    public ClaimCase getClaimCase() {
        return claimCase;
    }
    public void setClaimCase(ClaimCase claimCase) {
        this.claimCase = claimCase;
    }
}