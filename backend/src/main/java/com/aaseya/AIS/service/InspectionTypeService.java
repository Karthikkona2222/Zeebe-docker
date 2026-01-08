package com.aaseya.AIS.service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.Inspection_SLA;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.Template;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dao.SkillDAO;
import com.aaseya.AIS.dao.TemplateDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.EntityDetailsDTO;
import com.aaseya.AIS.dto.GetAllInspection_TypeDTO;
import com.aaseya.AIS.dto.InspectionTypeAdminSkillDTO;
import com.aaseya.AIS.dto.InspectionTypeDTO;
import com.aaseya.AIS.dto.InspectionTypeEntityDTO;
import com.aaseya.AIS.dto.InspectionTypeGetAdminDTO;
import com.aaseya.AIS.dto.InspectionTypeGetAdminDTO.GoalDeadlineDTO;
import com.aaseya.AIS.dto.InspectionTypeGetAdminDTO.SlaDetailDTO;
import com.aaseya.AIS.dto.InspectionTypePrimaryDetailsDTO;
import com.aaseya.AIS.dto.InspectionTypeRequestDTO;
import com.aaseya.AIS.dto.InspectionTypeSLADTO;
import com.aaseya.AIS.dto.InspectionTypeSkillAdminDTO;
import com.aaseya.AIS.dto.PeriodicityDTO;
import com.aaseya.AIS.dto.SkillDTO;
import com.aaseya.AIS.dto.TemplateDTO;
import com.aaseya.AIS.dto.UsersDTO;

import jakarta.transaction.Transactional;

@Service
public class InspectionTypeService {

	@Autowired
	private InspectionTypeDAO inspectionTypeDAO;

	@Autowired
	private UsersDAO usersDAO;

	@Autowired
	private SkillDAO skillDAO;

	@Autowired
	private TemplateDAO templateDAO;

	@Autowired
	private EntityDAO entityDAO;

	public List<InspectionTypeDTO> getInspectionType() {
		List<InspectionTypeDTO> inspectionTypeList = new ArrayList<InspectionTypeDTO>();
		List<Inspection_Type> inspectionTypes = inspectionTypeDAO.getAllInspectionTypes();

		for (Inspection_Type inspection_type : inspectionTypes) {
			InspectionTypeDTO inspectionTypeDTO = new InspectionTypeDTO();
			inspectionTypeDTO.setEntitySize(inspection_type.getEntitySize());
			inspectionTypeDTO.setHigh(inspection_type.getHigh());
			inspectionTypeDTO.setHigh(inspection_type.getHigh());
			inspectionTypeDTO.setIns_type_id(inspection_type.getIns_type_id());
			inspectionTypeDTO.setIsActive(String.valueOf(inspection_type.isActive()));
			inspectionTypeDTO.setLow(inspection_type.getLow());
			inspectionTypeDTO.setName(inspection_type.getName());
			inspectionTypeDTO.setThreshold(inspection_type.getThreshold());
			inspectionTypeDTO.setMedium(inspection_type.getMedium());

			inspectionTypeList.add(inspectionTypeDTO);
		}
		return inspectionTypeList;
	}

	public List<String> getInspectionTypeNames() {
		return inspectionTypeDAO.getAllInspectionTypeNames();
	}

	public Inspection_Type getInspectionTypeById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Optional<Inspection_Type> getAllInspectionType() {
		// TODO Auto-generated method stub
		return null;
	}

//    @Transactional
//    public InspectionTypeSkillDTO getInspectionTypeById(long id) {
//        Inspection_Type inspectionType = inspectionTypeDAO.findById(id);
//
//        if (inspectionType != null) {
//            return convertToDTO(inspectionType);
//        } else {
//            // Handle not found case
//            return null; // or throw an exception or return an optional
//        }
//    }

	@Transactional
	public InspectionTypeDTO getInspectionTypeDetailsByName(String name) {
		Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeByName(name);

		if (inspectionType == null) {
			return null;
		}

		Set<String> inspectionSkills = inspectionType.getSkills().stream().map(Skill::getSkill)
				.collect(Collectors.toSet());

		List<Users> usersList = usersDAO.getUsersByRole("Inspector");

		List<UsersDTO> filteredUsers = usersList.stream().filter(user -> user.getSkill().stream().map(Skill::getSkill)
				.collect(Collectors.toSet()).containsAll(inspectionSkills)).map(user -> {
					UsersDTO userDTO = new UsersDTO();
					// userDTO.setUserID(user.getUserID());
					userDTO.setUserName(user.getUserName());
					userDTO.setEmailID(user.getEmailID());
					userDTO.setRole(user.getRole());
					userDTO.setSkills(user.getSkill().stream().map(Skill::getSkill).collect(Collectors.toList()));
					return userDTO;
				}).collect(Collectors.toList());

		InspectionTypeDTO inspectionTypeDTO = new InspectionTypeDTO();
		inspectionTypeDTO.setIns_type_id(inspectionType.getIns_type_id());
		inspectionTypeDTO.setName(inspectionType.getName());
		inspectionTypeDTO.setSkills(inspectionSkills.stream().collect(Collectors.toList()));
		inspectionTypeDTO.setUsers(filteredUsers);

		return inspectionTypeDTO;
	}

	/// addInspectionTypeToEntities///
	@Transactional
	public void addInspectionTypeToEntities(String inspectionTypeId, List<String> entityIds) {
		// Fetch the InspectionType by ID
		Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeById(inspectionTypeId);

		// Fetch NewEntity objects by IDs
		Set<NewEntity> newEntities = new HashSet<>();
		if (entityIds != null) {
			for (String entityId : entityIds) {
				NewEntity newEntity = entityDAO.getEntityByEntityId(entityId);
				if (newEntity != null) {
					newEntities.add(newEntity);
				}
			}
		}

		// Create Many-to-Many relationship
		inspectionType.setNewEntities(newEntities);
		for (NewEntity newEntity : newEntities) {
			if (newEntity.getInspectionTypes() == null) {
				newEntity.setInspectionTypes(new HashSet<>());
			}
			newEntity.getInspectionTypes().add(inspectionType);
		}

		// Save the changes
		inspectionTypeDAO.save(inspectionType);

	}/// addInspectionTypeToEntities///


