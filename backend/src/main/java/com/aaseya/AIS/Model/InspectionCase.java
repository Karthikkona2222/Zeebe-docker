package com.aaseya.AIS.Model;

import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "InspectionCase")
public class InspectionCase {

	@Id
	@Column(name = "inspectionID")
	private long inspectionID;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inspection_plan_id") // Foreign key column
    private InspectionPlan inspectionPlan; // Reference to InspectionPlan

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "entity_id")
	private NewEntity entity;

	@Column(name = "inspector_source")
	private String inspector_source;

	@Column(name = "status")
	private String status;

	@Column(name = "inspector_feedback_by_reviewer")
	private String inspectorFeedbackByReviewer;

	@Column(name = "inspector_feedback_by_approver")
	private String inspectorFeedbackByApprover;

	@Column(name = "start_date", nullable = true)
	private LocalDate start_date;

	@Column(name = "end_date", nullable = true)
	private LocalDate end_date;

	@Column(name = "dateOfInspection")
	private String dateOfInspection;

	@Column(name = "reason")
	private String reason;

	@Column(name = "inspector_comments")
	private String inspector_comments;

	@Column(name = "approver_comments")
	private String approver_comments;

	@Column(name = "reviewer_comments")
	private String reviewer_comments;

	@Column(name = "createdBy")
	private String createdBy;

	@Column(name = "inspectorID")
	private String inspectorID;

	@Column(name = "dueDateSatisfiedByInspector")
	private Boolean dueDateSatisfiedByInspector;

	@Column(name = "reviewerID")
	private String reviewerID;

	@Column(name = "approverID")
	private String approverID;

	@Column(name = "inspection_type")
	private String inspectionType;

	@Column(name = "template_id")
	private Long template_id;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@Column(name = "custom_pre_inspection_checklist")
	private String custom_pre_inspection_checklist;

	@Column(name = "recommendedAction")
	private String recommendedAction;

	@Column(name = "dateOfFollowUp")
	private LocalDate dateOfFollowUp;

	@Column(name = "dateOfReinspection")
	private LocalDate dateOfReinspection;

	
	
	@Column(name = "caseCreationType")
	private String caseCreationType;


	@Column(name = "controlTypeId")
	private Long controlTypeId;

	@Column(name = "group_id")
	private Long groupId;
	
	@Column(name = "AssignedCategory")
	private Boolean AssignedCategory;

	@Column(name = "leadId")
	private Long leadId;
	
	// @Column(name = "entity_id")
	private String entityID;

	@Column(name = "is_preinspection")
	private Boolean is_preinspection;
	
	@Column(name = "complianceID")
	private String complianceID;
	
	@Column(name = "process_type")
	private String processType;
	
	@Column(name = "recommendation")
	private String recommendation;
	
	
	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public Boolean getIs_preinspection() {
		return is_preinspection;
	}

	public void setIs_preinspection(Boolean is_preinspection) {
		this.is_preinspection = is_preinspection;
	}

	public Boolean getIs_preinspection_submitted() {
		return is_preinspection_submitted;
	}

	public final String getComplianceID() {
		return complianceID;
	}

	public final void setComplianceID(String complianceID) {
		this.complianceID = complianceID;
	}

	public void setIs_preinspection_submitted(Boolean is_preinspection_submitted) {
		this.is_preinspection_submitted = is_preinspection_submitted;
	}

	@Column(name = "is_preinspection_submitted")
	private Boolean is_preinspection_submitted;

	@OneToMany(mappedBy = "inspectionCase", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<InspectionChecklistandAnswers> inspectionChecklistandAnswers;

	@OneToMany(mappedBy = "inspectionCase", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Approver_Reviewer_Comment> approverReviewerComments;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinTable(name = "inspection_case_pre_inspection_checklist", joinColumns = @JoinColumn(name = "inspection_case_id", referencedColumnName = "inspectionID"), inverseJoinColumns = @JoinColumn(name = "pre_inspection_checklist_id", referencedColumnName = "id"))
	private List<PreInspectionChecklist> preInspectionChecklists;

	@Column(name = "custom_inspection_checklist")
	private String customInspectionChecklist;

	@Column(name = "created_date")
	private LocalDate createdDate;

	public String getCaseCreationType() {
		return caseCreationType;
	}

	public void setCaseCreationType(String caseCreationType) {
		this.caseCreationType = caseCreationType;
	}

	public long getInspectionID() {
		return inspectionID;
	}

	public void setInspectionID(long inspectionID) {
		this.inspectionID = inspectionID;
	}
	
	 public InspectionPlan getInspectionPlan() {
	        return inspectionPlan;
	    }

	    public void setInspectionPlan(InspectionPlan inspectionPlan) {
	        this.inspectionPlan = inspectionPlan;
	    }

	public NewEntity getEntity() {
		return entity;
	}

	public Long getControlTypeId() {
		return controlTypeId;
	}

	public void setControlTypeId(Long controlTypeId) {
		this.controlTypeId = controlTypeId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getLeadId() {
		return leadId;
	}

	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}

	public String getInspectorFeedbackByReviewer() {
		return inspectorFeedbackByReviewer;
	}

	public void setInspectorFeedbackByReviewer(String inspectorFeedbackByReviewer) {
		this.inspectorFeedbackByReviewer = inspectorFeedbackByReviewer;
	}

	public String getInspectorFeedbackByApprover() {
		return inspectorFeedbackByApprover;
	}

	public void setInspectorFeedbackByApprover(String inspectorFeedbackByApprover) {
		this.inspectorFeedbackByApprover = inspectorFeedbackByApprover;
	}

	public Boolean getDueDateSatisfiedByInspector() {
		return dueDateSatisfiedByInspector;
	}

	public void setDueDateSatisfiedByInspector(Boolean dueDateSatisfiedByInspector) {
		this.dueDateSatisfiedByInspector = dueDateSatisfiedByInspector;
	}

	public void setEntity(NewEntity entity) {
		this.entity = entity;
	}

	public String getRecommendedAction() {
		return recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}

	public LocalDate getDateOfFollowUp() {
		return dateOfFollowUp;
	}

	public void setDateOfFollowUp(LocalDate dateOfFollowUp) {
		this.dateOfFollowUp = dateOfFollowUp;
	}

	public LocalDate getDateOfReinspection() {
		return dateOfReinspection;
	}

	public void setDateOfReinspection(LocalDate dateOfReinspection) {
		this.dateOfReinspection = dateOfReinspection;
	}

	public String getInspector_source() {
		return inspector_source;
	}

	public void setInspector_source(String inspector_source) {
		this.inspector_source = inspector_source;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getStart_date() {
		return start_date;
	}

	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}

	public LocalDate getEnd_date() {
		return end_date;
	}

	public void setEnd_date(LocalDate end_date) {
		this.end_date = end_date;
	}

	public String getDateOfInspection() {
		return dateOfInspection;
	}

	public void setDateOfInspection(String dateOfInspection) {
		this.dateOfInspection = dateOfInspection;
	}

	public String getCustom_pre_inspection_checklist() {
		return custom_pre_inspection_checklist;
	}

	public void setCustom_pre_inspection_checklist(String custom_pre_inspection_checklist) {
		this.custom_pre_inspection_checklist = custom_pre_inspection_checklist;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getInspector_comments() {
		return inspector_comments;
	}

	public void setInspector_comments(String inspector_comments) {
		this.inspector_comments = inspector_comments;
	}

	public String getApprover_comments() {
		return approver_comments;
	}

	public void setApprover_comments(String approver_comments) {
		this.approver_comments = approver_comments;
	}

	public String getReviewer_comments() {
		return reviewer_comments;
	}

	public void setReviewer_comments(String reviewer_comments) {
		this.reviewer_comments = reviewer_comments;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getInspectorID() {
		return inspectorID;
	}

	public String getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}

	public Long getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(Long template_id) {
		this.template_id = template_id;
	}

	public String getEntityID() {
		return entityID;
	}

	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	public void setInspectorID(String inspectorID) {
		this.inspectorID = inspectorID;
	}

	public String getReviewerID() {
		return reviewerID;
	}

	public void setReviewerID(String reviewerID) {
		this.reviewerID = reviewerID;
	}

	public String getApproverID() {
		return approverID;
	}

	public void setApproverID(String approverID) {
		this.approverID = approverID;
	}

	public List<InspectionChecklistandAnswers> getInspectionChecklistandAnswers() {
		return inspectionChecklistandAnswers;
	}

	public void setInspectionChecklistandAnswers(List<InspectionChecklistandAnswers> inspectionChecklistandAnswers) {
		this.inspectionChecklistandAnswers = inspectionChecklistandAnswers;
	}

	public List<Approver_Reviewer_Comment> getApproverReviewerComments() {
		return approverReviewerComments;
	}

	public void setApproverReviewerComments(List<Approver_Reviewer_Comment> approverReviewerComments) {
		this.approverReviewerComments = approverReviewerComments;
	}

	public void setPreInspectionChecklists(List<PreInspectionChecklist> list) {
		this.preInspectionChecklists = list;
	}

	public String getCustomInspectionChecklist() {
		return customInspectionChecklist;
	}

	public void setCustomInspectionChecklist(String customInspectionChecklist) {
		this.customInspectionChecklist = customInspectionChecklist;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public List<PreInspectionChecklist> getPreInspectionChecklists() {
		return preInspectionChecklists;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}
	

	public Boolean getAssignedCategory() {
		return AssignedCategory;
	}

	public void setAssignedCategory(Boolean assignedCategory) {
		AssignedCategory = assignedCategory;
	}

	

	@Override
	public String toString() {
		return "InspectionCase [inspectionID=" + inspectionID + ", inspectionPlan=" + inspectionPlan + ", entity="
				+ entity + ", inspector_source=" + inspector_source + ", status=" + status
				+ ", inspectorFeedbackByReviewer=" + inspectorFeedbackByReviewer + ", inspectorFeedbackByApprover="
				+ inspectorFeedbackByApprover + ", start_date=" + start_date + ", end_date=" + end_date
				+ ", dateOfInspection=" + dateOfInspection + ", reason=" + reason + ", inspector_comments="
				+ inspector_comments + ", approver_comments=" + approver_comments + ", reviewer_comments="
				+ reviewer_comments + ", createdBy=" + createdBy + ", inspectorID=" + inspectorID
				+ ", dueDateSatisfiedByInspector=" + dueDateSatisfiedByInspector + ", reviewerID=" + reviewerID
				+ ", approverID=" + approverID + ", inspectionType=" + inspectionType + ", template_id=" + template_id
				+ ", dueDate=" + dueDate + ", custom_pre_inspection_checklist=" + custom_pre_inspection_checklist
				+ ", recommendedAction=" + recommendedAction + ", dateOfFollowUp=" + dateOfFollowUp
				+ ", dateOfReinspection=" + dateOfReinspection + ", caseCreationType=" + caseCreationType
				+ ", controlTypeId=" + controlTypeId + ", groupId=" + groupId + ", AssignedCategory=" + AssignedCategory
				+ ", leadId=" + leadId + ", entityID=" + entityID + ", is_preinspection=" + is_preinspection
				+ ", is_preinspection_submitted=" + is_preinspection_submitted + ", inspectionChecklistandAnswers="
				+ inspectionChecklistandAnswers + ", approverReviewerComments=" + approverReviewerComments
				+ ", preInspectionChecklists=" + preInspectionChecklists + ", customInspectionChecklist="
				+ customInspectionChecklist + ", createdDate=" + createdDate + "]";
	}

	public InspectionCase() {
		super();
		// TODO Auto-generated constructor stub
	}
}
