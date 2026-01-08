package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dto.CaseSummaryDTO;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InspectionCaseDAOCaseSummaryTest {

    @InjectMocks
    private InspectionCaseDAO dao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<Object[]> cq;

    @Mock
    private Root<InspectionCase> root;

    @Mock
    private TypedQuery<Object[]> typedQuery;

    // ⚠ IMPORTANT: RAW TYPES (NO GENERICS)
    @Mock
    private Path path;

    @Mock
    private Predicate predicate;

    @Mock
    private Expression stringExpr;

    @Mock
    private Expression countExpr;

    @BeforeEach
    void setup() {

        // EntityManager + Criteria
        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Object[].class)).thenReturn(cq);
        when(cq.from(InspectionCase.class)).thenReturn(root);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);

        // select + groupBy
        when(cq.multiselect(any(), any(), any())).thenReturn(cq);
        when(cq.groupBy(any(Expression.class), any(Expression.class))).thenReturn(cq);

        // where() — ALL overloads (critical)
        lenient().when(cq.where(any(Predicate[].class))).thenReturn(cq);
        lenient().when(cq.where(any(Predicate.class))).thenReturn(cq);
        lenient().when(cq.where(any(Expression.class))).thenReturn(cq);

        // root.get()
        when(root.get(anyString())).thenReturn(path);

        // ✅ FIXED: path.in()
        when(path.in(anyCollection())).thenReturn(predicate);

        // Predicate creation
        when(cb.equal(any(), any())).thenReturn(predicate);
        when(cb.isNull(any())).thenReturn(predicate);
        when(cb.notEqual(any(), any())).thenReturn(predicate);
        when(cb.or(any(), any(), any())).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(cb.conjunction()).thenReturn(predicate);

        // Expressions
        when(cb.lower(any())).thenReturn(stringExpr);
        when(cb.trim(any())).thenReturn(stringExpr);
        when(cb.count(any())).thenReturn(countExpr);
        when(cb.coalesce(any(), any())).thenReturn(stringExpr);

        // alias()
        when(stringExpr.alias(anyString())).thenReturn(stringExpr);
        when(countExpr.alias(anyString())).thenReturn(countExpr);
    }
}