	public List<GetAllInspection_TypeDTO> getAllInspectionIdsAndNames() {
		return inspectionTypeDAO.findAllInspectionIdsAndNames();
	}

	public InspectionTypeAdminSkillDTO getInspectionTypeById(long inspectionTypeId) {
		return inspectionTypeDAO.getInspectionTypeById(inspectionTypeId);
	}

	public InspectionTypeGetAdminDTO getInspectionTypeDetails(long insTypeId) {
		Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeWithSla(insTypeId);

		if (inspectionType == null) {
			throw new RuntimeException("Inspection Type not found for ID: " + insTypeId);
		}

		// Map entity to DTO
		InspectionTypeGetAdminDTO dto = new InspectionTypeGetAdminDTO();
		dto.setInspectionId(inspectionType.getIns_type_id());
		dto.setInspectionType(inspectionType.getName());
		dto.setThreshold(inspectionType.getThreshold());
		dto.setEnity_size(inspectionType.getEntitySize());

		List<InspectionTypeGetAdminDTO.SlaDetailDTO> slaDetails = inspectionType.getInspectionSLAs().stream()
				.map(sla -> {
					InspectionTypeGetAdminDTO.SlaDetailDTO slaDto = new InspectionTypeGetAdminDTO.SlaDetailDTO();
					slaDto.setEntitySize(sla.getEntitySize());

					InspectionTypeGetAdminDTO.GoalDeadlineDTO inspector = new InspectionTypeGetAdminDTO.GoalDeadlineDTO();
					inspector.setGoal(sla.getInspectorGoal());
					inspector.setDeadline(sla.getInspectorDeadline());
					slaDto.setInspector(inspector);

					InspectionTypeGetAdminDTO.GoalDeadlineDTO reviewer = new InspectionTypeGetAdminDTO.GoalDeadlineDTO();
					reviewer.setGoal(sla.getReviewerGoal());
					reviewer.setDeadline(sla.getReviewerDeadline());
					slaDto.setReviewer(reviewer);

					InspectionTypeGetAdminDTO.GoalDeadlineDTO approver = new InspectionTypeGetAdminDTO.GoalDeadlineDTO();
					approver.setGoal(sla.getApproverGoal());
					approver.setDeadline(sla.getApproverDeadline());
					slaDto.setApprover(approver);

					return slaDto;
				}).collect(Collectors.toList());

		dto.setSlaDetails(slaDetails);
		return dto;
	}

	public List<InspectionTypeSkillAdminDTO> getInspectionTypesWithSkills() {
		// Fetch all inspection types
		List<Inspection_Type> inspectionTypes = inspectionTypeDAO.getAllInspectionTypes();

		// Transform the data into DTOs
		List<InspectionTypeSkillAdminDTO> result = new ArrayList<>();
		for (Inspection_Type inspectionType : inspectionTypes) {
			// Fetch skills for the current inspection type
			List<Skill> skills = inspectionTypeDAO.getSkillsByInspectionTypeId(inspectionType.getIns_type_id());

			// Map to InspectionTypeSkillAdminDTO
			InspectionTypeSkillAdminDTO dto = new InspectionTypeSkillAdminDTO();
			dto.setInsTypeId(inspectionType.getIns_type_id());
			dto.setName(inspectionType.getName());
			dto.setIsActive(inspectionType.isActive());

			// Extract skill names into a list of strings
			List<String> skillNames = new ArrayList<>();
			if (skills != null) {
				for (Skill skill : skills) {
					skillNames.add(skill.getSkill());
				}
			}
			dto.setSkills(skillNames); // Set the skill names

			result.add(dto); // Add the DTO to the result list
		}

		return result; // Return the final list of DTOs

	}

	public InspectionTypeDTO getInspectionTypewithEntity1(long insTypeId) {
		// Fetch Inspection_Type by ins_type_id using the DAO
		Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeByInspectionId(insTypeId);
		InspectionTypeDTO inspectionTypeDTO = new InspectionTypeDTO();

		if (inspectionType != null) {
			inspectionTypeDTO.setIns_type_id(inspectionType.getIns_type_id());
			inspectionTypeDTO.setName(inspectionType.getName());
			inspectionTypeDTO.setThreshold(inspectionType.getThreshold());

			// Prepare entity details
			List<EntityDetailsDTO> entityDetails = new ArrayList<>();
			if (inspectionType.getNewEntities() != null) {
				for (NewEntity entity : inspectionType.getNewEntities()) {
					EntityDetailsDTO entityDTO = new EntityDetailsDTO();
					entityDTO.setEntityId(entity.getEntityid());
					entityDTO.setName(entity.getName());
					entityDTO.setAddress(entity.getFloor() + ", " + entity.getFacility() + ", " + entity.getAddress());
					entityDetails.add(entityDTO);
				}
			}
			inspectionTypeDTO.setEntityDetails(entityDetails);
		} else {
			// Handle the case where the Inspection_Type is not found
			throw new RuntimeException("Inspection Type not found for ID: " + insTypeId);
		}

		return inspectionTypeDTO;
	}

