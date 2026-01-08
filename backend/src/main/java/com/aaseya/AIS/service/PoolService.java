package com.aaseya.AIS.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dao.PoolDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.ClaimCaseDTO;
import com.aaseya.AIS.dto.DashboardCountsDTO;
import com.aaseya.AIS.dto.PolicyDetailsDTO;

import jakarta.transaction.Transactional;

@Service
public class PoolService {

    @Autowired
    private PoolDAO poolDAO;
    
    @Autowired
    private UsersDAO usersDAO;


    public List<ClaimCaseDTO> getClaimCasesByPoolName(String poolName) {
        List<ClaimCase> claimCases = poolDAO.findClaimCasesByPoolName(poolName);

        return claimCases.stream().map(claimCase -> {
            PolicyDetails pd = claimCase.getPolicyDetails();
            PolicyDetailsDTO pdDTO = new PolicyDetailsDTO();
            if (pd != null) {
                pdDTO.setPolicyId(pd.getPolicyId());
                pdDTO.setCustomerName(pd.getCustomerName());
                pdDTO.setGender(pd.getGender());
                pdDTO.setDateOfBirth(pd.getDateOfBirth());
                pdDTO.setAddress(pd.getAddress());
                pdDTO.setPhoneNumber(pd.getPhoneNumber());
                pdDTO.setEmail(pd.getEmail());
                pdDTO.setPolicyType(pd.getPolicyType());
                pdDTO.setCoverageStartDate(pd.getCoverageStartDate());
                pdDTO.setCoverageEndDate(pd.getCoverageEndDate());
                pdDTO.setPremiumAmount(pd.getPremiumAmount());
                pdDTO.setMedicalHistory(pd.getMedicalHistory());
                pdDTO.setInsurancePlanName(pd.getInsurancePlanName());
                pdDTO.setCreatedTimestamp(pd.getCreatedTimestamp());
            }

            ClaimCaseDTO ccDTO = new ClaimCaseDTO();
            ccDTO.setClaimId(claimCase.getClaimId());
            ccDTO.setClaimType(claimCase.getClaimType());
            ccDTO.setPolicyType(claimCase.getPolicyType());
            ccDTO.setStatus(claimCase.getStatus());
            ccDTO.setCreatedTimestamp(claimCase.getCreatedTimestamp());
            ccDTO.setPolicyDetails(pdDTO);
            ccDTO.setDischargeSummary(claimCase.getDischargeSummary());
            ccDTO.setClaimCaseDetails(claimCase.getValidationDetails());
            return ccDTO;
        }).toList();
    }
    
 // Method to assign user to assignedInspector/reviewer/approver according to poolName
    public boolean assignUserToClaimByPool(Long claimId, String poolName, String username) {
        ClaimCase claimCase = poolDAO.findClaimCaseById(claimId);
        if (claimCase == null) {
            return false;
        }

        String normalizedPoolName = poolName.toLowerCase().replaceAll("\\s+", "");

        switch (normalizedPoolName) {
            case "inspectorpool":
                claimCase.setAssignedInspector(username);
                claimCase.setStatus("pending");
                break;
            case "reviewerpool":
                claimCase.setAssignedReviewer(username);
                claimCase.setStatus("pending_inreview");
                break;
            case "approverpool":
                claimCase.setAssignedApprover(username);
                claimCase.setStatus("pending_inapproval");
                break;
            default:
                return false; // invalid pool name
        }
        
        
        claimCase.setPool(null);
        poolDAO.updateClaimCase(claimCase);
        return true;
    }
    
    public DashboardCountsDTO getDashboardCountsByEmail(String email) {
        Users user = usersDAO.findByEmail(email);
        DashboardCountsDTO dto = new DashboardCountsDTO();
        if (user == null || user.getRole() == null) {
            return dto; // empty counts if user not found or role missing
        }

        Map<String, Long> counts = poolDAO.getCaseStatusCountsByRoleAndEmail(user.getRole(), email);

        dto.setTotalCount(counts.getOrDefault("totalCount", 0L));
        dto.setNewCount(counts.getOrDefault("newCount", 0L));
        dto.setPendingCount(counts.getOrDefault("pendingCount", 0L));
        dto.setReopenedCount(counts.getOrDefault("reopenedCount", 0L));
        dto.setCompletedCount(counts.getOrDefault("completedCount", 0L));

        return dto;
    }

}


