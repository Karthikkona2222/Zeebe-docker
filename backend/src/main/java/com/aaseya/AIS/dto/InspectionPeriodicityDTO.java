package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class InspectionPeriodicityDTO {

    private UUID id;
    private UUID inspectionTypeId; // or String inspectionTypeName if needed
    private String scheduleType; 
    private Integer interval; 
    private List<String> daysOfWeek; 
    private List<Integer> daysOfMonth; 
    private String weekPosition; 
    private Integer customDays; 
    private LocalDate startDate; 
    private LocalDate endDate;

    public InspectionPeriodicityDTO() {
    }

//    public InspectionPeriodicityDTO(UUID id, UUID inspectionTypeId, String scheduleType, Integer interval,
//                                     List<String> daysOfWeek, List<Integer> daysOfMonth, String weekPosition,
//                                     Integer customDays, LocalDate startDate, LocalDate endDate) {
//        this.id = id;
//        this.inspectionTypeId = inspectionTypeId;
//        this.scheduleType = scheduleType;
//        this.interval = interval;
//        this.daysOfWeek = daysOfWeek;
//        this.daysOfMonth = daysOfMonth;
//        this.weekPosition = weekPosition;
//        this.customDays = customDays;
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getInspectionTypeId() {
        return inspectionTypeId;
    }

    public void setInspectionTypeId(UUID inspectionTypeId) {
        this.inspectionTypeId = inspectionTypeId;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<Integer> getDaysOfMonth() {
        return daysOfMonth;
    }

    public void setDaysOfMonth(List<Integer> daysOfMonth) {
        this.daysOfMonth = daysOfMonth;
    }

    public String getWeekPosition() {
        return weekPosition;
    }

    public void setWeekPosition(String weekPosition) {
        this.weekPosition = weekPosition;
    }

    public Integer getCustomDays() {
        return customDays;
    }

    public void setCustomDays(Integer customDays) {
        this.customDays = customDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
