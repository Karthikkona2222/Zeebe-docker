package com.aaseya.AIS.dao;

import java.util.List;

import org.apache.tomcat.util.digester.ArrayStack;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.Model.Checklist_Item;
import com.aaseya.AIS.Model.CorrectiveAction;
import com.aaseya.AIS.Model.InspectionMapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import org.hibernate.query.Query;

@Repository
public class ChecklistDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public List<Checklist_Category> getChecklistCategoryForTemplate(final long templateId) {
		List<Checklist_Category> categoryList = new ArrayStack<Checklist_Category>();
		try {
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();

			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Checklist_Category> query = cb.createQuery(Checklist_Category.class);
			Root<Checklist_Category> root = query.from(Checklist_Category.class);
			query.where(cb.equal(root.join("templates").get("template_id"), templateId));
			categoryList = session.createQuery(query).getResultList();
			System.out.println("CategoryList : " + categoryList);
			transaction.commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	public List<Checklist_Item> getChecklistItemForCategory(final long categoryId) {
		List<Checklist_Item> checklist = new ArrayStack<Checklist_Item>();
		try {
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();

			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Checklist_Item> query = cb.createQuery(Checklist_Item.class);
			Root<Checklist_Item> root = query.from(Checklist_Item.class);
			query.where(cb.equal(root.join("checklist_categorys").get("checklist_cat_id"), categoryId));

			checklist = session.createQuery(query).getResultList();
			transaction.commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checklist;
	}
	 public void saveCheckList_Item(Checklist_Item checklist_Item) {
	        Session session = sessionFactory.openSession();
	        Transaction transaction = session.getTransaction();
	        transaction.begin();

	        session.persist(checklist_Item);
	        transaction.commit();
	        session.close();
	    }

	    public void saveCorrectiveActions(CorrectiveAction correctiveAction) {
	        Session session = sessionFactory.openSession();
	        Transaction transaction = session.getTransaction();
	        transaction.begin();

	        session.persist(correctiveAction);
	        transaction.commit();
	        session.close();
	    }
	    public void saveCheckListCategory(Checklist_Category checklistCategory) {
	        Session session = sessionFactory.openSession();
	        Transaction transaction = session.getTransaction();
	        transaction.begin();

	        session.merge(checklistCategory);  // Use saveOrUpdate for both insert and update

	        transaction.commit();
	        session.close();
	    }
	 // Retrieve a Checklist_Item by its ID
	    public Checklist_Item getCheckListItemById(long checklistItemId) {
	        Session session = sessionFactory.openSession();
	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<Checklist_Item> query = cb.createQuery(Checklist_Item.class);
	        Root<Checklist_Item> root = query.from(Checklist_Item.class);
	        query.select(root).where(cb.equal(root.get("id"), checklistItemId));  // Use the actual ID field name here
 
	        Checklist_Item checklistItem = session.createQuery(query).uniqueResult();
	        session.close();
	        return checklistItem;
	    }
 
	    public Checklist_Category getChecklistCategoryByName(String checklistCategoryName) {
	        Session session = sessionFactory.openSession();
	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<Checklist_Category> query = cb.createQuery(Checklist_Category.class);
	        Root<Checklist_Category> root = query.from(Checklist_Category.class);
	        query.select(root).where(cb.equal(root.get("checklist_category_name"), checklistCategoryName));
 
	        Checklist_Category checklistCategory = session.createQuery(query).uniqueResult();
	        session.close();
	        return checklistCategory;
	    }

public List<Checklist_Item> getAllChecklistItems() {

	Session session = null;
	List<Checklist_Item> result = null;

	try {
		session = sessionFactory.openSession(); 
		var criteriaBuilder = session.getCriteriaBuilder();
		var criteriaQuery = criteriaBuilder.createQuery(Checklist_Item.class);
		var root = criteriaQuery.from(Checklist_Item.class);
		criteriaQuery.select(root);

		Query<Checklist_Item> query = session.createQuery(criteriaQuery);
		result = query.getResultList(); 
	} catch (Exception e) {
		
		e.printStackTrace();
	} finally {
		if (session != null) {
			session.close();
		}
	}

	return result;
}
public Checklist_Item getChecklistItemById(long checklistId) {
    Session session = sessionFactory.openSession();
    Transaction transaction = null;
    Checklist_Item checklistItem = null;

    try {
        transaction = session.beginTransaction();

       
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Checklist_Item> criteriaQuery = criteriaBuilder.createQuery(Checklist_Item.class);
        Root<Checklist_Item> root = criteriaQuery.from(Checklist_Item.class);

        // Add criteria to filter by checklist_id
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("checklist_id"), checklistId));

        checklistItem = session.createQuery(criteriaQuery).uniqueResult();

        transaction.commit();
    } catch (Exception e) {
        if (transaction != null) transaction.rollback();
        e.printStackTrace();
    } finally {
        session.close();
    }

    return checklistItem;
}

//Get the assigned inspector with checklist
	  @PersistenceContext
	private EntityManager entityManager;

