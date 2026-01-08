package com.aaseya.AIS.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dao.PeriodicityDetailsByInspectionTypeDAO;
import com.aaseya.AIS.dto.PeriodicityDetailsDTO;

import jakarta.transaction.Transactional;

@Service
public class PeriodicityDetailsByInspectionTypeService {

    @Autowired
    private PeriodicityDetailsByInspectionTypeDAO dao;

    @Transactional
    public PeriodicityDetailsDTO getPeriodicityDetailsByEntityId(String entityId) {
 
        // Step 1: Fetch schedules for the given entity
        List<Inspection_Type_Schedule> schedules = dao.getSchedulesByEntityId(entityId);
        if (schedules == null || schedules.isEmpty()) {
            throw new RuntimeException("No schedules found for entityId: " + entityId);
        }
 
        // Step 2: Fetch entity details (common for all inspection types)
        NewEntity entity = dao.getEntityById(entityId);
        if (entity == null) {
            throw new RuntimeException("Entity not found with id: " + entityId);
        }
 
        // Step 3: Initialize DTO and populate entity details
        PeriodicityDetailsDTO dto = new PeriodicityDetailsDTO();
        dto.setEntityId(entity.getEntityid());
        dto.setEntityName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setEntityType(entity.getType());
 
        List<PeriodicityDetailsDTO.InspectionTypeDetails> inspectionTypeDTOs = new ArrayList<>();
 
        // Step 4: Build inspection type + periodicity data for each schedule
        for (Inspection_Type_Schedule schedule : schedules) {
 
            // Get Inspection_Type details using ins_type_id from schedule
            Inspection_Type inspectionType = schedule.getInspectionType();
            if (inspectionType == null && schedule.getInspectionType() != null) {
                inspectionType = dao.getInspectionTypeById(schedule.getInspectionType().getIns_type_id());
            }
 
            if (inspectionType == null) {
                continue; // Skip if not found
            }
 
            // Build InspectionTypeDetails
            PeriodicityDetailsDTO.InspectionTypeDetails it = new PeriodicityDetailsDTO.InspectionTypeDetails();
            it.setIns_type_id(inspectionType.getIns_type_id());
            it.setInspectionTypeName(inspectionType.getName());
 
            // Build Periodicity info from schedule
            PeriodicityDetailsDTO.Periodicity periodicity = new PeriodicityDetailsDTO.Periodicity();
            periodicity.setScheduleType(schedule.getScheduleType());
            periodicity.setStartDate(schedule.getStartDate());
            periodicity.setEndDate(schedule.getEndDate());
            periodicity.setInterval(schedule.getInterval());
            periodicity.setDaysOfWeek(schedule.getDaysOfWeek());
            periodicity.setDaysOfMonth(schedule.getDaysOfMonth());
            periodicity.setWeekPosition(schedule.getWeekPosition());
            periodicity.setCustomDays(schedule.getCustomDays());
 
            it.setPeriodicity(periodicity);
            inspectionTypeDTOs.add(it);
        }
 
        // Step 5: Add inspection type list to DTO
        dto.setInspectionTypes(inspectionTypeDTOs);
        return dto;
    }
}