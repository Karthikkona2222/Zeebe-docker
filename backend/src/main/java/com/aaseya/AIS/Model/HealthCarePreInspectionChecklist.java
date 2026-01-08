package com.aaseya.AIS.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "healthcare_pre_inspection_checklist")
public class HealthCarePreInspectionChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_checklist_id")
    private Long preChecklistId;

    @Column(name = "pre_checklist_description")
    private String preChecklistDescription;

    @Column(name = "answer_type")
    private String answertype;
    
	public Long getPreChecklistId() {
		return preChecklistId;
	}

	public void setPreChecklistId(Long preChecklistId) {
		this.preChecklistId = preChecklistId;
	}

	public String getPreChecklistDescription() {
		return preChecklistDescription;
	}

	public void setPreChecklistDescription(String preChecklistDescription) {
		this.preChecklistDescription = preChecklistDescription;
	}

	public String getAnswertype() {
		return answertype;
	}

	public void setAnswertype(String answertype) {
		this.answertype = answertype;
	}

	@Override
	public String toString() {
		return "HealthCarePreInspectionChecklist [preChecklistId=" + preChecklistId + ", preChecklistDescription="
				+ preChecklistDescription + ", answertype=" + answertype + "]";
	}


   
    
}
