package com.aaseya.AIS.dao;

import com.aaseya.AIS.dto.IDPSummaryResponseDTO;
import com.aaseya.AIS.Model.IDPSummary;
import com.aaseya.AIS.Model.InspectionCase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class IDPAISummaryDAO {

    @PersistenceContext
    private EntityManager em;

    // ============================================================
    // A) Search by entityName + inspectionType + source (ADP)
    // ============================================================
    public List<IDPSummaryResponseDTO> findByEntityInspectionSource(
            String entityName,
            String inspectionType,
            String source
    ) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IDPSummaryResponseDTO> cq = cb.createQuery(IDPSummaryResponseDTO.class);
        Root<IDPSummary> root = cq.from(IDPSummary.class);
        Join<IDPSummary, InspectionCase> ic = root.join("inspectionCase", JoinType.LEFT);

        cq.select(cb.construct(
                IDPSummaryResponseDTO.class,
                root.get("inspectionID"),
                root.get("entity").get("name"),
                root.get("equipmentType"),
                root.get("equipmentId"),
                root.get("riskScore"),
                root.get("riskLevel"),
                root.get("lastInspectionDate"),
                root.get("inspectionType").get("name"),
                ic.get("inspectorID"),
                root.get("status"),
                cb.nullLiteral(String.class),
                ic.get("inspector_source")        // NEW FIELD
        ));

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isNotNull(root.get("inspectionID")));

        if (entityName != null)
            predicates.add(cb.equal(root.get("entity").get("name"), entityName));

        if (inspectionType != null)
            predicates.add(cb.equal(root.get("inspectionType").get("name"), inspectionType));

        // Only apply if user passed ?source=ADP
        if (source != null && source.equalsIgnoreCase("ADP"))
            predicates.add(cb.equal(ic.get("inspector_source"), "ADP"));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(cq).getResultList();
    }

    // ============================================================
    // B) Get Summary by inspectionID (REPLACES processInstanceKey)
    // ============================================================
    public List<IDPSummaryResponseDTO> findByInspectionID(Long inspectionID) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IDPSummaryResponseDTO> cq = cb.createQuery(IDPSummaryResponseDTO.class);
        Root<IDPSummary> root = cq.from(IDPSummary.class);

        Join<IDPSummary, InspectionCase> ic = root.join("inspectionCase", JoinType.LEFT);

        cq.select(cb.construct(
                IDPSummaryResponseDTO.class,
                root.get("inspectionID"),
                root.get("entity").get("name"),
                root.get("equipmentType"),
                root.get("equipmentId"),
                root.get("riskScore"),
                root.get("riskLevel"),
                root.get("lastInspectionDate"),
                root.get("inspectionType").get("name"),
                ic.get("inspectorID"),
                root.get("status"),
                cb.nullLiteral(String.class),
                ic.get("inspector_source")
        ));

        cq.where(cb.equal(root.get("inspectionID"), inspectionID));

        return em.createQuery(cq).getResultList();
    }

    // ============================================================
    // C) Get All Joined Summaries (DEFAULT case – no filters)
    // ============================================================
    public List<IDPSummaryResponseDTO> findAllSummaries() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IDPSummaryResponseDTO> cq = cb.createQuery(IDPSummaryResponseDTO.class);
        Root<IDPSummary> root = cq.from(IDPSummary.class);

        Join<IDPSummary, InspectionCase> ic = root.join("inspectionCase", JoinType.LEFT);

        cq.select(cb.construct(
                IDPSummaryResponseDTO.class,
                root.get("inspectionID"),
                root.get("entity").get("name"),
                root.get("equipmentType"),
                root.get("equipmentId"),
                root.get("riskScore"),
                root.get("riskLevel"),
                root.get("lastInspectionDate"),
                root.get("inspectionType").get("name"),
                ic.get("inspectorID"),
                root.get("status"),
                cb.nullLiteral(String.class),
                ic.get("inspector_source")
        ));

        cq.where(cb.isNotNull(root.get("inspectionID")));

        return em.createQuery(cq).getResultList();
    }
}
