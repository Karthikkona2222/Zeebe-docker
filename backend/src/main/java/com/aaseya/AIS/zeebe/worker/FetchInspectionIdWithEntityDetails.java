package com.aaseya.AIS.zeebe.worker;

import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Inspection_Type_Schedule;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dto.StartAISRequestDTO;
import com.aaseya.AIS.service.InspectionCaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Component
public class FetchInspectionIdWithEntityDetails {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private InspectionCaseService inspectionCaseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @JobWorker(type = "inspection-schedule-job", autoComplete = true)
    @Transactional
    public Map<String, Object> handleInspectionScheduleJob() {

        LocalDate today = LocalDate.now();
        System.out.println("🔹 Inspection Schedule Job started for date: " + today);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inspection_Type_Schedule> cq = cb.createQuery(Inspection_Type_Schedule.class);
        Root<Inspection_Type_Schedule> root = cq.from(Inspection_Type_Schedule.class);
        root.join("inspectionType", JoinType.INNER);
        cq.select(root).distinct(true);

        List<Inspection_Type_Schedule> schedules = entityManager.createQuery(cq).getResultList();
        System.out.println("Fetched Schedules Count: " + schedules.size());

        Map<String, Map<String, Object>> entityMap = new LinkedHashMap<>();

        for (Inspection_Type_Schedule schedule : schedules) {
            Inspection_Type inspectionType = schedule.getInspectionType();
            if (inspectionType == null) continue;

            // Skip if skippedDate matches today
            if (schedule.getSkippedDate() != null && schedule.getSkippedDate().isEqual(today)) {
                System.out.println("⏭️ Skipping (skippedDate matched) inspectionTypeId=" + inspectionType.getIns_type_id()
                        + " for entityId=" + schedule.getEntityId());
                continue;
            }

            if (inspectionType.getNewEntities() == null) continue;

            for (NewEntity entity : inspectionType.getNewEntities()) {
                if (schedule.getEntityId() == null || !schedule.getEntityId().equals(entity.getEntityid())) continue;

                if (!isEntityScheduleDueToday(schedule, today)) continue;

                entityMap.computeIfAbsent(entity.getEntityid(), k -> {
                    Map<String, Object> eMap = new LinkedHashMap<>();
                    eMap.put("entityId", entity.getEntityid());
                    eMap.put("entityName", entity.getName());
                    eMap.put("dateOfInspection", today.toString());
                    eMap.put("reason", "Periodic schedule");
                    eMap.put("createdBy", "samuel@gmail.com");
                    eMap.put("inspectionSource", "Periodic");
                    eMap.put("createdDate", today.toString());
                    eMap.put("is_preinspection", false);
                    eMap.put("inspections", new ArrayList<Map<String, Object>>());
                    return eMap;
                });

                List<Map<String, Object>> inspections =
                        (List<Map<String, Object>>) entityMap.get(entity.getEntityid()).get("inspections");

                Map<String, Object> inspectionDetails = new LinkedHashMap<>();
                inspectionDetails.put("inspectionTypeId", inspectionType.getIns_type_id());
                inspectionDetails.put("inspectionTypeName", inspectionType.getName());
                inspectionDetails.put("templateId",
                        inspectionType.getTemplates() != null && !inspectionType.getTemplates().isEmpty()
                                ? inspectionType.getTemplates().iterator().next().getTemplate_id() : null);
                inspectionDetails.put("controlTypeId",
                        inspectionType.getControlTypes() != null && !inspectionType.getControlTypes().isEmpty()
                                ? inspectionType.getControlTypes().iterator().next().getControlTypeId() : null);

                inspections.add(inspectionDetails);
            }
        }

        List<Map<String, Object>> responseList = new ArrayList<>(entityMap.values());
        printResponse(responseList);

        int totalTriggered = triggerAISProcesses(responseList);
        System.out.println("--------------------------------------------------");
        System.out.println("✅ TOTAL AISProcessV3 triggered = " + totalTriggered);
        System.out.println("--------------------------------------------------");

        Map<String, Object> variables = new HashMap<>();
        variables.put("inspectionSchedules", responseList);
        return variables;
    }