	 public List<Object[]> getChecklistCategoriesAndItems(Long inspectionID, String inspectorID) {
		    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

		    // Root entity: InspectionMapping
		    Root<InspectionMapping> inspectionMappingRoot = query.from(InspectionMapping.class);

		    // Join Checklist_Category using checklistCatID from InspectionMappingId
		    Root<Checklist_Category> checklistCategoryRoot = query.from(Checklist_Category.class);

		    // Condition: Matching checklistCatID from InspectionMapping with Checklist_Category's ID
		    Predicate joinCondition = cb.equal(
		        checklistCategoryRoot.get("checklist_cat_id"), 
		        inspectionMappingRoot.get("id").get("checklistCatID")
		    );

		    // Left Join with Checklist_Item
		    Join<Checklist_Category, Checklist_Item> itemJoin = checklistCategoryRoot.join("checklist_items", JoinType.LEFT);

		    // Condition: Filtering by inspectionID
		    Predicate filterByInspectionID = cb.equal(
		        inspectionMappingRoot.get("id").get("inspectionID"), 
		        inspectionID
		    );

		    // Condition: Filtering by inspectorID if provided
		    Predicate filterByInspectorID = inspectorID != null && !inspectorID.isEmpty()
		        ? cb.equal(inspectionMappingRoot.get("inspectorID"), inspectorID)
		        : cb.conjunction(); // No filtering if inspectorID is null

		    // Applying conditions
		    query.multiselect(
		            checklistCategoryRoot.get("checklist_cat_id"),     // Category ID
		            checklistCategoryRoot.get("checklist_category_name"), // Category Name
		            itemJoin,                                           // Checklist Item
		            inspectionMappingRoot.get("inspectorID")            // Inspector ID
		    ).where(cb.and(joinCondition, filterByInspectionID, filterByInspectorID));

		    return entityManager.createQuery(query).getResultList();
		}
	 
	//Get the Checklists based on inspectorId

	    public List<Object[]> getChecklistByInspectorID(String inspectorID) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

	        // Root entity: InspectionMapping
	        Root<InspectionMapping> inspectionMappingRoot = query.from(InspectionMapping.class);

	        // Join Checklist_Category using checklistCatID from InspectionMappingId
	        Root<Checklist_Category> checklistCategoryRoot = query.from(Checklist_Category.class);

	        // Condition: Matching checklistCatID from InspectionMapping with Checklist_Category's ID
	        Predicate joinCondition = cb.equal(
	            checklistCategoryRoot.get("checklist_cat_id"), 
	            inspectionMappingRoot.get("id").get("checklistCatID")
	        );

	        // Left Join with Checklist_Item
	        Join<Checklist_Category, Checklist_Item> itemJoin = checklistCategoryRoot.join("checklist_items", JoinType.LEFT);

	        // Condition: Filtering only for the given inspectorID
	        Predicate filterByInspectorID = cb.equal(
	            inspectionMappingRoot.get("inspectorID"), 
	            inspectorID
	        );

	        // Selecting the required fields
	        query.multiselect(
	                inspectionMappingRoot.get("id").get("inspectionID"), // Inspection ID
	                checklistCategoryRoot.get("checklist_cat_id"),       // Category ID
	                checklistCategoryRoot.get("checklist_category_name"), // Category Name
	                itemJoin                                             // Checklist Item
	        ).where(cb.and(joinCondition, filterByInspectorID));

	        return entityManager.createQuery(query).getResultList();
	    }

}


