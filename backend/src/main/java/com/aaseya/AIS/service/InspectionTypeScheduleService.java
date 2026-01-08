package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.dao.InspectionTypeScheduleDAO;
import com.aaseya.AIS.dto.ScheduleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InspectionTypeScheduleService {

    @Autowired
    private InspectionTypeScheduleDAO scheduleDAO;

    public void saveSchedule(Inspection_Type_Schedule schedule) {
        scheduleDAO.saveSchedule(schedule);
    }

    public ScheduleDTO getScheduleById(Long id) {
        Inspection_Type_Schedule entity = scheduleDAO.getScheduleById(id);

        if (entity == null) return null;

        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleType(entity.getScheduleType());
        dto.setInterval(entity.getInterval());
        dto.setDaysOfWeek(entity.getDaysOfWeek());
        dto.setDaysOfMonth(entity.getDaysOfMonth());
        dto.setWeekPosition(entity.getWeekPosition());
        dto.setCustomDays(entity.getCustomDays());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setSkippedDate(entity.getSkippedDate());

        return dto;
    }
}