	// addNewInspectionTypeWithSkills//
	@Transactional
	public long processInspectionType(InspectionTypePrimaryDetailsDTO inspectionTypePrimaryDetailsDTO, String action) {
	    Inspection_Type inspectionType;
	    if ("edit".equalsIgnoreCase(action)) {
	        boolean nameExists = inspectionTypeDAO.existsByNameExcludingId(
	            inspectionTypePrimaryDetailsDTO.getName(),
	            inspectionTypePrimaryDetailsDTO.getIns_type_id()
	        );
	        if (nameExists) {
	            throw new IllegalArgumentException("Inspection name already exists for a different record.");
	        }

	        inspectionType = inspectionTypeDAO.findById(inspectionTypePrimaryDetailsDTO.getIns_type_id());
	        if (inspectionType == null) {
	            throw new IllegalArgumentException(
	                "Inspection type with ID " + inspectionTypePrimaryDetailsDTO.getIns_type_id() + " not found."
	            );
	        }

	        // Update fields only if provided
	        if (inspectionTypePrimaryDetailsDTO.getName() != null && !inspectionTypePrimaryDetailsDTO.getName().isEmpty()) {
	            inspectionType.setName(inspectionTypePrimaryDetailsDTO.getName());
	        }
	        if (inspectionTypePrimaryDetailsDTO.getThreshold() != null && !inspectionTypePrimaryDetailsDTO.getThreshold().isEmpty()) {
	            inspectionType.setThreshold(inspectionTypePrimaryDetailsDTO.getThreshold());
	        }
	        inspectionType.setActive(true);

	        if (inspectionTypePrimaryDetailsDTO.getHigh() != null) {
	            inspectionType.setHigh(inspectionTypePrimaryDetailsDTO.getHigh());
	        }
	        if (inspectionTypePrimaryDetailsDTO.getMedium() != null) {
	            inspectionType.setMedium(inspectionTypePrimaryDetailsDTO.getMedium());
	        }
	        if (inspectionTypePrimaryDetailsDTO.getLow() != null) {
	            inspectionType.setLow(inspectionTypePrimaryDetailsDTO.getLow());
	        }
	        if (inspectionTypePrimaryDetailsDTO.getEntitySize() != null && !inspectionTypePrimaryDetailsDTO.getEntitySize().isEmpty()) {
	            inspectionType.setEntitySize(inspectionTypePrimaryDetailsDTO.getEntitySize());
	        }

	        ControlType controlType = inspectionTypeDAO.findByControlTypeId(inspectionTypePrimaryDetailsDTO.getControlTypeId());
	        if (controlType != null) {
	            // use a mutable set (Hibernate requires mutable collections)
	            Set<ControlType> controlTypes = inspectionType.getControlTypes() != null ? new HashSet<>(inspectionType.getControlTypes()) : new HashSet<>();
	            controlTypes.add(controlType);
	            inspectionType.setControlTypes(controlTypes);
	        }

	    } else if ("save".equalsIgnoreCase(action)) {
	        if (inspectionTypeDAO.existsByName(inspectionTypePrimaryDetailsDTO.getName())) {
	            throw new IllegalArgumentException("Inspection name already exists.");
	        }

	        inspectionType = new Inspection_Type();
	        inspectionType.setName(inspectionTypePrimaryDetailsDTO.getName());
	        inspectionType.setThreshold(inspectionTypePrimaryDetailsDTO.getThreshold());
	        inspectionType.setActive(true);
	        inspectionType.setHigh(inspectionTypePrimaryDetailsDTO.getHigh());
	        inspectionType.setMedium(inspectionTypePrimaryDetailsDTO.getMedium());
	        inspectionType.setLow(inspectionTypePrimaryDetailsDTO.getLow());
	        inspectionType.setEntitySize(inspectionTypePrimaryDetailsDTO.getEntitySize());

	        ControlType controlType = inspectionTypeDAO.findByControlTypeId(inspectionTypePrimaryDetailsDTO.getControlTypeId());
	        if (controlType != null) {
	            Set<ControlType> controlTypes = new HashSet<>();
	            controlTypes.add(controlType);
	            inspectionType.setControlTypes(controlTypes);
	        }
	        inspectionTypeDAO.save(inspectionType);
	    } else {
	        throw new IllegalArgumentException("Invalid action: " + action);
	    }

	 // Handle new skills (null-safe)
	    Set<Skill> updatedSkills = new HashSet<>();
	    List<String> newSkills = inspectionTypePrimaryDetailsDTO.getNewSkills();
	    if (newSkills != null) {
	        for (String skillName : newSkills) {
	            if (skillName != null && !skillName.isEmpty()) {
	                Skill existingSkill = skillDAO.findBySkill(skillName);
	                if (existingSkill != null) {
	                    updatedSkills.add(existingSkill);
	                } else {
	                    Skill newSkill = new Skill();
	                    newSkill.setSkill(skillName);
	                    newSkill.setActive(true);
	                    skillDAO.save(newSkill);
	                    updatedSkills.add(newSkill);
	                }
	            }
	        }
	    }

	    // Handle existing skills (accept numbers or strings safely)
	    List<?> existingSkillsListRaw = inspectionTypePrimaryDetailsDTO.getExistingSkills();
	    if (existingSkillsListRaw != null) {
	        for (Object skillObj : existingSkillsListRaw) {
	            if (skillObj == null) continue;
	            Long id = null;
	            try {
	                if (skillObj instanceof Number) {
	                    id = ((Number) skillObj).longValue();
	                } else {
	                    // covers String, etc.
	                    id = Long.parseLong(skillObj.toString());
	                }
	            } catch (NumberFormatException nfe) {
	                System.err.println("Invalid skill id format: " + skillObj);
	                continue;
	            }

	            Skill existingSkill = skillDAO.findById(id);
	            if (existingSkill != null) {
	                // ensure bidirectional mapping only when inspectionType exists
	                if (existingSkill.getInspectionTypes() == null) {
	                    existingSkill.setInspectionTypes(new HashSet<>());
	                }
	                existingSkill.getInspectionTypes().add(inspectionType);
	                updatedSkills.add(existingSkill);
	            } else {
	                System.err.println("Skill ID " + id + " not found. Skipping.");
	            }
	        }
	    }


	    // Associate skills with inspection type
	    inspectionType.setSkills(updatedSkills);
	    inspectionTypeDAO.save(inspectionType);

	    return inspectionType.getIns_type_id();
	}

