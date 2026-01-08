package com.aaseya.AIS.dto;
 
import java.time.LocalDate;
import java.util.List;
 
public class PeriodicityDetailsDTO {
	  // Entity-level information
    private String entityId;
    private String entityName;
    private String address;
    private String entityType;

    // A list of inspection types for the entity
    private List<InspectionTypeDetails> inspectionTypes;

    // inner class (or you can create separate top-level class)
    public static class InspectionTypeDetails {
        private Long ins_type_id;
        private String inspectionTypeName;
        private Periodicity periodicity;

        // getters & setters
        public Long getIns_type_id() { return ins_type_id; }
        public void setIns_type_id(Long ins_type_id) { this.ins_type_id = ins_type_id; }

        public String getInspectionTypeName() { return inspectionTypeName; }
        public void setInspectionTypeName(String inspectionTypeName) { this.inspectionTypeName = inspectionTypeName; }

        public Periodicity getPeriodicity() { return periodicity; }
        public void setPeriodicity(Periodicity periodicity) { this.periodicity = periodicity; }
    }

    // inner class for periodicity details
    public static class Periodicity {
        private String scheduleType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer interval;
        private List<String> daysOfWeek;
        private List<Integer> daysOfMonth;
        private String weekPosition;
        private Integer customDays;

        // getters & setters
        public String getScheduleType() { return scheduleType; }
        public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public Integer getInterval() { return interval; }
        public void setInterval(Integer interval) { this.interval = interval; }

        public List<String> getDaysOfWeek() { return daysOfWeek; }
        public void setDaysOfWeek(List<String> daysOfWeek) { this.daysOfWeek = daysOfWeek; }

        public List<Integer> getDaysOfMonth() { return daysOfMonth; }
        public void setDaysOfMonth(List<Integer> daysOfMonth) { this.daysOfMonth = daysOfMonth; }

        public String getWeekPosition() { return weekPosition; }
        public void setWeekPosition(String weekPosition) { this.weekPosition = weekPosition; }

        public Integer getCustomDays() { return customDays; }
        public void setCustomDays(Integer integer) { this.customDays = integer; }
    }

    // getters & setters for entity-level info
    public String getEntityId() { return entityId; }
    public void setEntityId(String string) { this.entityId = string; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public List<InspectionTypeDetails> getInspectionTypes() { return inspectionTypes; }
    public void setInspectionTypes(List<InspectionTypeDetails> inspectionTypes) { this.inspectionTypes = inspectionTypes; }
}
