package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.dao.GetInspectionCasesDAO;
import com.aaseya.AIS.dto.InspectionCaseResponseDTO;
import com.aaseya.AIS.dto.ScheduleDetailsDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetInspectionCasesService {

    @Autowired
    private GetInspectionCasesDAO inspectionCasesDAO;

    @PersistenceContext
    private EntityManager entityManager;

    public List<InspectionCaseResponseDTO> getInspectionCases(String createdBy, String inspectorSource, String createdDateFilter) {

        List<InspectionCase> cases = inspectionCasesDAO.getInspectionCases(createdBy, inspectorSource, createdDateFilter);

        return cases.stream().map(inspectionCase -> {
            InspectionCaseResponseDTO dto = new InspectionCaseResponseDTO();

            dto.setInspectionID(inspectionCase.getInspectionID());
            dto.setInspector_source(inspectionCase.getInspector_source());
            dto.setStatus(inspectionCase.getStatus());

            // Entity details
            if (inspectionCase.getEntity() != null) {
                dto.setEntityid(inspectionCase.getEntity().getEntityid());
                dto.setName(inspectionCase.getEntity().getName());
                dto.setRepresentative_email(inspectionCase.getEntity().getRepresentativeEmail());
                dto.setRepresentative_name(inspectionCase.getEntity().getRepresentativeName());
                dto.setRepresentative_phoneno(inspectionCase.getEntity().getRepresentativePhoneNo());
                if (inspectionCase.getEntity().getSubSegment() != null)
                    dto.setSubSegment(inspectionCase.getEntity().getSubSegment().getName());
                if (inspectionCase.getEntity().getSegment() != null)
                    dto.setSegment(inspectionCase.getEntity().getSegment().getSegment_name());
                dto.setSize(inspectionCase.getEntity().getSize());
            }

            // Lookup Inspection_Type using inspectionCase.inspectionType (string)
            String inspectionTypeName = inspectionCase.getInspectionType();
            if (inspectionTypeName != null && !inspectionTypeName.isEmpty()) {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<Inspection_Type> cq = cb.createQuery(Inspection_Type.class);
                Root<Inspection_Type> root = cq.from(Inspection_Type.class);
                cq.select(root).where(cb.equal(cb.lower(root.get("name")), inspectionTypeName.toLowerCase()));
                List<Inspection_Type> types = entityManager.createQuery(cq).getResultList();

                if (!types.isEmpty()) {
                    Inspection_Type type = types.get(0);
                    dto.setInspection_type(type.getName());
                    dto.setInspection_type_Id(type.getIns_type_id());

                 // ✅ Fetch schedule(s) for this inspection type
                    CriteriaBuilder cb2 = entityManager.getCriteriaBuilder();
                    CriteriaQuery<Inspection_Type_Schedule> cq2 = cb2.createQuery(Inspection_Type_Schedule.class);
                    Root<Inspection_Type_Schedule> root2 = cq2.from(Inspection_Type_Schedule.class);
                    cq2.select(root2).where(cb2.equal(root2.get("inspectionType"), type));

                    List<Inspection_Type_Schedule> schedules = entityManager.createQuery(cq2).getResultList();

                    if (!schedules.isEmpty()) {
                        // If you expect multiple schedules, you can map them into a List<PeriodicityDTO>
                        Inspection_Type_Schedule schedule = schedules.get(0);

                        ScheduleDetailsDTO scheduleDTO = new ScheduleDetailsDTO();
                        scheduleDTO.setScheduleType(schedule.getScheduleType());
                        scheduleDTO.setInterval(schedule.getInterval());
                        scheduleDTO.setStartDate(schedule.getStartDate());
                        scheduleDTO.setEndDate(schedule.getEndDate());
                        scheduleDTO.setDaysOfMonth(schedule.getDaysOfMonth());
                        scheduleDTO.setDaysOfWeek(schedule.getDaysOfWeek());
                        scheduleDTO.setWeekPosition(schedule.getWeekPosition());

                        dto.setScheduleDetails(scheduleDTO);
                    }
                }
            }
            // constants
            dto.setReference_case("");   // constant empty
            dto.setEfforts("2.0");       // constant string value

            // Preinspection info
            dto.setIs_preinspection(inspectionCase.getIs_preinspection());
            dto.setIs_preinspection_submitted(inspectionCase.getIs_preinspection_submitted());

            // Due date
            dto.setDue_date(inspectionCase.getDueDate());

            // Created info
            dto.setCreatedBy(inspectionCase.getCreatedBy());
            dto.setCreatedDate(inspectionCase.getCreatedDate());

            return dto;
        }).collect(Collectors.toList());
    }
}
