package com.aaseya.AIS.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.InspectionAttachments;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionPlan;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dto.CaseSummaryDTO;
import com.aaseya.AIS.dto.InspectionCaseDTO;
import com.aaseya.AIS.dto.InspectionCaseDetailsDTO;
import com.aaseya.AIS.dto.InspectionCaseResponseDTO;
import com.aaseya.AIS.dto.InspectionFilters;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class InspectionCaseDAO {

	@Autowired
	private SessionFactory sessionFactory;
	@PersistenceContext
	private final EntityManager entityManager;
	private final CriteriaPaginationUtil criteriaPaginationUtil;

	@Autowired
	public InspectionCaseDAO(EntityManager entityManager, CriteriaPaginationUtil criteriaPaginationUtil) {
		this.entityManager = entityManager;
		this.criteriaPaginationUtil = criteriaPaginationUtil; // Ensure it is injected correctly
	}

	public List<InspectionCase> getAllInspectionCases() {
		Session session = sessionFactory.openSession();
		List<InspectionCase> inspectionCases = session.createQuery("from InspectionCase", InspectionCase.class).list();
		session.close();
		return inspectionCases;
	}

	public List<InspectionCase> findInspectionCasesByCreatedByAndDueDateFilter(String createdBy, String dueDateFilter) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = buildQuery(createdBy, dueDateFilter);
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("createdBy", createdBy);
			setDueDateParameters(query, dueDateFilter);
			inspectionCases = query.getResultList();
			transaction.commit();
		} catch (HibernateException e) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw new RuntimeException(e);
		} finally {
			session.close();
		}
		return inspectionCases;
	}
	
	public List<InspectionCase> findByInspectionPlan(InspectionPlan inspectionPlan) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionCase> query = cb.createQuery(InspectionCase.class);
        Root<InspectionCase> root = query.from(InspectionCase.class);
        
        // Join InspectionCase with InspectionPlan
        Join<InspectionCase, InspectionPlan> planJoin = root.join("inspectionPlan");

        // Add where condition
        query.select(root).where(cb.equal(planJoin.get("inspectionPlanId"), inspectionPlan.getInspectionPlanId()));

        return entityManager.createQuery(query).getResultList();
    }

	private String buildQuery(String createdBy, String dueDateFilter) {
		String hql = "FROM InspectionCase ic WHERE ic.createdBy = :createdBy";
		if (!"ALL".equals(dueDateFilter)) {
			hql += " and ic.dueDate ";
			if ("today".equals(dueDateFilter)) {
				hql += "= :dueDate";
			} else {
				hql += " between :startDate and :endDate";
			}
		}
		System.out.println(hql);
		return hql;
	}

	private void setDueDateParameters(Query<InspectionCase> query, String dueDateFilter) {
		switch (dueDateFilter) {
		case "today":
			query.setParameter("dueDate", LocalDate.now());
			break;
		case "week":
			query.setParameter("startDate", LocalDate.now().minusDays(7));
			query.setParameter("endDate", LocalDate.now());
			break;
		case "month":
			query.setParameter("startDate", LocalDate.now().minusMonths(1));
			query.setParameter("endDate", LocalDate.now());
			break;
		default:
			// Handle default case or throw an exception
			break;
		}
	}

	public List<InspectionCase> findInspectionCasesByCreatedByAndStatus(String createdBy, String status) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ic.createdBy = :createdBy AND ic.status = :status";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("createdBy", createdBy);
			query.setParameter("status", status);
			inspectionCases = query.getResultList();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;
	}

