package com.aaseya.AIS.service;
 
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Template;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
 
@Service
public class SaveInspectionCaseService {
 
    @PersistenceContext
    private EntityManager em;
 
    @Transactional
    public Map<String, Object> saveInspectionCaseAndUpdateSummary(long adpProcessKey,
                                                                  Long businessKey,
                                                                  Map<String, Object> mergedJson,
                                                                  Map<String, Object> outputTarget) {
 
        System.out.println(" Starting transaction for processKey: " + businessKey);
 
        String entityId       = String.valueOf(mergedJson.get("entityId"));
        String inspectionType = String.valueOf(mergedJson.get("inspectionType"));  // <-- THIS IS NAME
        String status         = String.valueOf(mergedJson.get("Status"));
 
        // Check if InspectionCase already exists
        InspectionCase ic = em.find(InspectionCase.class, businessKey);
        if (ic == null) {
            ic = new InspectionCase();
            ic.setInspectionID(businessKey);
            ic.setEntity(em.getReference(NewEntity.class, entityId));
            ic.setEntityID(entityId);
            ic.setInspectionType(inspectionType);  // <-- NAME stored here
            ic.setStatus(status);
            ic.setCreatedBy("samuel@gmail.com");
            ic.setInspector_source("ADP");
            ic.setCreatedDate(LocalDate.now().minusDays(2));
            ic.setDateOfInspection(LocalDate.now().toString());
            ic.setDueDate(LocalDate.now());
            ic.setProcessType("ADP");
 
            em.persist(ic);
            System.out.println(" Created new InspectionCase: " + businessKey);
        } else {
            ic.setStatus(status);
            System.out.println(" Updated existing InspectionCase: " + businessKey);
        }
 
        // -----------------------------------------------------
        // ⭐ NEW LOGIC — DETERMINE TEMPLATE BASED ON NAME
        // -----------------------------------------------------
        try {
            System.out.println(" Looking for Inspection_Type by name: " + inspectionType);
 
            // 1️⃣ Find Inspection_Type WHERE name = inspectionType
            List<Inspection_Type> types = em.createQuery(
                    "SELECT t FROM Inspection_Type t WHERE t.name = :name", Inspection_Type.class)
                    .setParameter("name", inspectionType)
                    .getResultList();
 
            if (types == null || types.isEmpty()) {
                System.out.println(" No Inspection_Type found for name = " + inspectionType);
            } else {
 
                Inspection_Type insType = types.get(0);
                Long insTypeId = insType.getIns_type_id();
                System.out.println(" Found ins_type_id = " + insTypeId);
 
                // 2️⃣ Get template IDs mapped to this inspection type (via JPA relation)
                List<Long> templateIds = insType.getTemplates()
                        .stream()
                        .map(Template::getTemplate_id)
                        .toList();
 
                System.out.println(" Mapped template_ids = " + templateIds);
 
                if (!templateIds.isEmpty()) {
 
                    Long chosenTemplateId;
 
                    if (templateIds.size() == 1) {
                        // Single template → use it
                        chosenTemplateId = templateIds.get(0);
                        System.out.println(" Only one template → chosen = " + chosenTemplateId);
 
                    } else {
                        // 3️⃣ Multiple templates → find the most used template in InspectionCase table
                        System.out.println(" Checking usage frequency in InspectionCase table…");
 
                        chosenTemplateId = templateIds.stream()
                                .map(tid -> Map.entry(
                                        tid,
                                        em.createQuery(
                                                "SELECT COUNT(ic) FROM InspectionCase ic WHERE ic.template_id = :tid",
                                                Long.class
                                        ).setParameter("tid", tid).getSingleResult()
                                ))
                                .max((a, b) -> Long.compare(a.getValue(), b.getValue()))
                                .map(Map.Entry::getKey)
                                .orElse(null);
 
                        System.out.println(" Most used template_id = " + chosenTemplateId);
                    }
 
                    // 4️⃣ Save chosen template_id in DB (forced update)
                    if (chosenTemplateId != null) {
                        System.out.println(" Saving template_id = " + chosenTemplateId);
 
                        em.createQuery(
                                "UPDATE InspectionCase ic SET ic.template_id = :tid WHERE ic.inspectionID = :id")
                                .setParameter("tid", chosenTemplateId)
                                .setParameter("id", businessKey)
                                .executeUpdate();
 
                        System.out.println(" template_id updated successfully in DB!");
                    }
                }
            }
 
        } catch (Exception e) {
            System.err.println(" Error during template resolution: " + e.getMessage());
            e.printStackTrace();
        }
        // -----------------------------------------------------
 
        em.flush();
 
        // Update ADP summary
        int adpUpdated = em.createQuery(
                "UPDATE IDPSummary s SET s.inspectionID = :inspectionID WHERE s.processInstanceKey = :adpKey")
                .setParameter("inspectionID", businessKey)
                .setParameter("adpKey", adpProcessKey)
                .executeUpdate();
 
        // Update AIS summary
        int aisUpdated = em.createQuery(
                "UPDATE IDPSummary s SET s.inspectionID = :inspectionID WHERE s.processInstanceKey = :aisKey")
                .setParameter("inspectionID", businessKey)
                .setParameter("aisKey", businessKey)
                .executeUpdate();
 
        System.out.println(" Updated ADP summary: " + adpUpdated + ", AIS summary: " + aisUpdated);
 
        outputTarget.put("BusinessKey", businessKey);
        outputTarget.put("inspectionCaseSaved", true);
        outputTarget.put("inspectionCaseID", businessKey);
 
        System.out.println(" Finished transaction for " + businessKey);
 
        return outputTarget;
    }
}
 
 