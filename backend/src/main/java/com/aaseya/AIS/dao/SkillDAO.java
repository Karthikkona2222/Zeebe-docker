package com.aaseya.AIS.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dto.SkillInspectionTypeDTO;
import com.aaseya.AIS.dto.SkillsDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class SkillDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@PersistenceContext
	private final EntityManager entityManager;
	
	private final CriteriaPaginationUtil paginationUtil;

	@Autowired
	public SkillDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.paginationUtil = paginationUtil;
	}

	public Skill saveSkillAndAssociation(Skill skill, List<Long> ins_type_id) {
		Transaction transaction = null;
		System.out.println(skill);
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();

			for (Long ins_type_id1 : ins_type_id) {
				Inspection_Type inspectionType = session.get(Inspection_Type.class, ins_type_id1);
				if (inspectionType != null) {
					skill.getInspectionTypes().add(inspectionType);
					inspectionType.getSkills().add(skill);
					System.out.println(inspectionType);
				}
			}
			System.out.println(skill);
			session.merge(skill);
			//session.persist(skill);
			transaction.commit();

			return skill;
		} catch (Exception e) {
//			if (transaction != null) {
//				transaction.rollback();
//			}
//			throw e;
		}
		return skill;
	}

//	public Skill save(Skill skill) {
//		return save(skill);
//	}
	public List<Skill> findByNames(List<String> skillNames) {
		Session session = null;
		List<Skill> skills = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Skill> cq = cb.createQuery(Skill.class);
			Root<Skill> root = cq.from(Skill.class);
			cq.select(root).where(root.get("skill").in(skillNames));
			skills = session.createQuery(cq).getResultList();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return skills;
	}

	public Skill findBySkill(String skillName) {
		Session session = sessionFactory.openSession();
		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Skill> criteriaQuery = criteriaBuilder.createQuery(Skill.class);
			Root<Skill> root = criteriaQuery.from(Skill.class);
			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("skill"), skillName));

			List<Skill> result = session.createQuery(criteriaQuery).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} finally {
			session.close();
		}
	}

	public void save(Skill skill) {
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			session.merge(skill); // Save or update the skill
			session.getTransaction().commit();
		} finally {
			session.close();
		}
	}

	public List<Skill> findSkillsBySkillName() {		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Skill> query = cb.createQuery(Skill.class);
		Root<Skill> root = query.from(Skill.class);

		query.select(root);
		
		return entityManager.createQuery(query).getResultList();
	}	

	// Get all skills with inspection type for add inspection type

	public Page<SkillInspectionTypeDTO> getSkillandRelatedInspectionTypes(Pageable pageable) {
		// Open a session from the SessionFactory
		Session session = sessionFactory.openSession();
 
		// Initialize CriteriaBuilder
		CriteriaBuilder cb = session.getCriteriaBuilder();
 
		// Create CriteriaQuery for Skill entity
		CriteriaQuery<Skill> cq = cb.createQuery(Skill.class);
		Root<Skill> skillRoot = cq.from(Skill.class);
 
		// Select the root without an explicit join or where clause
		cq.select(skillRoot).distinct(true);
 
		// Execute the query and fetch the skills
		List<Skill> skills = session.createQuery(cq).getResultList();
 
		// Close the session (best practice to avoid memory leaks)
		session.close();
 
		// Transform the result into DTOs
		List<SkillInspectionTypeDTO> result = new ArrayList<>();
 
		for (Skill skill : skills) {
			// Create a new list of InspectionDTOs
			List<String> inspectionDTOs = new ArrayList<>();
 
			// Loop through the Inspection_Type entities associated with the current skill
			if (skill.getInspectionTypes() != null) { // Avoid null pointer exceptions
				for (Inspection_Type inspectionType : skill.getInspectionTypes()) {
					// Add the inspection type name to the list
					inspectionDTOs.add(inspectionType.getName());
				}
			}
 
			// Create the SkillInspectionDTO for the current skill and add it to the result
			// list
			SkillInspectionTypeDTO skillInspectionDTO = new SkillInspectionTypeDTO();
			skillInspectionDTO.setSkill(skill.getSkill());
			skillInspectionDTO.setSkillId(skill.getSkillId());
			skillInspectionDTO.setActive(skill.isActive());
			skillInspectionDTO.setInspectiontype(inspectionDTOs);
			result.add(skillInspectionDTO);
		}
 
		return paginationUtil.getPaginatedData(
	            cq,
	            pageable,
	            Skill.class,
	            skill -> {
	                // Transform Skill entity to SkillInspectionTypeDTO
	                SkillInspectionTypeDTO dto = new SkillInspectionTypeDTO();
	                dto.setSkill(skill.getSkill());
	                dto.setSkillId(skill.getSkillId());
	                dto.setActive(skill.isActive());
 
	                // Map associated inspection types
	                if (skill.getInspectionTypes() != null) {
	                    List<String> inspectionTypes = skill.getInspectionTypes()
	                            .stream()
	                            .map(Inspection_Type::getName)
	                            .collect(Collectors.toList());
	                    dto.setInspectiontype(inspectionTypes);
	                }
 
	                return dto;
	            }
	    );
	}

	public Skill findById(long skillId) {
        Session session = null;
        Transaction transaction = null;
        Skill skill = null;

        try {
            session = sessionFactory.openSession(); 
            transaction = session.beginTransaction(); 

            skill = session.get(Skill.class, skillId); 

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); 
            }
            e.printStackTrace(); 
        } finally {
            if (session != null) {
                session.close(); 
            }
        }

        return skill;
    }
	 // Method to update skill relationships
    public void updateSkillRelationships(long skillId, List<Long> inspectionIds, long userId) {
        // Fetch the Skill entity
        Skill skill = entityManager.find(Skill.class, skillId);
        if (skill == null) {
            throw new IllegalArgumentException("Skill not found with ID: " + skillId);
        }

        // Fetch the User entity
        Users user = entityManager.find(Users.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        // Fetch the Inspection_Type entities
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inspection_Type> query = builder.createQuery(Inspection_Type.class);
        Root<Inspection_Type> root = query.from(Inspection_Type.class);
        query.select(root).where(root.get("id").in(inspectionIds));

        List<Inspection_Type> inspectionTypes = entityManager.createQuery(query).getResultList();
        if (inspectionTypes.isEmpty()) {
            throw new IllegalArgumentException("No Inspection Types found with the provided IDs");
        }

        // Update relationships
        skill.getUsers().add(user); // Associate the user with the skill
        skill.getInspectionTypes().addAll(inspectionTypes); // Associate the inspection types with the skill

        // Save changes
        entityManager.merge(skill);
    }

    public void updateSkillRelationships(long skillId, List<Long> inspectionIds, List<Long> userIds) {
        // Fetch the Skill entity
        Skill skill = entityManager.find(Skill.class, skillId);
        if (skill == null) {
            throw new IllegalArgumentException("Skill not found with ID: " + skillId);
        }

        // Fetch the User entities
        List<Users> users = findUsersByIds(userIds);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found with the provided IDs");
        }

        // Fetch the Inspection_Type entities
        List<Inspection_Type> inspectionTypes = findInspectionTypesByIds(inspectionIds);
        if (inspectionTypes.isEmpty()) {
            throw new IllegalArgumentException("No Inspection Types found with the provided IDs");
        }

        // Update relationships: Add users and inspection types, ensuring no duplicates
        for (Users user : users) {
            if (!skill.getUsers().contains(user)) {
                skill.getUsers().add(user); // Associate the user with the skill
            }
        }

        for (Inspection_Type inspectionType : inspectionTypes) {
            skill.getInspectionTypes().add(inspectionType); // Associate the inspection types with the skill
        }

        // Save changes to the database
        entityManager.merge(skill);
    }

    /**
     * Check if a skill exists by name
     */
    public boolean existsBySkillName(String skillName) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Skill> root = query.from(Skill.class);

        query.select(builder.count(root))
             .where(builder.equal(root.get("skill"), skillName));

        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    /**
     * Find a Skill entity by ID
     */
    public Skill findById(Long skillId) {
        return entityManager.find(Skill.class, skillId);
    }

    /**
     * Save or update a Skill entity
     */
    public void saveSkill(Skill skill) {
        if (skill.getSkillId() == 0) {  // Skill is new, hence use persist
            entityManager.persist(skill);
        } else {
            entityManager.merge(skill);  // Update existing skill
        }
    }

    /**
     * Find a list of Inspection_Type entities by their IDs
     */
    public List<Inspection_Type> findInspectionTypesByIds(List<Long> ids) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inspection_Type> query = builder.createQuery(Inspection_Type.class);
        Root<Inspection_Type> root = query.from(Inspection_Type.class);

        query.select(root).where(root.get("id").in(ids));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Find a list of Users entities by their IDs
     */
    public List<Users> findUsersByIds(List<Long> ids) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Users> query = builder.createQuery(Users.class);
        Root<Users> root = query.from(Users.class);

        query.select(root).where(root.get("id").in(ids));

        return entityManager.createQuery(query).getResultList();
    }
}


