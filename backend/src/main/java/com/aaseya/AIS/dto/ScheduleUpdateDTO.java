package com.aaseya.AIS.dto;

import java.time.LocalDate;
import java.util.List;

public class ScheduleUpdateDTO {
    private String scheduleType;
    private Integer interval;
    private List<String> daysOfWeek;
    private List<Integer> daysOfMonth;
    private String weekPosition;
    private Integer customDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate skippedDate;
    
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
	public LocalDate getSkippedDate() {
		return skippedDate;
	}
	public void setSkippedDate(LocalDate skippedDate) {
		this.skippedDate = skippedDate;
	}  
    
}
