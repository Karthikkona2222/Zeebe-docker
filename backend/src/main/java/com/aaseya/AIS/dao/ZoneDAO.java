package com.aaseya.AIS.dao;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.SubSegment;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dto.ZoneDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.persistence.criteria.CriteriaBuilder.In;

@Repository
public class ZoneDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@PersistenceContext
	private EntityManager entityManager;

	public void saveZone(Zone zone) {
		// Create a session object
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();

		session.persist(zone);
		transaction.commit();
		session.close();
	}

	public List<Zone> getZoneName(List<String> names) {
		if (names == null || names.isEmpty()) {
			return Collections.emptyList(); // Return an empty list if names is null or empty
		}

		Session session = sessionFactory.openSession();

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Zone> cr = cb.createQuery(Zone.class);
		Root<Zone> root = cr.from(Zone.class);

		cr.select(root).where(root.get("name").in(names));

		List<Zone> zones = session.createQuery(cr).getResultList();
		session.close();
		return zones;
	}

////////////////////////////////fetch all zone details based on inspection id//////////
	public Zone getZoneByIdforZones(String zoneId) {
		Session session = null;
		Zone zone = null;
		try {
			session = sessionFactory.openSession();
			zone = session.get(Zone.class, zoneId);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return zone;
	}

////////////////////////////////fetch all zone details based on inspection id//////////
	public Set<Zone> findByIds(List<String> zoneIds) {
		Session session = null;
		Set<Zone> zones = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Zone> cq = cb.createQuery(Zone.class);
			Root<Zone> root = cq.from(Zone.class);
			cq.select(root).where(root.get("zoneId").in(zoneIds));
			zones = session.createQuery(cq).getResultList().stream().collect(Collectors.toSet());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return zones;
	}

	public void saveZoneWithCriteria(Zone zone) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Zone> cq = cb.createQuery(Zone.class);
			Root<Zone> root = cq.from(Zone.class);
			cq.select(root).where(cb.equal(root.get("name"), zone.getName()));
			List<Zone> existingZones = session.createQuery(cq).getResultList();

			if (existingZones.isEmpty()) {
				session.persist(zone);
				transaction.commit();
			} else {
				throw new RuntimeException("Zone with the given name already exists");
			}
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

/////////////////Get all zones for user registration and add zone///////
	public List<ZoneDTO> getAllZones1() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Zone> query = cb.createQuery(Zone.class);
		Root<Zone> root = query.from(Zone.class);
		query.select(root);

		List<Zone> zones = entityManager.createQuery(query).getResultList();

		return zones.stream().map(zone -> new ZoneDTO(zone.getZoneId(), zone.getName(), zone.getIsDefaultZone()))
				.peek(System.out::println).collect(Collectors.toList());
	}

	public Zone getZoneById(String zoneId) {
		Session session = null;
		Transaction transaction = null;
		Zone zone = null;

		try {
			// Open a new session
			session = sessionFactory.openSession();
			// Begin transaction
			transaction = session.beginTransaction();

			// Retrieve the Zone object by ID
			zone = session.get(Zone.class, zoneId); // Use zoneId directly

			// Commit the transaction
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback(); // Rollback in case of an error
			}
			e.printStackTrace(); // Log the exception
		} finally {
			if (session != null) {
				session.close(); // Ensure the session is closed
			}
		}

		return zone; // Return the retrieved Zone object
	}
}
