package com.aaseya.AIS.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.dto.GetAllInspection_TypeDTO;
import com.aaseya.AIS.dto.InspectionTypeAdminSkillDTO;
import com.aaseya.AIS.dto.SkillDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class InspectionTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@PersistenceContext
	private final EntityManager entityManager;
	
	@Autowired
    private CriteriaPaginationUtil paginationUtil;

	public void saveInspectionType(Inspection_Type inspectiontype) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();

		session.persist(inspectiontype);
		transaction.commit();
		session.close();
	}

	public List<Inspection_Type> getAllInspectionTypes() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<Inspection_Type> inspectiontypes = null;

		try {
			transaction = session.beginTransaction();
			Query<Inspection_Type> query = session.createQuery("FROM Inspection_Type", Inspection_Type.class);
			inspectiontypes = query.getResultList();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectiontypes;
	}

	public List<String> getAllInspectionTypeNames() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<String> inspectionTypeNames = null;

		try {
			transaction = session.beginTransaction();
			Query<String> query = session.createQuery("SELECT name FROM Inspection_Type", String.class);
			inspectionTypeNames = query.getResultList();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectionTypeNames;
	}

	public Inspection_Type getInspectionTypeByName(String name) {
		Session session = null;
		Inspection_Type inspectionType = null;
		try {
			session = sessionFactory.openSession();
			inspectionType = session
					.createQuery("FROM Inspection_Type WHERE name = :name", Inspection_Type.class)
					.setParameter("name", name).uniqueResult();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return inspectionType;
	}

	// Method to find an InspectionType by name (or other criteria)
	public Inspection_Type findByName(String name) {
		Session session = sessionFactory.openSession();
		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Inspection_Type> criteriaQuery = criteriaBuilder.createQuery(Inspection_Type.class);
			Root<Inspection_Type> root = criteriaQuery.from(Inspection_Type.class);
			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), name));

			List<Inspection_Type> result = session.createQuery(criteriaQuery).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} finally {
			session.close();
		}
	}

