package com.aaseya.AIS.service;
 
import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.HealthcareChecklistandAnswers;
import com.aaseya.AIS.dao.ClaimCaseDAO;
import com.aaseya.AIS.dao.HealthcareChecklistandAnswersDAO;
import com.aaseya.AIS.dto.ClaimDTO;
import com.aaseya.AIS.dto.ClaimResponseDTO;
import com.aaseya.AIS.dto.HealthChecklistAnswersDTO;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
public class ClaimReviewService {
 
    private final ClaimCaseDAO claimCaseDAO;
    private final HealthcareChecklistandAnswersDAO checklistDAO;
 
    public ClaimReviewService(ClaimCaseDAO claimCaseDAO, HealthcareChecklistandAnswersDAO checklistDAO) {
        this.claimCaseDAO = claimCaseDAO;
        this.checklistDAO = checklistDAO;
    }
 
    @Transactional(readOnly = true)
    public ClaimResponseDTO getClaimWithChecklist(Long claimId) {
        // 1. Fetch ClaimCase selecting only required fields.
        ClaimCase claim = claimCaseDAO.getById(claimId);

        // 2. Fetch full HealthcareChecklistandAnswers list by claimId with associated checklist and category eagerly fetched.
        // Assuming checklistDAO.getByClaimId fetches all needed associations eagerly or via JOIN FETCH queries.
        List<HealthcareChecklistandAnswers> checklist = checklistDAO.getByClaimIdWithCategoryAndChecklistItem(claimId);

        // 3. Map `ClaimDTO` with only required fields
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setClaimId(claim.getClaimId());
        claimDTO.setInspectorReport(claim.getInspectorReport());
        claimDTO.setReviewerReport(claim.getReviewerReport());
        claimDTO.setApproverReport(claim.getApproverReport());
        claimDTO.setRecommendedAction(claim.getRecommendedAction());
        claimDTO.setAmountStatus(claim.getAmountStatus());

        // 4. Map checklist answers including full associated category and checklist item info
        List<HealthChecklistAnswersDTO> answers = checklist.stream().map(ans -> {
            HealthChecklistAnswersDTO dto = new HealthChecklistAnswersDTO();
            dto.setAnswer(ans.getAnswer());
            dto.setAttachment(ans.getAttachment());
            dto.setComment(ans.getComment());
            dto.setCorrectiveAction(ans.getCorrectiveAction());

            // Checklist Item details
            dto.setChecklistId(ans.getChecklistItem().getChecklistId());
            dto.setChecklistDescription(ans.getChecklistItem().getChecklistDescription());
            dto.setCategoryId(ans.getCategory().getCategoryId());
            dto.setCategoryName(ans.getCategory().getCategoryName());

            dto.setClaimId(ans.getClaimCase().getClaimId());
            return dto;
        }).collect(Collectors.toList());

        // 5. Prepare and return response DTO
        ClaimResponseDTO response = new ClaimResponseDTO();
        response.setClaim(claimDTO);

        if (!checklist.isEmpty()) {
            response.setCategoryId(checklist.get(0).getCategory().getCategoryId());
            response.setCategoryName(checklist.get(0).getCategory().getCategoryName());
        }
        response.setChecklistAnswers(answers);

        return response;
    }

}