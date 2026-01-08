package com.aaseya.AIS.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.dto.EntityInspectionReportDTO;
import com.aaseya.AIS.dto.EntityRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class EntityInspectionReportDAOTest {

    private InspectionReportDAO dao;
    private EntityManager entityManager;

    private CriteriaBuilder cb;
    private CriteriaQuery<InspectionCase> cq;
    private Root<InspectionCase> root;
    private Predicate predicate;
    private Expression<LocalDate> expr;
    private TypedQuery<InspectionCase> typedQuery;

    @BeforeEach
    void setUp() throws Exception {

        entityManager = mock(EntityManager.class);
        dao = new InspectionReportDAO();

        Field f = InspectionReportDAO.class.getDeclaredField("entityManager");
        f.setAccessible(true);
        f.set(dao, entityManager);

        cb = mock(CriteriaBuilder.class);
        cq = mock(CriteriaQuery.class);
        root = mock(Root.class);
        predicate = mock(Predicate.class);
        expr = mock(Expression.class);
        typedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(InspectionCase.class)).thenReturn(cq);
        when(cq.from(InspectionCase.class)).thenReturn(root);

        lenient().when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        lenient().when(cb.between(any(Expression.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(predicate);
        lenient().when(cb.function(anyString(), eq(LocalDate.class), any(), any()))
                .thenReturn(expr);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        lenient().when(cb.or(any(Predicate[].class))).thenReturn(predicate);

        when(cq.select(any())).thenReturn(cq);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
    }
}
