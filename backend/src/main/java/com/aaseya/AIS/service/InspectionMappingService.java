package com.aaseya.AIS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionMapping;
import com.aaseya.AIS.Model.InspectionMappingId;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionMappingDAO;
import com.aaseya.AIS.dto.AssignRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InspectionMappingService {

	@Autowired
	private InspectionMappingDAO inspectionMappingDAO;

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO; // DAO for InspectionCase validation

	// DAO for ChecklistCategory validation

	@Transactional
	public String assignInspection(Long inspectionID, String action, List<AssignRequestDTO> requestDTOList) {
		// Validate InspectionID
		Optional<InspectionCase> inspectionCaseOpt = inspectionCaseDAO.getInspectionCaseById(inspectionID);
		if (inspectionCaseOpt.isEmpty()) {
			return "Invalid Inspection ID!";
		}
		InspectionCase inspectionCase = inspectionCaseOpt.get();

		boolean hasNewMappings = false; // Track if any new mapping is added

		for (AssignRequestDTO requestDTO : requestDTOList) {
			InspectionMappingId mappingId = new InspectionMappingId(inspectionID, requestDTO.getChecklistCatId());

			// Save new Inspection Mapping
			InspectionMapping mapping = new InspectionMapping();
			mapping.setId(mappingId); // Set composite key
			mapping.setInspectorID(requestDTO.getAssignTo()); // Set inspector ID

			inspectionMappingDAO.saveInspectionMapping(mapping);
			hasNewMappings = true;
		}

		// Update assignedCategory field based on action
		boolean assignedCategory = action.equalsIgnoreCase("SUBMIT");
		inspectionCase.setAssignedCategory(assignedCategory);
		inspectionCaseDAO.updatingInspectionCase(inspectionCase);

		// Ensure return statement is placed outside the loop
		if (!hasNewMappings) {
			return "No new assignments were made. All mappings already exist.";
		}

		return "Inspection Mapping " + (assignedCategory ? "Submitted" : "Saved") + " Successfully!";
	}
	
	//Save inpectionID, CategoryID, inspectorID in InspectionMapping
	
		@Transactional
	    public void savesInspectionMapping(Long inspectionId, String inspectorId) {
	        // Fetch templateId linked to the inspectionId
	        Long templateId = inspectionMappingDAO.getTemplateIdByInspectionId(inspectionId);
	        if (templateId == null) {
	            throw new RuntimeException("No template found for inspection ID: " + inspectionId);
	        }

	        // Fetch checklist category IDs linked to the templateId
	        List<Long> checklistCatIds = inspectionMappingDAO.getChecklistCategoryIdsByTemplateId(templateId);
	        if (checklistCatIds.isEmpty()) {
	            throw new RuntimeException("No checklist categories found for template ID: " + templateId);
	        }

	        // Prepare InspectionMapping records
	        List<InspectionMapping> mappings = new ArrayList<>();
	        for (Long checklistCatId : checklistCatIds) {
	            InspectionMappingId mappingId = new InspectionMappingId(inspectionId, checklistCatId);
	            InspectionMapping mapping = new InspectionMapping(mappingId, inspectorId);
	            mappings.add(mapping);
	        }

	        // Save all mappings
	        inspectionMappingDAO.updateInspectionMapping(mappings);
	    }
}