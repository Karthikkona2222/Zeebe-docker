package com.aaseya.AIS.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dto.PeriodicityDTO;

import jakarta.transaction.Transactional;

@Service
public class InspectionPeriodicityService {

    @Autowired
    private InspectionTypeDAO inspectionTypeDAO;

    @Autowired
    private EntityDAO entityDao;

    @Transactional
    public void processPeriodicity(long inspectionTypeId, PeriodicityDTO pdto, List<String> entityIds, String action) {
        // Validate inspection type
        Inspection_Type inspectionType = inspectionTypeDAO.findById(inspectionTypeId);
        if (inspectionType == null) {
            throw new IllegalArgumentException("Inspection type with ID " + inspectionTypeId + " not found.");
        }

        // Null-safe initialization
        List<Inspection_Type_Schedule> schedules = inspectionType.getInspectionTypeSchedule();
        if (schedules == null) {
            schedules = new ArrayList<>();
        }
        if (entityIds == null) {
            entityIds = new ArrayList<>();
        }

        if (pdto == null) {
            // Default empty DTO to avoid NullPointerException
            pdto = new PeriodicityDTO();
            pdto.setScheduleType("Don't Repeat");
            pdto.setInterval(0);
        }

        switch (action.toLowerCase()) {
            case "save":
                for (String entityId : entityIds) {
                    Inspection_Type_Schedule newSchedule = new Inspection_Type_Schedule();
                    newSchedule.setInspectionType(inspectionType);
                    newSchedule.setEntityId(entityId);
                    mapDtoToEntity(pdto, newSchedule);
                    schedules.add(newSchedule);
                }
                inspectionType.setInspectionTypeSchedule(schedules);
                break;

            case "edit":
                for (String entityId : entityIds) {
                    // Verify entity exists
                    NewEntity exists = entityDao.getEntityByEntityId(entityId);
                    if (exists == null) {
                        throw new IllegalArgumentException("Entity ID " + entityId + " doesn't exist.");
                    }

                    // Update or add schedule
                    Inspection_Type_Schedule existingSchedule = schedules.stream()
                            .filter(s -> entityId.equals(s.getEntityId()))
                            .findFirst()
                            .orElse(null);

                    if (existingSchedule != null) {
                        mapDtoToEntity(pdto, existingSchedule);
                    } else {
                        Inspection_Type_Schedule newSchedule = new Inspection_Type_Schedule();
                        newSchedule.setInspectionType(inspectionType);
                        newSchedule.setEntityId(entityId);
                        mapDtoToEntity(pdto, newSchedule);
                        schedules.add(newSchedule);
                    }
                }
                inspectionType.setInspectionTypeSchedule(schedules);
                break;

            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

 // Null-safe DTO → entity mapping
    private void mapDtoToEntity(PeriodicityDTO dto, Inspection_Type_Schedule entity) {
        String scheduleType = dto.getScheduleType() != null ? dto.getScheduleType() : "Don't Repeat";

        entity.setScheduleType(scheduleType);
        entity.setInterval(dto.getInterval() != null ? dto.getInterval() : 0);
        entity.setDaysOfWeek(dto.getDaysOfWeek());
        entity.setDaysOfMonth(dto.getDaysOfMonth());
        entity.setWeekPosition(dto.getWeekPosition());
        entity.setCustomDays(dto.getCustomDays());

        // ✅ Do not force LocalDate.now() when null
        if ("Don't Repeat".equalsIgnoreCase(scheduleType)) {
            entity.setStartDate(dto.getStartDate());  // can be null
            entity.setEndDate(null);
        } else {
            entity.setStartDate(dto.getStartDate());  // keep null if not provided
            entity.setEndDate(dto.getEndDate());      // can also be null
        }
    }
}
