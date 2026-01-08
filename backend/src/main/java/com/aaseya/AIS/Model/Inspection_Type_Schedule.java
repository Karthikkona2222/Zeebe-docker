package com.aaseya.AIS.Model;
 
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
 
@Entity
@Table(name = "Inspection_Type_Schedule")
public class Inspection_Type_Schedule {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
 
    // ✅ Renamed field to match mappedBy in Inspection_Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ins_type_id", referencedColumnName = "ins_type_id")
    private Inspection_Type inspectionType;
 
    @Column(name = "schedule_type")
    private String scheduleType;
 
    @Column(name = "interval")
    private Integer interval;
 
    @Column(name = "day_of_week")
    private List<String> daysOfWeek;
 
    @Column(name = "day_of_month")
    private List<Integer> daysOfMonth;
 
    @Column(name = "week_position")
    private String weekPosition;
 
    @Column(name = "custom_days")
    private Integer customDays;
 
    @Column(name = "start_date")
    private LocalDate startDate;
 
    @Column(name = "end_date")
    private LocalDate endDate;
 
    @Column(name = "entity_id")
    private String entityId;
 
    @Column(name = "skipped_Date")
    private LocalDate skippedDate;
 
    public Inspection_Type_Schedule() {}
 
    // Getters & Setters
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Inspection_Type getInspectionType() {
        return inspectionType;
    }
 
    public void setInspectionType(Inspection_Type inspectionType) {
        this.inspectionType = inspectionType;
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
 
    public String getEntityId() {
        return entityId;
    }
 
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
 
    public LocalDate getSkippedDate() {
        return skippedDate;
    }
 
    public void setSkippedDate(LocalDate skippedDate) {
        this.skippedDate = skippedDate;
    }
 
    @Override
    public String toString() {
        return "Inspection_Type_Schedule [id=" + id +
                ", scheduleType=" + scheduleType +
                ", interval=" + interval +
                ", daysOfWeek=" + daysOfWeek +
                ", daysOfMonth=" + daysOfMonth +
                ", weekPosition=" + weekPosition +
                ", customDays=" + customDays +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", entityId=" + entityId +
                ", skippedDate=" + skippedDate + "]";
    }
}
 
 