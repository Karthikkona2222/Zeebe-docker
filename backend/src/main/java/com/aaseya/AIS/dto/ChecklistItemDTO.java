package com.aaseya.AIS.dto;

public class ChecklistItemDTO {
    private long checklist_id;
    private String checklist_name;
    private String answer_type;
    //private int weightage;

    // Getters and Setters
    public long getChecklist_id() {
        return checklist_id;
    }

    public void setChecklist_id(long checklist_id) {
        this.checklist_id = checklist_id;
    }

    public String getChecklist_name() {
        return checklist_name;
    }

    public void setChecklist_name(String checklist_name) {
        this.checklist_name = checklist_name;
    }

    public String getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(String answer_type) {
        this.answer_type = answer_type;
    }

//    public int getWeightage() {
//        return weightage;
//    }
//
//    public void setWeightage(int i) {
//        this.weightage = i;
//    }
}
