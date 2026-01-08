package com.aaseya.AIS.dao;
 
 
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
 
import com.aaseya.AIS.Model.Checklist_Item;
import com.aaseya.AIS.Model.CorrectiveAction;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import java.util.List;
import java.util.function.Function;
 
@Repository
public class ChecklistItemDAO {
 
    @PersistenceContext
    private final EntityManager entityManager;
    
    @Autowired
	private SessionFactory sessionFactory;
    
    @Autowired
    private CriteriaPaginationUtil paginationUtil;
 
    @Transactional
    public Page<Checklist_Item> getAllChecklistItems(Pageable pageable) {
        // Create CriteriaQuery for Checklist_Item
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Checklist_Item> query = cb.createQuery(Checklist_Item.class);

        // Use CriteriaPaginationUtil to paginate and fetch results
        return paginationUtil.getPaginatedData(query, pageable, Checklist_Item.class, Function.identity());
    }
	public Checklist_Item getChecklistItemById(long checklistId) {
        Session session = sessionFactory.openSession();
        org.hibernate.Transaction transaction = null;
        Checklist_Item checklistItem = null;
        try {
            transaction = session.beginTransaction();
            checklistItem = session.get(Checklist_Item.class, checklistId);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return checklistItem;
    }

	 public ChecklistItemDAO(EntityManager entityManager) {
	        this.entityManager = entityManager;
	    }

	    public Checklist_Item getChecklistItemById(Long checklistId) {
	        return entityManager.find(Checklist_Item.class, checklistId);
	    }

	    public void saveOrUpdateChecklistItem(Checklist_Item checklistItem) {
	        Session session = entityManager.unwrap(Session.class);
	        session.saveOrUpdate(checklistItem);
	    }

	    @Transactional
	    public void updateChecklistAndMappings(Checklist_Item checklistItem, List<Long> correctiveActionIds, List<String> newCorrectiveActions) {
	        Session session = entityManager.unwrap(Session.class);

	        // Clear existing corrective actions
	        for (CorrectiveAction correctiveAction : checklistItem.getCorrectiveactions()) {
	            correctiveAction.getChecklist_items().remove(checklistItem);
	        }
	        checklistItem.getCorrectiveactions().clear();

	        // Update existing corrective actions by IDs
	        if (correctiveActionIds != null && !correctiveActionIds.isEmpty()) {
	            for (Long correctiveActionId : correctiveActionIds) {
	                CorrectiveAction correctiveAction = session.get(CorrectiveAction.class, correctiveActionId);
	                if (correctiveAction != null) {
	                    checklistItem.getCorrectiveactions().add(correctiveAction);
	                    correctiveAction.getChecklist_items().add(checklistItem);
	                } else {
	                    System.out.println("Corrective Action with ID " + correctiveActionId + " not found.");
	                }
	            }
	        }

	        // Add new corrective actions
	        if (newCorrectiveActions != null && !newCorrectiveActions.isEmpty()) {
	            for (String actionName : newCorrectiveActions) {
	                CorrectiveAction newAction = new CorrectiveAction();
	                newAction.setCorrective_action_name(actionName);
	                session.persist(newAction); // Persist the new corrective action
	                checklistItem.getCorrectiveactions().add(newAction); // Associate with the checklist item
	                newAction.getChecklist_items().add(checklistItem); // Maintain bidirectional mapping
	            }
	        }

	        // Merge and flush checklistItem to save all changes
	        session.merge(checklistItem);
	        session.flush();
	    }
}