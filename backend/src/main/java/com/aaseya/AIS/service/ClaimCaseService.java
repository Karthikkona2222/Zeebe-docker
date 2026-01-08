package com.aaseya.AIS.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.Pool;
import com.aaseya.AIS.dao.ClaimCaseDAO;
import com.aaseya.AIS.dao.PoolDAO;
import com.aaseya.AIS.dto.ClaimCaseResponseDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service

public class ClaimCaseService {

	@Autowired

	private ClaimCaseDAO claimCaseDAO;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
    private PoolDAO poolDAO;

	public void saveExtractedData(Long claimId, String dischargeSummaryJson, String validationResultJson) {
        ClaimCase claimCase = claimCaseDAO.getClaimCaseById(claimId);
        if (claimCase != null) {
            claimCase.setDischargeSummary(dischargeSummaryJson);
            if (validationResultJson != null) {
                claimCase.setValidationDetails(validationResultJson);
            }
            claimCase.setStatus("new");  // Set status
            
            claimCase.setCreatedBy("csr@gmail.com");  // Set createdBy
            
            // Fetch Pool with id=1 and set it
            Pool pool = poolDAO.findPoolById(1L);
            if (pool == null) {
                throw new RuntimeException("Pool with ID 1 not found");
            }
            claimCase.setPool(pool);
            
            claimCaseDAO.saveOrUpdateClaimCase(claimCase);
        } else {
            throw new RuntimeException("ClaimCase not found with ID: " + claimId);
        }
    }
	
	@Transactional(readOnly = true)
    public List<ClaimCaseResponseDTO> fetchClaimCases(String emailID) {
        return claimCaseDAO.getClaimCasesByEmail(emailID);
    }

	public Map<String, Object> fetchDischargeSummary(Long claimId) {
        return claimCaseDAO.getDischargeSummaryByClaimId(claimId);
    }
}
	