	public InspectionTypeDTO getInspectionTypewithEntity(long insTypeId) {
		// Fetch Inspection_Type by ins_type_id using the DAO
		Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeByInspectionId(insTypeId);
		InspectionTypeDTO inspectionTypeDTO = new InspectionTypeDTO();

		if (inspectionType != null) {
			inspectionTypeDTO.setIns_type_id(inspectionType.getIns_type_id());
			inspectionTypeDTO.setName(inspectionType.getName());
			inspectionTypeDTO.setThreshold(inspectionType.getThreshold());

			// Prepare entity details
			List<EntityDetailsDTO> entityDetails = new ArrayList<>();
			if (inspectionType.getNewEntities() != null) {
				for (NewEntity entity : inspectionType.getNewEntities()) {
					EntityDetailsDTO entityDTO = new EntityDetailsDTO();
					entityDTO.setEntityId(entity.getEntityid());
					entityDTO.setName(entity.getName());
					entityDTO.setAddress(entity.getFloor() + ", " + entity.getFacility() + ", " + entity.getAddress());
					entityDetails.add(entityDTO);
				}
			}
			inspectionTypeDTO.setEntityDetails(entityDetails);
		} else {
			// Handle the case where the Inspection_Type is not found
			throw new RuntimeException("Inspection Type not found for ID: " + insTypeId);
		}
		return inspectionTypeDTO;
	}

	public InspectionTypeService(InspectionTypeDAO inspectionTypeDAO) {
		this.inspectionTypeDAO = inspectionTypeDAO;
	}

	public void updateInspectionType(InspectionTypeDTO dto) {
		// Fetch the Inspection_Type entity
		Inspection_Type inspectionType = inspectionTypeDAO.findById(dto.getIns_type_id());
		if (inspectionType == null) {
			throw new RuntimeException("Inspection_Type with ID " + dto.getIns_type_id() + " not found.");
		}

		// Update basic fields
		inspectionType.setName(dto.getName());
		inspectionType.setThreshold(dto.getThreshold());

		// Fetch the NewEntity objects based on the entity IDs provided in the DTO
		Set<NewEntity> updatedEntities = fetchEntitiesByIds(dto.getNewEntities());

		// Update the mapping without modifying entity data
		if (inspectionType.getNewEntities() == null) {
			inspectionType.setNewEntities(new HashSet<>());
		} else {
			inspectionType.getNewEntities().clear();
		}
		inspectionType.getNewEntities().addAll(updatedEntities);

		// Persist changes
		inspectionTypeDAO.update(inspectionType);
	}

	private Set<NewEntity> fetchEntitiesByIds(List<String> entityIds) {
		Set<NewEntity> entities = new HashSet<>();
		for (String entityId : entityIds) {
			if (entityId == null || entityId.trim().isEmpty()) {
				throw new IllegalArgumentException("Entity ID cannot be null or empty.");
			}
			NewEntity entity = inspectionTypeDAO.findEntityById(entityId);
			if (entity == null) {
				throw new RuntimeException("Entity with ID " + entityId + " not found.");
			}
			entities.add(entity);
		}
		return entities;
	}

