package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dto.ScheduleUpdateDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionScheduleService {

    @Autowired
    private InspectionTypeDAO inspectionTypeDAO;

    @Autowired
    private EntityDAO entityDAO;

    @Transactional
    public void updateSchedule(Long inspectionTypeId, String entityId, ScheduleUpdateDTO dto) {

        // 🔹 Fetch inspection type
        Inspection_Type inspectionType = inspectionTypeDAO.findById(inspectionTypeId);
        if (inspectionType == null) {
            throw new IllegalArgumentException("Inspection type with ID " + inspectionTypeId + " not found.");
        }

        // 🔹 Fetch entity
        NewEntity newEntity = entityDAO.getEntityByEntityId(entityId);
        if (newEntity == null) {
            throw new IllegalArgumentException("Entity with ID " + entityId + " not found.");
        }

        // 🔹 Ensure mapping exists in inspection_type_entity
        if (!inspectionType.getNewEntities().contains(newEntity)) {
            inspectionType.getNewEntities().add(newEntity);
            newEntity.getInspectionTypes().add(inspectionType);
        }

        // 🔹 Find existing schedule for this inspectionType + entityId
        List<Inspection_Type_Schedule> schedules = inspectionType.getInspectionTypeSchedule();
        Inspection_Type_Schedule schedule = schedules.stream()
                .filter(s -> entityId.equals(s.getEntityId()))
                .findFirst()
                .orElse(null);

        if (schedule == null) {
            // 🔹 Create a new schedule if none exists
            schedule = new Inspection_Type_Schedule();
            schedule.setInspectionType(inspectionType);
            schedule.setEntityId(entityId);
            schedules.add(schedule);
        }

        // 🔹 Update schedule fields from DTO
        mapDtoToEntity(dto, schedule);

        // 🔹 Save inspection type (CascadeType.ALL ensures schedule & mapping are saved)
        inspectionTypeDAO.save(inspectionType);
    }

    private void mapDtoToEntity(ScheduleUpdateDTO dto, Inspection_Type_Schedule entity) {
        entity.setScheduleType(dto.getScheduleType());
        entity.setInterval(dto.getInterval());
        entity.setDaysOfWeek(dto.getDaysOfWeek());
        entity.setDaysOfMonth(dto.getDaysOfMonth());
        entity.setWeekPosition(dto.getWeekPosition());
        entity.setCustomDays(dto.getCustomDays());

        // ✅ Save NULL if frontend passes nothing or blank
        if (dto.getStartDate() == null) {
            entity.setStartDate(null);
        } else {
            entity.setStartDate(dto.getStartDate());
        }

        entity.setEndDate(dto.getEndDate());
        entity.setSkippedDate(dto.getSkippedDate());
    }

}