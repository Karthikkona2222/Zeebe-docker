package com.aaseya.AIS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Template;
import com.aaseya.AIS.dao.TempCheckInspDAO;
import com.aaseya.AIS.dto.TempCheckInspDTO;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class TempCheckInspService {

    @Autowired
    private TempCheckInspDAO templateDAO;

    // Add template if action is 'add'
    public void addTemplate(TempCheckInspDTO tempCheckInspDTO) {
        // Check if a template with the same name already exists
        Template existingTemplate = templateDAO.getTemplateByName(tempCheckInspDTO.getTemplate_name());
        if (existingTemplate != null) {
            throw new RuntimeException("Template with name '" + tempCheckInspDTO.getTemplate_name() + "' already exists. No duplicates allowed.");
        }

        // Create a new template
        Template newTemplate = new Template();
        newTemplate.setTemplate_name(tempCheckInspDTO.getTemplate_name());
        newTemplate.setVersion(tempCheckInspDTO.getVersion()); // Set version
        newTemplate.setActive(tempCheckInspDTO.isActive()); // Set active status

        // Fetch and set the inspection type
        Inspection_Type inspectionType = templateDAO.getInspectionTypeById(tempCheckInspDTO.getIns_type_id());
        if (inspectionType == null) {
            throw new RuntimeException("Inspection Type not found with ID: " + tempCheckInspDTO.getIns_type_id());
        }
        newTemplate.getInspection_types().add(inspectionType);

        // Fetch and set checklist categories
        List<Checklist_Category> checklistCategories = new ArrayList<>();
        for (Long checklistId : tempCheckInspDTO.getChecklist_ids()) {
            Checklist_Category checklist = templateDAO.getChecklistById(checklistId);
            if (checklist == null) {
                throw new RuntimeException("Checklist Category not found with ID: " + checklistId);
            }
            checklistCategories.add(checklist);
        }
        newTemplate.setChecklist_categorys(checklistCategories);

        // Save the new template
        templateDAO.saveTemplate(newTemplate);
    }

    // Edit template if action is 'edit'
    public void editTemplate(TempCheckInspDTO tempCheckInspDTO) {
        // Fetch the existing template by ID
        Template existingTemplate = templateDAO.getTemplateById(tempCheckInspDTO.getTemplate_id());
        if (existingTemplate == null) {
            throw new RuntimeException("Template with ID " + tempCheckInspDTO.getTemplate_id() + " not found.");
        }

        // Update the template name if changed
        if (!existingTemplate.getTemplate_name().equals(tempCheckInspDTO.getTemplate_name())) {
            // Check if the new template name already exists
            Template duplicateTemplate = templateDAO.getTemplateByName(tempCheckInspDTO.getTemplate_name());
            if (duplicateTemplate != null) {
                throw new RuntimeException("Template name '" + tempCheckInspDTO.getTemplate_name() + "' already exists.");
            }
            existingTemplate.setTemplate_name(tempCheckInspDTO.getTemplate_name());
        }

        // Update inspection type if necessary
        if (tempCheckInspDTO.getIns_type_id() > 0) {
            Inspection_Type newInspectionType = templateDAO.getInspectionTypeById(tempCheckInspDTO.getIns_type_id());
            if (newInspectionType == null) {
                throw new RuntimeException("Inspection Type not found with ID: " + tempCheckInspDTO.getIns_type_id());
            }
            existingTemplate.getInspection_types().clear(); // Clear old inspection types
            existingTemplate.getInspection_types().add(newInspectionType); // Set new inspection type
        }

        // Update checklist categories
        List<Checklist_Category> checklistCategories = new ArrayList<>();
        for (Long checklistId : tempCheckInspDTO.getChecklist_ids()) {
            Checklist_Category checklist = templateDAO.getChecklistById(checklistId);
            if (checklist == null) {
                throw new RuntimeException("Checklist Category not found with ID: " + checklistId);
            }
            checklistCategories.add(checklist);
        }
        existingTemplate.setChecklist_categorys(checklistCategories);

        // Update other details like version and active status
        existingTemplate.setVersion(tempCheckInspDTO.getVersion());
        existingTemplate.setActive(tempCheckInspDTO.isActive());

        // Save the updated template
        templateDAO.saveTemplate(existingTemplate);
    }
}