	@Transactional
	public InspectionTypeRequestDTO getInspectionTypeByIdForEdit(long inspectionTypeId) {
	    InspectionTypeRequestDTO inspectionTypeRequestDTO = new InspectionTypeRequestDTO();
	    Inspection_Type inspection_Type = inspectionTypeDAO.findById(inspectionTypeId);

	    if (inspection_Type == null) {
	        throw new RuntimeException("Inspection Type not found for ID: " + inspectionTypeId);
	    }

	    // 🔹 Map Primary Details
	    InspectionTypePrimaryDetailsDTO primaryDetailsDTO = new InspectionTypePrimaryDetailsDTO();
	    primaryDetailsDTO.setIns_type_id(inspectionTypeId);
	    primaryDetailsDTO.setName(inspection_Type.getName());
	    primaryDetailsDTO.setThreshold(inspection_Type.getThreshold());
	    primaryDetailsDTO.setHigh(inspection_Type.getHigh());
	    primaryDetailsDTO.setMedium(inspection_Type.getMedium());
	    primaryDetailsDTO.setLow(inspection_Type.getLow());

	    if (inspection_Type.getControlTypes() != null && !inspection_Type.getControlTypes().isEmpty()) {
	        ControlType controlType = inspection_Type.getControlTypes().iterator().next();
	        primaryDetailsDTO.setControlTypeName(controlType.getControlTypeName());
	        primaryDetailsDTO.setControlTypeId(controlType.getControlTypeId());
	    } else {
	        primaryDetailsDTO.setControlTypeName("Not Available");
	        primaryDetailsDTO.setControlTypeId(-1);
	    }

		List<SkillDTO> skills = new ArrayList<SkillDTO>();
		if (inspection_Type.getSkills() != null) {
			skills = inspection_Type.getSkills().stream().map(skill -> {
				SkillDTO skillDTO = new SkillDTO();
				skillDTO.setSkillId(skill.getSkillId());
				skillDTO.setSkill(skill.getSkill());

				List<String> inspectionTypeNames = new ArrayList<String>();
				Set<Inspection_Type> inspection_Types = skill.getInspectionTypes() == null ? new HashSet<>()
						: skill.getInspectionTypes();
				inspectionTypeNames = inspection_Types.stream().map(inspectionType -> inspectionType.getName())
						.collect(Collectors.toList());
				skillDTO.setInspectionTypeNames(inspectionTypeNames);
				return skillDTO;
			}).collect(Collectors.toList());
		}

		primaryDetailsDTO.setSkills(skills);


	    inspectionTypeRequestDTO.setInspectionTypePrimaryDetails(primaryDetailsDTO);

	    // 🔹 SLA Mapping
	    inspectionTypeRequestDTO.setInspectionSLA(inspection_Type.getInspectionSLAs());

	    // 🔹 Entities + Periodicity (nested per entity)
	    List<EntityDetailsDTO> entityDetailsDTOs = inspection_Type.getNewEntities() == null ? new ArrayList<>()
	            : inspection_Type.getNewEntities().stream().map(entity -> {
	        EntityDetailsDTO detailsDTO = new EntityDetailsDTO();
	        detailsDTO.setEntityId(entity.getEntityid());
	        detailsDTO.setName(entity.getName());
	        detailsDTO.setAddress(entity.getFloor() + ", " + entity.getFacility() + ", " + entity.getAddress());

	        List<PeriodicityDTO> periodicities = inspection_Type.getInspectionTypeSchedule() == null ? new ArrayList<>()
	                : inspection_Type.getInspectionTypeSchedule().stream()
	                .filter(schedule -> entity.getEntityid().equals(schedule.getEntityId()))
	                .map(schedule -> {
	                    PeriodicityDTO dto = new PeriodicityDTO();
	                    dto.setScheduleType(schedule.getScheduleType());
	                    dto.setInterval(schedule.getInterval());
	                    dto.setDaysOfWeek(schedule.getDaysOfWeek());
	                    dto.setDaysOfMonth(schedule.getDaysOfMonth());
	                    dto.setWeekPosition(schedule.getWeekPosition());
	                    dto.setCustomDays(schedule.getCustomDays());
	                    dto.setStartDate(schedule.getStartDate());
	                    dto.setEndDate(schedule.getEndDate());
	                    dto.setEntityId(schedule.getEntityId());
	                    return dto;
	                })
	                .collect(Collectors.toList());

	        detailsDTO.setPeriodicity(periodicities);
	        return detailsDTO;
	    }).collect(Collectors.toList());

	    inspectionTypeRequestDTO.setEntityDetailsDTOs(entityDetailsDTOs);

	    // 🔹 Standalone PeriodicityDetails (flat list for all entities)
	    List<PeriodicityDTO> periodicityDetails = inspection_Type.getInspectionTypeSchedule() == null ? new ArrayList<>()
	            : inspection_Type.getInspectionTypeSchedule().stream()
	            .map(schedule -> {
	                PeriodicityDTO dto = new PeriodicityDTO();
	                dto.setScheduleType(schedule.getScheduleType());
	                dto.setInterval(schedule.getInterval());
	                dto.setDaysOfWeek(schedule.getDaysOfWeek());
	                dto.setDaysOfMonth(schedule.getDaysOfMonth());
	                dto.setWeekPosition(schedule.getWeekPosition());
	                dto.setCustomDays(schedule.getCustomDays());
	                dto.setStartDate(schedule.getStartDate());
	                dto.setEndDate(schedule.getEndDate());
	                dto.setEntityId(schedule.getEntityId());

	                inspection_Type.getNewEntities().stream()
	                        .filter(e -> e.getEntityid().equals(schedule.getEntityId()))
	                        .findFirst()
	                        .ifPresent(e -> dto.setName(e.getName()));

	                return dto;
	            })
	            .collect(Collectors.toList());

	    inspectionTypeRequestDTO.setPeriodicityDetails(periodicityDetails);

	    // 🔹 Template Mapping (latest)
	    Template latestTemplate = templateDAO.findLatestTemplateByInspectionType(inspectionTypeId);
	    if (latestTemplate != null) {
	        TemplateDTO templateDTO = new TemplateDTO();
	        templateDTO.setTemplate_id(latestTemplate.getTemplate_id());
	        templateDTO.setTemplate_name(latestTemplate.getTemplate_name());
	        templateDTO.setVersion(latestTemplate.getVersion());
	        templateDTO.setEffective_from(latestTemplate.getEffective_from());
	        templateDTO.setActive(latestTemplate.isActive());

	        inspectionTypeRequestDTO.setTemplateDTO(templateDTO);
	    }

	    return inspectionTypeRequestDTO;
	}



	//for getting nextScheduledDate for ins_type_id and entityId - START

	public PeriodicityDTO getNextScheduleForEntity(long insTypeId, String entityId) {
	    Inspection_Type inspectionType = inspectionTypeDAO.findById(insTypeId);

	    if (inspectionType == null) {
	        throw new RuntimeException("Inspection Type not found for id: " + insTypeId);
	    }

	    // find schedule for this entity
	    Optional<Inspection_Type_Schedule> optionalSchedule = inspectionType.getInspectionTypeSchedule() == null ? Optional.empty()
	            : inspectionType.getInspectionTypeSchedule().stream()
	            .filter(schedule -> entityId.equals(schedule.getEntityId()))
	            .findFirst();

	    if (optionalSchedule.isEmpty()) {
	        throw new RuntimeException("No schedule found for entityId: " + entityId);
	    }

	    Inspection_Type_Schedule schedule = optionalSchedule.get();

	    // build DTO
	    PeriodicityDTO dto = new PeriodicityDTO();
	    dto.setScheduleType(schedule.getScheduleType());
	    dto.setInterval(schedule.getInterval());
	    dto.setDaysOfWeek(schedule.getDaysOfWeek());
	    dto.setDaysOfMonth(schedule.getDaysOfMonth());
	    dto.setWeekPosition(schedule.getWeekPosition());
	    dto.setCustomDays(schedule.getCustomDays());
	    dto.setStartDate(schedule.getStartDate());
	    dto.setEndDate(schedule.getEndDate());
	    dto.setEntityId(schedule.getEntityId());

	    // ✅ compute next scheduled date
	    LocalDate nextDate = getNextScheduledDate(schedule);
	    dto.setNextScheduledDate(nextDate);

	    return dto;
	}

