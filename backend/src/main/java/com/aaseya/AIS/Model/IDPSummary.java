package com.aaseya.AIS.Model;
 
import jakarta.persistence.*;
import java.time.LocalDate;
 
import com.fasterxml.jackson.annotation.JsonFormat;
 
@Entity
@Table(name = "IDPSummary")
public class IDPSummary {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    /* ----------------------------------------------------------
       RELATION WITH Inspection_Type USING name
       ---------------------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspectionType", referencedColumnName = "name")
    private Inspection_Type inspectionType;
 
    /* ----------------------------------------------------------
       RELATION WITH NewEntity USING entity_name
       ---------------------------------------------------------- */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entityName", referencedColumnName = "entity_name")
    private NewEntity entity;
 
    @Column(name = "entityId")
    private String entityId;
 
    /* ----------------------------------------------------------
       Existing Zeebe process ID (must NOT be touched)
       ---------------------------------------------------------- */
    @Column(name = "processInstanceKey")
    private Long processInstanceKey;
 
    /* ----------------------------------------------------------
       NEW COLUMN: inspectionID (FK to InspectionCase)
       ---------------------------------------------------------- */
    @Column(name = "inspectionID")
    private Long inspectionID;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "inspectionID",
        referencedColumnName = "inspectionID",
        insertable = false,
        updatable = false
    )
    private InspectionCase inspectionCase;
 
    public Long getInspectionID() {
        return inspectionID;
    }
 
    public void setInspectionID(Long inspectionID) {
        this.inspectionID = inspectionID;
    }
 
    public InspectionCase getInspectionCase() {
        return inspectionCase;
    }
 
    public void setInspectionCase(InspectionCase inspectionCase) {
        this.inspectionCase = inspectionCase;
    }
 
    /* ----------------------------------------------------------
       Existing fields (unchanged)
       ---------------------------------------------------------- */
 
    @Column(name = "isNewEntity")
    private Boolean isNewEntity = Boolean.FALSE;
 
    @Column(name = "location")
    private String location;
 
    @Column(name = "equipmentId")
    private String equipmentId;
 
    @Column(name = "equipmentType")
    private String equipmentType;
 
    @Column(name = "Severity")
    private Integer severity = 0;
 
    @Column(name = "Likelihood")
    private Integer likelihood = 0;
 
    @Column(name = "complianceGap")
    private Integer complianceGap = 0;
 
    @Column(name = "historicalRisk")
    private Integer historicalRisk = 0;
 
    @Column(name = "certificateNumber")
    private String certificateNumber;
 
    @Column(name = "remarksOrDefectsFound", columnDefinition = "text")
    private String remarksOrDefectsFound;
 
    @Column(name = "lastInspectionDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastInspectionDate;
 
    @Column(name = "riskScore")
    private Integer riskScore;
 
    @Column(name = "riskLevel")
    private String riskLevel;
 
    @Column(name = "Status")
    private String status;
 
    @Column(name = "outputStatus")
    private String outputStatus;
 
    @Column(name = "mergedJson", columnDefinition = "text")
    private String mergedJson;
 
    /* ----------------------------------------------------------
       Standard Getters / Setters
       ---------------------------------------------------------- */
 
    public Long getId() {
        return id;
    }
 
    public Inspection_Type getInspectionType() {
        return inspectionType;
    }
 
    public void setInspectionType(Inspection_Type inspectionType) {
        this.inspectionType = inspectionType;
    }
 
    public NewEntity getEntity() {
        return entity;
    }
 
    public void setEntity(NewEntity entity) {
        this.entity = entity;
    }
 
    public String getEntityId() {
        return entityId;
    }
 
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
 
    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }
 
    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }
 
    public Boolean getIsNewEntity() {
        return isNewEntity;
    }
 
    public void setIsNewEntity(Boolean isNewEntity) {
        this.isNewEntity = isNewEntity;
    }
 
    public String getLocation() {
        return location;
    }
 
    public void setLocation(String location) {
        this.location = location;
    }
 
    public String getEquipmentId() {
        return equipmentId;
    }
 
    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
 
    public String getEquipmentType() {
        return equipmentType;
    }
 
    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }
 
    public Integer getSeverity() {
        return severity;
    }
 
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }
 
    public Integer getLikelihood() {
        return likelihood;
    }
 
    public void setLikelihood(Integer likelihood) {
        this.likelihood = likelihood;
    }
 
    public Integer getComplianceGap() {
        return complianceGap;
    }
 
    public void setComplianceGap(Integer complianceGap) {
        this.complianceGap = complianceGap;
    }
 
    public Integer getHistoricalRisk() {
        return historicalRisk;
    }
 
    public void setHistoricalRisk(Integer historicalRisk) {
        this.historicalRisk = historicalRisk;
    }
 
    public String getCertificateNumber() {
        return certificateNumber;
    }
 
    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }
 
    public String getRemarksOrDefectsFound() {
        return remarksOrDefectsFound;
    }
 
    public void setRemarksOrDefectsFound(String remarksOrDefectsFound) {
        this.remarksOrDefectsFound = remarksOrDefectsFound;
    }
 
    public LocalDate getLastInspectionDate() {
        return lastInspectionDate;
    }
 
    public void setLastInspectionDate(LocalDate lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }
 
    public Integer getRiskScore() {
        return riskScore;
    }
 
    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }
 
    public String getRiskLevel() {
        return riskLevel;
    }
 
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public String getOutputStatus() {
        return outputStatus;
    }
 
    public void setOutputStatus(String outputStatus) {
        this.outputStatus = outputStatus;
    }
 
    public String getMergedJson() {
        return mergedJson;
    }
 
    public void setMergedJson(String mergedJson) {
        this.mergedJson = mergedJson;
    }
 
    @Transient
    public String getInspectionTypeName() {
        return inspectionType != null ? inspectionType.getName() : null;
    }
 
    @Transient
    public String getEntityName() {
        return entity != null ? entity.getName() : null;
    }
}
 