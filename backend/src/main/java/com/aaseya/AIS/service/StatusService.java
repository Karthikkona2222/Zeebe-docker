package com.aaseya.AIS.service;
 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
 
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Segment;
import com.aaseya.AIS.Model.SubSegment;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.StatusDAO;
import com.aaseya.AIS.dto.EntityDTO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;
 
@Service
public class StatusService {
 
    @Autowired
    private EntityDAO entityDAO;
    @Autowired
    private InspectionCaseDAO inspectionCaseDAO;
    @Autowired
    private StatusDAO statusDAO;
 
    public List<EntityDTO> getName() {
        List<EntityDTO> EntityNameList = new ArrayList<EntityDTO>();
        List<NewEntity> name = entityDAO.getAllEntityName();
 
        for (NewEntity entity : name) {
            EntityDTO entityDTO = new EntityDTO();
            entityDTO.setEntityid(entity.getEntityid());
            entityDTO.setName(entity.getName());
            entityDTO.setAddress(entity.getAddress());            
            entityDTO.setFacility(entity.getFacility());
            entityDTO.setType(entity.getType());
 
            EntityNameList.add(entityDTO);
        }
        return EntityNameList;
    }
 
    public List<String> getEntityNames() {
        return entityDAO.getAllEntityNames();
    }

   @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<InspectionCase_EntityDTO> getEntitiesByStatus(String createdBy) {
        List<InspectionCase> inspectionCases = statusDAO.findInspectionCasesByStatus(createdBy);
        return inspectionCases.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private InspectionCase_EntityDTO toDto(InspectionCase inspectionCase) {
        NewEntity entity = inspectionCase.getEntity();
        InspectionCase_EntityDTO dto = new InspectionCase_EntityDTO();
        dto.setInspectionID(inspectionCase.getInspectionID());
		dto.setInspector_source(inspectionCase.getInspector_source());
		dto.setStatus(inspectionCase.getStatus());
		dto.setDateOfInspection(inspectionCase.getDateOfInspection());
		dto.setEntityid(entity.getEntityid());
		dto.setName(entity.getName());
		dto.setReason(inspectionCase.getReason());
		dto.setRepresentative_email(entity.getRepresentativeEmail());
		dto.setEfforts("2.00");
		dto.setAssigned_inspector(inspectionCase.getInspectorID());
		dto.setReference_case("");
		dto.setInspection_type(inspectionCase.getInspectionType());
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date = null;
		try {
			// Parsing the string representation of date to Date object
 
			dto.setDue_date((date.plusDays(2)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		dto.setSize(entity.getSize());
		dto.setRepresentative_name(entity.getRepresentativeName());
		dto.setRepresentative_phoneno(entity.getRepresentativePhoneNo());
		SubSegment subSegement = entity.getSubSegment();
		if (subSegement != null) {
			dto.setSubSegment(subSegement.getName());
		}
		Segment segment = entity.getSegment();
		if (segment != null) {
			dto.setSegment(segment.getSegment_name());
		}
		return dto;
    }
    public Map<String, Long> getStatusCountByCreatedBy(String createdBy) {
        return inspectionCaseDAO.getStatusCountByCreatedBy(createdBy);
    }

	
}
 
 
