package com.aaseya.AIS.utility;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.dto.InspectionCaseDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Component
public class CriteriaPaginationUtil {

	private final EntityManager entityManager;

	public CriteriaPaginationUtil(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Generic method to handle pagination for any entity class and its DTO.
	 * 
	 * @param query        CriteriaQuery for the entity
	 * @param pageable     Pageable object to handle pagination
	 * @param entityClass  The entity class type
	 * @param dtoConverter A function to convert an entity to its corresponding DTO
	 * @param <T>          The entity class type
	 * @param <D>          The DTO class type
	 * @return A paginated result with DTOs
	 */
	public <T, D> Page<D> getPaginatedData(CriteriaQuery<T> query, Pageable pageable, Class<T> entityClass,
			Function<T, D> dtoConverter) {

		// CriteriaBuilder to handle queries
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		Root<T> root = query.from(entityClass);
		if (entityClass.isInstance(Inspection_Type.class)) {
			query.orderBy(criteriaBuilder.asc(root.get("ins_type_id")));
		}

		// Create TypedQuery to fetch the entity data
		TypedQuery<T> typedQuery = entityManager.createQuery(query);

		// Apply pagination
		typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		typedQuery.setMaxResults(pageable.getPageSize());

		System.out.println(pageable.getPageNumber());
		System.out.println(pageable.getPageSize());
		System.out.println("Query: " + typedQuery.unwrap(org.hibernate.query.Query.class).getQueryString());

		// Fetch the paginated result
		List<T> entities = typedQuery.getResultList();
		System.out.println(entities);

		// Convert the entities to DTOs
		List<D> dtoList = entities.stream().map(dtoConverter).collect(Collectors.toList());

		// Count the total number of records (needed for pagination metadata)
		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
		Root<T> countRoot = countQuery.from(entityClass);
		countQuery.select(criteriaBuilder.count(countRoot));

		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
		long totalCount = countTypedQuery.getSingleResult();

		// Return a Page containing the DTOs and pagination metadata
		return new PageImpl<>(dtoList, pageable, totalCount);
	}
	
//	public <T, D> Page<D> getPaginatedDataForUI(CriteriaQuery<D> query,Root<T> root, Pageable pageable, Class<D> entityClass) {
//
//		// CriteriaBuilder to handle queries
//		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//		
//
//		// Create TypedQuery to fetch the entity data
//		TypedQuery<D> typedQuery = entityManager.createQuery(query);
//
//		// Apply pagination
//		typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
//		typedQuery.setMaxResults(pageable.getPageSize());
//
//		System.out.println(pageable.getPageNumber());
//		System.out.println(pageable.getPageSize());
//		System.out.println("Query: " + typedQuery.unwrap(org.hibernate.query.Query.class).getQueryString());
//
//		// Fetch the paginated result
//		List<D> entities = typedQuery.getResultList();
//		System.out.println(entities);
//
//		// Convert the entities to DTOs
//		List<D> dtoList = entities.stream().collect(Collectors.toList());
//
//		// Count the total number of records (needed for pagination metadata)
//		CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//		Root<D> countRoot = countQuery.from(entityClass);
//		countQuery.select(criteriaBuilder.count(countRoot));
//
//		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
//		long totalCount = countTypedQuery.getSingleResult();
//
//		// Return a Page containing the DTOs and pagination metadata
//		return new PageImpl<>(dtoList, pageable, totalCount);
//	}

	public Page<InspectionCaseDTO> getPaginatedDataForUI(CriteriaQuery<InspectionCaseDTO> query,
			Root<InspectionCase> inspectionCase, Pageable pageable, Class<InspectionCase> class1) {
		// CriteriaBuilder to handle queries
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		

		// Create TypedQuery to fetch the entity data
		TypedQuery<InspectionCaseDTO> typedQuery = entityManager.createQuery(query);
		long count=typedQuery.getResultList().size();
		System.out.println(count);
		// Apply pagination
		typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		typedQuery.setMaxResults(pageable.getPageSize());

		System.out.println(pageable.getPageNumber());
		System.out.println(pageable.getPageSize());
		System.out.println("Query: " + typedQuery.unwrap(org.hibernate.query.Query.class).getQueryString());

		// Fetch the paginated result
		List<InspectionCaseDTO> entities = typedQuery.getResultList();
		System.out.println(entities);

		// Convert the entities to DTOs
		List<InspectionCaseDTO> dtoList = entities.stream().collect(Collectors.toList());

		

		// Return a Page containing the DTOs and pagination metadata
		return new PageImpl<>(dtoList, pageable, count);
	}
}
