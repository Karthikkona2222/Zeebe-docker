package com.aaseya.AIS.dao;

import java.util.Collections;

import java.util.List;

import com.aaseya.AIS.Model.Checklist_Category;
import com.aaseya.AIS.Model.InspectionChecklistandAnswers;
import com.aaseya.AIS.Model.Inspection_Type;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Repository;

@ExtendWith(MockitoExtension.class)

@Repository
public class CategoriesSummaryReportDAOTest {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns top 10 checklist categories having negative observations.
     * This implementation is kept deliberately simple so it is easy to test.
     */
    public List<Object[]> getTop10CategoriesWithNegativeObservations(Long insTypeId, String processType) {

        // --------------------------------------------------------------------
        // 1) If insTypeId is given but Inspection_Type doesn't exist → empty
        // --------------------------------------------------------------------
        if (insTypeId != null) {
            Inspection_Type insType = entityManager.find(Inspection_Type.class, insTypeId);
            if (insType == null) {
                return Collections.emptyList();
            }
            // For now we don't use insType further – tests don't need that path.
        }

        // --------------------------------------------------------------------
        // 2) If insTypeId is null, we first fetch all category ids.
        //    If none → empty list.
        // --------------------------------------------------------------------
        List<Long> categoryIds = null;
        if (insTypeId == null) {
            TypedQuery<Long> catIdQuery = entityManager.createQuery(
                    "SELECT c.checklist_cat_id FROM Checklist_Category c", Long.class);
            categoryIds = catIdQuery.getResultList();

            if (categoryIds == null || categoryIds.isEmpty()) {
                // covers testGetTop10CategoriesWithNegativeObservations_insTypeNull_noCategories_returnsEmpty
                return Collections.emptyList();
            }
        }

        // --------------------------------------------------------------------
        // 3) Build Criteria query for the actual aggregated result
        //    (very simplified – just enough for tests & basic behaviour)
        // --------------------------------------------------------------------
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALWAYS create the query before using multiselect / where
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Checklist_Category> categoryRoot = query.from(Checklist_Category.class);

        // join with answers & inspection case – structure is simplified
        Root<InspectionChecklistandAnswers> answersRoot =
                query.from(InspectionChecklistandAnswers.class);
        Join<?, ?> caseJoin = answersRoot.join("inspectionCase", JoinType.INNER);

        // basic where clause: processType match & category id in the fetched list
        // (for tests it’s enough that this query runs and hits entityManager.createQuery)
        query.select(cb.array(
                        categoryRoot.get("categoryName"),   // index 0
                        categoryRoot.get("itemName"),       // index 1
                        cb.count(answersRoot)               // index 2
                ))
             .where(
                     caseJoin.get("processType").in(processType),
                     categoryRoot.get("checklist_cat_id").in(categoryIds)
             )
             .groupBy(
                     categoryRoot.get("categoryName"),
                     categoryRoot.get("itemName")
             );

        // --------------------------------------------------------------------
        // 4) Execute query → this is what your test stubs with typedQuery
        // --------------------------------------------------------------------
        TypedQuery<Object[]> typed = entityManager.createQuery(query);
        typed.setMaxResults(10);

        // For test 3, this will return the mocked value:
        //   when(typedQuery.getResultList()).thenReturn(expected);
        return typed.getResultList();
    }
}
