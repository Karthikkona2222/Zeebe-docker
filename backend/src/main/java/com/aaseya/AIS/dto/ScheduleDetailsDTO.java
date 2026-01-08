package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.List;

public class ScheduleDetailsDTO {

    private String scheduleType;
    private Integer interval;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Integer> daysOfMonth;
    private List<String> daysOfWeek;
    private String weekPosition;

    // Getters and Setters
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

    public List<Integer> getDaysOfMonth() {
        return daysOfMonth;
    }

    public void setDaysOfMonth(List<Integer> daysOfMonth) {
        this.daysOfMonth = daysOfMonth;
    }

    public List<String> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<String> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getWeekPosition() {
        return weekPosition;
    }

    public void setWeekPosition(String weekPosition) {
        this.weekPosition = weekPosition;
    }
}
