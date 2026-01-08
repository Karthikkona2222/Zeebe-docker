package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.InspectionEscalation;
import com.aaseya.AIS.dao.InspectionEscalationDAO;
import com.aaseya.AIS.dto.InspectionEscalationDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionEscalationService {

    @Autowired
    private InspectionEscalationDAO inspectionEscalationDAO;

    /**
     * Save an InspectionEscalation record along with associated users.
     * 
     * @param inspectionId       The ID of the inspection related to the escalation.
     * @param escalationMessage   The message for the escalation.
     * @param userIds            List of user IDs associated with this escalation.
     */
    public void saveInspectionEscalation(InspectionEscalationDTO requestDTO) {
        inspectionEscalationDAO.saveInspectionEscalation(requestDTO);
    }

}