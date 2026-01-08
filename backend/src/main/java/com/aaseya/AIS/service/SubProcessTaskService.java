package com.aaseya.AIS.service;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.HealthCareChecklistCategory;
import com.aaseya.AIS.Model.HealthCareChecklistItem;
import com.aaseya.AIS.Model.HealthcareChecklistandAnswers;
import com.aaseya.AIS.Model.Pool;
import com.aaseya.AIS.dao.ChecklistCategoryDAO;
import com.aaseya.AIS.dao.ClaimCaseDAO;
import com.aaseya.AIS.dao.HealthCareChecklistCategoryDAO;
import com.aaseya.AIS.dao.HealthCareChecklistItemDAO;
import com.aaseya.AIS.dao.HealthcareChecklistandAnswersDAO;

import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
@Service
public class SubProcessTaskService {
	
	@Autowired
	private HealthCareChecklistCategoryDAO categoryDao;
	
	@Autowired
	private HealthCareChecklistItemDAO checklistItemDao;
	
	@Autowired
	private HealthcareChecklistandAnswersDAO healthcareChecklistandAnswersDAO;
 
    private final OperateService operateService;
    private final TaskListService tasklistService;
    private final ClaimCaseDAO claimCaseDao;  // <-- DAO for DB update
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    public SubProcessTaskService(OperateService operateService,
                                 TaskListService tasklistService,
                                 ClaimCaseDAO claimCaseDao) {
        this.operateService = operateService;
        this.tasklistService = tasklistService;
        this.claimCaseDao = claimCaseDao;
    }
 
    public String completeTaskByClaimIdAndProcessKey(Long claimId, String parentProcessInstanceKey, String claimReport) {
        try {
            // Step 1: Fetch ClaimCase by claimId and update pool_id to 2
            ClaimCase claimCase = claimCaseDao.findByClaimId(claimId);
            if (claimCase == null) {
                return "ClaimCase not found for claimId: " + claimId;
            }
            Pool pool = claimCaseDao.findPoolById(2L);
            if (pool == null) {
                return "Pool with ID 2 not found";
            }
            claimCase.setPool(pool);
            claimCaseDao.save(claimCase);

            logger.info("ClaimCase with claimId {} updated with pool_id=2", claimId);

            // Step 2: Accept claimReport but do not save
            logger.info("Received claimReport (not saved): {}", claimReport);

            // Step 3: Use parentProcessInstanceKey for subprocess key retrieval and task completion
            String subProcessKey = operateService.searchProcessInstances(parentProcessInstanceKey, null);
            if (subProcessKey == null) {
                logger.info("No subprocess instance found for parentProcessInstanceKey: " + parentProcessInstanceKey);
                return "No subprocess instance found for parentProcessInstanceKey: " + parentProcessInstanceKey;
            }
            logger.info("Subprocess key found: {}", subProcessKey);

            return tasklistService.completeTaskByProcessInstanceKey(subProcessKey, null);
        } catch (Exception e) {
            logger.error("Error while completing subprocess task for claimId: " + claimId, e);
            return "Error: " + e.getMessage();
        }
    }

