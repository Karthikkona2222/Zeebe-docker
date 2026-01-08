package com.aaseya.AIS.dao;

import java.time.DayOfWeek;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.UserSkill;
import com.aaseya.AIS.Model.UserSkillId;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dto.GetAllSkillDTO;
import com.aaseya.AIS.dto.UserSkillDTO;
import com.aaseya.AIS.dto.UsersDTO;
import com.aaseya.AIS.dto.UsersRoleDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class UsersDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@PersistenceContext
	private final EntityManager entityManager;

	@Autowired
	private UserSkillDAO userSkillDAO;

	@Autowired
	private SkillDAO skillDAO;

	@Autowired
	private ZoneDAO zoneDAO;

	private final CriteriaPaginationUtil paginationUtil;

	@Autowired
	public UsersDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.paginationUtil = paginationUtil;
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();

	}

	public void saveUsers(Users users) {
		// Create a session object
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();

		session.persist(users);
		transaction.commit();
		session.close();
	}

//	
//	@Transactional
//    public List<Users> findAll() {
//        return getCurrentSession().createQuery("from User", Users.class).list();
//    }
//
//    @Transactional
//    public Users findById(Long id) {
//        return getCurrentSession().get(Users.class, id);
//    }
	public List<Users> getAllUsers() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<Users> users = null;

		try {
			transaction = session.beginTransaction();
			users = session.createQuery("FROM Users", Users.class).getResultList();

			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return users;
	}
	
	public Users findByEmail(String email) {
        try {
            return entityManager.createQuery(
                "SELECT u FROM Users u WHERE LOWER(u.emailID) = :email", Users.class)
                .setParameter("email", email.toLowerCase())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }}