    /**
     * Determines whether the given schedule should trigger today.
     */
    private boolean isEntityScheduleDueToday(Inspection_Type_Schedule schedule, LocalDate today) {
        LocalDate startDate = schedule.getStartDate();
        if (startDate == null) return false;

        LocalDate endDate = schedule.getEndDate();
        if (endDate != null && today.isAfter(endDate)) return false;

        String type = schedule.getScheduleType() != null ? schedule.getScheduleType().trim().toLowerCase() : "";
        int interval = (schedule.getInterval() != null && schedule.getInterval() > 0) ? schedule.getInterval() : 1;

        switch (type) {
        case "don't repeat":
            System.out.println("Stopping process for inspectionTypeId=" 
                    + schedule.getInspectionType().getIns_type_id() + " (Do not repeat)");
            return false;

            case "weekly": {
                if (schedule.getDaysOfWeek() == null || schedule.getDaysOfWeek().isEmpty()) return false;
                DayOfWeek todayDay = today.getDayOfWeek();
                boolean dayMatches = schedule.getDaysOfWeek().stream()
                        .map(String::toUpperCase)
                        .anyMatch(d -> d.equals(todayDay.name()));
                if (!dayMatches) return false;

                long weeksSinceStart = ChronoUnit.WEEKS.between(
                        startDate.with(DayOfWeek.MONDAY), today.with(DayOfWeek.MONDAY));
                return weeksSinceStart >= 0 && weeksSinceStart % interval == 0;
            }

            case "monthly": {
                if (today.isBefore(startDate)) return false;

                YearMonth startMonth = YearMonth.from(startDate);
                YearMonth currentMonth = YearMonth.from(today);
                long monthsSinceStart = ChronoUnit.MONTHS.between(startMonth, currentMonth);

                // ✅ Updated logic as per requirement:
                // interval = 1 → include start month
                // interval > 1 → trigger only on interval-th, 2*interval-th months after start
                if ((interval == 1 && monthsSinceStart < 0) ||
                    (interval > 1 && ((monthsSinceStart + 1) % interval != 0))) {
                    return false;
                }

                // ✅ Days of month option
                if (schedule.getDaysOfMonth() != null && schedule.getDaysOfMonth().contains(today.getDayOfMonth())) {
                    return true;
                }

                // ✅ Week position option (e.g., FIRST_TUESDAY)
                if (schedule.getWeekPosition() != null && !schedule.getWeekPosition().isEmpty()) {
                    String[] parts = schedule.getWeekPosition().split("_");
                    if (parts.length == 2) {
                        String weekPart = parts[0].toUpperCase();
                        DayOfWeek targetDay = DayOfWeek.valueOf(parts[1].toUpperCase());
                        LocalDate candidateDate =
                                getWeekPositionDate(currentMonth.getYear(), currentMonth.getMonth(), weekPart, targetDay);
                        return candidateDate != null && candidateDate.equals(today);
                    }
                }
                return false;
            }

            case "custom": {
                long daysSinceStart = ChronoUnit.DAYS.between(startDate, today);
                return daysSinceStart >= 0 && ((daysSinceStart + 1) % interval == 0);
            }

            default:
                return false;
        }
    }

    private LocalDate getWeekPositionDate(int year, Month month, String position, DayOfWeek dayOfWeek) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        return switch (position.toUpperCase()) {
            case "FIRST" -> firstDay.with(TemporalAdjusters.firstInMonth(dayOfWeek));
            case "SECOND" -> firstDay.with(TemporalAdjusters.dayOfWeekInMonth(2, dayOfWeek));
            case "THIRD" -> firstDay.with(TemporalAdjusters.dayOfWeekInMonth(3, dayOfWeek));
            case "FOURTH" -> firstDay.with(TemporalAdjusters.dayOfWeekInMonth(4, dayOfWeek));
            case "FIFTH" -> firstDay.with(TemporalAdjusters.dayOfWeekInMonth(5, dayOfWeek));
            default -> null;
        };
    }

    private void printResponse(List<Map<String, Object>> responseList) {
        try {
            System.out.println("======= Inspection Schedule JobWorker Response By Entity =======");
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseList);
            System.out.println(prettyJson);
            System.out.println("================================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int triggerAISProcesses(List<Map<String, Object>> responseList) {
        int totalTriggered = 0;

        for (Map<String, Object> entityVars : responseList) {
            String entityId = (String) entityVars.get("entityId");
            String entityName = (String) entityVars.get("entityName");
            String dateOfInspection = (String) entityVars.get("dateOfInspection");
            String reason = (String) entityVars.get("reason");
            String createdBy = (String) entityVars.get("createdBy");
            String inspectionSource = (String) entityVars.get("inspectionSource");
            LocalDate createdDate = LocalDate.parse((String) entityVars.get("createdDate"), DateTimeFormatter.ISO_DATE);
            boolean isPreInspection = (boolean) entityVars.get("is_preinspection");

            List<Map<String, Object>> inspections = (List<Map<String, Object>>) entityVars.get("inspections");

            for (Map<String, Object> insp : inspections) {
                StartAISRequestDTO dto = new StartAISRequestDTO();
                dto.setEntityId(entityId);
                dto.setInspectionType((String) insp.get("inspectionTypeName"));
                dto.setDateOfInspection(dateOfInspection);
                dto.setReason(reason);
                dto.setCreatedBy(createdBy);
                dto.setInspectionSource(inspectionSource);
                dto.setCreatedDate(createdDate);
                dto.setIs_preinspection(isPreInspection);

                Object templateIdObj = insp.get("templateId");
                Long templateId = templateIdObj != null ? ((Number) templateIdObj).longValue() : null;
                dto.setTemplateId(templateId);

                Object controlTypeIdObj = insp.get("controlTypeId");
                Long controlTypeId = controlTypeIdObj != null ? ((Number) controlTypeIdObj).longValue() : null;
                dto.setControlTypeId(controlTypeId);

                Map<String, Object> processVars = new HashMap<>();
                processVars.put("entityId", dto.getEntityId());
                processVars.put("inspectionType", dto.getInspectionType());
                processVars.put("dateOfInspection", dto.getDateOfInspection());
                processVars.put("reason", dto.getReason());
                processVars.put("createdBy", dto.getCreatedBy());
                processVars.put("inspectionSource", dto.getInspectionSource());
                processVars.put("createdDate", dto.getCreatedDate().toString());
                processVars.put("is_preinspection", dto.isIs_preinspection());
                processVars.put("templateId", dto.getTemplateId());
                processVars.put("controlTypeId", dto.getControlTypeId());

                ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
                        .bpmnProcessId("AISProcessV3")
                        .latestVersion()
                        .variables(processVars)
                        .send()
                        .join();

                long processInstanceKey = event.getProcessInstanceKey();
                String businessKey = "AIS" + processInstanceKey;

                processVars.put("AISBusinessKey", businessKey);
                zeebeClient.newSetVariablesCommand(processInstanceKey).variables(processVars).send().join();

                inspectionCaseService.saveInspectionCase(dto, businessKey);

                System.out.println("   ✅ Triggered: " + dto.getInspectionType() + " | BusinessKey: " + businessKey);
                totalTriggered++;
            }
        }
        return totalTriggered;
    }
}
