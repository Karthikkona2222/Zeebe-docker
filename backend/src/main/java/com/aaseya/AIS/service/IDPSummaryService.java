package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.*;
import com.aaseya.AIS.dao.*;
import com.aaseya.AIS.dto.IDPSummaryResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IDPSummaryService {
	
	@Autowired
    private IDPSummaryDAO dao;

    private final IDPSummaryDAO summaryDAO;
    private final InspectionTypeDAO inspectionTypeDAO;
    private final EntityDAO entityDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IDPSummaryService(IDPSummaryDAO summaryDAO,
                             InspectionTypeDAO inspectionTypeDAO,
                             EntityDAO entityDAO) {

        this.summaryDAO = summaryDAO;
        this.inspectionTypeDAO = inspectionTypeDAO;
        this.entityDAO = entityDAO;
    }

    public IDPSummary saveSummary(Map<String, Object> json) {
        IDPSummary summary = new IDPSummary();

        // ---------------------------------------------------------
        // RELATION MAPPING
        // ---------------------------------------------------------
        String inspectionTypeName = (String) json.get("inspectionType");
        Inspection_Type ins = inspectionTypeDAO.getInspectionTypesByName(inspectionTypeName);
        summary.setInspectionType(ins);

        String entityName = (String) json.get("entityName");
        NewEntity entity = entityDAO.getEntityByName(entityName);
        summary.setEntity(entity);

        // ---------------------------------------------------------
        // SIMPLE FIELDS
        // ---------------------------------------------------------
        summary.setEntityId((String) json.get("entityId"));
        summary.setIsNewEntity((Boolean) json.get("isNewEntity"));
        summary.setLocation((String) json.get("location"));
        summary.setEquipmentId((String) json.get("equipmentId"));
        summary.setEquipmentType((String) json.get("equipmentType"));

        summary.setSeverity(toInt(json.get("Severity")));
        summary.setLikelihood(toInt(json.get("Likelihood")));
        summary.setComplianceGap(toInt(json.get("complianceGap")));
        summary.setHistoricalRisk(toInt(json.get("historicalRisk")));

        summary.setCertificateNumber((String) json.get("certificateNumber"));
        summary.setRemarksOrDefectsFound((String) json.get("remarksOrDefectsFound"));

        if (json.get("lastInspectionDate") != null) {
            summary.setLastInspectionDate(LocalDate.parse(json.get("lastInspectionDate").toString()));
        }

        summary.setRiskScore(toInt(json.get("riskScore")));
        summary.setRiskLevel((String) json.get("riskLevel"));
        summary.setProcessInstanceKey((Long) json.get("processInstanceKey"));

        summary.setStatus((String) json.get("Status"));
        summary.setOutputStatus((String) json.get("outputStatus"));

        // save full JSON as text
        try {
            summary.setMergedJson(objectMapper.writeValueAsString(json));
        } catch (Exception e) {
            summary.setMergedJson("{}");
        }

        // SAVE USING DAO
        return summaryDAO.save(summary);
    }

    private int toInt(Object obj) {
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return 0;
        }
    }
    
   
    
}
