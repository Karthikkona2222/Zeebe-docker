package com.aaseya.AIS.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.InspectionChecklistandAnswers;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.PreInspectionChecklist;
import com.aaseya.AIS.dto.StartAISRequestDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class PreInspectionChecklistDAO {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private JdbcTemplate jdbcTemplate; // Assuming you're using JdbcTemplate. Adjust if using JPA/Hibernate

	@PersistenceContext
	private final EntityManager entityManager;

	private final CriteriaPaginationUtil paginationUtil;

	@Autowired
	public PreInspectionChecklistDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.paginationUtil = paginationUtil;
	}

	public List<PreInspectionChecklist> getPreInspectionChecklist(String name) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<PreInspectionChecklist> PreInspectionChecklist = null;
		Inspection_Type inspection_Type = null;

		try {
			transaction = session.beginTransaction();
			inspection_Type = session.createQuery("FROM Inspection_Type WHERE name = :name", Inspection_Type.class)
					.setParameter("name", name).getSingleResult();
			PreInspectionChecklist = inspection_Type.getPreInspectionChecklist();
			System.out.println("PreInspectionChecklist" + inspection_Type.getPreInspectionChecklist());
			System.out.println("name" + name);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return PreInspectionChecklist;

	}

	public Set<PreInspectionChecklist> getPreInspectionChecklist(long inspectionCaseId) {
		String sql = "SELECT pic.id, pic.name, pic.answer_type, pic.Comments FROM inspection_case_pre_inspection_checklist icpic "
				+ "JOIN pre_inspection_checklist pic ON icpic.pre_inspection_checklist_id = pic.id "
				+ "WHERE icpic.inspection_case_id = ?";
		return new HashSet<>(jdbcTemplate.query(sql, new Object[] { inspectionCaseId },
				new BeanPropertyRowMapper<>(PreInspectionChecklist.class)));
	}

	public PreInspectionChecklist saveChecklist(PreInspectionChecklist checklist) {
		entityManager.persist(checklist);
		return checklist;
	}

//	public PreInspectionChecklist findChecklistById(Long id) {
//		return entityManager.find(PreInspectionChecklist.class, id);
//	}

	@Transactional
	public PreInspectionChecklist updateChecklist(PreInspectionChecklist checklist) {
		return entityManager.merge(checklist);
	}

/////GetAllPreInspectionChecklists/////
	@Transactional
	public Page<PreInspectionChecklist> getAllPreInspectionChecklists(Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<PreInspectionChecklist> criteriaQuery = criteriaBuilder.createQuery(PreInspectionChecklist.class);

		// Root<PreInspectionChecklist> checklistRoot =
		// criteriaQuery.from(PreInspectionChecklist.class);

		// criteriaQuery.select(checklistRoot);

		return paginationUtil.getPaginatedData(criteriaQuery, pageable, PreInspectionChecklist.class, entity -> entity);
	}
	///// GetAllPreInspectionChecklists/////

	///////////////////////////////// Get the preinspection checklist by
	///////////////////////////////// id/////////////////////
	public PreInspectionChecklist findChecklistById(Long id) {
		try (Session session = sessionFactory.openSession()) {
			EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<PreInspectionChecklist> query = cb.createQuery(PreInspectionChecklist.class);

			Root<PreInspectionChecklist> root = query.from(PreInspectionChecklist.class);
			root.fetch("inspectionType", JoinType.LEFT);

			query.select(root).where(cb.equal(root.get("id"), id));

			PreInspectionChecklist checklist = entityManager.createQuery(query).getSingleResult();
			entityManager.close();
			return checklist;
		}
	}

}
