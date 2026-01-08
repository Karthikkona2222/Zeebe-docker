package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.dao.ChecklistCategoryDAO;
import com.aaseya.AIS.dto.ChecklistCategoriesDTO;
import com.aaseya.AIS.dto.ChecklistCategoryDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChecklistCategoryService {

    @Autowired
    private ChecklistCategoryDAO checklistCategoryDAO;
    public Page<ChecklistCategoriesDTO> getAllChecklistCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated data from DAO
        Page<Checklist_Category> categories = checklistCategoryDAO.getAllChecklistCategories(pageable);

        // Map entities to DTOs and return as a Page
        return categories.map(category -> new ChecklistCategoriesDTO(
                category.getChecklist_cat_id(),
                category.getChecklist_category_name(),
                category.getIsActive()
        ));
    }


    
  //Get category data for edit category
    public ChecklistCategoryDTO getChecklistCategoryById(long checklist_cat_id) throws Exception {
        return checklistCategoryDAO.getChecklistCategoryById(checklist_cat_id);
    }
    
  //Edit the checklist category
    public void updateChecklistCategory(long categoryId, String categoryName, int threshold, int weightage, List<Long> checklistIds) {
        checklistCategoryDAO.updateChecklistCategoryWithItems(categoryId, categoryName, threshold, weightage, checklistIds);
    }
}