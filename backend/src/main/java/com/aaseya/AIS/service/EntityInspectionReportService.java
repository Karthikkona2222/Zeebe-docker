package com.aaseya.AIS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dao.InspectionReportDAO;
import com.aaseya.AIS.dto.EntityInspectionCasesReportResponseDTO;
import com.aaseya.AIS.dto.EntityInspectionReportDTO;
import com.aaseya.AIS.dto.EntityReportDTO;
import com.aaseya.AIS.dto.EntityRequestDTO;

import jakarta.persistence.EntityNotFoundException;
@Service
public class EntityInspectionReportService {
	@Autowired
    private InspectionReportDAO inspectionReportDAO;
	
	public EntityReportDTO getEntityReportById(String entityId) {
	    // Retrieve entity by ID using DAO
	    NewEntity newEntity = inspectionReportDAO.getEntityReportById(entityId);

	    // If the entity is found, populate the DTO
	    if (newEntity != null) {
	        EntityReportDTO reportDTO = new EntityReportDTO();

	        // Setting properties from NewEntity to EntityReportDTO
	        reportDTO.setEntityId(newEntity.getEntityid());
	        reportDTO.setName(newEntity.getName());
	        reportDTO.setFloor(newEntity.getFloor());
	        reportDTO.setAddress(newEntity.getAddress());
	        reportDTO.setFacility(newEntity.getFacility());
	        reportDTO.setCountryName(newEntity.getCountryName());
	        reportDTO.setRepresentative_phoneno(newEntity.getRepresentativePhoneNo());
	        reportDTO.setRepresentative_name(newEntity.getRepresentativeName());
	        reportDTO.setRepresentative_email(newEntity.getRepresentativeEmail());
	        reportDTO.setSegment(newEntity.getSegment() != null ? newEntity.getSegment().getSegment_name() : null);
	        reportDTO.setSubSegment(newEntity.getSubSegment() != null ? newEntity.getSubSegment().getName() : null);
	        reportDTO.setIsAddress(newEntity.getIsAddress());
	        reportDTO.setSize(newEntity.getSize());
	        reportDTO.setZonename(newEntity.getZoneName());

	        // Optionally, you can also include building if needed
	        // reportDTO.setBuilding(newEntity.getBuilding());

	        return reportDTO;
	    } else {
	        // If entity not found, throw exception
	        throw new EntityNotFoundException("Entity with ID " + entityId + " not found");
	    }
	}

//    public EntityInspectionReportDTO getInspectionReportByEntityAndDate(EntityRequestDTO requestDTO) {
//        // Call DAO method to fetch the inspection report based on the request
//        return inspectionReportDAO.getInspectionReportByEntityAndDate(requestDTO);
//    }
    
	 public EntityInspectionReportDTO getInspectionReportByEntityAndDate(EntityRequestDTO requestDTO) {
	        return inspectionReportDAO.getInspectionReportByEntityAndDate(requestDTO);
	    }

//    public List<EntityInspectionCasesReportResponseDTO> getInspectionCases(EntityRequestDTO requestDTO) {
//        // Call DAO method to fetch the inspection report based on the request
//        return inspectionReportDAO.getInspectionCases(requestDTO);
//    }
	 
	 public List<EntityInspectionCasesReportResponseDTO> getInspectionCases(EntityRequestDTO requestDTO) {
	        return inspectionReportDAO.getInspectionCases(requestDTO);
	    }
	 

}