	/**
	 * Compute the next scheduled date based on the type of schedule.
	 */
	private LocalDate getNextScheduledDate(Inspection_Type_Schedule schedule) {
	    LocalDate today = LocalDate.now();
	    LocalDate base = (schedule.getStartDate() != null ? schedule.getStartDate() : today);

	    switch (schedule.getScheduleType().toUpperCase()) {
	        case "WEEKLY":
	            int weeklyInterval = (schedule.getInterval() != null ? schedule.getInterval() : 1);
	            LocalDate candidateWeek = base;

	            while (!candidateWeek.isAfter(today)) {
	                candidateWeek = candidateWeek.plusWeeks(weeklyInterval);
	            }

	            // If daysOfWeek specified, pick the first matching day after today
	            if (schedule.getDaysOfWeek() != null && !schedule.getDaysOfWeek().isEmpty()) {
	                for (int i = 0; i < 14; i++) {
	                    LocalDate candidate = today.plusDays(i);
	                    DayOfWeek dow = candidate.getDayOfWeek();
	                    if (schedule.getDaysOfWeek().contains(dow.toString())) {
	                        if (schedule.getEndDate() == null || !candidate.isAfter(schedule.getEndDate())) {
	                            return candidate;
	                        } else {
	                            return null;
	                        }
	                    }
	                }
	            }

	            return (schedule.getEndDate() != null && candidateWeek.isAfter(schedule.getEndDate())) ? null : candidateWeek;

	        case "MONTHLY":
	            int monthlyInterval = (schedule.getInterval() != null ? schedule.getInterval() : 1);
	            LocalDate candidateMonth = base;

	            while (!candidateMonth.isAfter(today)) {
	                candidateMonth = candidateMonth.plusMonths(monthlyInterval);
	            }

	            // Handle weekPosition (e.g., SECOND_TUESDAY)
	            if (schedule.getWeekPosition() != null && !schedule.getWeekPosition().isEmpty()) {
	                String[] parts = schedule.getWeekPosition().split("_");
	                if (parts.length == 2) {
	                    String weekPart = parts[0];     // e.g. SECOND
	                    String dayPart = parts[1];      // e.g. TUESDAY

	                    int weekNumber;
	                    switch (weekPart.toUpperCase()) {
	                        case "FIRST": weekNumber = 1; break;
	                        case "SECOND": weekNumber = 2; break;
	                        case "THIRD": weekNumber = 3; break;
	                        case "FOURTH": weekNumber = 4; break;
	                        case "LAST": weekNumber = -1; break;
	                        default: weekNumber = 1;
	                    }

	                    DayOfWeek targetDay = DayOfWeek.valueOf(dayPart.toUpperCase());

	                    // Compute nth weekday of candidateMonth
	                    LocalDate firstDayOfMonth = candidateMonth.withDayOfMonth(1);
	                    int diff = (targetDay.getValue() - firstDayOfMonth.getDayOfWeek().getValue() + 7) % 7;
	                    LocalDate firstOccurrence = firstDayOfMonth.plusDays(diff);

	                    LocalDate nthOccurrence;
	                    if (weekNumber == -1) {
	                        // last occurrence
	                        LocalDate lastDay = candidateMonth.withDayOfMonth(candidateMonth.lengthOfMonth());
	                        int backDiff = (lastDay.getDayOfWeek().getValue() - targetDay.getValue() + 7) % 7;
	                        nthOccurrence = lastDay.minusDays(backDiff);
	                    } else {
	                        nthOccurrence = firstOccurrence.plusWeeks(weekNumber - 1);
	                    }

	                    candidateMonth = nthOccurrence;
	                }
	            }
	            // Handle daysOfMonth if provided instead
	            else if (schedule.getDaysOfMonth() != null && !schedule.getDaysOfMonth().isEmpty()) {
	                int day = Math.min(schedule.getDaysOfMonth().get(0), candidateMonth.lengthOfMonth());
	                candidateMonth = candidateMonth.withDayOfMonth(day);
	            }

	            return (schedule.getEndDate() != null && candidateMonth.isAfter(schedule.getEndDate())) ? null : candidateMonth;

	        case "CUSTOM":
	            int customInterval = (schedule.getInterval() != null ? schedule.getInterval() : 30);
	            LocalDate candidateCustom = base;

	            while (!candidateCustom.isAfter(today)) {
	                candidateCustom = candidateCustom.plusDays(customInterval);
	            }

	            return (schedule.getEndDate() != null && candidateCustom.isAfter(schedule.getEndDate())) ? null : candidateCustom;

	        default:
	            return today;
	    }
	}

	//for getting nextScheduledDate for ins_type_id and entityId-----END