//	public void save(Inspection_Type inspectionType) {
//		Session session = sessionFactory.openSession();
//		try {
//			session.beginTransaction();
//			session.merge(inspectionType); // This will save or update the Inspection_Type
//			session.getTransaction().commit();
//		} finally {
//			session.close();
//		}
//	}
	
	@Transactional
	public void save(Inspection_Type inspectionType) {
	    entityManager.merge(inspectionType);
	}

	///// AddInspectionTypeToEntities

	public Inspection_Type getInspectionTypeById(String inspectionTypeId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Inspection_Type inspectionType = null;

		try {
			transaction = session.beginTransaction();
			Query<Inspection_Type> query = session
					.createQuery("FROM Inspection_Type WHERE ins_type_id = :inspectionTypeId", Inspection_Type.class);
			query.setParameter("inspectionTypeId", Long.parseLong(inspectionTypeId));
			inspectionType = query.getSingleResult();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectionType;
	}

	//// Add Inspectiontype to entities///

	public List<GetAllInspection_TypeDTO> findAllInspectionIdsAndNames() {
		// Unwrap the entity manager to obtain a JPA CriteriaBuilder instance
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Define the return type for the query
		CriteriaQuery<GetAllInspection_TypeDTO> query = cb.createQuery(GetAllInspection_TypeDTO.class);

		// Specify the root of the query (the entity to fetch data from)
		Root<Inspection_Type> root = query.from(Inspection_Type.class);

		// Construct the selection using the fields required for the DTO
		query.select(cb.construct(GetAllInspection_TypeDTO.class, root.get("ins_type_id"), // Assuming `ins_type_id` is
																							// the field in
																							// Inspection_Type
				root.get("name") // Assuming `name` is the field in Inspection_Type
		));

		// Create and execute the query
		return entityManager.createQuery(query).getResultList();
	}

	@Transactional
	public InspectionTypeAdminSkillDTO getInspectionTypeById(long inspectionTypeId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Inspection_Type> query = cb.createQuery(Inspection_Type.class);
		Root<Inspection_Type> inspectionTypeRoot = query.from(Inspection_Type.class);
		query.select(inspectionTypeRoot).where(cb.equal(inspectionTypeRoot.get("ins_type_id"), inspectionTypeId));
		Inspection_Type inspection_Type = entityManager.createQuery(query).getSingleResult();
		InspectionTypeAdminSkillDTO inspectionTypeAdminSkillDTO = new InspectionTypeAdminSkillDTO();
		// set the fields of inspectiontype admin DTO from inspectionType retrive from
		// DB
		inspectionTypeAdminSkillDTO.setName(inspection_Type.getName());
		inspectionTypeAdminSkillDTO.setThreshold(inspection_Type.getThreshold());
		inspectionTypeAdminSkillDTO.setIsActive(String.valueOf(inspection_Type.isActive()));
		inspectionTypeAdminSkillDTO.setHigh(inspection_Type.getHigh());
		inspectionTypeAdminSkillDTO.setMedium(inspection_Type.getMedium());
		inspectionTypeAdminSkillDTO.setLow(inspection_Type.getLow());
		inspectionTypeAdminSkillDTO.setEntitySize(inspection_Type.getEntitySize());
		
		// Fetch and set control type name and id if available
	    if (inspection_Type.getControlTypes() != null && !inspection_Type.getControlTypes().isEmpty()) {
	        // Assuming ControlType is an entity and you want to get both the id and the name
	        ControlType controlType = inspection_Type.getControlTypes().iterator().next(); // Get the first control type
	        inspectionTypeAdminSkillDTO.setControlTypeName(controlType.getControlTypeName()); // Assuming getControlTypeName() exists in ControlType
	        inspectionTypeAdminSkillDTO.setControlTypeId(controlType.getControlTypeId()); // Assuming getControlTypeId() exists in ControlType
	    } else {
	        inspectionTypeAdminSkillDTO.setControlTypeName("Not Available"); // Default value if no control type is found
	        inspectionTypeAdminSkillDTO.setControlTypeId(-1); // Default value, adjust as needed
	    }


		// get list of skills associated with inspection type
		Set<Skill> skills = inspection_Type.getSkills();
		List<SkillDTO> skillDTOs = new ArrayList<>();
		for (Skill skill : skills) {
			SkillDTO skillDTO = new SkillDTO(skill.getSkillId(), skill.getSkill());
			skillDTO.setActive(skill.isActive());

			// get the list of inspection types names associated with above all skills and
			// set in skill dto

			CriteriaBuilder cb1 = entityManager.getCriteriaBuilder();

			// Define the CriteriaQuery to fetch inspection type names
			CriteriaQuery<String> criteriaQuery = cb1.createQuery(String.class);

			// Define the root for Inspection_Type
			Root<Inspection_Type> inspectionTypeRoot1 = criteriaQuery.from(Inspection_Type.class);

			// Join the skills
			Join<Inspection_Type, Skill> skillJoin = inspectionTypeRoot1.join("skills", JoinType.INNER);

			// Select the name field and apply the condition
			criteriaQuery.select(inspectionTypeRoot1.get("name"))
					.where(cb1.equal(skillJoin.get("skillId"), skill.getSkillId()));

			// Execute the query using the EntityManager
			List<String> inspectionTypeNames = entityManager.createQuery(criteriaQuery).getResultList();

			// Set the inspection type names in the SkillDTO
			skillDTO.setInspectionTypeNames(inspectionTypeNames);

			// Add the SkillDTO to the list
			skillDTOs.add(skillDTO);
		}
		// set skill dto in inspectiontypeadmin dto

		inspectionTypeAdminSkillDTO.setSkills(skillDTOs);

		return inspectionTypeAdminSkillDTO;
	}

	public Inspection_Type getInspectionTypeWithSla(long insTypeId) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaQuery<Inspection_Type> query = cb.createQuery(Inspection_Type.class);
		Root<Inspection_Type> root = query.from(Inspection_Type.class);

		root.fetch("inspectionSLAs", JoinType.LEFT); // Join with Inspection_SLA
		query.select(root).where(cb.equal(root.get("ins_type_id"), insTypeId));

		return session.createQuery(query).uniqueResult();
	}

	public List<Skill> getSkillsByInspectionTypeId(Long insTypeId) {
		Session session = sessionFactory.openSession();
		try {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Skill> query = cb.createQuery(Skill.class);
			Root<Skill> root = query.from(Skill.class);
			Join<Skill, Inspection_Type> join = root.join("inspectionTypes");
			query.select(root).where(cb.equal(join.get("ins_type_id"), insTypeId));
			return session.createQuery(query).getResultList();
		} finally {
			session.close();
		}
	}

	public Inspection_Type getInspectionTypeByInspectionId(long insTypeId) {
		Transaction transaction = null;
		Inspection_Type inspectionType = null;

		Session session = sessionFactory.openSession();
		try {
			transaction = session.beginTransaction();

			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

			CriteriaQuery<Inspection_Type> criteriaQuery = criteriaBuilder.createQuery(Inspection_Type.class);

			Root<Inspection_Type> root = criteriaQuery.from(Inspection_Type.class);

			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("ins_type_id"), insTypeId));

			inspectionType = session.createQuery(criteriaQuery).uniqueResult();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return inspectionType;
	}

	public Set<Inspection_Type> findByIds(List<Long> inspectionTypeIds) {
		Set<Inspection_Type> inspection_Types = null;
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Inspection_Type> query = cb.createQuery(Inspection_Type.class);

			Root<Inspection_Type> root = query.from(Inspection_Type.class);

			query.select(root).where(root.get("ins_type_id").in(inspectionTypeIds));

			if (session.createQuery(query).getResultList() != null)
				return session.createQuery(query).getResultList().stream().collect(Collectors.toSet());
			else
				return inspection_Types;
		}
	}
	
	public InspectionTypeDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Inspection_Type findById(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inspection_Type> query = cb.createQuery(Inspection_Type.class);
        Root<Inspection_Type> root = query.from(Inspection_Type.class);

        query.select(root).where(cb.equal(root.get("ins_type_id"), id));

        List<Inspection_Type> results = entityManager.createQuery(query).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public NewEntity findEntityById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity ID must not be null or empty.");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<NewEntity> query = cb.createQuery(NewEntity.class);
        Root<NewEntity> root = query.from(NewEntity.class);

        query.select(root).where(cb.equal(root.get("entityid"), id));

        List<NewEntity> results = entityManager.createQuery(query).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void update(Inspection_Type inspectionType) {
        entityManager.merge(inspectionType);
    }
	
	public boolean existsByName(String name) {
	    Session session = sessionFactory.openSession();
	    try {
	        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
	        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
	        Root<Inspection_Type> root = criteriaQuery.from(Inspection_Type.class);
	        criteriaQuery.select(criteriaBuilder.count(root)).where(criteriaBuilder.equal(root.get("name"), name));

	        Long count = session.createQuery(criteriaQuery).getSingleResult();
	        return count > 0;
	    } finally {
	        session.close();
	    }
	}
	
	 public boolean existsByNameExcludingId(String name, long id) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
	        Root<Inspection_Type> root = cq.from(Inspection_Type.class);

	        Predicate namePredicate = cb.equal(root.get("name"), name);
	        Predicate excludeIdPredicate = cb.notEqual(root.get("ins_type_id"), id);

	        cq.select(cb.count(root)).where(cb.and(namePredicate, excludeIdPredicate));

	        Long count = entityManager.createQuery(cq).getSingleResult();
	        return count != null && count > 0;
	    }
	 
	 public Page<Inspection_Type> getAllInspectionTypes(Pageable pageable) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<Inspection_Type> query = cb.createQuery(Inspection_Type.class);
	        Root<Inspection_Type> root = query.from(Inspection_Type.class);

	        // Fetch related skills eagerly
	        root.fetch("skills", JoinType.LEFT);
	        query.select(root).distinct(true);

	        // Use CriteriaPaginationUtil to paginate
	        return paginationUtil.getPaginatedData(query, pageable, Inspection_Type.class, Function.identity());
	    }
	 public ControlType findByControlTypeId(long id) {
	        try (Session session = sessionFactory.openSession()) {
	            // Step 1: Get CriteriaBuilder instance
	            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

	            // Step 2: Create CriteriaQuery object for ControlType
	            CriteriaQuery<ControlType> criteriaQuery = criteriaBuilder.createQuery(ControlType.class);

	            // Step 3: Define root (the entity we are querying)
	            Root<ControlType> root = criteriaQuery.from(ControlType.class);

	            // Step 4: Add a condition (WHERE clause) to filter by ID
	            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("controlTypeId"), id));

	            // Step 5: Execute the query
	            Query<ControlType> query = session.createQuery(criteriaQuery);
	            return query.uniqueResult();  // Return a single result or null if not found
	        } catch (Exception e) {
	            // Handle exception (e.g., log it)
	            e.printStackTrace();
	            return null;
	        }
	    }
	 public List<GetAllInspection_TypeDTO> findInspectionIdsAndNamesByControlTypeId(long controlTypeId) {
		    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		    CriteriaQuery<GetAllInspection_TypeDTO> query = cb.createQuery(GetAllInspection_TypeDTO.class);
		    Root<Inspection_Type> inspectionTypeRoot = query.from(Inspection_Type.class);

		    // Join with ControlType entity
		    Join<Object, Object> controlTypeJoin = inspectionTypeRoot.join("controlTypes", JoinType.INNER);

		    // Select only required fields (ID and Name)
		    query.select(cb.construct(
		            GetAllInspection_TypeDTO.class,
		            inspectionTypeRoot.get("id"),
		            inspectionTypeRoot.get("name")
		    )).where(cb.equal(controlTypeJoin.get("controlTypeId"), controlTypeId));

		    return entityManager.createQuery(query).getResultList();
		}


	   @Transactional
	    public boolean isInspectionTypeExists(String inspectionTypeName) {
	        try {
	            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	            CriteriaQuery<Long> query = cb.createQuery(Long.class);
	            Root<Inspection_Type> root = query.from(Inspection_Type.class);

	            query.select(cb.count(root))
	                 .where(cb.equal(cb.lower(root.get("name")), inspectionTypeName.toLowerCase()));

	            Long count = entityManager.createQuery(query).getSingleResult();
	            return count != null && count > 0;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    @Transactional
	    public Inspection_Type getInspectionTypesByName(String inspectionTypeName) {
	        try {
	            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	            CriteriaQuery<Inspection_Type> query = cb.createQuery(Inspection_Type.class);
	            Root<Inspection_Type> root = query.from(Inspection_Type.class);

	            query.select(root)
	                 .where(cb.equal(cb.lower(root.get("name")), inspectionTypeName.toLowerCase()));

	            List<Inspection_Type> result = entityManager.createQuery(query).getResultList();
	            return result.isEmpty() ? null : result.get(0);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public void linkEntityWithInspectionType(Long inspectionTypeId, String entityId) {
	        // Check if mapping already exists
	        String checkQuery = "SELECT COUNT(*) FROM Inspection_Type_Entity WHERE ins_type_id = :inspectionTypeId AND entity_id = :entityId";
	        Long count = ((Number) entityManager.createNativeQuery(checkQuery)
	                .setParameter("inspectionTypeId", inspectionTypeId)
	                .setParameter("entityId", entityId)
	                .getSingleResult()).longValue();

	        if (count == 0) {
	            // Only insert if not already linked
	            String insertQuery = "INSERT INTO Inspection_Type_Entity (ins_type_id, entity_id) VALUES (:inspectionTypeId, :entityId)";
	            entityManager.createNativeQuery(insertQuery)
	                    .setParameter("inspectionTypeId", inspectionTypeId)
	                    .setParameter("entityId", entityId)
	                    .executeUpdate();
	        }
	    }
	
}
