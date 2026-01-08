package com.aaseya.AIS.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.Pool;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Repository
public class PoolDAO {
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public List<ClaimCase> findClaimCasesByPoolName(String poolNameInput) {
	    String normalizedInput = poolNameInput.replaceAll("\\s+", "").toLowerCase();

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // Query Pool matching normalized pool name (ignore spaces and case)
	    CriteriaQuery<Pool> poolQuery = cb.createQuery(Pool.class);
	    Root<Pool> poolRoot = poolQuery.from(Pool.class);

	    // Use function to apply LOWER and REPLACE:
	    Expression<String> normalizedPoolNameDB = cb.function(
	        "REPLACE", String.class,
	        cb.lower(poolRoot.get("poolName")),
	        cb.literal(" "),
	        cb.literal("")
	    );

	    poolQuery.select(poolRoot)
	             .where(cb.equal(normalizedPoolNameDB, normalizedInput));

	    Pool pool;
	    try {
	        pool = entityManager.createQuery(poolQuery).getSingleResult();
	    } catch (NoResultException e) {
	        return List.of(); // no pool found
	    }

	    // Query ClaimCase for found Pool
	    CriteriaQuery<ClaimCase> claimQuery = cb.createQuery(ClaimCase.class);
	    Root<ClaimCase> claimRoot = claimQuery.from(ClaimCase.class);
	    claimQuery.select(claimRoot)
	              .where(cb.equal(claimRoot.get("pool"), pool));

	    return entityManager.createQuery(claimQuery).getResultList();
	}

	// Find ClaimCase by id
    public ClaimCase findClaimCaseById(Long claimId) {
        return entityManager.find(ClaimCase.class, claimId);
    }

    // Update ClaimCase entity
    @Transactional
    public void updateClaimCase(ClaimCase claimCase) {
        entityManager.merge(claimCase);
    }
    
    public Map<String, Long> getCaseStatusCountsByRoleAndEmail(String role, String email) {
        String assignedField;
        String pendingStatus;

        String normalizedRole = role.toLowerCase().replaceAll("\\s+", "");

        switch (normalizedRole) {
            case "inspector":
            case "inspectorpool":
                assignedField = "assignedInspector";
                pendingStatus = "pending";
                break;
            case "reviewer":
            case "reviewerpool":
                assignedField = "assignedReviewer";
                pendingStatus = "pending_inreview";
                break;
            case "approver":
            case "approverpool":
                assignedField = "assignedApprover";
                pendingStatus = "pending_inapproval";
                break;
            default:
                return Collections.emptyMap();
        }

        // Query all statuses for assigned email in assignedField
        String jpql = "SELECT c.status, COUNT(c) FROM ClaimCase c " +
                      "WHERE LOWER(c." + assignedField + ") = :email " +
                      "GROUP BY c.status";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .setParameter("email", email.toLowerCase())
                .getResultList();

        Map<String, Long> statusCounts = new HashMap<>();
        long total = 0;
        long completedCount = 0;

        // Temporary map to store counts by status for completed calculation
        Map<String, Long> countsByStatus = new HashMap<>();

        for (Object[] row : resultList) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            total += count;

            countsByStatus.put(status.toLowerCase(), count);

            // Collect pending and reopened counts now
            if (status.equalsIgnoreCase(pendingStatus)) {
                statusCounts.put("pendingCount", count);
            } else if (status.equalsIgnoreCase("reopened")) {
                statusCounts.put("reopenedCount", count);
            }
        }

        // Calculate completedCount based on role and status counts
        switch (normalizedRole) {
            case "inspector":
            case "inspectorpool":
                completedCount += countsByStatus.getOrDefault("pending_inreview", 0L);
                completedCount += countsByStatus.getOrDefault("pending_review", 0L);
                completedCount += countsByStatus.getOrDefault("pending_inapproval", 0L);
                completedCount += countsByStatus.getOrDefault("pending_approval", 0L);
                completedCount += countsByStatus.getOrDefault("completed", 0L);
                break;

            case "reviewer":
            case "reviewerpool":
                completedCount += countsByStatus.getOrDefault("pending_inapproval", 0L);
                completedCount += countsByStatus.getOrDefault("pending_approval", 0L);
                completedCount += countsByStatus.getOrDefault("completed", 0L);
                break;

            case "approver":
            case "approverpool":
//                completedCount += countsByStatus.getOrDefault("pending_approval", 0L);
                completedCount += countsByStatus.getOrDefault("completed", 0L);
                // Reopened already handled
                break;
        }

        statusCounts.put("completedCount", completedCount);
        statusCounts.put("totalCount", total);

        // Ensure keys exist
        statusCounts.putIfAbsent("pendingCount", 0L);
        statusCounts.putIfAbsent("reopenedCount", 0L);
        statusCounts.putIfAbsent("newCount", 0L);

        return statusCounts;
    }

    
    public Pool findPoolById(Long poolId) {
        return entityManager.find(Pool.class, poolId);
    }

}
	