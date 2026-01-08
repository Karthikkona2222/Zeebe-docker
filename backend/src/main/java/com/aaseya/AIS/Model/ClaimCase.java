package com.aaseya.AIS.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "ClaimCase")
public class ClaimCase {

	@Id
	@Column(name = "claim_id", columnDefinition = "int8")
	private Long claimId;

	@Column(name = "claim_type")
	private String claimType;

	@Column(name = "policy_type")
	private String policyType;

	@Column(name = "created_timestamp")
	private LocalDateTime createdTimestamp;

	@Column(name = "created_by")
	private String createdBy; // who intiates the case username

	@Column(name = "status")
	private String status; // e.g., 'new', 'in_review', 'approved'

	@Column(name = "assignedInspector")
	private String assignedInspector; // After assigning himself from the pool it will update here.
	
	@Column(name = "assignedReviewer")
	private String assignedReviewer;
	
	@Column(name = "assignedApprover")
	private String assignedApprover;
	
	// New fields for reports
    @Column(name = "inspector_report", columnDefinition = "TEXT")
    private String inspectorReport;

    @Column(name = "reviewer_report", columnDefinition = "TEXT")
    private String reviewerReport;

    @Column(name = "approver_report", columnDefinition = "TEXT")
    private String approverReport;

	@Column(name = "due_date")
	private LocalDate dueDate; // Date when inspector action is due

	@Column(name = "dischargeSummary", columnDefinition = "TEXT")
	private String dischargeSummary;

	@Column(name = "validationDetails", columnDefinition = "TEXT")
	private String validationDetails;
	
	@Column(name ="recommendedAction")
	private String recommendedAction;
	
	@Column(name = "amountStatus")
	private String amountStatus;
	

	// --- Start of Change: from @ManyToMany to @ManyToOne ---
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "policy_id", referencedColumnName = "policy_id") // This creates the foreign key column
	@JsonManagedReference
	private PolicyDetails policyDetails;

	// --- End of Change ---

	@OneToMany(mappedBy = "claimCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<CSRDocuments> csrDocuments;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pool_id")
	private Pool pool;

	


	@PrePersist
	protected void onCreate() {
		this.createdTimestamp = LocalDateTime.now();
	}

	// --- Getters & Setters Updated for policyDetails ---
	
	public Pool getPool() {
	    return pool;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getAmountStatus() {
		return amountStatus;
	}

	public void setAmountStatus(String amountStatus) {
		this.amountStatus = amountStatus;
	}

	public String getRecommendedAction() {
		return recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignedInspector() {
		return assignedInspector;
	}

	public void setAssignedInspector(String assignedInspector) {
		this.assignedInspector = assignedInspector;
	}

	public String getAssignedReviewer() {
		return assignedReviewer;
	}

	public void setAssignedReviewer(String assignedReviewer) {
		this.assignedReviewer = assignedReviewer;
	}

	public String getInspectorReport() {
		return inspectorReport;
	}

	public void setInspectorReport(String inspectorReport) {
		this.inspectorReport = inspectorReport;
	}

	public String getReviewerReport() {
		return reviewerReport;
	}

	public void setReviewerReport(String reviewerReport) {
		this.reviewerReport = reviewerReport;
	}

	public String getApproverReport() {
		return approverReport;
	}

	public void setApproverReport(String approverReport) {
		this.approverReport = approverReport;
	}

	public String getAssignedApprover() {
		return assignedApprover;
	}

	public void setAssignedApprover(String assignedApprover) {
		this.assignedApprover = assignedApprover;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public void setPool(Pool pool) {
	    this.pool = pool;
	}
	public PolicyDetails getPolicyDetails() {
		return policyDetails;
	}

	public void setPolicyDetails(PolicyDetails policyDetails) {
		this.policyDetails = policyDetails;
	}
	// --- End of Update ---

	// Other getters and setters remain the same...
	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public List<CSRDocuments> getCsrDocuments() {
		return csrDocuments;
	}

	public void setCsrDocuments(List<CSRDocuments> csrDocuments) {
		this.csrDocuments = csrDocuments;
	}

	public String getDischargeSummary() {
		return dischargeSummary;
	}

	public void setDischargeSummary(String dischargeSummary) {
		this.dischargeSummary = dischargeSummary;
	}

	public String getValidationDetails() {
		return validationDetails;
	}

	public void setValidationDetails(String validationDetails) {
		this.validationDetails = validationDetails;
	}

	@Override
	public String toString() {
		return "ClaimCase [claimId=" + claimId + ", claimType=" + claimType + ", policyType=" + policyType
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", status=" + status
				+ ", assignedInspector=" + assignedInspector + ", assignedReviewer=" + assignedReviewer
				+ ", assignedApprover=" + assignedApprover + ", inspectorReport=" + inspectorReport
				+ ", reviewerReport=" + reviewerReport + ", approverReport=" + approverReport + ", dueDate=" + dueDate
				+ ", dischargeSummary=" + dischargeSummary + ", validationDetails=" + validationDetails
				+ ", recommendedAction=" + recommendedAction + ", amountStatus=" + amountStatus + ", policyDetails="
				+ policyDetails + ", csrDocuments=" + csrDocuments + ", pool=" + pool + "]";
	}

	

	
}
