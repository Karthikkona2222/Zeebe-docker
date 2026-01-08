package com.aaseya.AIS.dao;
 
import jakarta.persistence.EntityManager;

import jakarta.persistence.TypedQuery;

import jakarta.persistence.criteria.CriteriaBuilder;

import jakarta.persistence.criteria.CriteriaQuery;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;

import org.mockito.quality.Strictness;
 
import java.time.LocalDate;

import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
 
@ExtendWith(MockitoExtension.class)

@MockitoSettings(strictness = Strictness.LENIENT)

class InspectionCaseStatsDAOTest {
 
    @InjectMocks

    private InspectionCaseDAO inspectionCaseDAO;
 
    // Deep-stub EntityManager so all CriteriaBuilder/Root/Expression chains work

    @Mock(answer = RETURNS_DEEP_STUBS)

    private EntityManager entityManager;
 
    @Mock

    private TypedQuery<Object[]> typedQuery;
 
    @BeforeEach

    void setup() {

        CriteriaBuilder cb = mock(CriteriaBuilder.class, RETURNS_DEEP_STUBS);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);

        when(entityManager.createQuery(any(CriteriaQuery.class))).thenReturn(typedQuery);

    }
 
    @Test

    void getCaseStatsByMonth_buildsMonthlyStatsCorrectly() {

        LocalDate startDate = LocalDate.of(2025, 1, 1);

        LocalDate endDate   = LocalDate.of(2025, 1, 31);

        String processType  = "ADP";
 
        // Simulate DB row: [ "2025-01", pending=1, in_progress=2, completed=3 ]

        List<Object[]> dbResult = new ArrayList<>();

        dbResult.add(new Object[] { "2025-01", 1L, 2L, 3L });
 
        when(typedQuery.getResultList()).thenReturn(dbResult);
 
        List<Map<String, Object>> result =

                inspectionCaseDAO.getCaseStatsByMonth(startDate, endDate, processType);
 
        assertNotNull(result);

        assertEquals(1, result.size());
 
        Map<String, Object> stats = result.get(0);
 
        // "2025-01" → "January 2025"

        assertEquals("January 2025", stats.get("month"));
 
        assertEquals(1L, stats.get("pendingCases"));

        assertEquals(2L, stats.get("inProgressCases"));

        assertEquals(3L, stats.get("completedCases"));
 
        // total = 1+2+3 = 6 → completion = (3*100)/6 = 50

        assertEquals(50L, stats.get("completionPercentage"));
 
        verify(entityManager).getCriteriaBuilder();

        verify(entityManager).createQuery(any(CriteriaQuery.class));

        verify(typedQuery).getResultList();

    }

}

