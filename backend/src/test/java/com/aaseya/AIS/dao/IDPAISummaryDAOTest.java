package com.aaseya.AIS.dao;

import com.aaseya.AIS.dto.IDPSummaryResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IDPAISummaryDAOTest {

    @InjectMocks
    private IDPAISummaryDAO dao;

    @Mock
    private EntityManager em;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<IDPSummaryResponseDTO> cq;

    @Mock
    private Root<IDPSummaryResponseDTO> root;

    @Mock
    private Join<Object, Object> join;

    @Mock
    private Path<Object> path;

    @Mock
    private Predicate predicate;

    @Mock
    private TypedQuery<IDPSummaryResponseDTO> typedQuery;

    @BeforeEach
    void init() {

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(IDPSummaryResponseDTO.class)).thenReturn(cq);
        when(cq.from(any(Class.class))).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        // joins & paths
        lenient().when(root.join(anyString(), any())).thenReturn(join);
        lenient().when(root.get(anyString())).thenReturn(path);
        lenient().when(join.get(anyString())).thenReturn(path);

        // predicates
        lenient().when(cb.equal(any(Expression.class), any())).thenReturn(predicate);
        lenient().when(cb.isNotNull(any())).thenReturn(predicate);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        lenient().when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        lenient().when(cb.conjunction()).thenReturn(predicate);

        // ✅ CRITICAL FIX — ALL where() overloads
        lenient().when(cq.where(any(Predicate[].class))).thenReturn(cq);
        lenient().when(cq.where(any(Predicate.class))).thenReturn(cq);
        lenient().when(cq.where(any(Expression.class))).thenReturn(cq);

        when(cq.select(any())).thenReturn(cq);
    }

    @Test
    void testFindByInspectionID() {
        List<IDPSummaryResponseDTO> result = dao.findByInspectionID(123L);
        assertNotNull(result);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindAllSummaries() {
        List<IDPSummaryResponseDTO> result = dao.findAllSummaries();
        assertNotNull(result);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindByEntityInspectionSource() {
        List<IDPSummaryResponseDTO> result =
                dao.findByEntityInspectionSource("EntityX", "TypeY", "ADP");

        assertNotNull(result);
        verify(typedQuery).getResultList();
    }
}
