package com.aaseya.AIS.dao;
 
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
 
import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.dto.ChecklistCategoryDTO;
import com.aaseya.AIS.dto.ChecklistItemDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;
import com.aaseya.AIS.Model.Checklist_Item;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;


 
@Repository
public class ChecklistCategoryDAO {
	
	@Autowired
    private SessionFactory sessionFactory;
 
    @PersistenceContext
    private final EntityManager entityManager;
    
    @Autowired
	private ChecklistDAO checklistDAO;
    
    private final CriteriaPaginationUtil paginationUtil ;
	@Autowired
	public ChecklistCategoryDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.paginationUtil = paginationUtil;
	}
 
    @Transactional
	public Page<Checklist_Category> getAllChecklistCategories(Pageable pageable) {
	    // Create CriteriaQuery for Checklist_Category
	    CriteriaQuery<Checklist_Category> query = entityManager.getCriteriaBuilder().createQuery(Checklist_Category.class);
	   // Root<Checklist_Category> root = query.from(Checklist_Category.class);
	   // query.select(root);  // Ensure that we are selecting from the root entity

	    // Use the pagination utility to fetch paginated results
	    return paginationUtil.getPaginatedData(
	            query,
	            pageable,
	            Checklist_Category.class,
	            entity -> entity // Just return the entity itself without converting to DTO
	    );
	}
    
    public ChecklistCategoryDTO getChecklistCategoryById(long checklist_cat_id) throws Exception {
        // Open a session from the SessionFactory
        Session session = sessionFactory.openSession();

        // Create CriteriaBuilder and CriteriaQuery
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Checklist_Category> criteria = builder.createQuery(Checklist_Category.class);

        // Root query from Checklist_Category entity
        Root<Checklist_Category> root = criteria.from(Checklist_Category.class);

        // Add the condition for checklist_cat_id
        criteria.select(root).where(builder.equal(root.get("checklist_cat_id"), checklist_cat_id));

        // Execute the query
        Checklist_Category checklistCategory = session.createQuery(criteria).uniqueResult();

        // Close the session to prevent memory leaks
        session.close();

        if (checklistCategory == null) {
            throw new Exception("Checklist Category not found for this id: " + checklist_cat_id);
        }

        // Mapping to DTO
        ChecklistCategoryDTO dto = new ChecklistCategoryDTO();
        dto.setChecklist_cat_id(checklistCategory.getChecklist_cat_id());
        dto.setChecklist_category_name(checklistCategory.getChecklist_category_name());
        dto.setCategory_weightage_local(checklistCategory.getCategory_weightage_local());
        dto.setCategory_threshold_local(checklistCategory.getCategory_threshold_local());

        // Lazy loading checklist items
        if (checklistCategory.getChecklist_items() != null) {
            // Map Checklist_Item entities to DTOs
            List<ChecklistItemDTO> checklistItems = checklistCategory.getChecklist_items().stream().map(item -> {
                ChecklistItemDTO itemDTO = new ChecklistItemDTO();
                itemDTO.setChecklist_id(item.getChecklist_id());
                itemDTO.setChecklist_name(item.getChecklist_name());
                itemDTO.setAnswer_type(item.getAnswer_type());
                //itemDTO.setWeightage(item.getWeightage());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setChecklist_items(checklistItems);
        }

        return dto;
    }
    
  //Edit the checklist category
    public void updateChecklistCategoryWithItems(long categoryId, String categoryName, int threshold, int weightage, List<Long> checklistIds) {
        Session session = sessionFactory.openSession();
        
        // Start a transaction
        session.beginTransaction();
        
        try {
            // Fetch the existing Checklist_Category
            Checklist_Category category = session.get(Checklist_Category.class, categoryId);
            if (category == null) {
                throw new EntityNotFoundException("Checklist_Category not found with id: " + categoryId);
            }
			/*
			 * Checklist_Category existingCategory =
			 * checklistDAO.getChecklistCategoryByName(categoryName);
			 * 
			 * if (existingCategory != null) { // Category already exists, you can either
			 * update it or return a message, etc. System.out.println("Category with name '"
			 * + categoryName + "' already exists.");
			 * 
			 * throw new Exception("Category with name '" + categoryName +
			 * "' already exists."); }
			 */
            // Update Checklist_Category fields
            category.setChecklist_category_name(categoryName);
            category.setCategory_threshold_local(threshold);
            category.setCategory_weightage_local(weightage);

            // Use CriteriaBuilder to fetch the Checklist_Items based on checklistIds
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Checklist_Item> query = builder.createQuery(Checklist_Item.class);
            Root<Checklist_Item> root = query.from(Checklist_Item.class);
            
            // Add the where clause to filter by checklistIds
            query.select(root).where(root.get("checklist_id").in(checklistIds));
            
            // Execute the query to get the checklist items
            Query<Checklist_Item> resultQuery = session.createQuery(query);
            List<Checklist_Item> checklistItems = resultQuery.getResultList();
            
            // Update the relationship with the fetched checklist items
            category.setChecklist_items(checklistItems);

            // Save changes (merge the updated category object)
            session.merge(category);

            // Commit the transaction
            session.getTransaction().commit();
            
        } catch (Exception e) {
            // Rollback if something goes wrong
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating Checklist_Category", e);
        } finally {
            // Close session
            session.close();
        }
    }
    
}