////////////// Save  the inspection for approver//////////
	public InspectionCase findById(Long id) {
		System.out.println("Inspection ID: " + id);
		Session session = sessionFactory.openSession();
		try {
			InspectionCase inspectionCase = session.get(InspectionCase.class, id);
			System.out.println("Inspection Case: " + inspectionCase);
			return inspectionCase;
		} finally {
			session.close();
		}

	}

	public void updateInspectionCase(InspectionCase inspectionCase) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.merge(inspectionCase);
		} finally {
			transaction.commit();
			session.close();
		}
	}

	//////// Save the inspection for approver///////////////

	public List<InspectionCase> getMycasesForManager(String User_id) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ic.createdBy=:User_id AND status<>'completed'";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("User_id", User_id);
			inspectionCases = query.getResultList();
			System.out.println("Inspectioncase" + inspectionCases);
			System.out.println("User_id" + User_id);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;

	}

	public List<InspectionCase> getInspectorPool(LocalDate date) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			LocalDate yesterday = date.minusDays(1);
			LocalDate tomorrow = date.plusDays(1);
			String hql = "FROM InspectionCase WHERE status='new' AND dueDate BETWEEN :yesterday AND :tomorrow";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("yesterday", yesterday);
			query.setParameter("tomorrow", tomorrow);
			inspectionCases = query.getResultList();
			System.out.println("Inspectioncase" + inspectionCases);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;

	}

	public List<InspectionCase> getMyCasesForInspector(String User_id) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE (ic.inspectorID=:User_id AND status IN ('in_progress', 'pending'))";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("User_id", User_id);
			inspectionCases = query.getResultList();
			System.out.println("Inspectioncase" + inspectionCases);
			System.out.println("User_id" + User_id);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;

	}

	public List<InspectionCase> getManagerPool(String User_id) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ic.createdBy<>:User_id AND status<>'completed'";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("User_id", User_id);
			inspectionCases = query.getResultList();
			System.out.println("Inspectioncase" + inspectionCases);
			System.out.println("User_id" + User_id);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;

	}

	public void saveInspectionCase(InspectionCase inspectionCase) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			if (session.contains(inspectionCase)) {
				// The entity is already managed
				session.persist(inspectionCase);
			} else {
				// The entity is detached
				inspectionCase = session.merge(inspectionCase);
				session.persist(inspectionCase);
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

//	public Map<String, Long> getStatusCountByCreatedBy1(String createdBy) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	// To get individual status names and its count and total count of status for
	// particular createdby//

	public Map<String, Long> getStatusCountByCreatedBy(String createdBy) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Map<String, Long> statusCountMap = new HashMap<>();

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT status, COUNT(status) FROM InspectionCase WHERE createdBy = :createdBy GROUP BY status";
			List<Object[]> results = session.createQuery(hql).setParameter("createdBy", createdBy).getResultList();

			for (Object[] result : results) {
				String status = (String) result[0];
				Long count = (Long) result[1];
				statusCountMap.put(status, count);
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return statusCountMap;
	}

	/////////////////////////////////////////////////////////////////////////
	// To get count and names of status, Inspector source based on createBy//
	public List<String> getInspectorSourcesByCreatedBy(String createdBy) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<String> inspectorSources = null;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT DISTINCT inspector_source FROM InspectionCase WHERE createdBy = :createdBy";
			inspectorSources = session.createQuery(hql, String.class).setParameter("createdBy", createdBy).list();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectorSources;
	}

	public Long getInspectorSourceCountByCreatedByAndInspectorSource(String createdBy, String inspectorSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Long count = 0L;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT COUNT(*) FROM InspectionCase WHERE createdBy = :createdBy AND inspector_source = :inspectorSource";
			count = (Long) session.createQuery(hql).setParameter("createdBy", createdBy)
					.setParameter("inspectorSource", inspectorSource).uniqueResult();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return count;
	}

	public Map<String, Long> getStatusCountsByCreatedByAndInspectorSource(String createdBy, String inspectorSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Map<String, Long> statusCounts = new HashMap<>();

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT status, COUNT(*) FROM InspectionCase WHERE createdBy = :createdBy AND inspector_source = :inspectorSource GROUP BY status";
			List<Object[]> results = session.createQuery(hql).setParameter("createdBy", createdBy)
					.setParameter("inspectorSource", inspectorSource).list();

			for (Object[] result : results) {
				statusCounts.put((String) result[0], (Long) result[1]);
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return statusCounts;
	}
	///////////////////////////////////////////////////////////////////

	// To get individual status names and its count and total count of status for
	// particular inspectorID//
	public Map<String, Long> getStatusCountByInspectorID(String inspectorID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Map<String, Long> statusCountMap = new HashMap<>();

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT status, COUNT(status) FROM InspectionCase WHERE inspectorID = :inspectorID GROUP BY status";
			List<Object[]> results = session.createQuery(hql).setParameter("inspectorID", inspectorID).getResultList();

			for (Object[] result : results) {
				String status = (String) result[0];
				Long count = (Long) result[1];
				// Exclude the "New" status
				if (!"New".equals(status)) {
					statusCountMap.put(status, count);
				}
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return statusCountMap;
	}

	//////////////////////////////////////////////////////////////////////////////
	// To get count and names of status, Inspector source based on inspectorID//
	public List<String> getInspectorSourcesByInspectorID(String inspectorID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<String> inspectorSources = null;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT DISTINCT inspector_source FROM InspectionCase WHERE inspectorID = :inspectorID";
			inspectorSources = session.createQuery(hql, String.class).setParameter("inspectorID", inspectorID).list();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectorSources;
	}

	public Long getInspectorSourceCountByInspectorIDAndInspectorSource(String inspectorID, String inspectorSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Long count = 0L;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT COUNT(*) FROM InspectionCase WHERE inspectorID = :inspectorID AND inspector_source = :inspectorSource";
			count = (Long) session.createQuery(hql).setParameter("inspectorID", inspectorID)
					.setParameter("inspectorSource", inspectorSource).uniqueResult();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return count;
	}

	public Map<String, Long> getStatusCountsByInspectorIDAndInspectorSource(String inspectorID,
			String inspectorSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Map<String, Long> statusCounts = new HashMap<>();

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT status, COUNT(*) FROM InspectionCase WHERE inspectorID = :inspectorID AND inspector_source = :inspectorSource GROUP BY status";
			List<Object[]> results = session.createQuery(hql).setParameter("inspectorID", inspectorID)
					.setParameter("inspectorSource", inspectorSource).list();

			for (Object[] result : results) {
				statusCounts.put((String) result[0], (Long) result[1]);
			}

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return statusCounts;
	}

	////////////////////////
	public List<InspectionCase> getUnasssignedcasesForInspector(String inspectorEmailId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ic.createdBy =:User_id AND status ='new'";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("User_id", inspectorEmailId);
			inspectionCases = query.getResultList();
			System.out.println("Inspectioncase" + inspectionCases);
			System.out.println("User_id" + inspectorEmailId);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;

	}

	public long getTemplateId(final String inspectionId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		long templateId = 0;
		try {
			transaction = session.beginTransaction();
			String hql = "Select template_id FROM InspectionCase ic WHERE ic.inspectionID =:inspectionId";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("inspectionId", inspectionId);
			templateId = query.getSingleResult();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		System.out.println("TemplateId : " + templateId);
		return templateId;
	}
//Getmycasesforapprover

	public List<InspectionCase> getMycasesForApprover(String approverID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ic.approverID = :approverID AND ic.status IN ('pending_approval', 'under_approval')";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("approverID", approverID);
			inspectionCases = query.getResultList();
			System.out.println("Inspection cases: " + inspectionCases);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;
	}

	// Get approver pool data

	public List<InspectionCase> getApproverPool(String approverID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ((ic.approverID IS NULL OR ic.approverID='' OR ic.approverID<>:approverID) AND status IN ('pending_approval', 'under_approval'))";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("approverID", approverID);
			inspectionCases = query.getResultList();
			System.out.println("Inspectioncase" + inspectionCases);
			System.out.println("approverID" + approverID);

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;

		// Get getMyCasesForInspectordate
	}

	public List<InspectionCase> getMyCasesForInspectordate(String due_date) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;

		try {
			transaction = session.beginTransaction();

			// Get today's date
			LocalDate today = LocalDate.now();

			// Define the date range: from yesterday to tomorrow
			LocalDate yesterday = today.minusDays(1);
			LocalDate tomorrow = today.plusDays(1);

			// Build the HQL query with date range and inspector ID
			String hql = "FROM InspectionCase ic " + "WHERE ic.inspectorID = :due_date "
					+ "AND ic.status IN ('in_progress', 'pending') "
					+ "AND ic.dateOfInspection BETWEEN :yesterday AND :tomorrow";

			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);

			query.setParameter("due_date", due_date);
			query.setParameter("yesterday", yesterday);
			query.setParameter("tomorrow", tomorrow);

			inspectionCases = query.getResultList();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectionCases;
	}

	public List<InspectionCase> getMyCasesForInspectorDate(String inspectorID, LocalDate due_date) {
	    Session session = sessionFactory.openSession();
	    Transaction transaction = null;
	    List<InspectionCase> inspectionCases = new ArrayList<>();

	    try {
	        transaction = session.beginTransaction();

	        LocalDate yesterday = due_date.minusDays(1);
	        LocalDate tomorrow = due_date.plusDays(1);
	        LocalDate dayaftertomorrow = due_date.plusDays(2);

	        // Fetch inspection IDs from InspectionMapping table where inspectorID matches
	        String hqlMapping = "SELECT im.id.inspectionID FROM InspectionMapping im WHERE im.inspectorID = :inspectorID";
	        List<Long> mappedInspectionIds = session.createQuery(hqlMapping, Long.class)
	                .setParameter("inspectorID", inspectorID)
	                .getResultList();

	        // Fetch cases from InspectionCase table
	        String hqlCase = "FROM InspectionCase ic WHERE " +
	                "(ic.inspectorID = :inspectorID OR ic.inspectionID IN (:mappedIds)) " +
	                "AND ic.status IN ('in_progress', 'pending') " +
	                "AND ic.dueDate IN (:yesterday, :today, :tomorrow, :dayaftertomorrow)";

	        Query<InspectionCase> query = session.createQuery(hqlCase, InspectionCase.class);
	        query.setParameter("inspectorID", inspectorID);
	        query.setParameter("yesterday", yesterday);
	        query.setParameter("today", due_date);
	        query.setParameter("tomorrow", tomorrow);
	        query.setParameter("dayaftertomorrow", dayaftertomorrow);

	        query.setParameter("mappedIds", mappedInspectionIds.isEmpty() ? List.of(-1L) : mappedInspectionIds);

	        inspectionCases = query.getResultList();

	        transaction.commit();
	    } catch (Exception e) {
	        if (transaction != null) {
	            transaction.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	        session.close();
	    }
	    return inspectionCases;
	}


	///// Get status count dashboard data for approver
	public List<Object[]> getStatusCountsByApproverID(String approverID) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			String hql = "SELECT status, COUNT(status) FROM InspectionCase WHERE approverID = :approverID GROUP BY status";
			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("approverID", approverID);
			return query.list();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	////////// Get source count for approver
	public List<String> getApproverSourceCountForApproverID(String approverID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<String> inspectorSources = null;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT DISTINCT inspector_source FROM InspectionCase WHERE approverID = :approverID";
			inspectorSources = session.createQuery(hql, String.class).setParameter("approverID", approverID).list();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectorSources;
	}

/////fetch all zone details based on the inspectionID//
	public InspectionCase getInspectionCaseByIdforZone(long inspectionId) {
		Session session = null;
		InspectionCase inspectionCase = null;
		try {
			session = sessionFactory.openSession();
			inspectionCase = session.get(InspectionCase.class, inspectionId);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return inspectionCase;
	}

	//////////////////

	public Long getInspectorSourceCountByApproverIdAndInspectorSource(String approverID, String inspectorSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Long count = 0L;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT COUNT(*) FROM InspectionCase WHERE approverID = :approverID AND inspector_source = :inspectorSource";
			count = (Long) session.createQuery(hql).setParameter("approverID", approverID)
					.setParameter("inspectorSource", inspectorSource).uniqueResult();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return count;
	}

	public Map<String, Long> getApproverSourceStatusCountForApproverID(String approverID, String inspectionSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<Object[]> results = null;
		Map<String, Long> statusCounts = new HashMap<>();
		try {
			transaction = session.beginTransaction();

			String hql = "SELECT status, COUNT(*) FROM InspectionCase WHERE approverID = :approverID AND inspector_source = :inspectionSource GROUP BY status";
			results = session.createQuery(hql, Object[].class).setParameter("approverID", approverID)
					.setParameter("inspectionSource", inspectionSource).list();

			for (Object[] result : results) {
				statusCounts.put((String) result[0], (Long) result[1]);
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return statusCounts;
	}

	public List<InspectionCase> getMyCasesForManagerByDate(String createdBy, LocalDate startDate, LocalDate endDate) {
		List<InspectionCase> inspectionCases = new ArrayList<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
			cq.where(cb.and(cb.equal(root.get("createdBy"), createdBy),
					cb.between(root.get("createdDate"), startDate, endDate)));
			inspectionCases = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return inspectionCases;
	}

	public List<InspectionCase> getManagerPoolByDate(String createdBy, LocalDate startDate, LocalDate endDate) {
		List<InspectionCase> inspectionCases = new ArrayList<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
			cq.where(cb.and(cb.notEqual(root.get("createdBy"), createdBy), cb.notEqual(root.get("status"), "completed"),
					cb.between(root.get("createdDate"), startDate, endDate)));
			inspectionCases = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return inspectionCases;
	}

	public List<InspectionCase> getMyCasesForApproverByDate(String approverID, LocalDate startDate, LocalDate endDate) {
		List<InspectionCase> inspectionCases = new ArrayList<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
			cq.where(cb.and(cb.equal(root.get("approverID"), approverID),
					root.get("status").in(Arrays.asList("pending_approval", "under_approval")),
					cb.between(root.get("dueDate"), startDate, endDate)));
			inspectionCases = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return inspectionCases;
	}

	public List<InspectionCase> getApproverPoolByDate(String approverId, LocalDate startDate, LocalDate endDate) {
		List<InspectionCase> inspectionCases = new ArrayList<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
			cq.where(cb.and(
					cb.or(cb.notEqual(root.get("approverID"), approverId), cb.equal(root.get("approverID"), ""),
							cb.isNull(root.get("approverID"))),
					root.get("status").in(Arrays.asList("pending_approval", "under_approval")),
					cb.between(root.get("dueDate"), startDate, endDate)));
			inspectionCases = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return inspectionCases;
	}

///////////////////////working days///////////////
	public InspectionCase getInspectionCaseById(long inspectionID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		InspectionCase inspectionCase = null;

		try {
			transaction = session.beginTransaction();
			inspectionCase = session.get(InspectionCase.class, inspectionID);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectionCase;
	}

////////////////////Working days/////////////////////////////////

	public List<Users> getUsersByInspectionType(String inspectionType) {
		Session session = sessionFactory.openSession();
		try {
			Query<Users> query = session.createQuery(
					"SELECT u FROM Users u JOIN u.skill s JOIN s.inspectionTypes it WHERE it.name = :inspectionType and u.role = 'Inspector'",
					Users.class);
			query.setParameter("inspectionType", inspectionType);
			List<Users> resultList = query.getResultList();
			System.out.println(resultList);
			return resultList;
		} finally {
			session.close();
		}
	}

	public Zone getZoneByEntityId(String entityId) {
		Session session = sessionFactory.openSession();
		try {
			Query<Zone> query = session
					.createQuery("SELECT z FROM Zone z JOIN z.newEntities e WHERE e.entityid = :entityId", Zone.class);
			query.setParameter("entityId", entityId);
			Zone result = query.uniqueResult();
			System.out.println(result);
			return result;
		} finally {
			session.close();
		}
	}

/////To get CustomPreInspectionChecklist data/////
	public String getCustomPreInspectionChecklist(long inspectionCaseId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
//			cq.select(root.get("custom_pre_inspection_checklist"));
			cq.where(cb.equal(root.get("inspectionID"), inspectionCaseId));
			String customChecklist = session.createQuery(cq).getSingleResult().getCustom_pre_inspection_checklist();
			if (customChecklist != null) {
				return customChecklist;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}

///// Get status count dashboard data for approver
	public List<Object[]> getStatusCountsForReviewer(String reviewerId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			String hql = "SELECT status, COUNT(status) FROM InspectionCase WHERE reviewerID = :reviewerID GROUP BY status";
			Query<Object[]> query = session.createQuery(hql, Object[].class);
			query.setParameter("reviewerID", reviewerId);
			return query.list();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

//////////Get source count for reviewer
	public List<String> getSourceCountForReviewer(String reviewerId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<String> inspectorSources = null;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT DISTINCT inspector_source FROM InspectionCase WHERE reviewerID = :reviewerID";
			inspectorSources = session.createQuery(hql, String.class).setParameter("reviewerID", reviewerId).list();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectorSources;
	}

	public Long getSourceCountForInspectionSource(String reviewerId, String inspectorSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Long count = 0L;

		try {
			transaction = session.beginTransaction();

			String hql = "SELECT COUNT(*) FROM InspectionCase WHERE reviewerID = :reviewerID AND inspector_source = :inspectorSource";
			count = (Long) session.createQuery(hql).setParameter("reviewerID", reviewerId)
					.setParameter("inspectorSource", inspectorSource).uniqueResult();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return count;
	}

	public Map<String, Long> getSourceStatusCountForReviewer(String reviewerId, String inspectionSource) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<Object[]> results = null;
		Map<String, Long> statusCounts = new HashMap<>();
		try {
			transaction = session.beginTransaction();

			String hql = "SELECT status, COUNT(*) FROM InspectionCase WHERE reviewerID = :reviewerID AND inspector_source = :inspectionSource GROUP BY status";
			results = session.createQuery(hql, Object[].class).setParameter("reviewerID", reviewerId)
					.setParameter("inspectionSource", inspectionSource).list();

			for (Object[] result : results) {
				statusCounts.put((String) result[0], (Long) result[1]);
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return statusCounts;
	}

	public List<InspectionCase> getMycasesForReviewer(String reviewerID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ic.reviewerID = :reviewerID AND ic.status IN ('pending_review', 'under_review')";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("reviewerID", reviewerID);
			inspectionCases = query.getResultList();
			System.out.println("Inspection cases: " + inspectionCases);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;
	}
	
	public List<InspectionCase> getMycasesForCompliance(String complianceId) {
	    Session session = sessionFactory.openSession();
	    Transaction transaction = null;
	    List<InspectionCase> inspectionCases = null;

	    try {
	        transaction = session.beginTransaction();
	        String hql = "FROM InspectionCase ic WHERE ic.complianceID = :complianceId";
	        Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
	        query.setParameter("complianceId", complianceId);
	        inspectionCases = query.getResultList();
	        transaction.commit();
	    } catch (Exception e) {
	        if (transaction != null) transaction.rollback();
	        e.printStackTrace();
	    } finally {
	        session.close();
	    }
	    return inspectionCases;
	}

	public List<InspectionCase> getMyCasesForComplianceByDate(
	        String complianceId, LocalDate startDate, LocalDate endDate) {

	    List<InspectionCase> inspectionCases = new ArrayList<>();
	    Session session = null;

	    try {
	        session = sessionFactory.openSession();
	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
	        Root<InspectionCase> root = cq.from(InspectionCase.class);

	        cq.where(cb.and(
	                cb.equal(root.get("complianceID"), complianceId),
	                cb.between(root.get("dueDate"), startDate, endDate)
	        ));

	        inspectionCases = session.createQuery(cq).getResultList();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (session != null) session.close();
	    }
	    return inspectionCases;
	}


	public List<InspectionCase> getMyCasesForReviewerByDate(String reviewerID, LocalDate startDate, LocalDate endDate) {
		List<InspectionCase> inspectionCases = new ArrayList<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
			cq.where(cb.and(cb.equal(root.get("reviewerID"), reviewerID),
					root.get("status").in(Arrays.asList("pending_review", "under_review")),
					cb.between(root.get("dueDate"), startDate, endDate)));
			inspectionCases = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return inspectionCases;
	}

	public List<InspectionCase> getReviewerPool(String reviewerID) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<InspectionCase> inspectionCases = null;
		try {
			transaction = session.beginTransaction();
			String hql = "FROM InspectionCase ic WHERE ((ic.reviewerID IS NULL OR ic.reviewerID='' OR ic.reviewerID<>:reviewerID) AND status IN ('pending_review', 'under_review'))";
			Query<InspectionCase> query = session.createQuery(hql, InspectionCase.class);
			query.setParameter("reviewerID", reviewerID);
			inspectionCases = query.getResultList();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return inspectionCases;
	}

	public List<InspectionCase> getReviewerPoolByDate(String reviewerID, LocalDate startDate, LocalDate endDate) {
		List<InspectionCase> inspectionCases = new ArrayList<>();
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
			Root<InspectionCase> root = cq.from(InspectionCase.class);
			cq.where(cb.and(
					cb.or(cb.notEqual(root.get("reviewerID"), reviewerID), cb.equal(root.get("reviewerID"), ""),
							cb.isNull(root.get("reviewerID"))),
					root.get("status").in(Arrays.asList("pending_review", "under_review")),
					cb.between(root.get("dueDate"), startDate, endDate)));
			inspectionCases = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return inspectionCases;
	}

	public Optional<InspectionCaseDetailsDTO> getInspectionCaseDetailsById(long inspectionId) {
		// Fetching details from InspectionCase and NewEntity
		CriteriaBuilder cb1 = entityManager.getCriteriaBuilder();
		CriteriaQuery<InspectionCaseDetailsDTO> query1 = cb1.createQuery(InspectionCaseDetailsDTO.class);
		Root<InspectionCase> inspectionCase = query1.from(InspectionCase.class);
		Join<InspectionCase, NewEntity> entity = inspectionCase.join("entity");

		query1.select(cb1.construct(InspectionCaseDetailsDTO.class, inspectionCase.get("inspectionID"),
				entity.get("entityid"), entity.get("name"), inspectionCase.get("inspectionType"),
				inspectionCase.get("inspector_source"), entity.get("size"), entity.get("representativeName"),
				entity.get("segment").get("segment_name"), entity.get("subSegment").get("name"),
				entity.get("representativePhoneNo"), entity.get("representativeEmail"), entity.get("address"),
				cb1.nullLiteral(LocalTime.class), cb1.nullLiteral(LocalTime.class), cb1.nullLiteral(LocalTime.class)))
				.where(cb1.equal(inspectionCase.get("inspectionID"), inspectionId));

		InspectionCaseDetailsDTO details = entityManager.createQuery(query1).getResultStream().findFirst().orElse(null);

		if (details == null) {
			return Optional.empty();
		}

		// Fetching the Inspection_Type corresponding to the entity size
		String entitySize = details.getSize();

		CriteriaQuery<Inspection_Type> query2 = cb1.createQuery(Inspection_Type.class);
		Root<Inspection_Type> inspectionTypeRoot = query2.from(Inspection_Type.class);

		query2.select(inspectionTypeRoot).where(cb1.equal(inspectionTypeRoot.get("name"), details.getInspectionType()));

		Inspection_Type inspectionType = entityManager.createQuery(query2).getResultStream().findFirst().orElse(null);

		if (inspectionType != null) {
			if ("low".equals(entitySize)) {
				details.setEfforts(inspectionType.getLow());
			} else if ("medium".equals(entitySize)) {
				details.setEfforts(inspectionType.getMedium());
			} else if ("high".equals(entitySize)) {
				details.setEfforts(inspectionType.getHigh());
			}
		}

		return Optional.of(details);
	}

	private static final Logger logger = LoggerFactory.getLogger(InspectionCaseDAO.class);

	public Page<InspectionCaseDTO> fetchInspectionCasesWithEntityDetails(String entityid, String inspectorID,
			String status, String inspector_source, LocalDate start_date, LocalDate end_date, LocalDate dueStartDate,
			LocalDate dueEndDate, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<InspectionCaseDTO> query = cb.createQuery(InspectionCaseDTO.class);
		Root<InspectionCase> inspectionCase = query.from(InspectionCase.class);
		Join<InspectionCase, NewEntity> entity = inspectionCase.join("entity");

		List<Predicate> predicates = new ArrayList<>();

		if (entityid != null) {
			predicates.add(cb.equal(entity.get("entityid"), entityid));
		}
		if (inspectorID != null && !inspectorID.isEmpty()) {
			predicates.add(cb.equal(inspectionCase.get("inspectorID"), inspectorID));
		}
		if (status != null && !status.isEmpty()) {
			predicates.add(cb.equal(inspectionCase.get("status"), status));
		}
		if (inspector_source != null && !inspector_source.isEmpty()) {
			predicates.add(cb.equal(inspectionCase.get("inspector_source"), inspector_source));
		}
		if (start_date != null && end_date != null) {
			predicates.add(cb.between(cb.function("TO_DATE", LocalDate.class, inspectionCase.get("dateOfInspection"),
					cb.literal("YYYY-MM-DD")), start_date, end_date));
		} else if (start_date != null) {
			predicates.add(cb.greaterThanOrEqualTo(cb.function("TO_DATE", LocalDate.class,
					inspectionCase.get("dateOfInspection"), cb.literal("YYYY-MM-DD")), start_date));
		} else if (end_date != null) {
			predicates.add(cb.lessThanOrEqualTo(cb.function("TO_DATE", LocalDate.class,
					inspectionCase.get("dateOfInspection"), cb.literal("YYYY-MM-DD")), end_date));
		}

		if (dueStartDate != null && dueEndDate != null) {
			predicates.add(cb.between(inspectionCase.get("dueDate"), dueStartDate, dueEndDate));
		} else if (dueStartDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(inspectionCase.get("dueDate"), dueStartDate));
		} else if (dueEndDate != null) {
			predicates.add(cb.lessThanOrEqualTo(inspectionCase.get("dueDate"), dueEndDate));
		}

		query.where(cb.and(predicates.toArray(new Predicate[0])));

		query.select(cb.construct(InspectionCaseDTO.class, inspectionCase.get("inspectionID"),
				inspectionCase.get("inspectorID"), inspectionCase.get("inspectionType"),
				inspectionCase.get("dateOfInspection"), inspectionCase.get("createdBy"), inspectionCase.get("status"),
				inspectionCase.get("approverID"), inspectionCase.get("inspector_source"),
				inspectionCase.get("start_date"), inspectionCase.get("end_date"), entity.get("name"),
				entity.get("building"), entity.get("floor"), entity.get("facility"), entity.get("address")));

		try {
			return criteriaPaginationUtil.getPaginatedDataForUI(query, inspectionCase, pageable, InspectionCase.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error fetching inspection cases: ", e);
			throw new RuntimeException("Error fetching inspection cases", e);
		}

	}

	//// getTransactionsSummaryReport
//	public CaseSummaryDTO getCaseSummary(String startDate, String endDate) {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//		// Create query for case summary
//		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
//		Root<InspectionCase> root = query.from(InspectionCase.class);
//
//		// Filter cases by date of inspection
//		Predicate datePredicate = cb.conjunction(); // Default to no filtering
//
//		// Adjusted logic to handle start and end dates
//		if (startDate != null) {
//			datePredicate = cb.greaterThanOrEqualTo(root.get("dateOfInspection"), startDate);
//		}
//		if (endDate != null) {
//			datePredicate = cb.and(datePredicate, cb.lessThanOrEqualTo(root.get("dateOfInspection"), endDate));
//		}
//
//		// Handle null values using coalesce for status and inspectorSource
//		Expression<String> statusExpression = cb.coalesce(root.get("status"), "Unknown");
//		Expression<String> inspectorSourceExpression = cb.coalesce(root.get("inspector_source"), "Unknown");
//
//		// Group by status and inspector source, count cases
//		query.multiselect(cb.count(root).alias("totalCases"), statusExpression.alias("status"),
//				inspectorSourceExpression.alias("inspector_source")).where(datePredicate)
//				.groupBy(statusExpression, inspectorSourceExpression);
//
//		// Execute query and process results
//		Map<String, Long> casesByStatus = new HashMap<>();
//		Map<String, Long> casesByInspectorSource = new HashMap<>();
//		long openCases = 0, pendingCases = 0, closedCases = 0;
//
//		long totalCases = 0; // Track total for validation
//
//		// Iterate over the results from the query
//		for (Object[] result : entityManager.createQuery(query).getResultList()) {
//			long count = (Long) result[0];
//			String status = (String) result[1];
//			String inspectorSource = (String) result[2];
//
//			totalCases += count;
//
//			// Increment the status counts
//			casesByStatus.put(status, casesByStatus.getOrDefault(status, 0L) + count);
//
//			// Increment the inspector source counts
//			casesByInspectorSource.put(inspectorSource,
//					casesByInspectorSource.getOrDefault(inspectorSource, 0L) + count);
//
//			// Logic to count open, pending, closed, and completed cases
//			switch (status.toLowerCase()) {
//			case "new":
//			case "reopened":
//			case "reinspection":
//			case "in_progress":
//			case "under_approval":
//			case "under_review":
//			case "unknown":
//			case "pending_review":
//			case "pending":
//			case "pending_approval":
//				openCases += count;
//				break;
//			case "closed":
//			case "completed":
//				closedCases += count;
//				break;
//			default:
//				// Handle unknown or other cases
//				break;
//			}
//
//			// Specifically count pending cases separately but include them in open cases as
//			// well
//			if (status.equalsIgnoreCase("pending_review") || status.equalsIgnoreCase("pending")
//					|| status.equalsIgnoreCase("pending_approval")) {
//				pendingCases += count;
//			}
//		}
//
//		// Validation: Ensure total case count matches between statuses and inspector
//		// sources
//		long statusTotal = casesByStatus.values().stream().mapToLong(Long::longValue).sum();
//		long sourceTotal = casesByInspectorSource.values().stream().mapToLong(Long::longValue).sum();
//
//		if (statusTotal != sourceTotal) {
//			throw new IllegalStateException("Mismatch between casesByStatus and casesByInspectorSource counts.");
//		}
//
//		return new CaseSummaryDTO(openCases, pendingCases, closedCases, casesByStatus, casesByInspectorSource);
//	}
	
	public CaseSummaryDTO getCaseSummary(String startDate, String endDate, String processType) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
	    Root<InspectionCase> root = query.from(InspectionCase.class);

	    List<Predicate> predicates = new ArrayList<>();

	    // Date filter
	    if (startDate != null && !startDate.isEmpty()) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfInspection"), startDate));
	    }
	    if (endDate != null && !endDate.isEmpty()) {
	        predicates.add(cb.lessThanOrEqualTo(root.get("dateOfInspection"), endDate));
	    }

	    // ************** PROCESS TYPE LOGIC **************
	    if (processType != null && !processType.trim().isEmpty()) {

	        if (processType.equalsIgnoreCase("ADP")) {
	            // ONLY ADP
	            predicates.add(cb.equal(cb.lower(root.get("processType")), "adp"));
	        } else {
	            // ANYTHING EXCEPT ADP (null, empty, manual, others)
	            predicates.add(
	                cb.or(
	                    cb.isNull(root.get("processType")),
	                    cb.equal(cb.trim(root.get("processType")), ""),
	                    cb.notEqual(cb.lower(root.get("processType")), "adp")
	                )
	            );
	        }
	    } 
	    // if processType not sent → DO NOT filter by process_type
	    // *************************************************

	    // Handle null values using coalesce
	    Expression<String> statusExpression = cb.coalesce(root.get("status"), "Unknown");
	    Expression<String> inspectorSourceExpression = cb.coalesce(root.get("inspector_source"), "Unknown");

	    query.multiselect(
	            cb.count(root).alias("total"),
	            statusExpression.alias("status"),
	            inspectorSourceExpression.alias("inspector_source")
	    ).where(predicates.toArray(new Predicate[0]))
	     .groupBy(statusExpression, inspectorSourceExpression);

	    Map<String, Long> casesByStatus = new HashMap<>();
	    Map<String, Long> casesByInspectorSource = new HashMap<>();
	    long openCases = 0, pendingCases = 0, closedCases = 0;

	    for (Object[] result : entityManager.createQuery(query).getResultList()) {
	        long count = (Long) result[0];
	        String status = (String) result[1];
	        String inspectorSource = (String) result[2];

	        // Status map count
	        casesByStatus.put(status, casesByStatus.getOrDefault(status, 0L) + count);

	        // Inspector source count
	        casesByInspectorSource.put(inspectorSource, casesByInspectorSource.getOrDefault(inspectorSource, 0L) + count);

	        // Open / Closed logic
	        switch (status.toLowerCase()) {
	            case "new":
	            case "reopened":
	            case "reinspection":
	            case "in_progress":
	            case "under_approval":
	            case "under_review":
	            case "unknown":
	            case "pending_review":
	            case "pending":
	            case "pending_approval":
	                openCases += count;
	                break;
	            case "closed":
	            case "completed":
	                closedCases += count;
	                break;
	        }

	        if (status.equalsIgnoreCase("pending_review") ||
	            status.equalsIgnoreCase("pending") ||
	            status.equalsIgnoreCase("pending_approval")) {
	            pendingCases += count;
	        }
	    }

	    return new CaseSummaryDTO(openCases, pendingCases, closedCases, casesByStatus, casesByInspectorSource);
	}


	// Fetch status counts (pending, in-progress, completed)

//	public List<Map<String, Object>> getCaseStatsByMonth(LocalDate startDate, LocalDate endDate) {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//		CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
//		Root<InspectionCase> root = query.from(InspectionCase.class);
//
//		// Convert `dateOfInspection` to DATE for filtering (PostgreSQL)
//		Expression<Date> dateExpression = cb.function("TO_DATE", Date.class, root.get("dateOfInspection"),
//				cb.literal("YYYY-MM-DD"));
//
//		// Define expressions for counting cases by status
//		Expression<Long> pendingCases = cb
//				.sum(cb.<Long>selectCase().when(cb.equal(root.get("status"), "pending"), 1L).otherwise(0L));
//
//		Expression<Long> inProgressCases = cb
//				.sum(cb.<Long>selectCase().when(cb.equal(root.get("status"), "in_progress"), 1L).otherwise(0L));
//
//		Expression<Long> completedCases = cb
//				.sum(cb.<Long>selectCase().when(cb.equal(root.get("status"), "completed"), 1L).otherwise(0L));
//
//		// Extract year and month for grouping
//		Expression<String> monthYear = cb.function("TO_CHAR", String.class, dateExpression, cb.literal("YYYY-MM"));
//
//		// Initialize predicates list
//		List<Predicate> predicates = new ArrayList<>();
//		predicates.add(cb.isNotNull(root.get("dateOfInspection")));
//		predicates.add(cb.notEqual(root.get("dateOfInspection"), ""));
//
//		// Apply date range filters
//		if (startDate != null) {
//			predicates.add(cb.greaterThanOrEqualTo(dateExpression, cb.literal(java.sql.Date.valueOf(startDate))));
//		}
//		if (endDate != null) {
//			predicates.add(cb.lessThanOrEqualTo(dateExpression, cb.literal(java.sql.Date.valueOf(endDate))));
//		}
//
//		// Build query
//		query.multiselect(monthYear, pendingCases, inProgressCases, completedCases)
//				.where(cb.and(predicates.toArray(new Predicate[0]))).groupBy(monthYear).orderBy(cb.asc(monthYear));
//
//		// Execute query
//		List<Object[]> resultList = entityManager.createQuery(query).getResultList();
//
//		// Process results
//
//		// Inside your DAO method, update the response processing section:
//		List<Map<String, Object>> response = new ArrayList<>();
//
//		for (Object[] result : resultList) {
//			String monthYear1 = (String) result[0]; // Expected format: "YYYY-MM"
//			Long pendingCount = (Long) result[1];
//			Long inProgressCount = (Long) result[2];
//			Long completedCount = (Long) result[3];
//
//			// Convert "YYYY-MM" into a full month name
//			YearMonth ym = YearMonth.parse(monthYear1, DateTimeFormatter.ofPattern("yyyy-MM"));
//			String formattedMonth = ym.getMonth().name(); // "APRIL"
//			formattedMonth = formattedMonth.charAt(0) + formattedMonth.substring(1).toLowerCase(); // "April"
//			String formattedMonthYear = formattedMonth + " " + ym.getYear(); // "April 2024"
//
//			// Calculate completion percentage safely
//			long totalCases = pendingCount + inProgressCount + completedCount;
//			long completionPercentage = totalCases > 0 ? (completedCount * 100) / totalCases : 0;
//
//			// Prepare monthly stats
//			Map<String, Object> monthlyStats = new HashMap<>();
//			monthlyStats.put("month", formattedMonthYear);
//			monthlyStats.put("completionPercentage", completionPercentage);
//			monthlyStats.put("pendingCases", pendingCount);
//			monthlyStats.put("inProgressCases", inProgressCount);
//			monthlyStats.put("completedCases", completedCount);
//
//			response.add(monthlyStats);
//		}
//
//		return response;
//	}
	
	public List<Map<String, Object>> getCaseStatsByMonth(LocalDate startDate, LocalDate endDate, String processType) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
	    Root<InspectionCase> root = query.from(InspectionCase.class);

	    // Convert `dateOfInspection` (String) to DATE
	    Expression<Date> dateExpression = cb.function(
	            "TO_DATE", Date.class, root.get("dateOfInspection"), cb.literal("YYYY-MM-DD")
	    );

	    // Status expressions
	    Expression<Long> pendingCases = cb.sum(
	            cb.<Long>selectCase().when(cb.equal(root.get("status"), "pending"), 1L).otherwise(0L)
	    );
	    Expression<Long> inProgressCases = cb.sum(
	            cb.<Long>selectCase().when(cb.equal(root.get("status"), "in_progress"), 1L).otherwise(0L)
	    );
	    Expression<Long> completedCases = cb.sum(
	            cb.<Long>selectCase().when(cb.equal(root.get("status"), "completed"), 1L).otherwise(0L)
	    );

	    Expression<String> monthYear = cb.function(
	            "TO_CHAR", String.class, dateExpression, cb.literal("YYYY-MM")
	    );

	    List<Predicate> predicates = new ArrayList<>();
	    predicates.add(cb.isNotNull(root.get("dateOfInspection")));
	    predicates.add(cb.notEqual(root.get("dateOfInspection"), ""));

	    // 🔥 PROCESS TYPE LOGIC
	    if (processType != null && !processType.trim().isEmpty()) {
	        if (processType.equalsIgnoreCase("ADP")) {
	            predicates.add(cb.equal(cb.lower(root.get("processType")), "adp"));
	        } else {
	            Predicate notAdp = cb.or(
	                    cb.notEqual(cb.lower(root.get("processType")), "adp"),
	                    cb.isNull(root.get("processType")),
	                    cb.equal(cb.trim(root.get("processType")), "")
	            );
	            predicates.add(notAdp);
	        }
	    }
	    // If NULL or empty → No process filter applied

	    // Date range filters
	    if (startDate != null) {
	        predicates.add(
	                cb.greaterThanOrEqualTo(dateExpression, java.sql.Date.valueOf(startDate))
	        );
	    }
	    if (endDate != null) {
	        predicates.add(
	                cb.lessThanOrEqualTo(dateExpression, java.sql.Date.valueOf(endDate))
	        );
	    }

	    // Build query
	    query.multiselect(monthYear, pendingCases, inProgressCases, completedCases)
	            .where(cb.and(predicates.toArray(new Predicate[0])))
	            .groupBy(monthYear)
	            .orderBy(cb.asc(monthYear));

	    // Execute query
	    List<Object[]> resultList = entityManager.createQuery(query).getResultList();

	    // 🧾 Prepare final response
	    List<Map<String, Object>> response = new ArrayList<>();

	    for (Object[] result : resultList) {
	        String monthYear1 = (String) result[0];
	        Long pending = (Long) result[1];
	        Long inProgress = (Long) result[2];
	        Long completed = (Long) result[3];

	        YearMonth ym = YearMonth.parse(monthYear1, DateTimeFormatter.ofPattern("yyyy-MM"));
	        String formattedMonth = ym.getMonth().name();
	        formattedMonth = formattedMonth.charAt(0) + formattedMonth.substring(1).toLowerCase();
	        String formattedMonthYear = formattedMonth + " " + ym.getYear();

	        long totalCases = pending + inProgress + completed;
	        long completionPercentage = totalCases > 0 ? (completed * 100) / totalCases : 0;

	        Map<String, Object> monthlyStats = new HashMap<>();
	        monthlyStats.put("month", formattedMonthYear);
	        monthlyStats.put("completionPercentage", completionPercentage);
	        monthlyStats.put("pendingCases", pending);
	        monthlyStats.put("inProgressCases", inProgress);
	        monthlyStats.put("completedCases", completed);

	        response.add(monthlyStats);
	    }

	    return response;
	}


//	public List<InspectionCase> getCasesCountByFilter(InspectionFilters inspectionFilters) {
//		List<InspectionCase> inspectionCases = new ArrayList<InspectionCase>();
//		try (Session session = sessionFactory.openSession()) {
//			CriteriaBuilder cb = session.getCriteriaBuilder();
//			CriteriaQuery<InspectionCase> query = cb.createQuery(InspectionCase.class);
//			Root<InspectionCase> root = query.from(InspectionCase.class);
//
//			List<Predicate> filters = new ArrayList<Predicate>();
//
//			filters.add(cb.between(
//					cb.function("TO_DATE", LocalDate.class, root.get("dateOfInspection"), cb.literal("yyyy-MM-dd")),
//					inspectionFilters.getFromDate(), inspectionFilters.getToDate()));
//			filters.add(cb.equal(root.get("inspectorID"), (inspectionFilters.getInspectorId())));
//			filters.add(root.get("inspectionType").in(inspectionFilters.getInspectionTypeIds()));
//			filters.add(root.get("inspector_source").in(inspectionFilters.getInspectionSource()));
//
//			query.where(cb.and(filters.toArray(new Predicate[0])));
//
//			inspectionCases = session.createQuery(query).getResultList();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return inspectionCases;
//	}
	
	
	public List<InspectionCase> getCasesCountByFilter(InspectionFilters inspectionFilters) {
	    List<InspectionCase> inspectionCases = new ArrayList<>();

	    try (Session session = sessionFactory.openSession()) {
	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<InspectionCase> query = cb.createQuery(InspectionCase.class);
	        Root<InspectionCase> root = query.from(InspectionCase.class);

	        List<Predicate> filters = new ArrayList<>();

	        filters.add(cb.between(
	                cb.function("TO_DATE", LocalDate.class, root.get("dateOfInspection"), cb.literal("yyyy-MM-dd")),
	                inspectionFilters.getFromDate(), inspectionFilters.getToDate()));

	        filters.add(cb.equal(root.get("inspectorID"), inspectionFilters.getInspectorId()));
	        filters.add(root.get("inspectionType").in(inspectionFilters.getInspectionTypeIds()));

	        if (inspectionFilters.getInspectionSource() != null && !inspectionFilters.getInspectionSource().isEmpty()) {
	            filters.add(root.get("inspector_source").in(inspectionFilters.getInspectionSource()));
	        }

	        // ⭐ Correct processType filtering
	        String processType = inspectionFilters.getProcessType();
	        if (processType != null && !processType.trim().isEmpty()) {
	            if (processType.equalsIgnoreCase("ADP")) {
	                // only ADP
	                filters.add(cb.equal(root.get("processType"), "ADP"));
	            } else {
	                // NOT ADP → include NULL, empty, and all others
	                Predicate notAdp = cb.notEqual(root.get("processType"), "ADP");
	                Predicate nullProcess = cb.isNull(root.get("processType"));
	                Predicate emptyProcess = cb.equal(cb.trim(root.get("processType")), "");

	                filters.add(cb.or(notAdp, nullProcess, emptyProcess));
	            }
	        }

	        query.where(cb.and(filters.toArray(new Predicate[0])));
	        inspectionCases = session.createQuery(query).getResultList();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return inspectionCases;
	}

	
	  public Optional<InspectionCase> getInspectionCaseById(Long inspectionID) {
	        Session session = sessionFactory.openSession();
	        try {
	            CriteriaBuilder cb = session.getCriteriaBuilder();
	            CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
	            Root<InspectionCase> root = cq.from(InspectionCase.class);
	            cq.select(root).where(cb.equal(root.get("inspectionID"), inspectionID));

	            return session.createQuery(cq).uniqueResultOptional();
	        } finally {
	            session.close();
	        }
	    }
	    // Update assignedCategory in InspectionCase
	    public void updatingInspectionCase(InspectionCase inspectionCase) {
	        Session session = sessionFactory.openSession();
	        try {
	            session.beginTransaction();
	            session.merge(inspectionCase); // Updates the assignedCategory field
	            session.getTransaction().commit();
	        } catch (Exception e) {
	            session.getTransaction().rollback();
	            throw e;
	        } finally {
	            session.close();
	        }
	   
	}
	    
	    public void save(InspectionCase inspectionCase) {
	        entityManager.persist(inspectionCase);
	    }
	    
	    @Transactional // ✅ Ensures a transaction is active when persisting attachments
		public void saveAttachments(List<InspectionAttachments> attachments) {
			for (InspectionAttachments attachment : attachments) {
				entityManager.merge(attachment);
			}
		}
		
		
		  @Transactional // Ensure database operations execute within a transaction
		    public void deleteAttachmentsByInspectionDetails(long inspectionID, long categoryID, long checklistID) {
		        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		        CriteriaDelete<InspectionAttachments> deleteQuery = cb.createCriteriaDelete(InspectionAttachments.class);
		        Root<InspectionAttachments> root = deleteQuery.from(InspectionAttachments.class);

		        deleteQuery.where(
		            cb.equal(root.get("inspectionChecklistandAnswers").get("id").get("inspectionID"), inspectionID),
		            cb.equal(root.get("inspectionChecklistandAnswers").get("id").get("categoryID"), categoryID),
		            cb.equal(root.get("inspectionChecklistandAnswers").get("id").get("checklistID"), checklistID)
		        );

		        entityManager.createQuery(deleteQuery).executeUpdate();
		    }

		  public List<byte[]> getAttachmentsByChecklistId(long checklistId, long inspectionID, long categoryID) {
			    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			    CriteriaQuery<byte[]> query = cb.createQuery(byte[].class);
			    Root<InspectionAttachments> root = query.from(InspectionAttachments.class);

			  
			    Predicate checklistPredicate = cb.equal(root.get("inspectionChecklistandAnswers").get("id").get("checklistID"), checklistId);
			    Predicate checklistPredicate2 = cb.equal(root.get("inspectionChecklistandAnswers").get("id").get("inspectionID"), inspectionID);
			    Predicate checklistPredicate3 = cb.equal(root.get("inspectionChecklistandAnswers").get("id").get("categoryID"), categoryID);


			    query.select(root.get("attachment")).where(cb.and(checklistPredicate, checklistPredicate2, checklistPredicate3));

			    return entityManager.createQuery(query).getResultList();
			}


		  public List<InspectionCase> findByEntityId(String entityId) {
		       CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		       CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
		       Root<InspectionCase> inspectionCaseRoot = cq.from(InspectionCase.class);

		       // Use the correct field name 'entityid' instead of 'entityId'
		       cq.where(cb.equal(inspectionCaseRoot.get("entity").get("entityid"), entityId));

		       return entityManager.createQuery(cq).getResultList();
		   }
		  
}

