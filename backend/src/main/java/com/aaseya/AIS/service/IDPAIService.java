package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.IDPAISummaryDAO;
import com.aaseya.AIS.dto.IDPSummaryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IDPAIService {

    @Autowired
    private IDPAISummaryDAO dao;

    public List<IDPSummaryResponseDTO> search(
            String entityName,
            String inspectionType,
            Long inspectionID,
            String source
    ) {

    	 // 1. Fetch by inspectionID
        if (inspectionID != null) {
            return dao.findByInspectionID(inspectionID);
        }

        // 2. If no params passed, default to source = "ADP"
        if (entityName == null && inspectionType == null && source == null) {
            return dao.findByEntityInspectionSource(null, null, "ADP");
        }

        // 3. If user passed filters
        return dao.findByEntityInspectionSource(entityName, inspectionType, source);
    }
}
