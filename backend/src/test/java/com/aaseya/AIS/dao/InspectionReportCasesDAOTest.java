package com.aaseya.AIS.dao;

import com.aaseya.AIS.Model.InspectionCase;

import com.aaseya.AIS.dto.EntityInspectionCasesReportResponseDTO;
import com.aaseya.AIS.dto.EntityRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InspectionReportCasesDAOTest {

    private InspectionReportDAO dao;
    private EntityManager entityManager;

    private CriteriaBuilder cb;
    private CriteriaQuery<InspectionCase> cq;
    private Root<InspectionCase> root;
    private Path<?> pathEntity;
    private Path<?> pathEntityId;
    private Predicate predicate;
    private Expression<LocalDate> expr;
    private TypedQuery<InspectionCase> typedQuery;

    @BeforeEach
    void setUp() throws Exception {

        entityManager = mock(EntityManager.class);
        dao = new InspectionReportDAO();

        var field = InspectionReportDAO.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(dao, entityManager);

        cb = mock(CriteriaBuilder.class);
        cq = mock(CriteriaQuery.class);
        root = mock(Root.class);
        pathEntity = mock(Path.class);
        pathEntityId = mock(Path.class);
        predicate = mock(Predicate.class);
        expr = mock(Expression.class);
        typedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(InspectionCase.class)).thenReturn(cq);
        when(cq.from(InspectionCase.class)).thenReturn(root);

        when(root.get("entity")).thenReturn((Path) pathEntity);
        when(pathEntity.get("id")).thenReturn((Path) pathEntityId);

        lenient().when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        lenient().when(cb.or(any(Predicate[].class))).thenReturn(predicate);

        when(cb.function(anyString(), eq(LocalDate.class), any(), any())).thenReturn(expr);
        when(cb.between(any(Expression.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(predicate);

        when(cq.select(any())).thenReturn(cq);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
    }
}