/////////////////////
	private Session getSession() {
		return sessionFactory.openSession();
	}

	public List<Users> getUsersByRole(String role) {
		return sessionFactory.openSession().createQuery("FROM Users WHERE role = :role", Users.class)
				.setParameter("role", role).list();
	}

	////
	public List<Users> getAllUsersBySkillsAndRole(List<String> skills, String role) {
		return (List<Users>) sessionFactory.openSession()
				.createQuery(
						"SELECT DISTINCT u FROM Users u JOIN u.skill s WHERE u.role = :role AND s.skill IN :skills")
				.setParameter("role", role).setParameterList("skills", skills).list();
	}

	//////////////////////////////// fetch all zone details based on inspection
	//////////////////////////////// id//////////
	public List<Users> getInspectorsByZoneId(String zoneId) {
		return sessionFactory.openSession()
				.createQuery("select u from Users u join u.zones z where z.zoneId = :zoneId and u.role = 'Inspector'")
				.setParameter("zoneId", zoneId).list();
	}
	///////////////////////////////////////////////////////////////

	///////////////////////////////// working-days////////////
	public List<Users> findUsersByRole(String role) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<Users> usersList = null;

		try {
			transaction = session.beginTransaction();
			Query<Users> query = session.createQuery("FROM Users WHERE role = :role");
			query.setParameter("role", role);
			usersList = query.list();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return usersList;
	}

	public List<DayOfWeek> findUserAvailabilityByUserId(long userId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<DayOfWeek> availableDays = null;

		try {
			transaction = session.beginTransaction();
			Query<DayOfWeek> query = session.createQuery(
					"SELECT ua.dayOfWeek FROM UserAvailability ua WHERE ua.user.userID = :userId", DayOfWeek.class);
			query.setParameter("userId", userId);
			availableDays = query.list();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}

		return availableDays;
	}
	///////////////////////////////// working-days////////////

	public Page<Users> getAllUsers1(int page, int size) {
		Session session = null;
		Pageable pageable = PageRequest.of(page, size);
		try {
			session = sessionFactory.openSession();
//			        CriteriaBuilder cb = session.getCriteriaBuilder();

			// Create CriteriaQuery for Users
			CriteriaQuery<Users> query = entityManager.getCriteriaBuilder().createQuery(Users.class);
//			        Root<Users> root = query.from(Users.class);
//			        query.select(root);

//				List<Users> users = session.createQuery(query).getResultList();
//				return users.stream()
//						.map((Users user) -> new UsersDTO(user.getRegistrationID(), user.getUserName(), user.getEmailID(),
//								user.getPhoneNumber(), user.getRole(), user.getStatus()))
//						.peek(System.out::println) // Optional: Log each mapped object
//						.collect(Collectors.toList());

			return paginationUtil.getPaginatedData(query, pageable, Users.class, user -> user);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}	

	///// Get User details based on emailid/////
	public Users getUserDetailsByEmail(String emailId) {
		Session session = null;
		Users user = null;

		try {

			session = sessionFactory.openSession();
			session.beginTransaction();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
			Root<Users> root = criteriaQuery.from(Users.class);
			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("emailID"), emailId));
			user = session.createQuery(criteriaQuery).uniqueResult();
			if (user != null) {
				user.getZones().size();
				user.getSkill().size();
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session != null && session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return user;
	}

	public void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	public List<UsersDTO> getAllUserDetails() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();

			// Create CriteriaQuery for Users
			CriteriaQuery<Users> query = cb.createQuery(Users.class);
			Root<Users> root = query.from(Users.class);
			query.select(root);

			List<Users> users = session.createQuery(query).getResultList();
			return users.stream()
					.map((Users user) -> new UsersDTO(user.getUserID(), user.getUserName(), user.getPhoneNumber(),
							user.getRole(), user.getStatus()))
					.peek(System.out::println) // Optional: Log each mapped object
					.collect(Collectors.toList());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Page<Users> getAllUsersWithSkills(String role,Pageable pageable) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Users> query = cb.createQuery(Users.class);
	    Root<Users> root = query.from(Users.class);

	    // Fetch related skills eagerly
	    root.fetch("skill", JoinType.LEFT);
	    query.select(root).distinct(true);
	    if ("Inspector".equalsIgnoreCase(role)) 	
	    	query.where(cb.equal(root.get("role"), role));
	    // Use CriteriaPaginationUtil for pagination
	    return paginationUtil.getPaginatedData(query, pageable, Users.class, Function.identity());
	}



	//// Edit the userDetails(zone,Skills)

	@Transactional
	public void updateUserDetails(UsersDTO usersDTO) {
		// Fetch the existing user by userID
		Users existingUser = fetchUserById(usersDTO.getUserID());
		if (existingUser == null) {
			throw new RuntimeException("User with ID " + usersDTO.getUserID() + " not found");
		}

		// Update basic fields including emailID
		existingUser.setUserName(usersDTO.getUserName());
		existingUser.setPhoneNumber(usersDTO.getPhoneNumber());
		existingUser.setRole(usersDTO.getRole());
		existingUser.setEmailID(usersDTO.getEmailID());

		// Replace zones: Clear existing and add new zones
		if (usersDTO.getZones() != null) {
			existingUser.getZones().clear(); // Clear existing zones
			for (String zoneId : usersDTO.getZones())

			{
				Zone newZone = zoneDAO.getZoneByIdforZones(zoneId);
				existingUser.getZones().add(newZone); // Add new zones
			}
		}

		// existingUser.getSkill().clear();
		List<UserSkill> userSkills = userSkillDAO.findByUserId(usersDTO.getUserID());
		if (usersDTO.getSkill() != null) {
			userSkillDAO.deleteUserSkills(userSkills);
		}

		for (UserSkillDTO userSkillDTO : usersDTO.getSkill())

		{
			System.out.println(userSkillDTO);
			UserSkillId userSkillId = new UserSkillId();
			userSkillId.setSkillId(userSkillDTO.getSkillId());
			userSkillId.setUserId(usersDTO.getUserID());
			UserSkill userSkill = new UserSkill();
			userSkill.setId(userSkillId);
			userSkill.setUser(existingUser);
			Skill skill = skillDAO.findById(userSkillDTO.getSkillId());
			System.out.println(skill);
			userSkill.setSkill(skill);
			userSkill.setStartDate(userSkillDTO.getStartDate());
			userSkill.setExpiryDate(userSkillDTO.getExpiryDate());

			userSkillDAO.save(userSkill);
		}

		// Merge the changes to persist the user
		entityManager.merge(existingUser);
	}

	// Fetch existing user by userID
	private Users fetchUserById(long userID) {
		return entityManager.find(Users.class, userID);
	}
	public UsersDTO findByUsernameAndPassword(String username, String decryptedPassword) {
	    try {
	        	        // Step 2: Query the database for the user using the username
	        Session session = entityManager.unwrap(Session.class);
	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<Users> query = cb.createQuery(Users.class);
	        Root<Users> root = query.from(Users.class);

	        // Step 3: Filter by username and plain-text password
	        query.select(root).where(
	            cb.equal(root.get("registrationID"), username),
	            cb.equal(root.get("password"), decryptedPassword) // Compare decrypted password with DB password
	        );

	        Users userEntity = session.createQuery(query).getSingleResult();

	        // Step 4: Convert entity data to DTO
	        UsersDTO usersDTO = new UsersDTO();
	        usersDTO.setUserID(userEntity.getUserID());
	        usersDTO.setUserName(userEntity.getUserName());
	        usersDTO.setEmailID(userEntity.getEmailID());
	        usersDTO.setPhoneNumber(userEntity.getPhoneNumber());
	        usersDTO.setRole(userEntity.getRole());

	        // Convert photo (byte[]) to Base64 String
	        String photoBase64 = userEntity.getPhoto() != null
	                ? Base64.getEncoder().encodeToString(userEntity.getPhoto())
	                : null;
	        usersDTO.setPhoto(photoBase64);

	        usersDTO.setRegistrationID(userEntity.getRegistrationID());

	        return usersDTO;

	    } catch (NoResultException e) {
	        // Log the error and provide a user-friendly response
	        System.err.println("No user found with the provided username and password.");
	        return null;
	    } catch (Exception e) {
	        // Log detailed exception for debugging
	        e.printStackTrace();
	        throw new RuntimeException("Error during decryption or database query", e);
	    }
	}
	///getAllInspectorNames//
	public List<UsersRoleDTO> getUserByRole(String role) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<UsersRoleDTO> query = cb.createQuery(UsersRoleDTO.class);
	    Root<Users> root = query.from(Users.class);

	    query.select(cb.construct(UsersRoleDTO.class, 
	        root.get("userName"), 
	        root.get("userID"),
	        root.get("emailID")  
	    ));

	    Predicate rolePredicate = cb.equal(root.get("role"), role);
	    query.where(rolePredicate);

	    return entityManager.createQuery(query).getResultList();
	}

}
