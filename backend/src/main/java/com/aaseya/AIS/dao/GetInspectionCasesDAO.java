package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.InspectionCase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public class GetInspectionCasesDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<InspectionCase> getInspectionCases(String createdBy, String inspectorSource, String createdDateFilter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InspectionCase> cq = cb.createQuery(InspectionCase.class);
        Root<InspectionCase> root = cq.from(InspectionCase.class);

        // fetch entity details
        root.fetch("entity", JoinType.LEFT);

        Predicate predicate = cb.conjunction();

        // Status != completed
        predicate = cb.and(predicate,
                cb.notEqual(cb.lower(root.get("status")), "completed"));

        // createdBy filter (skip if "all")
        if (createdBy != null && !"all".equalsIgnoreCase(createdBy)) {
            predicate = cb.and(predicate,
                    cb.equal(cb.lower(root.get("createdBy")), createdBy.toLowerCase()));
        }

        // inspectorSource filter (skip if "all")
        if (inspectorSource != null && !"all".equalsIgnoreCase(inspectorSource)) {
            predicate = cb.and(predicate,
                    cb.equal(cb.lower(root.get("inspector_source")), inspectorSource.toLowerCase()));
        }

        // createdDate filter
        if (createdDateFilter != null && !"all".equalsIgnoreCase(createdDateFilter)) {
            LocalDate today = LocalDate.now();

            if (createdDateFilter.equalsIgnoreCase("today")) {
                // Exact date match
                predicate = cb.and(predicate,
                        cb.equal(root.get("createdDate"), today));

            } else if (createdDateFilter.equalsIgnoreCase("week")) {
                // Start (Monday) and End (Sunday) of current week
                LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
                LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

                predicate = cb.and(predicate,
                        cb.between(root.get("createdDate"), startOfWeek, endOfWeek));

            } else if (createdDateFilter.equalsIgnoreCase("month")) {
                // First and last day of current month
                LocalDate startOfMonth = today.withDayOfMonth(1);
                LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

                predicate = cb.and(predicate,
                        cb.between(root.get("createdDate"), startOfMonth, endOfMonth));
            }
        }

        cq.select(root).where(predicate).distinct(true);

        TypedQuery<InspectionCase> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
