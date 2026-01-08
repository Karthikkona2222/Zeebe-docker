package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.HealthCareChecklistCategory;
import com.aaseya.AIS.Model.HealthCareChecklistItem;
import com.aaseya.AIS.Model.HealthCarePreInspectionChecklist;
import com.aaseya.AIS.dao.HealthCareChecklistCategoryDAO;
import com.aaseya.AIS.dao.HealthCareChecklistItemDAO;
import com.aaseya.AIS.dao.HealthCarePreInspectionChecklistDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HealthCareChecklistService {

    @Autowired
    private HealthCareChecklistCategoryDAO categoryDAO;

    @Autowired
    private HealthCareChecklistItemDAO itemDAO;

    @Autowired
    private HealthCarePreInspectionChecklistDAO preInspectionDAO;

    // Categories
    public List<HealthCareChecklistCategory> getAllCategories() {
        return categoryDAO.findAll();
    }

    public HealthCareChecklistCategory getCategoryById(Long categoryId) {
        return categoryDAO.findById(categoryId);
    }

    // Items by category
    public List<HealthCareChecklistItem> getItemsByCategoryId(Long categoryId) {
        return itemDAO.findByCategoryId(categoryId);
    }

    // Pre Inspection Checklist items
    public List<HealthCarePreInspectionChecklist> getAllPreInspectionItems() {
        return preInspectionDAO.findAll();
    }
    
    public List<Object> getAllChecklistItems() {
        List<Object> allItems = new ArrayList<>();
        allItems.addAll(itemDAO.findAll()); // All healthcare items
//        allItems.addAll(healthCarePreInspectionChecklistDAO.findAll()); // All pre-inspection items
        return allItems;
    }
    
}
