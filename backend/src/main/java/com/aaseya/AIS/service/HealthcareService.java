package com.aaseya.AIS.service;

import com.aaseya.AIS.Model.CSRDocuments;
import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.dao.ClaimCaseDAO;
import com.aaseya.AIS.dao.PolicyDetailsDAO;
import com.aaseya.AIS.dto.StartHealthcareRequestDTO;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HealthcareService {

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ClaimCaseDAO claimCaseDAO;

    @Autowired
    private PolicyDetailsDAO policyDetailsDAO;

    @Transactional
    public long startHealthcareProcess(StartHealthcareRequestDTO requestDTO) {

        // 1. 💾 Fetch Policy Details from the database
        PolicyDetails existingPolicy = policyDetailsDAO.findById(requestDTO.getPolicyId());
        if (existingPolicy == null) {
            throw new RuntimeException("Policy with ID " + requestDTO.getPolicyId() + " not found.");
        }

        // 2. 🚀 Start Camunda Process using fetched data
        Map<String, Object> variables = new HashMap<>();
        
        // Populate variables from the fetched PolicyDetails entity
        variables.put("policyId", existingPolicy.getPolicyId());
        variables.put("customerName", existingPolicy.getCustomerName());
        variables.put("dateOfBirth", existingPolicy.getDateOfBirth() != null ? existingPolicy.getDateOfBirth().toString() : null);
        variables.put("address", existingPolicy.getAddress());
        variables.put("phoneNumber", existingPolicy.getPhoneNumber());
        variables.put("email", existingPolicy.getEmail());
        variables.put("policyType", existingPolicy.getPolicyType());
        variables.put("coverageStartDate", existingPolicy.getCoverageStartDate() != null ? existingPolicy.getCoverageStartDate().toString() : null);
        variables.put("coverageEndDate", existingPolicy.getCoverageEndDate() != null ? existingPolicy.getCoverageEndDate().toString() : null);

        // Add claimType from the request DTO
        variables.put("claimType", requestDTO.getClaimType());
        variables.put("policyType", requestDTO.getPolicyType());
        
        // Add documents from the request DTO
        variables.put("documents", requestDTO.getDocuments());

        ProcessInstanceEvent processInstanceEvent = zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId("AISTest")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        long businessKey = processInstanceEvent.getProcessInstanceKey();

        // Also store businessKey back into the process instance
        zeebeClient.newSetVariablesCommand(businessKey)
                .variables(Map.of("BusinessKey", businessKey))
                .send()
                .join();

        // 3. 💾 Persist ClaimCase and Documents
        ClaimCase claimCase = new ClaimCase();
        claimCase.setClaimId(businessKey);
        claimCase.setClaimType(requestDTO.getClaimType());
        claimCase.setPolicyType(requestDTO.getPolicyType()); // Use policy type from the database
        
        // Link the policy to the claim
        claimCase.setPolicyDetails(existingPolicy);

        // Map and prepare CSRDocuments
        List<CSRDocuments> documentEntities = requestDTO.getDocuments().stream().map(docDTO -> {
            CSRDocuments docEntity = new CSRDocuments();
            docEntity.setDocumentType(docDTO.getDocumentType());
            docEntity.setDocument(docDTO.getDocument());
            docEntity.setClaimCase(claimCase); // Set the relationship
            return docEntity;
        }).collect(Collectors.toList());
        
        claimCase.setCsrDocuments(documentEntities);
        
        // Save the entire aggregate (ClaimCase and its documents)
        claimCaseDAO.save(claimCase);

        return businessKey;
    }
}
