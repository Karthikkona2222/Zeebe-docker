package com.aaseya.AIS.service;

import com.aaseya.AIS.dao.ClaimRequestDocumentsDao;
import com.aaseya.AIS.Model.ClaimRequestDocuments;
import com.aaseya.AIS.Model.PolicyDetails;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimRequestDocumentsService {

    @Autowired
    private ClaimRequestDocumentsDao claimRequestDocumentsDao;

    @Transactional
    public ClaimRequestDocuments saveDocumentForPolicy(String policyId, ClaimRequestDocuments document) throws Exception {
        PolicyDetails policy = claimRequestDocumentsDao.findPolicyById(policyId);
        if (policy == null) {
            throw new Exception("Policy not found with id: " + policyId);
        }
        document.setPolicyDetails(policy);
        return claimRequestDocumentsDao.saveClaimRequestDocument(document);
    }
    
    public List<PolicyDetails> getPolicyByIdOrCustomerName(String policyId, String customerName) {
        return claimRequestDocumentsDao.findByPolicyIdOrCustomerName(policyId, customerName);
    }
}
