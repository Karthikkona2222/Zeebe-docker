package com.aaseya.AIS.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Optional;
import java.util.Set;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.aaseya.AIS.Model.EntityAttachments;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dto.EntitiesInspectionTypeDTO;
import com.aaseya.AIS.dto.EntityDTO;
import com.aaseya.AIS.dto.EntityInformationDTO;
import com.aaseya.AIS.dto.EntityRegistrationDTO;
import com.aaseya.AIS.dto.LicenseDTO;
import com.aaseya.AIS.dto.ProductDTO;
import com.aaseya.AIS.dto.SiteDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class EntityDAO {
	@Autowired
	private SessionFactory sessionFactory;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
    private CriteriaPaginationUtil paginationUtil;

	public boolean addUpdateEntity(NewEntity entity) {
		try (Session session = sessionFactory.openSession()) {

			Transaction transaction = session.getTransaction();
			transaction.begin();
			session.merge(entity);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public List<NewEntity> getAllEntityName() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<NewEntity> entity_name = null;
		try {
			transaction = session.beginTransaction();
			Query<NewEntity> query = session.createQuery("FROM Entity", NewEntity.class);
			entity_name = query.getResultList();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return entity_name;
	}

	public List<String> getAllEntityNames() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<String> entity_name = null;
		try {
			transaction = session.beginTransaction();
			Query<String> query = session.createQuery("SELECT name FROM NewEntity", String.class);
			entity_name = query.getResultList();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return entity_name;
	}

	public List<NewEntity> findEntitiesByCreatedBy(String createdBy) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<NewEntity> entities = null;
		try {
			transaction = session.beginTransaction();
			String hql = "SELECT ne FROM NewEntity ne JOIN ne.inspectionCases ic WHERE ic.createdBy = :createdBy";
			Query<NewEntity> query = session.createQuery(hql, NewEntity.class);
			query.setParameter("createdBy", createdBy);
			entities = query.getResultList();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return entities;
	}

	public EntityInformationDTO getAllAddressByEntity(String name) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		EntityInformationDTO entityInformationDTO = new EntityInformationDTO();
		try {
			transaction = session.beginTransaction();
			Query<Object[]> query = session.createQuery(
					"SELECT name, floor, address,  entityid FROM NewEntity WHERE name =:entityname",
					Object[].class);
			query.setParameter("entityname", name);
			Object[] object = query.getSingleResult();

			entityInformationDTO.setName(object[0].toString());
			entityInformationDTO.setFloor(object[1].toString());
//			entityInformationDTO.setLocation(object[2].toString());
			entityInformationDTO.setAddress(object[2].toString());
//			entityInformationDTO.setBuilding(object[4].toString());
			entityInformationDTO.setEntityId(object[3].toString());


			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return entityInformationDTO;
	}

	public String saveEntity(NewEntity newEntity) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();

		try {
			if (session.contains(newEntity)) {
				// The entity is already managed
				session.persist(newEntity);
			} else {
				// The entity is detached
				newEntity = session.merge(newEntity);
				session.persist(newEntity);
			}

			transaction.commit();
			System.out.println(newEntity.getEntityid());
			return newEntity.getEntityid();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {

			session.close();
		}
		return newEntity.getEntityid();

	}

	public NewEntity getEntityByEntityId(final String entityId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		NewEntity entity = new NewEntity();
		try {
			transaction = session.beginTransaction();
			Query<NewEntity> query = session.createQuery("FROM NewEntity where entityid =:entityId", NewEntity.class);
			query.setParameter("entityId", entityId);
			entity = query.getSingleResult();
			System.out.println("Entity : " + entity);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
		return entity;
	}
	
///// /////fetch all zone details based on the inspectionID//
	public NewEntity getEntityById(String entityId) {
	    Session session = sessionFactory.openSession();
	    try {
	        return session.get(NewEntity.class, entityId);
	    } finally {
	        session.close();  
	    }
	}
	///////////////////////////////
	
	
	////addInspectionTypeToEntities//
	
	
	 public void save(NewEntity newEntity) {
	        Session session = sessionFactory.openSession();
	        Transaction transaction = null;

	        try {
	            transaction = session.beginTransaction();
	            session.saveOrUpdate(newEntity);  // Save or update the NewEntity entity
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

	 
	 public List<NewEntity> getAllEntitiesWithInspectionTypes() {
		    Session session = null;
		    List<NewEntity> entities = null;
		    try {
		        session = sessionFactory.openSession();
		        HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
		        CriteriaQuery<NewEntity> cq = cb.createQuery(NewEntity.class);
		        Root<NewEntity> root = cq.from(NewEntity.class);

		        // Fetch associated inspection types
		        root.fetch("inspectionTypes", JoinType.LEFT);
		        cq.select(root).distinct(true);

		        Query<NewEntity> query = session.createQuery(cq);
		        entities = query.getResultList();
		    } finally {
		        if (session != null) {
		            session.close();
		        }
		    }
		    return entities;
		}
	 public NewEntity findEntityById(String entityId) {
		    try {
		        // Create CriteriaBuilder and CriteriaQuery
		        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		        CriteriaQuery<NewEntity> cq = cb.createQuery(NewEntity.class);
		        Root<NewEntity> root = cq.from(NewEntity.class);

		        // Fetch related entities (zones and inspection types)
		        root.fetch("zones", JoinType.LEFT); // Ensure zones are eagerly fetched
		        root.fetch("inspectionTypes", JoinType.LEFT); // Ensure inspectionTypes are eagerly fetched

		        // Add the WHERE clause
		        cq.where(cb.equal(root.get("entityid"), entityId));

		        // Execute the query
		        return entityManager.createQuery(cq).getSingleResult();
		    } catch (NoResultException e) {
		        return null; // Handle case where no entity is found
		    }
	 }

	/// Get all entities with id,name and address for entity mapping///

		public List<EntityDTO> retrieveEntityList() {
		    List<EntityDTO> entityList = new ArrayList<>();
		    Session session = null;
		    Transaction transaction = null;

		    try {
		        session = sessionFactory.openSession();
		        transaction = session.beginTransaction();

		        CriteriaBuilder cb = session.getCriteriaBuilder();
		        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		        Root<NewEntity> root = cq.from(NewEntity.class);

		        // Select only necessary fields: entityId, name, and address components
		        cq.multiselect(
		        	    root.get("entityid"), 
		        	    root.get("name"),
		        	    cb.concat(
		        	        cb.concat(
		        	            cb.coalesce(cb.concat(root.get("floor"), ", "), ""), // Add a comma after floor if it exists
		        	            cb.concat(cb.coalesce(root.get("facility"), ""), ", ")
		        	        ),
		        	        cb.coalesce(root.get("address"), "")
		        	    )
		        	);


		        Query<Object[]> query = session.createQuery(cq);
		        List<Object[]> results = query.getResultList();

		        for (Object[] result : results) {
		            String entityId = (String) result[0];
		            String entityName = (String) result[1];
		            String address = (String) result[2]; // Combined floor, facility, and address

		            entityList.add(new EntityDTO(entityId, entityName, address));
		        }

		        transaction.commit();
		    } catch (Exception e) {
		        if (transaction != null)
		            transaction.rollback();
		        e.printStackTrace();
		    } finally {
		        if (session != null)
		            session.close();
		    }

		    return entityList;
		}

		public Page<NewEntity> getPaginatedEntitiesWithInspectionTypes(Pageable pageable) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<NewEntity> query = cb.createQuery(NewEntity.class);
	        Root<NewEntity> root = query.from(NewEntity.class);
	 
	        // Fetch associated inspection types
	        root.fetch("inspectionTypes", JoinType.LEFT);
	        query.select(root).distinct(true);
	 
	        // Use CriteriaPaginationUtil for pagination
	        return paginationUtil.getPaginatedData(query, pageable, NewEntity.class, Function.identity());
	    }
		
		// EntityRegistration based on Entity_Type & Edit EntityRegistration details
		// based on entityId

		public String saveEntityDetails(EntityRegistrationDTO entityRegistrationDTO) {
		    Session session = null;
		    Transaction transaction = null;

		    try {
		        session = sessionFactory.openSession();
		        transaction = session.beginTransaction();

		        List<Long> inspectionIds = entityRegistrationDTO.getInsTypeId();
		        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
		        NewEntity newEntity;

		        boolean isEdit = "edit".equalsIgnoreCase(entityRegistrationDTO.getAction());
		        boolean isSave = "save".equalsIgnoreCase(entityRegistrationDTO.getAction());

		        Set<String> existingFileNames = new HashSet<>();//Checking duplicate attachments
		        
		        if (isEdit) {
		            newEntity = session.get(NewEntity.class, entityRegistrationDTO.getEntityId());
		            if (newEntity == null) {
		                throw new RuntimeException("Entity with ID " + entityRegistrationDTO.getEntityId() + " not found.");
		            }

	           
		         // Clear existing Inspection Types
		            for (Inspection_Type inspectionType : new HashSet<>(newEntity.getInspectionTypes())) {
		                inspectionType.getNewEntities().remove(newEntity);
		            }
		            newEntity.getInspectionTypes().clear();
		            
		            // Only delete attachments if deleteAttachmentIds are provided
	                if (entityRegistrationDTO.getDeleteAttachmentIds() != null && !entityRegistrationDTO.getDeleteAttachmentIds().isEmpty()) {
	                    Iterator<EntityAttachments> iterator = newEntity.getEntityAttachments().iterator();
	                    while (iterator.hasNext()) {
	                        EntityAttachments attachment = iterator.next();
	                        if (entityRegistrationDTO.getDeleteAttachmentIds().contains(attachment.getAttachmentId())) {
	                            if (attachment.getLocation() != null) {
	                                deleteFile(attachment.getLocation());
	                            }
	                            session.remove(attachment);
	                            iterator.remove();
	                        }
	                    }
	                }
		            
		            //newEntity.getEntityAttachments().clear();
		        } else if (isSave) {
		            newEntity = new NewEntity();
		            newEntity.setType(entityRegistrationDTO.getEntityType());
		            newEntity.setActive(true);
		        } else {
		            throw new IllegalArgumentException("Invalid action: " + entityRegistrationDTO.getAction());
		        }

		        JsonNode jsonNode = null;
		        String entityType = isEdit ? newEntity.getType() : entityRegistrationDTO.getEntityType();

		        if ("product".equalsIgnoreCase(entityType)) {
				    // Convert JsonNode to ObjectNode
				    ObjectNode productNode = objectMapper.valueToTree(entityRegistrationDTO.getProduct());
				    newEntity.setName(entityRegistrationDTO.getProduct().getProductName());
				    // Add productAddress to the JSON
				    jsonNode = productNode; // Assign back to jsonNode
				} else if ("license".equalsIgnoreCase(entityType)) {
				    // Convert JsonNode to ObjectNode
				    ObjectNode licenseNode = objectMapper.valueToTree(entityRegistrationDTO.getLicense());
				    newEntity.setName(entityRegistrationDTO.getLicense().getLicenseName());
				    // Add licenseAddress to the JSON
				    jsonNode = licenseNode; // Assign back to jsonNode
				} else if ("site".equalsIgnoreCase(entityType)) {
				    jsonNode = objectMapper.valueToTree(entityRegistrationDTO.getSite());
				    newEntity.setName(entityRegistrationDTO.getSite().getSiteName());
				}

		        newEntity.setEntityDetails(jsonNode);

		        // **Step 1: Persist NewEntity Before Attachments**
		        session.merge(newEntity);

		        // **Step 2: Handle Inspection Types**
		        Set<Inspection_Type> inspectionTypes = new HashSet<>();
		        for (Long inspectionId : inspectionIds) {
		            Inspection_Type inspectionType = session.get(Inspection_Type.class, inspectionId);
		            if (inspectionType != null) {
		                inspectionTypes.add(inspectionType);
		                inspectionType.getNewEntities().add(newEntity);
		            } else {
		                throw new RuntimeException("Inspection_Type with ID " + inspectionId + " not found.");
		            }
		        }
		        newEntity.setInspectionTypes(inspectionTypes);
		        session.merge(newEntity); // Update after setting relationships
		        

		        // **Step 3: Handle File Uploads**
		        List<MultipartFile> attachments = entityRegistrationDTO.getAttachments();
		        List<String> skippedFiles = new ArrayList<>();
		        
		        if (attachments != null && !attachments.isEmpty()) {

		            //File resourceDir = new ClassPathResource("").getFile();
		            //String uploadDir = resourceDir.getParentFile().getParentFile().getAbsolutePath() + "\\src\\main\\resources\\uploads\\";

		        	String uploadDir = "D:\\Camunda-AIS\\EntityRegistrationAttachments\\";
		        	
		            // Ensure the upload directory exists
		            File uploadFolder = new File(uploadDir);
		            if (!uploadFolder.exists() && !uploadFolder.mkdirs()) {
		                throw new RuntimeException("Failed to create upload directory in resources.");
		            }

		            // Get existing attachment filenames if action is 'edit'
		            //Set<String> existingFileNames = new HashSet<>();
		            if (isEdit) {
		                for (EntityAttachments attachment : newEntity.getEntityAttachments()) {
		                    existingFileNames.add(attachment.getFileName());
		                }
		            }

		            for (MultipartFile file : attachments) {
		                if (!file.isEmpty()) {
		                    String originalFilename = file.getOriginalFilename();
		                    
		                    //  Skip duplicate filenames
		                    if (isEdit && existingFileNames.contains(originalFilename)) {		                    	
		                        skippedFiles.add(originalFilename); // Store skipped filenames for response message
		                        continue; // Skip adding duplicate file
		                    }
		                    
		                    String fileType = file.getContentType();
		                    String filePath = uploadDir + System.currentTimeMillis() + "_" + originalFilename;

		                    //  Check for duplicate filenames if action is 'edit'
		                    if (isEdit && existingFileNames.contains(originalFilename)) {
		                        throw new RuntimeException("Duplicate attachment detected: " + originalFilename + " is already uploaded.");
		                    }             

		                    // Save file to local storage
		                    File destinationFile = new File(filePath);
		                    try {
		                        file.transferTo(destinationFile);
		                    } catch (IOException ex) {
		                        throw new RuntimeException("Error saving file: " + originalFilename, ex);
		                    }

		                    // Save file record in the database
		                    EntityAttachments attachment = new EntityAttachments();
		                    attachment.setAttachmentType(fileType);
		                    attachment.setFileType(getFileExtension(destinationFile));
		                    attachment.setFileName(originalFilename);
		                    attachment.setLocation(filePath);
		                    attachment.setNewEntity(newEntity);

		                    session.persist(attachment); // Persist attachment after newEntity is saved
		                }
		            }
		        }
		            

		        transaction.commit();
		        
		     // **Return response message including skipped files**
		        String responseMessage = isEdit ? " Entity updated successfully "+newEntity.getEntityid() : entityRegistrationDTO.getEntityType()+" successfully registered with entityId "+newEntity.getEntityid() ;
		        if (!skippedFiles.isEmpty()) {
		            responseMessage += ". The following files were already present and were not uploaded again: " + String.join(", ", skippedFiles);
		        }
		        return responseMessage;
		        
		        //return newEntity.getEntityid();
		        
		    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("Error updating file details", e);
			} 
//	      catch (Exception e) {
//		        if (transaction != null) {
//		            transaction.rollback();
//		        }
//		        e.printStackTrace();
//		        throw new RuntimeException("Error saving entity details.", e);
//		    } 
		    finally {
		        if (session != null) {
		            session.close();
		        }
		    }
		}


		// Get entity registration details by entity id checking for entity type
			public EntityRegistrationDTO getEntityRegistration(String entityId) {
				System.out.println(entityId);
				try {
					// Create CriteriaBuilder
					CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

					// Create CriteriaQuery for the entity class (EntityRegistration)
					CriteriaQuery<NewEntity> criteriaQuery = criteriaBuilder.createQuery(NewEntity.class);
					Root<NewEntity> root = criteriaQuery.from(NewEntity.class);

					// Add WHERE clause to filter by entityId
					criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("entityid"), entityId));

					// Execute query
					TypedQuery<NewEntity> query = entityManager.createQuery(criteriaQuery);
					NewEntity entityRegistration = query.getSingleResult();

					EntityRegistrationDTO entityRegistrationDTO = new EntityRegistrationDTO();
					entityRegistrationDTO.setEntityType(entityRegistration.getType());
					
					
					// uncomment this if required all fields
					//entityRegistrationDTO.setInspections(entityRegistration.getInspectionTypes());

					// Initialize inspections set if it's null
					if (entityRegistrationDTO.getInspectionTypes() == null) {
					    entityRegistrationDTO.setInspectionTypes(new HashSet<>());
					}

					for (Inspection_Type inspectionType : entityRegistration.getInspectionTypes()) {
					    Inspection_Type instype = new Inspection_Type();
					    instype.setIns_type_id(inspectionType.getIns_type_id());
					    instype.setActive(inspectionType.isActive());
					    instype.setName(inspectionType.getName());

					    // Add to inspections set
					    entityRegistrationDTO.getInspectionTypes().add(instype);
					}
						
					
					
					ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
					// Map related entities to DTOs (e.g., SiteDTO, ProductDTO, LicenseDTO)
					if (entityRegistration.getType() != null && entityRegistration.getType().equalsIgnoreCase("site")) {
						JsonNode jsonNode = entityRegistration.getEntityDetails();
						SiteDTO siteDTO = objectMapper.treeToValue(jsonNode, SiteDTO.class);
						entityRegistrationDTO.setSite(siteDTO);
					}

					if (entityRegistration.getType() != null && entityRegistration.getType().equalsIgnoreCase("product")) {
						JsonNode jsonNode = entityRegistration.getEntityDetails();
						ProductDTO productDTO = objectMapper.treeToValue(jsonNode, ProductDTO.class);
						entityRegistrationDTO.setProduct(productDTO);
					}

					if (entityRegistration.getType() != null && entityRegistration.getType().equalsIgnoreCase("license")) {
						JsonNode jsonNode = entityRegistration.getEntityDetails();
						LicenseDTO licenseDTO = objectMapper.treeToValue(jsonNode, LicenseDTO.class);
						entityRegistrationDTO.setLicense(licenseDTO);
					}
					
					 // **Fetching & Mapping Attachments**
			        List<EntityAttachments> attachmentsList = entityRegistration.getEntityAttachments();
			        List<EntityAttachments> dtoAttachmentsList = new ArrayList<>();

			        for (EntityAttachments attachment : attachmentsList) {
			            EntityAttachments dtoAttachment = new EntityAttachments();
			            dtoAttachment.setAttachmentId(attachment.getAttachmentId());
			            dtoAttachment.setFileName(attachment.getFileName());
			            dtoAttachment.setLocation(attachment.getLocation());
			            dtoAttachment.setAttachmentType(attachment.getAttachmentType());
			            dtoAttachment.setFileType(attachment.getFileType());
			            dtoAttachmentsList.add(dtoAttachment);
			        }

			        entityRegistrationDTO.setEntityAttachments(dtoAttachmentsList);

			        return entityRegistrationDTO;

				} catch (NoResultException e) {
					throw new EntityNotFoundException(
							"Entity ID '" + entityId + "' is not found. Please provide a correct Entity ID.");
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error fetching entity details", e);
				}
			}
			
			
			public static String getFileExtension(File file) {
		        return Optional.ofNullable(file.getName())
		                .filter(f -> f.contains("."))
		                .map(f -> f.substring(f.lastIndexOf(".") + 1).toLowerCase())
		                .orElse("unknown");
		    }
			
		    public static boolean deleteFile(String filePath) {
		        File file = new File(filePath);
		        if (file.exists()) {
		            return file.delete(); // Deletes the file and returns true if successful
		        }
		        return false; // File doesn't exist
		    }
			
		
		
		public List<EntitiesInspectionTypeDTO> getEntitiesWithOptionalInspectionType(Long insTypeId) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<EntitiesInspectionTypeDTO> query = cb.createQuery(EntitiesInspectionTypeDTO.class);
	        Root<NewEntity> root = query.from(NewEntity.class);
 
	       
	        query.select(cb.construct(
	                EntitiesInspectionTypeDTO.class,
	                root.get("entityid"),
	                root.get("name")
	        ));
 
	        if (insTypeId != null) {
	            
	            Join<NewEntity, Inspection_Type> join = root.join("inspectionTypes", JoinType.INNER);
	            query.where(cb.equal(join.get("ins_type_id"), insTypeId));
	        }
 
	        return entityManager.createQuery(query).getResultList();
	    }
		@Transactional
		public NewEntity getEntityByName(String entityName) {
		    try {
		        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		        CriteriaQuery<NewEntity> query = cb.createQuery(NewEntity.class);
		        Root<NewEntity> root = query.from(NewEntity.class);

		        query.select(root)
		             .where(cb.equal(cb.lower(root.get("name")), entityName.toLowerCase()));

		        List<NewEntity> result = entityManager.createQuery(query).getResultList();
		        return result.isEmpty() ? null : result.get(0);
		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
		}
}

	    




	    

