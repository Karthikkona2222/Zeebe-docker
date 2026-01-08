package com.aaseya.AIS.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dao.SkillDAO;
import com.aaseya.AIS.dto.InspectionTypeDTO;
import com.aaseya.AIS.dto.SkillDTO;
import com.aaseya.AIS.dto.SkillDetailDTO;
import com.aaseya.AIS.dto.SkillInspectionTypeDTO;
import com.aaseya.AIS.dto.SkillRequestDTO;
import com.aaseya.AIS.dto.SkillsDTO;
import com.aaseya.AIS.dto.UsersDTO;

import jakarta.transaction.Transactional;

@Service
public class SkillService {

	@Autowired
	private SkillDAO skillDAO;

	public Skill saveSkill(SkillDTO skillDTO) {
		Skill skill = new Skill();
		System.out.println(skillDTO);
		skill.setSkill(skillDTO.getSkill());
		skill.setActive(skillDTO.isActive());

		if (skill.getInspectionTypes() == null) {
			skill.setInspectionTypes(new HashSet<>());
		}
		skillDAO.save(skill);
		return skillDAO.saveSkillAndAssociation(skill, skillDTO.getIns_type_id());
	}

	public List<SkillsDTO> getSkillsBySkillName() {
		List<Skill> skills = skillDAO.findSkillsBySkillName();
		return skills.stream().map(skill -> new SkillsDTO(skill.getSkillId(), skill.getSkill()

		)).collect(Collectors.toList());
	}


	// Get all skills with inspection type for add inspection type

	public Page<SkillInspectionTypeDTO> getSkillandRelatedInspectionTypes(int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return skillDAO.getSkillandRelatedInspectionTypes(pageable);
	}

	

	@Transactional
	public String handleSkillAction(SkillRequestDTO skillRequest) {
		if ("add".equalsIgnoreCase(skillRequest.getAction())) {
			return addSkill(skillRequest);
		} else if ("edit".equalsIgnoreCase(skillRequest.getAction())) {
			return editSkill(skillRequest);
		} else {
			throw new IllegalArgumentException("Invalid action: " + skillRequest.getAction());
		}
	}

	/**
	 * Adds a new skill.
	 */
	@Transactional
	public String addSkill(SkillRequestDTO skillRequest) {
		// Validate skill name uniqueness
		if (skillDAO.existsBySkillName(skillRequest.getSkillName())) {
			return "This skill name already exists.";
		}

		// Create new Skill entity
		Skill skill = new Skill();
		skill.setSkill(skillRequest.getSkillName());
		skill.setActive(true);

		// Map inspection types and users
		Set<Inspection_Type> inspectionTypes = new HashSet<>(
				skillDAO.findInspectionTypesByIds(skillRequest.getInspectionTypeIds()));
		List<Users> users = skillDAO.findUsersByIds(skillRequest.getUserIds());

		// Set the bidirectional relationships
		for (Inspection_Type inspectionType : inspectionTypes) {
			inspectionType.getSkills().add(skill); // Add the skill to the inspectionType's skills set
		}

		for (Users user : users) {
			user.getSkill().add(skill); // Add the skill to the user's skills set
		}

		// Set the skill's relationships with inspection types and users
		skill.setInspectionTypes(inspectionTypes);
		skill.setUsers(users);

		// Save the skill
		skillDAO.saveSkill(skill);

		return "Skill added successfully.";
	}
		
			    /**
			     * Edits an existing skill.
			     */
		public String editSkill(SkillRequestDTO skillRequest) {
		    // Fetch the existing skill
		    Skill skill = skillDAO.findById(skillRequest.getSkillId());
		    if (skill == null) {
		        return "Skill ID does not exist.";
		    }

		  

		    // Update the skill name if it's different
		    if (!skill.getSkill().equals(skillRequest.getSkillName())) {
		        if (skillDAO.existsBySkillName(skillRequest.getSkillName())) {
		            return "This skill name already exists.";
		        }
		        skill.setSkill(skillRequest.getSkillName());
		    }

		    // Map the new inspection types and users
		    Set<Inspection_Type> newInspectionTypes = new HashSet<>(skillDAO.findInspectionTypesByIds(skillRequest.getInspectionTypeIds()));
		    List<Users> newUsers = skillDAO.findUsersByIds(skillRequest.getUserIds());

		    // Remove old relationships first (bidirectional)
		    for (Inspection_Type oldInspectionType : skill.getInspectionTypes()) {
		        oldInspectionType.getSkills().remove(skill); // Remove the skill from old inspection type
		    }

		    for (Users oldUser : skill.getUsers()) {
		        oldUser.getSkill().remove(skill); // Remove the skill from old user (note: use getSkills() if it's a Set)
		    }

		    // Clear the existing relationships and set new ones
		    skill.getInspectionTypes().clear();
		    skill.getInspectionTypes().addAll(newInspectionTypes);

		    skill.getUsers().clear();
		    skill.getUsers().addAll(newUsers);

		    // Update bidirectional relationships (add the skill to the inspection type and user)
		    for (Inspection_Type inspectionType : newInspectionTypes) {
		        inspectionType.getSkills().add(skill); // Add the updated skill to inspection type
		    }

		    for (Users user : newUsers) {
		        user.getSkill().add(skill); // Add the updated skill to user
		    }

		    // Save the updated skill
		    skillDAO.saveSkill(skill);

		    return "Skill updated successfully.";
	}

	public SkillDetailDTO getSkillDetails(long skillId) {
		SkillDetailDTO skillDetailDTO = new SkillDetailDTO();
		Skill skill = skillDAO.findById(skillId);

		skillDetailDTO.setName(skill.getSkill());

		// Set Inspection type for skill
		List<InspectionTypeDTO> inspectionTypeDTOs = new ArrayList<>();
		for (Inspection_Type inspection_Type : skill.getInspectionTypes()) {
			InspectionTypeDTO inspectionTypeDTO = new InspectionTypeDTO();
			inspectionTypeDTO.setIns_type_id(inspection_Type.getIns_type_id());
			inspectionTypeDTO.setName(inspection_Type.getName());
			inspectionTypeDTOs.add(inspectionTypeDTO);
		}
		skillDetailDTO.setInspectionTypeDTO(inspectionTypeDTOs);

		// Set users for skill
		List<UsersDTO> usersDTOs = new ArrayList<UsersDTO>();
		for (Users user : skill.getUsers()) {
			UsersDTO usersDTO = new UsersDTO();
			usersDTO.setUserID(user.getUserID());
			usersDTO.setUserName(user.getUserName());
			usersDTO.setRole(user.getRole());

			List<String> skills = new ArrayList<String>();
			user.getSkill().stream().forEach(skillValue -> skills.add(skillValue.getSkill()));
			usersDTO.setSkills(skills);

			usersDTOs.add(usersDTO);
		}
		skillDetailDTO.setUsersDTO(usersDTOs);

		return skillDetailDTO;

	}

}
