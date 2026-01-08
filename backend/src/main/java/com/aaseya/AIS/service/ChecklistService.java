package com.aaseya.AIS.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.Model.Checklist_Item;
import com.aaseya.AIS.Model.CorrectiveAction;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.PreInspectionChecklist;
import com.aaseya.AIS.Model.SaveSubmitPreInspectionChecklist;
import com.aaseya.AIS.Model.SaveSubmitPreInspectionChecklist.SaveSubmitPreInspectionId;
import com.aaseya.AIS.dao.ChecklistDAO;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.SaveSubmitPreInspectionChecklistDAO;
import com.aaseya.AIS.dto.ChecklistCategoryDTO;
import com.aaseya.AIS.dto.ChecklistDTO;
import com.aaseya.AIS.dto.SavedPreInspectionDTO;
import com.aaseya.AIS.dto.addChecklistitemsDTO;

import jakarta.transaction.Transactional;

@Service
public class ChecklistService {

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;

	@Autowired
	private ChecklistDAO checklistDAO;

	@Autowired
	private PreInspectionChecklistService preInspectionChecklistService;

	@Autowired
	private SaveSubmitPreInspectionChecklistDAO saveSubmitPreInspectionChecklistDAO;

	public List<ChecklistDTO> getInspectionChecklist(final String inspectionId) {
		List<ChecklistDTO> checklist = new ArrayList<>();

		long templateId = inspectionCaseDAO.getTemplateId(inspectionId);

		List<Checklist_Category> checklistCategories = checklistDAO.getChecklistCategoryForTemplate(templateId);
		System.out.println("ChecklistCategories : " + checklistCategories);
		for (Checklist_Category checklist_category : checklistCategories) {
			ChecklistDTO checklistDTO = new ChecklistDTO();

			checklistDTO.setCategoryId(checklist_category.getChecklist_cat_id());
			checklistDTO.setCategoryName(checklist_category.getChecklist_category_name());
			checklistDTO.setChecklistItem(checklist_category.getChecklist_items());
			System.out.println("Checklist : " + checklist_category.getChecklist_items());
			checklist.add(checklistDTO);
		}

		return checklist;
	}

	public SaveSubmitPreInspectionChecklist saveChecklist(SaveSubmitPreInspectionChecklist dto) {

		return saveSubmitPreInspectionChecklistDAO.saveOrUpdate(dto);
	}

	public SavedPreInspectionDTO getChecklistsByInspectionId(long inspectionid) {
		SavedPreInspectionDTO savedPreInspectionDTO = new SavedPreInspectionDTO();
		Set<PreInspectionChecklist> preInspectionChecklists = preInspectionChecklistService
				.getPreInspectionChecklistDTO(inspectionid).getPreInspectionChecklists();

		List<SaveSubmitPreInspectionChecklist> savedChecklists = saveSubmitPreInspectionChecklistDAO
				.getByInspectionId(inspectionid);

		for (PreInspectionChecklist checklist : preInspectionChecklists) {
			SaveSubmitPreInspectionId id = new SaveSubmitPreInspectionId();
			id.setInspectionid(inspectionid);
			id.setPreinspectionchecklistid(checklist.getId());
			SaveSubmitPreInspectionChecklist saveSubmitPreInspectionChecklist = savedChecklists.stream()
					.filter(checklistItem -> checklistItem.getId().equals(id)).findFirst().orElseGet(() -> null);
			if (saveSubmitPreInspectionChecklist != null)
				checklist.setSelected_answer(saveSubmitPreInspectionChecklist.getSelected_answer());
		}
		savedPreInspectionDTO.setPreInspectionChecklist(preInspectionChecklists);
		InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionid);
		savedPreInspectionDTO.setIs_PreInspection(
				inspectionCase.getIs_preinspection() != null ? inspectionCase.getIs_preinspection() : null);
		savedPreInspectionDTO.setIs_PreInspection_Submitted(
				inspectionCase.getIs_preinspection_submitted() != null ? inspectionCase.getIs_preinspection_submitted()
						: null);

