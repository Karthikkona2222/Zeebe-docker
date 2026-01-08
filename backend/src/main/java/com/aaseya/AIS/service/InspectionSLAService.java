package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dao.InspectionSLADAO;
import com.aaseya.AIS.dto.AISResponseDTO;
import com.aaseya.AIS.dto.InspectionTypeSLADTO;
import com.aaseya.AIS.dto.InspectionTypeSLADTO.SLAEntityDetails;
import com.aaseya.AIS.Model.Inspection_SLA;
import com.aaseya.AIS.Model.Inspection_Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InspectionSLAService {

    @Autowired
    private InspectionTypeDAO inspectionTypeDAO;

    @Autowired
    private InspectionSLADAO inspectionSLADAO;

    /**
     * Create or update Inspection SLA entries for an inspection type.
     *
     * @param inspectionTypeName inspection type name
     * @param entitySizes        map of entitySize -> SLA details
     * @param action             "save" | "edit" | "delete"
     * @return AISResponseDTO with status/message
     */
    public AISResponseDTO createInspectionSLA(String inspectionTypeName,
                                              Map<String, InspectionTypeSLADTO.SLAEntityDetails> entitySizes,
                                              String action) {
        AISResponseDTO response = new AISResponseDTO();

        Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeByName(inspectionTypeName);

        if (inspectionType == null) {
            response.setStatus("FAILURE");
            response.setMessage("Inspection Type not found.");
            return response;
        }

        if (entitySizes == null || entitySizes.isEmpty()) {
            response.setStatus("FAILURE");
            response.setMessage("No SLA entity sizes provided.");
            return response;
        }

        if ("save".equalsIgnoreCase(action)) {
            entitySizes.forEach((entitySize, slaDetails) -> {
                boolean exists = inspectionSLADAO.existsByInspectionTypeAndEntitySize(inspectionType, entitySize);
                if (!exists) {
                    Inspection_SLA inspectionSLA = createNewInspectionSLA(inspectionType, entitySize, slaDetails);
                    inspectionSLADAO.saveInspectionSLA(inspectionSLA);
                } else {
                    // optional: update existing if you want idempotent save behavior
                    // Inspection_SLA existing = inspectionSLADAO.findByInspectionTypeAndEntitySize(inspectionType, entitySize);
                    // updateInspectionSLA(existing, slaDetails);
                    // inspectionSLADAO.saveInspectionSLA(existing);
                }
            });
            response.setStatus("SUCCESS");
            response.setMessage("Inspection SLA saved successfully.");
            return response;
        } else if ("edit".equalsIgnoreCase(action)) {
            entitySizes.forEach((entitySize, slaDetails) -> {
                Inspection_SLA existingSLA = inspectionSLADAO.findByInspectionTypeAndEntitySize(inspectionType, entitySize);
                if (existingSLA != null) {
                    updateInspectionSLA(existingSLA, slaDetails);
                    inspectionSLADAO.saveInspectionSLA(existingSLA);
                } else {
                    // If edit requested but SLA not found, you can decide to create or skip.
                    // For now, log and skip.
                    System.err.println("No existing SLA found for entitySize=" + entitySize + " on inspectionType=" + inspectionTypeName);
                }
            });
            response.setStatus("SUCCESS");
            response.setMessage("Inspection SLA updated successfully.");
            return response;
        } else if ("delete".equalsIgnoreCase(action)) {
            // If delete functionality is required, implement here.
            response.setStatus("FAILURE");
            response.setMessage("Delete action not implemented.");
            return response;
        } else {
            response.setStatus("FAILURE");
            response.setMessage("Invalid action. Use 'save' or 'edit'.");
            return response;
        }
    }


    private Inspection_SLA createNewInspectionSLA(Inspection_Type inspectionType, String entitySize,
                                                  SLAEntityDetails slaDetails) {
        Inspection_SLA inspectionSLA = new Inspection_SLA();
        inspectionSLA.setInspectionType(inspectionType);
        inspectionSLA.setEntitySize(entitySize);
        updateInspectionSLA(inspectionSLA, slaDetails);
        return inspectionSLA;
    }

    private void updateInspectionSLA(Inspection_SLA inspectionSLA, SLAEntityDetails slaDetails) {
        if (slaDetails == null) return;
        inspectionSLA.setInspectorGoal(slaDetails.getInspectorGoal());
        inspectionSLA.setInspectorDeadline(slaDetails.getInspectorDeadline());
        inspectionSLA.setReviewerGoal(slaDetails.getReviewerGoal());
        inspectionSLA.setReviewerDeadline(slaDetails.getReviewerDeadline());
        inspectionSLA.setApproverGoal(slaDetails.getApproverGoal());
        inspectionSLA.setApproverDeadline(slaDetails.getApproverDeadline());
    }
}