	public Page<InspectionTypeSkillAdminDTO> getAllInspectionTypesWithSkills(Pageable pageable) {
		Page<Inspection_Type> paginatedInspectionTypes = inspectionTypeDAO.getAllInspectionTypes(pageable);

		// Map the paginated entities to DTOs
		return paginatedInspectionTypes.map(entity -> {
			InspectionTypeSkillAdminDTO dto = new InspectionTypeSkillAdminDTO();

			// Set the Inspection_Type fields
			dto.setInsTypeId(entity.getIns_type_id());
			dto.setName(entity.getName());
			dto.setIsActive(entity.isActive());

			// Map the related skills to a list of skill names (as strings)
			if (entity.getSkills() != null) {
				List<String> skillNames = entity.getSkills().stream().map(Skill::getSkill) // Get skill names
						.collect(Collectors.toList());
				dto.setSkills(skillNames);
			}

			return dto;
		});
	}
	public List<GetAllInspection_TypeDTO> getInspectionIdsAndNamesByControlTypeId(long controlTypeId) {
	    return inspectionTypeDAO.findInspectionIdsAndNamesByControlTypeId(controlTypeId);
	}

	// addNewPeriodicityInspectionTypeWithSkills//
	@Transactional
	public long processPeriodicInspectionType(InspectionTypePrimaryDetailsDTO inspectionTypePrimaryDetailsDTO, String action) {
		Inspection_Type inspectionType;
		if ("edit".equalsIgnoreCase(action)) {
			boolean nameExists = inspectionTypeDAO.existsByNameExcludingId(inspectionTypePrimaryDetailsDTO.getName(),
					inspectionTypePrimaryDetailsDTO.getIns_type_id());
			if (nameExists) {
				throw new IllegalArgumentException("Inspection name already exists for a different record.");
			}

			// Fetch the existing Inspection Type by ID
			inspectionType = inspectionTypeDAO.findById(inspectionTypePrimaryDetailsDTO.getIns_type_id());

			if (inspectionType == null) {
				throw new IllegalArgumentException(
						"Inspection type with ID " + inspectionTypePrimaryDetailsDTO.getIns_type_id() + " not found.");
			}

			// Update fields in the Inspection Type only if they are not null or empty
			if (inspectionTypePrimaryDetailsDTO.getName() != null
					&& !inspectionTypePrimaryDetailsDTO.getName().isEmpty()) {
				inspectionType.setName(inspectionTypePrimaryDetailsDTO.getName());
			}
			if (inspectionTypePrimaryDetailsDTO.getThreshold() != null
					&& !inspectionTypePrimaryDetailsDTO.getThreshold().isEmpty()) {
				inspectionType.setThreshold(inspectionTypePrimaryDetailsDTO.getThreshold());
			}
			inspectionType.setActive(true);

			if (inspectionTypePrimaryDetailsDTO.getHigh() != null) {
				inspectionType.setHigh(inspectionTypePrimaryDetailsDTO.getHigh());
			}
			if (inspectionTypePrimaryDetailsDTO.getMedium() != null) {
				inspectionType.setMedium(inspectionTypePrimaryDetailsDTO.getMedium());
			}
			if (inspectionTypePrimaryDetailsDTO.getLow() != null) {
				inspectionType.setLow(inspectionTypePrimaryDetailsDTO.getLow());
			}
			if (inspectionTypePrimaryDetailsDTO.getEntitySize() != null
					&& !inspectionTypePrimaryDetailsDTO.getEntitySize().isEmpty()) {
				inspectionType.setEntitySize(inspectionTypePrimaryDetailsDTO.getEntitySize());
			}
			// Set the control type
	        ControlType controlType = inspectionTypeDAO.findByControlTypeId(inspectionTypePrimaryDetailsDTO.getControlTypeId());
	        if (controlType != null) {
	            Set<ControlType> controlTypes = inspectionType.getControlTypes() != null ? new HashSet<>(inspectionType.getControlTypes()) : new HashSet<>();
	            controlTypes.add(controlType);
	            inspectionType.setControlTypes(controlTypes);
	        }

		} else if ("save".equalsIgnoreCase(action)) {
			// Check if the inspection name already exists
			if (inspectionTypeDAO.existsByName(inspectionTypePrimaryDetailsDTO.getName())) {
				throw new IllegalArgumentException("Inspection name already exists.");
			}

			// Create a new Inspection Type
			inspectionType = new Inspection_Type();
			inspectionType.setName(inspectionTypePrimaryDetailsDTO.getName());
			inspectionType.setThreshold(inspectionTypePrimaryDetailsDTO.getThreshold());
			inspectionType.setActive(true);
			inspectionType.setHigh(inspectionTypePrimaryDetailsDTO.getHigh());
			inspectionType.setMedium(inspectionTypePrimaryDetailsDTO.getMedium());
			inspectionType.setLow(inspectionTypePrimaryDetailsDTO.getLow());
			inspectionType.setEntitySize(inspectionTypePrimaryDetailsDTO.getEntitySize());

			ControlType controlType = inspectionTypeDAO.findByControlTypeId(inspectionTypePrimaryDetailsDTO.getControlTypeId());
	        if (controlType != null) {
	            Set<ControlType> controlTypes = new HashSet<>();
	            controlTypes.add(controlType);
	            inspectionType.setControlTypes(controlTypes);
	        }
			inspectionTypeDAO.save(inspectionType);
		} 
		else {
			throw new IllegalArgumentException("Invalid action: " + action);
		}

		// Handle new skills
		Set<Skill> updatedSkills = new HashSet<>();
		if (inspectionTypePrimaryDetailsDTO.getNewSkills() != null) {
			for (String skillName : inspectionTypePrimaryDetailsDTO.getNewSkills()) {
				if (skillName != null && !skillName.isEmpty()) {
					Skill existingSkill = skillDAO.findBySkill(skillName);
					if (existingSkill != null) {
						// Existing skill found, add it to the updated skills set
						updatedSkills.add(existingSkill);
					} else {
						// New skill, create and save
						Skill newSkill = new Skill();
						newSkill.setSkill(skillName);
						newSkill.setActive(true);
						skillDAO.save(newSkill);
						updatedSkills.add(newSkill);

					}
				}
			}
		}

		// Handle existing skills
		if (inspectionTypePrimaryDetailsDTO.getExistingSkills() != null) {
			for (String skillId : inspectionTypePrimaryDetailsDTO.getExistingSkills()) {
				if (skillId != null && !skillId.isEmpty()) {
					Long parsedId = null;
					try {
						parsedId = Long.parseLong(skillId);
					} catch (NumberFormatException nfe) {
						System.err.println("Invalid skill id format: " + skillId);
						continue;
					}
					Skill existingSkill = skillDAO.findById(parsedId);
					if (existingSkill != null) {
						if (existingSkill.getInspectionTypes() == null) {
							existingSkill.setInspectionTypes(new HashSet<>());
						}
						existingSkill.getInspectionTypes().add(inspectionType);
						updatedSkills.add(existingSkill);
					}
				}
			}
		}
		System.out.println(updatedSkills);
		// Associate updated skills with the Inspection Type
		inspectionType.setSkills(updatedSkills);
		inspectionTypeDAO.save(inspectionType);

		return inspectionType.getIns_type_id();
	}
	
	
	@Transactional
	public void addInspectionTypeToEntitiesPeriodicty(String inspectionTypeId, List<String> entityIds) {
	    Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeById(inspectionTypeId);
	    if (inspectionType == null) {
	        throw new IllegalArgumentException("Inspection type not found for id: " + inspectionTypeId);
	    }

	    Set<NewEntity> newEntities = inspectionType.getNewEntities();
	    if (newEntities == null) {
	        newEntities = new HashSet<>();
	    }

	    if (entityIds != null) {
	        for (String entityId : entityIds) {
	            if (entityId == null) continue;
	            NewEntity newEntity = entityDAO.getEntityByEntityId(entityId);
	            if (newEntity != null) {
	                newEntities.add(newEntity);
	                // maintain bidirectional mapping
	                if (newEntity.getInspectionTypes() == null) {
	                    newEntity.setInspectionTypes(new HashSet<>());
	                }
	                newEntity.getInspectionTypes().add(inspectionType);
	            } else {
	                System.err.println("Entity not found for id: " + entityId);
	            }
	        }
	    }

	    inspectionType.setNewEntities(newEntities);
	    inspectionTypeDAO.save(inspectionType);
	}

