package com.aaseya.AIS.service;
 
 
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.dao.PolicyDetailsDAO;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class PolicyDetailsService {
 
    private final PolicyDetailsDAO policyDetailsDAO;
 
    public PolicyDetailsService(PolicyDetailsDAO dao) {
        this.policyDetailsDAO = dao;
    }
 
    public Page<PolicyDetails> getAllPolicies(Pageable pageable) {
        return policyDetailsDAO.findAll(pageable);
    }
}