package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.Checklist_Item;
import com.aaseya.AIS.dao.ChecklistItemDAO;
import com.aaseya.AIS.dto.CheckList_ItemDTO;
import com.aaseya.AIS.dto.addChecklistitemsDTO;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChecklistItemService {

	@Autowired
	private final ChecklistItemDAO checklistItemDAO;

	public Page<CheckList_ItemDTO> getChecklistItems(Pageable pageable) {
        Page<Checklist_Item> paginatedChecklistItems = checklistItemDAO.getAllChecklistItems(pageable);

        // Map the paginated entity list to DTOs
        return paginatedChecklistItems.map(item -> {
            CheckList_ItemDTO dto = new CheckList_ItemDTO();
            dto.setChecklist_id(item.getChecklist_id());
            dto.setChecklist_name(item.getChecklist_name());
            dto.setAnswer_type(item.getAnswer_type());
            dto.setIsActive(item.isActive());
            return dto;
        });
    }

	public Checklist_Item getChecklistDetails(long checklistId) {
		Checklist_Item checklistItem = checklistItemDAO.getChecklistItemById(checklistId);

		if (checklistItem != null && "Custom".equalsIgnoreCase(checklistItem.getAnswer_type())) {

			checklistItem
					.setSelected_corrective_action(List.of("Custom Option 1", "Custom Option 2", "Custom Option 3"));
		}

		return checklistItem;
	}
	 public ChecklistItemService(ChecklistItemDAO checklistItemDAO) {
	        this.checklistItemDAO = checklistItemDAO;
	    }

	 public Checklist_Item updateChecklistItem(Long checklistId, addChecklistitemsDTO checklistItemDTO) throws Exception {
	        Checklist_Item checklistItem = checklistItemDAO.getChecklistItemById(checklistId);

	        if (checklistItem != null) {
	            checklistItem.setChecklist_name(checklistItemDTO.getChecklistName());
	            checklistItem.setSeverity(checklistItemDTO.getSeverity());
	            checklistItem.setWeightage(checklistItemDTO.getWeightage());
	            checklistItem.setAnswer_type(checklistItemDTO.getAnswerType());
	            checklistItem.setPre_defined_answer_type(checklistItemDTO.getPredefinedAnswerTypes());

	            // Update mappings with corrective actions
	            checklistItemDAO.updateChecklistAndMappings(
	                checklistItem,
	                checklistItemDTO.getCorrectiveActions(),
	                checklistItemDTO.getNewCorrectiveActions()
	            );

	            return checklistItem;
	        } else {
	            throw new Exception("Checklist item not found with ID: " + checklistId);
	        }
	    }

	    public Checklist_Item saveChecklistItem(addChecklistitemsDTO checklistItemDTO) {
	        Checklist_Item checklistItem = new Checklist_Item();
	        checklistItem.setChecklist_name(checklistItemDTO.getChecklistName());
	        checklistItem.setSeverity(checklistItemDTO.getSeverity());
	        checklistItem.setWeightage(checklistItemDTO.getWeightage());
	        checklistItem.setAnswer_type(checklistItemDTO.getAnswerType());
	        checklistItem.setPre_defined_answer_type(checklistItemDTO.getPredefinedAnswerTypes());

	        // Save the new checklist item along with corrective actions
	        checklistItemDAO.saveOrUpdateChecklistItem(
	            checklistItem
	        );

	        return checklistItem;
	    }
	}