	@Transactional
	public void DeleteInspectionTypeToEntitiesPeriodicty(String inspectionTypeId, List<String> entityIds) {	

		// Fetch the InspectionType by ID
		Inspection_Type inspectionType = inspectionTypeDAO.getInspectionTypeById(inspectionTypeId);
		if (inspectionType == null) {
			throw new IllegalArgumentException("Inspection type not found for id: " + inspectionTypeId);
		}

		// Fetch NewEntity objects by IDs
		Set<NewEntity> newEntities = inspectionType.getNewEntities();
		if (newEntities == null) {
			newEntities = new HashSet<>();
		}
		
		if (entityIds != null && !entityIds.isEmpty()) {
			// Remove entities referenced in entityIds
			newEntities.removeIf(e -> entityIds.contains(e.getEntityid()));

			// Remove back references from removed entities
			for (NewEntity rec : newEntities) {
				if (rec.getInspectionTypes() != null) {
					rec.getInspectionTypes().removeIf(it -> it.getIns_type_id() == inspectionType.getIns_type_id());
				}
			}
		}

		// Update mapping on inspectionType
		inspectionType.setNewEntities(newEntities);

		// Remove schedules for removed entities
		List<Inspection_Type_Schedule> schedules = inspectionType.getInspectionTypeSchedule();
		if (schedules == null) {
			schedules = new ArrayList<>();
		}

		if (entityIds == null || entityIds.isEmpty()) {
		    // nothing to remove from schedules
		} else {
		    schedules.removeIf(schedule -> entityIds.contains(schedule.getEntityId()));
		}
		inspectionType.setInspectionTypeSchedule(schedules);

		inspectionTypeDAO.save(inspectionType);
	}
		
	public void updateTemplateMapping(long inspectionTypeId, PeriodicityDTO periodicityDTO) {
	    Inspection_Type inspectionType = inspectionTypeDAO.findById(inspectionTypeId);
	    if (inspectionType == null) {
	        throw new IllegalArgumentException("Inspection type not found for id: " + inspectionTypeId);
	    }

	    // If null dto -> clear mapping and persist
	    if (periodicityDTO == null) {
	        inspectionType.setSelectedTemplateId(null);
	        inspectionType.setSelectedTemplateName(null);
	        inspectionTypeDAO.save(inspectionType);
	        return;
	    }

	    // Read values defensively
	    String scheduleType = periodicityDTO.getScheduleType();
	    Long selectedTemplateId = periodicityDTO.getSelectedTemplateId();
	    String selectedTemplateName = periodicityDTO.getSelectedTemplateName();

	    // If periodic/custom and no template provided, just log a warning and continue (no exception)
	    if (scheduleType != null &&
	        ("Custom".equalsIgnoreCase(scheduleType) || "Periodic".equalsIgnoreCase(scheduleType)) &&
	        selectedTemplateId == null) {

	        System.out.println("WARN: updateTemplateMapping called for inspectionTypeId=" + inspectionTypeId +
	                           " with scheduleType=" + scheduleType + " but selectedTemplateId is null. Proceeding without mapping.");

	        // Clear mapping to prevent stale template reference
	        inspectionType.setSelectedTemplateId(null);
	        inspectionType.setSelectedTemplateName(null);

	        inspectionTypeDAO.save(inspectionType);
	        return;
	    }

	    // Normal behaviour: set mapping if provided
	    inspectionType.setSelectedTemplateId(selectedTemplateId);
	    inspectionType.setSelectedTemplateName(selectedTemplateName);
	    inspectionTypeDAO.save(inspectionType);
	}
}