		return savedPreInspectionDTO;
	}

	@Transactional
	public void addCheckListItems(Checklist_Item checklist_Item) {
		if (checklist_Item != null) {
			// Take only the first item from the checklistItem list

			// Create a new Checklist_Item object to persist

			checklist_Item.setActive(true); // Set as active by default

			// Now associate corrective actions
			List<String> correctiveActions = checklist_Item.getSelected_corrective_action(); // Get corrective actions
																								// from DTO
			if (correctiveActions != null && !correctiveActions.isEmpty()) {
				Set<CorrectiveAction> correctiveActionSet = new HashSet<>();

				// Creating corrective action objects and adding them to the set
				for (String action : correctiveActions) {
					CorrectiveAction correctiveAction = new CorrectiveAction();
					correctiveAction.setCorrective_action_name(action);
					correctiveAction.setChecklist_items(new HashSet<>() {
						{
							add(checklist_Item);
						}
					});
					// Add to the corrective actions set
					correctiveActionSet.add(correctiveAction);
				}

				// Associate the corrective actions with the checklist item
				checklist_Item.setCorrectiveactions(correctiveActionSet); // Associate corrective actions
			}

			// Save the checklist item with corrective actions in one call
			checklistDAO.saveCheckList_Item(checklist_Item); // Save checklist item, cascade will save corrective
																// actions
		}

		// Optionally log or debug
		System.out.println("Checklist_Item: " + checklist_Item.toString());
	}

	@Transactional
	public void addChecklistCategory(ChecklistCategoryDTO checklistCategoryDTO) throws Exception {
		if (checklistCategoryDTO != null) {
			// Step 1: Check if the Checklist_Category already exists by name
			Checklist_Category existingCategory = checklistDAO
					.getChecklistCategoryByName(checklistCategoryDTO.getChecklist_category_name());

			if (existingCategory != null) {
				// Category already exists, you can either update it or return a message, etc.
				System.out.println("Category with name '" + checklistCategoryDTO.getChecklist_category_name()
						+ "' already exists.");
				
				throw new Exception("Category with name '" + checklistCategoryDTO.getChecklist_category_name()
				+ "' already exists.");
			} else {
				// Step 2: Create a new Checklist_Category and associate the checklist items
				Checklist_Category checklistCategory = new Checklist_Category();
				checklistCategory.setChecklist_category_name(checklistCategoryDTO.getChecklist_category_name());
				checklistCategory.setCategory_threshold_local(checklistCategoryDTO.getCategory_threshold_local());
				checklistCategory.setCategory_weightage_local(checklistCategoryDTO.getCategory_weightage_local());
				checklistCategory.setIsActive(true); 
				// Retrieve Checklist_Items using the provided checklist_ids
				List<Checklist_Item> checklistItems = checklistCategoryDTO.getChecklist_ids().stream()
						.map(id -> checklistDAO.getCheckListItemById(id)) // Assuming checklistItemDAO has a
																			// getCheckListItemById method
						.filter(item -> item != null) // Avoid null values if item not found
						.collect(Collectors.toList());

				// Associate the retrieved Checklist_Items with the Checklist_Category
				checklistCategory.setChecklist_items(checklistItems);

				// Save the Checklist_Category (this will cascade and save Checklist_Items if
				// CascadeType.PERSIST is configured)
				checklistDAO.saveCheckListCategory(checklistCategory);
			}

		}
	}

	@Transactional
	public List<addChecklistitemsDTO> getAllChecklistItems() {
		List<Checklist_Item> checklistItems = checklistDAO.getAllChecklistItems();

		return checklistItems.stream().map(item -> {
			addChecklistitemsDTO dto = new addChecklistitemsDTO();
			dto.setId(item.getChecklist_id());
			dto.setChecklistName(item.getChecklist_name());
			dto.setSeverity(item.getSeverity());
			dto.setWeightage(item.getWeightage());
			dto.setAnswerType(item.getAnswer_type());

			if ("Pre-defined".equalsIgnoreCase(item.getAnswer_type())) {
				dto.setPredefinedAnswerTypes(item.getPre_defined_answer_type());
			} else {
				dto.setPredefinedAnswerTypes(null);
			}

			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	public Checklist_Item getChecklistItemById(long checklistId) {
		return checklistDAO.getChecklistItemById(checklistId);
	}
	
	// Get the assigned inspector with checklist

		@Transactional
		public Object getChecklistByInspectionID(Long inspectionID, String inspectorID) {
		    List<Object[]> results = checklistDAO.getChecklistCategoriesAndItems(inspectionID, inspectorID);
		    Map<Long, ChecklistDTO> checklistDTOMap = new HashMap<>();

		    for (Object[] result : results) {
		        Long categoryId = (Long) result[0];
		        String categoryName = (String) result[1];
		        Checklist_Item checklistItem = (Checklist_Item) result[2];
		        String inspector = (String) result[3];

		        // Ensure we avoid duplicate categories
		        ChecklistDTO dto = checklistDTOMap.computeIfAbsent(categoryId, key -> {
		            ChecklistDTO newDTO = new ChecklistDTO();
		            newDTO.setCategoryId(categoryId);
		            newDTO.setCategoryName(categoryName);
		            newDTO.setInspectorID(inspector);
		            newDTO.setChecklistItem(new ArrayList<>()); // Initialize list
		            return newDTO;
		        });

		        // Add checklist item only if not null
		        if (checklistItem != null) {
		            dto.getChecklistItem().add(checklistItem);
		        }
		    }

		    // Return an empty list if no records are found instead of an error message
		    return new ArrayList<>(checklistDTOMap.values());
		}

		 
		//Get the Checklists based on inspectorId
		 @Transactional
		    public List<ChecklistDTO> getChecklistByInspectorID(String inspectorID) {
		        List<Object[]> results = checklistDAO.getChecklistByInspectorID(inspectorID);

		        // Map to store categoryId as key and ChecklistDTO as value
		        Map<Long, ChecklistDTO> categoryMap = new HashMap<>();

		        for (Object[] result : results) {
		            String inspectionID = String.valueOf(result[0]); // Convert to String
		            Long categoryId = (Long) result[1];
		            String categoryName = (String) result[2];
		            Checklist_Item checklistItem = (Checklist_Item) result[3];

		            // Get or create the ChecklistDTO
		            ChecklistDTO checklistDTO = categoryMap.computeIfAbsent(categoryId, key -> {
		                ChecklistDTO newChecklist = new ChecklistDTO();
		                newChecklist.setCategoryId(categoryId);
		                newChecklist.setCategoryName(categoryName);
		                newChecklist.setInspectorID(inspectorID);
		                newChecklist.setChecklistItem(new ArrayList<>());
		                newChecklist.setInspectionids(new ArrayList<>());
		                return newChecklist;
		            });

		            // Add unique inspection ID
		            if (!checklistDTO.getInspectionids().contains(inspectionID)) {
		                checklistDTO.getInspectionids().add(inspectionID);
		            }

		            // Add checklist item if not null
		            if (checklistItem != null) {
		                checklistDTO.getChecklistItem().add(checklistItem);
		            }
		        }

		        return new ArrayList<>(categoryMap.values());
		    }
}
