package com.aaseya.AIS.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.dao.ControlTypeDAO;
import com.aaseya.AIS.dto.ControlTypeDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ControlTypeService {

	@Autowired
	private ControlTypeDAO controlTypeDAO;

	@PersistenceContext
	private EntityManager entityManager;

	public Page<ControlType> getAllControlTypes(Pageable pageable) {
		return controlTypeDAO.getAllControlTypes(pageable);
	}

	public ControlType saveControlType(ControlTypeDTO controlTypeDTO) {
		// Check if ControlType with the same name already exists
		ControlType existingControlType = controlTypeDAO.findByControlTypeName(controlTypeDTO.getControlTypeName());
		if (existingControlType != null) {
			// If it exists, return the existing ControlType or handle it as needed
			return existingControlType; // or throw an exception or return null
		}

		// Create a new ControlType entity
		ControlType controlType = new ControlType();
		controlType.setControlTypeName(controlTypeDTO.getControlTypeName());

		// Save the ControlType entity and map it with the Inspection_Type entities
		return controlTypeDAO.saveControlType(controlType, controlTypeDTO.getInspectionTypeIds());
	}

	// Add this method to check for existing ControlType by name
	public ControlType findByControlTypeName(String controlTypeName) {
		return controlTypeDAO.findByControlTypeName(controlTypeName);
	}

	public ControlType updateControlType(ControlTypeDTO controlTypeDTO) {
		// Find the existing ControlType by ID
		ControlType existingControlType = controlTypeDAO.findById(controlTypeDTO.getControlTypeId());
		if (existingControlType == null) {
			return null; // ControlType not found
		}

		// Check if the new controlTypeName already exists (excluding the current one)
		ControlType duplicateControlType = controlTypeDAO.findByControlTypeName(controlTypeDTO.getControlTypeName());
		if (duplicateControlType != null
				&& duplicateControlType.getControlTypeId() != existingControlType.getControlTypeId()) {
			throw new IllegalArgumentException(
					"Control Type name '" + controlTypeDTO.getControlTypeName() + "' already exists.");
		}

		// Update the fields
		existingControlType.setControlTypeName(controlTypeDTO.getControlTypeName());

		// Update the Inspection_Type mappings
		List<Long> inspectionTypeIds = controlTypeDTO.getInspectionTypeIds();
		if (inspectionTypeIds != null) {
			List<Inspection_Type> inspectionTypes = entityManager
					.createQuery("SELECT i FROM Inspection_Type i WHERE i.ins_type_id IN :ids", Inspection_Type.class)
					.setParameter("ids", inspectionTypeIds).getResultList();
			existingControlType.setInspectionTypes(new HashSet<>(inspectionTypes));
		}

		// Merge the updated ControlType
		return controlTypeDAO.saveControlType(existingControlType, inspectionTypeIds);
	}
	public Map<String, Object> getControlTypeWithInspectionDetails(Long controlTypeId) {
        ControlType controlType = controlTypeDAO.getControlTypeWithInspectionDetails(controlTypeId);

        if (controlType == null) {
            throw new RuntimeException("ControlType not found for ID: " + controlTypeId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("controlTypeName", controlType.getControlTypeName());

        // Transform the Inspection_Type entities into a simplified format
        List<Map<String, Object>> inspections = controlType.getInspectionTypes().stream().map(inspectionType -> {
            Map<String, Object> inspectionMap = new HashMap<>();
            inspectionMap.put("ins_type_id", inspectionType.getIns_type_id());
            inspectionMap.put("name", inspectionType.getName());
            return inspectionMap;
        }).collect(Collectors.toList());

        result.put("inspections", inspections);
        return result;
    }
	//Get All ControlTypes Without Pagination
		public List<ControlType> getControlTypes() {
		    return controlTypeDAO.getControlTypes();
		}
	    }






