package com.aaseya.AIS.dto;

import java.time.LocalDate;

public class IDPSummaryResponseDTO {

    private String entityName;
    private String equipmentType;
    private String equipmentId;
    private Integer riskScore;
    private String riskLevel;
    private LocalDate lastInspectionDate;
    private String inspectionType;
    private long inspectionID;
    private String inspectorID;
    private String observations;
    private String recomendations;
    private String inspectorSource;
    

    public IDPSummaryResponseDTO(long inspectionID, String entityName, String equipmentType, String equipmentId,
                                 Integer riskScore, String riskLevel, LocalDate lastInspectionDate, String inspectionType,
                                 String inspectorID, String observations, String recomandations, String inspectorSource ) {
        this.inspectionID = inspectionID;
    	this.entityName = entityName;
        this.equipmentType = equipmentType;
        this.equipmentId = equipmentId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.lastInspectionDate = lastInspectionDate;
        this.inspectionType = inspectionType;
        this.inspectorID = inspectorID;
        this.observations = observations;
        this.recomendations = recomendations;
        this.inspectorSource = inspectorSource;
    }

    public long getinspectionID() {return inspectionID;}
    public String getEntityName() { return entityName; }
    public String getEquipmentType() { return equipmentType; }
    public String getEquipmentId() { return equipmentId; }
    public Integer getRiskScore() { return riskScore; }
    public String getRiskLevel() { return riskLevel; }
    public LocalDate getLastInspectionDate() {return lastInspectionDate; }
    public String getInspectionType() { return inspectionType; }
    public String getInspectorID() {return inspectorID; }
    public String getObservations() {return observations; }
    public String getRecomendations() {return recomendations; }
    public String getInspectorSource() { return inspectorSource; }
}