    public String completeTaskByClaimId(Long claimId, Map<String, Object> payload) {
        try {
            ClaimCase claimCase = claimCaseDao.findByClaimId(claimId);
            if (claimCase == null) return "ClaimCase not found for claimId: " + claimId;

            Pool pool = claimCaseDao.findPoolById(2L);
            if (pool == null) return "Pool with ID 2 not found";

            claimCase.setPool(pool);

            // Set inspector report from payload
            String claimReport = (String) payload.get("claimReport");
            claimCase.setInspectorReport(claimReport);
            claimCase.setStatus("pending_review");
            claimCaseDao.save(claimCase);

            // Save checklist answers
         // Extract checklists from payload
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> checklists = (List<Map<String, Object>>) payload.get("checklists");
            for (Map<String, Object> checklist : checklists) {
                Long checklistId = Long.valueOf(checklist.get("checklistId").toString());
                Map<String, Object> categoryMap = (Map<String, Object>) checklist.get("category");
                Long categoryId = Long.valueOf(categoryMap.get("categoryId").toString());
                String answer = checklist.get("answer") != null ? checklist.get("answer").toString() : null;
                String comment = checklist.get("comment") != null ? checklist.get("comment").toString() : null;
                String correctiveAction = checklist.get("correctiveAction") != null ? checklist.get("correctiveAction").toString() : null;
                String attachment = checklist.get("attachment") != null ? checklist.get("attachment").toString() : null;
                
                HealthCareChecklistCategory category = categoryDao.findById(categoryId);
                HealthCareChecklistItem checklistItem = checklistItemDao.findById(checklistId);
                
                if (category == null) {
                    logger.error("Category not found for ID: {}", categoryId);
                    throw new IllegalArgumentException("Category not found for ID: " + categoryId);
                }
                if (checklistItem == null) {
                    logger.error("ChecklistItem not found for ID: {}", checklistId);
                    throw new IllegalArgumentException("ChecklistItem not found for ID: " + checklistId);
                }
                
                

                
                // Check existing answer for same claimId, checklistId, and categoryId
                List<HealthcareChecklistandAnswers> existingAnswers = healthcareChecklistandAnswersDAO.findByChecklistIdAndCategoryIdAndClaimId(checklistId, categoryId, claimId);
                
                if (existingAnswers != null && !existingAnswers.isEmpty()) {
                    // Update the first existing record
                    HealthcareChecklistandAnswers existingAnswer = existingAnswers.get(0);
                    existingAnswer.setAnswer(answer);
                    existingAnswer.setComment(comment);
                    existingAnswer.setCorrectiveAction(correctiveAction);
                    existingAnswer.setAttachment(attachment);
                    
                    healthcareChecklistandAnswersDAO.save(existingAnswer); // save updates
                } else {
                    // Create new record
                    HealthcareChecklistandAnswers newAnswer = new HealthcareChecklistandAnswers();
                    newAnswer.setCategory(category);
                    newAnswer.setChecklistItem(checklistItem);
                    newAnswer.setClaimCase(claimCase);
                    newAnswer.setAnswer(answer);
                    newAnswer.setComment(comment);
                    newAnswer.setCorrectiveAction(correctiveAction);
                    newAnswer.setAttachment(attachment);
                    
                    healthcareChecklistandAnswersDAO.save(newAnswer); // save new
                }
            }


            // Continue with process instance logic...

            String parentProcessInstanceKey = String.valueOf(claimId);
            String subProcessKey = operateService.searchProcessInstances(parentProcessInstanceKey, null);
            if (subProcessKey == null) return "No subprocess instance found for parentProcessInstanceKey: " + parentProcessInstanceKey;
            return tasklistService.completeTaskByProcessInstanceKey(subProcessKey, null);
        } catch (Exception e) {
            logger.error("Error while completing subprocess task for claimId: " + claimId, e);
            return "Error: " + e.getMessage();
        }
    }

    
    public String completeTaskByClaimIdAtReviewer(Long claimId, Map<String, Object> payload) {
        try {
            ClaimCase claimCase = claimCaseDao.findByClaimId(claimId);
            if (claimCase == null) return "ClaimCase not found for claimId: " + claimId;

            Pool pool = claimCaseDao.findPoolById(3L);  // Set pool to 3 for reviewer
            if (pool == null) return "Pool with ID 3 not found";

            claimCase.setPool(pool);

            // Extract values from payload
            String reviewerReport = (String) payload.get("reviewerReport");
            String recommendedAction = (String) payload.get("recommendedAction");
            String amountStatus = (String) payload.get("amountStatus");

            
            claimCase.setReviewerReport(reviewerReport);
            claimCase.setRecommendedAction(recommendedAction);
            claimCase.setAmountStatus(amountStatus);
            claimCase.setStatus("pending_approval");


            claimCaseDao.save(claimCase);

            // Use claimId as string parent process instance key
            String parentProcessInstanceKey = String.valueOf(claimId);

            String subProcessKey = operateService.searchProcessInstances(parentProcessInstanceKey, null);
            if (subProcessKey == null) {
                return "No subprocess instance found for parentProcessInstanceKey: " + parentProcessInstanceKey;
            }
            
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("reviewStatus", true);
            return tasklistService.completeTaskByProcessInstanceKey(subProcessKey, null);
        } catch (Exception e) {
            logger.error("Error while completing subprocess task for claimId: " + claimId, e);
            return "Error: " + e.getMessage();
        }
    }

    
    public String completeTaskByClaimIdAtApprover(Long claimId, Map<String, Object> payload) {
        try {
            ClaimCase claimCase = claimCaseDao.findByClaimId(claimId);
            if (claimCase == null) return "ClaimCase not found for claimId: " + claimId;

//            Pool pool = claimCaseDao.findPoolById(4L);  // Approver pool id
//            if (pool == null) return "Pool with ID 4 not found";

//            claimCase.setPool(pool);

            String approverReport = (String) payload.get("approverReport");
            String recommendedAction = (String) payload.get("recommendedAction"); // e.g. "re inspection", "follow up", "no action"

            claimCase.setApproverReport(approverReport);
            claimCase.setRecommendedAction(recommendedAction);
            claimCase.setStatus("completed"); // or your status value for mark complete

            claimCaseDao.save(claimCase);

            String parentProcessInstanceKey = String.valueOf(claimId);
            String subProcessKey = operateService.searchProcessInstances(parentProcessInstanceKey, null);
            if (subProcessKey == null) {
                return "No subprocess instance found for parentProcessInstanceKey: " + parentProcessInstanceKey;
            }
            
            Map<String, Object> outputs = new HashMap<>();
            outputs.put("approved", true);
            return tasklistService.completeTaskByProcessInstanceKey(subProcessKey, null);
        } catch (Exception e) {
            logger.error("Error while completing subprocess task for claimId: " + claimId, e);
            return "Error: " + e.getMessage();
        }
    }



}