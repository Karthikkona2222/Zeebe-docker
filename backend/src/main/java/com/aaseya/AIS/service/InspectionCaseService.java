package com.aaseya.AIS.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.ApproverReviewerCommentId;
import com.aaseya.AIS.Model.Approver_Reviewer_Comment;
import com.aaseya.AIS.Model.Checklist_Item;
import com.aaseya.AIS.Model.InspectionAttachments;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionChecklistandAnswers;
import com.aaseya.AIS.Model.InspectionChecklistandAnswersId;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dao.ApproverReviewerDAO;
import com.aaseya.AIS.dao.InspectionAnswerDAO;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionCommentsDAO;
import com.aaseya.AIS.dao.InspectionMappingDAO;
import com.aaseya.AIS.dao.UsersDAO;
import com.aaseya.AIS.dto.ApproverCommentsDTO;
import com.aaseya.AIS.dto.CaseSummaryDTO;
import com.aaseya.AIS.dto.ChecklistDTO;
import com.aaseya.AIS.dto.InspectionCaseDTO;
import com.aaseya.AIS.dto.InspectionCaseDetailsDTO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;
import com.aaseya.AIS.dto.InspectionFilters;
import com.aaseya.AIS.dto.InspectionHistoryDTO;
import com.aaseya.AIS.dto.SaveInspectionDTO;
import com.aaseya.AIS.dto.StartAISRequestDTO;
import com.aaseya.AIS.dto.SubmitInspectionDTO;
import com.aaseya.AIS.dto.UsersDTO;



import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Service
public class InspectionCaseService {

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;

	@Autowired
	private NewEntityService entityService;

	@Autowired
	private InspectionAnswerDAO inspectionAnswerDAO;

	@Autowired
	private InspectionCommentsDAO inspectionCommentsDAO;

	@Autowired
	private ChecklistService checklistService;

	@Autowired
	private ApproverReviewerDAO approverReviewerDAO;

	@Autowired
	private TaskListService tasklistservice;

	@Autowired
	private UsersDAO usersDAO;

	@Autowired
	private InspectionMappingDAO inspectionMappingDAO;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@PersistenceContext
	private EntityManager entityManager;

	public boolean saveInspectionCase(final StartAISRequestDTO startAISRequestDTO, final String businessKey) {
		boolean result = true;
		try {
			InspectionCase inspectionCase = new InspectionCase();
			inspectionCase.setInspectionType(startAISRequestDTO.getInspectionType());
			inspectionCase.setInspectionID(Long.parseLong(businessKey.substring(3)));
			inspectionCase.setDateOfInspection(startAISRequestDTO.getDateOfInspection());
			inspectionCase.setReason(startAISRequestDTO.getReason());
			inspectionCase.setCreatedBy(startAISRequestDTO.getCreatedBy());
		//	inspectionCase.setTemplate_id(startAISRequestDTO.getTemplateId());
			if (startAISRequestDTO.getTemplateId() != null) {
			    inspectionCase.setTemplate_id(startAISRequestDTO.getTemplateId());
			} else {
			    inspectionCase.setTemplate_id(null); 
			}
			if (startAISRequestDTO.getControlTypeId() != null) {
			    inspectionCase.setControlTypeId(startAISRequestDTO.getControlTypeId());
			} else {
			    inspectionCase.setControlTypeId(null); 
			}
			NewEntity newEntity = entityService.getEntityById(startAISRequestDTO.getEntityId());
			inspectionCase.setEntity(newEntity);
			inspectionCase.setInspector_source(startAISRequestDTO.getInspectionSource());
			inspectionCase.setStatus("new");
			inspectionCase.setControlTypeId(startAISRequestDTO.getControlTypeId());
			inspectionCase.setPreInspectionChecklists(startAISRequestDTO.getPre_Inspection_Checklists());

			if (startAISRequestDTO.getCustom_pre_inspection_checklist() != null) {
			    String custom_pre_inspection_checklist = String.join(",",
			            startAISRequestDTO.getCustom_pre_inspection_checklist());
			    inspectionCase.setCustom_pre_inspection_checklist(custom_pre_inspection_checklist);
			}

			
			Boolean isPreinspection = startAISRequestDTO.isIs_preinspection();
			if (isPreinspection == null) {
			    isPreinspection = false; 
			}
			inspectionCase.setIs_preinspection(isPreinspection);
			inspectionCase.setIs_preinspection_submitted(false);
			
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(inspectionCase.getDateOfInspection(), dateFormat);
			try {

				inspectionCase.setDueDate((date.plusDays(2)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			inspectionCase.setCreatedDate(startAISRequestDTO.getCreatedDate());

			inspectionCaseDAO.saveInspectionCase(inspectionCase);
			System.out.println(inspectionCase);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public InspectionCase updateInspectionComments(Long inspectionId, String inspectorComment, String reviwerComment,
			String approverComment) {
		// This service is for updating new inspectorId and updating status to pending
		InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionId);
		System.out.println(inspectionCase);
		if (inspectionCase != null) {

			if (inspectorComment != null)
				inspectionCase.setInspector_comments(inspectorComment);
			if (reviwerComment != null)
				inspectionCase.setReviewer_comments(reviwerComment);
			if (approverComment != null)
				inspectionCase.setApprover_comments(approverComment);
//			inspectionCaseDAO.updateInspectionCase(inspectionCase);
		}
		return inspectionCase;
	}

	public InspectionCase updateInspectionCase(Long inspectionId, String inspectorId, String status,
			InspectionCase inspectionCase) {
		// This service is for updating the inspectorId and status to pending
		if (inspectionCase == null) {
			inspectionCase = inspectionCaseDAO.findById(inspectionId);
		}

		if (inspectionCase != null) {
			if (inspectorId != null && !inspectorId.isEmpty()) {
				inspectionCase.setInspectorID(inspectorId);
			}
			if (status != null) {
				inspectionCase.setStatus(status);
			}
			// Additional fields can be set here if needed
		}

		return inspectionCase;
	}

	public boolean isDueDateSatisfied(Long inspectionId) {
		InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionId);
		if (inspectionCase != null && inspectionCase.getDueDate() != null) {
			LocalDate today = LocalDate.now();
			return !today.isAfter(inspectionCase.getDueDate());
		}
		return false; // Default to false if inspection case is not found or due date is null
	}

	public List<InspectionCase_EntityDTO> getManagerPool(String createdBy, String dateFilter) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = new ArrayList<>();
			if (dateFilter.equalsIgnoreCase("all"))
				inspectionCases = inspectionCaseDAO.getManagerPool(createdBy);
			else if (dateFilter.equalsIgnoreCase("month"))
				inspectionCases = inspectionCaseDAO.getManagerPoolByDate(createdBy, LocalDate.now().minusDays(30),
						LocalDate.now());
			else if (dateFilter.equalsIgnoreCase("week"))
				inspectionCases = inspectionCaseDAO.getManagerPoolByDate(createdBy, LocalDate.now().minusDays(7),
						LocalDate.now());
			else
				inspectionCases = inspectionCaseDAO.getManagerPoolByDate(createdBy, LocalDate.now(), LocalDate.now());
			System.out.println(inspectionCases);
			inspection = inspectionCases.stream()
					// .filter(inspectionCase -> filterByDueDate(inspectionCase.getDueDate(),
					// dueDateFilter))
					.map(entityService::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	public List<InspectionCase_EntityDTO> getMycasesForInspector(String inspectorEmailId) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = inspectionCaseDAO.getMyCasesForInspector(inspectorEmailId);
			System.out.println("User_id" + inspectorEmailId);
			inspection = inspectionCases.stream().map(entityService::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	public Map<String, List<InspectionCase_EntityDTO>> getInspectorPool(LocalDate date) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		Map<LocalDate, List<InspectionCase_EntityDTO>> map = new HashMap<>();
		Map<String, List<InspectionCase_EntityDTO>> newMap = new HashMap<>();
		try {
			List<InspectionCase> inspectionCases = inspectionCaseDAO.getInspectorPool(date);
			LocalDate yesterday = date.minusDays(1);
			LocalDate tomorrow = date.plusDays(1);
			inspection = inspectionCases.stream().map(entityService::toDto).collect(Collectors.toList());
			map = inspection.stream().collect(Collectors.groupingBy(e -> e.getDue_date()));
			for (Map.Entry<LocalDate, List<InspectionCase_EntityDTO>> entry : map.entrySet()) {
				if (entry.getKey().equals(date))
					newMap.put("Today", map.get(date));
				if (entry.getKey().equals(yesterday))
					newMap.put("Yesterday", map.get(yesterday));
				if (entry.getKey().equals(tomorrow))
					newMap.put("Tomorrow", map.get(tomorrow));
				inspection = inspectionCases.stream().map(entityService::toDto).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newMap;
	}

	public List<InspectionCase_EntityDTO> getUnasssignedcasesForInspector(String inspectorEmailId) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = inspectionCaseDAO.getUnasssignedcasesForInspector(inspectorEmailId);
			System.out.println("User_id" + inspectorEmailId);
			inspection = inspectionCases.stream().map(entityService::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	public long getTemplateId(final String inspectionId) {
		return inspectionCaseDAO.getTemplateId(inspectionId);
	}

	public void saveInspectionAnswers(SaveInspectionDTO saveInspectionDTO) {
		for (InspectionChecklistandAnswers inChecklistandAnswers : saveInspectionDTO
				.getInspectionChecklistandAnswers()) {
			inChecklistandAnswers.setSelected_corr_action(
					Stream.ofNullable(inChecklistandAnswers.getCorrective_actions()).flatMap(Collection::stream)
							.filter(corrective_action -> corrective_action != null).collect(Collectors.joining(",")));

		}
		// if (inChecklistandAnswers.getAttachment() != null &&
		// !inChecklistandAnswers.getAttachment().isEmpty()) {

		// ✅ Save checklist answers
		inspectionAnswerDAO.saveInspectionAnswers(saveInspectionDTO);

		// ✅ Save approver/reviewer comments if present
		if (saveInspectionDTO.getApprover_Reviewer_Comment() != null) {
			inspectionCommentsDAO.saveInspectionComment(saveInspectionDTO);
		}
		List<InspectionAttachments> attachmentsList = new ArrayList<>();

		for (InspectionChecklistandAnswers checklistAnswer : saveInspectionDTO.getInspectionChecklistandAnswers()) {
			if (checklistAnswer.getAttachment() != null && !checklistAnswer.getAttachment().isEmpty()) {
				long inspectionID = checklistAnswer.getId().getInspectionID();
				long categoryID = checklistAnswer.getId().getCategoryID();
				long checklistID = checklistAnswer.getId().getChecklistID();

				// ❌ Delete existing attachments that match inspectionID, categoryID,
				// checklistID
				inspectionCaseDAO.deleteAttachmentsByInspectionDetails(inspectionID, categoryID, checklistID);

				// ✅ Add new attachments
				for (String base64Attachment : checklistAnswer.getAttachment()) {
					InspectionAttachments attachment = new InspectionAttachments();
					attachment.setAttachmentFromBase64(base64Attachment); // Convert Base64 to Blob
					attachment.setInspectionChecklistandAnswers(checklistAnswer); // Associate with checklist answer

					attachmentsList.add(attachment);
				}
			}

			// ✅ Save all new attachments in batch
			inspectionCaseDAO.saveAttachments(attachmentsList);
		}
	}

	public SubmitInspectionDTO getSubmittedInspection(final Long inspectionId, String inspectorId) {
		SubmitInspectionDTO submitInspectionDTO = new SubmitInspectionDTO();
		submitInspectionDTO.setInspectionId(inspectionId.toString());
		List<ChecklistDTO> checklist_answers = new ArrayList<>();

		List<Approver_Reviewer_Comment> approver_Reviewer_Comments = inspectionCommentsDAO
				.getApproverReviewerComments(inspectionId);
		checklist_answers = checklistService.getInspectionChecklist(inspectionId.toString());
		List<InspectionChecklistandAnswers> checklistandAnswers = inspectionAnswerDAO
				.getInspectionChecklistAnswers(inspectionId);
		String inspectorID = "";
		if (inspectorId != null) {

			List<Long> categoryIDList = inspectionMappingDAO.getCategoryByInspectorId(inspectorId, inspectionId);
			System.out.println("category details" + categoryIDList);
			checklist_answers = checklist_answers.stream()
					.filter(checklist -> categoryIDList.contains(checklist.getCategoryId()))
					.collect(Collectors.toList());
		}
		for (ChecklistDTO checklistDTO : checklist_answers) {

			inspectorID = inspectionMappingDAO.getInspectorID(inspectionId, checklistDTO.getCategoryId());
			checklistDTO.setInspectorID(inspectorID);

//			if (inspectorId != null) {
//				if (inspectorID.equals(inspectorId)) {
//					System.out.println("get inspector details" + inspectorId);
//					continue; // Skip this category if it doesn't match the inspectorId
//				} else {
//					System.out.println("inspector details" + inspectorId);
//					break;
//				}
//			}

			InspectionChecklistandAnswersId inChecklistandAnswersId = new InspectionChecklistandAnswersId();
			inChecklistandAnswersId.setInspectionID(inspectionId);
			inChecklistandAnswersId.setCategoryID(checklistDTO.getCategoryId());

			for (Checklist_Item checklist_Item : checklistDTO.getChecklistItem()) {
				inChecklistandAnswersId.setChecklistID(checklist_Item.getChecklist_id());

				if (checklistandAnswers != null && !checklistandAnswers.isEmpty()) {
					for (InspectionChecklistandAnswers e : checklistandAnswers) {
						if (e.getId().equals(inChecklistandAnswersId)) {
							checklist_Item.setSelected_answer(e.getSelected_answer());
							checklist_Item.setComment(e.getComment());
							if (e.getSelected_corr_action() != null) {
								checklist_Item.setSelected_corrective_action(
										Arrays.asList(e.getSelected_corr_action().split(",")));
							}
						}
					}
				}

				// Fetch and convert attachments using DAO
				List<String> attachmentBase64List = new ArrayList<>();
				List<byte[]> attachments = inspectionCaseDAO.getAttachmentsByChecklistId(
						checklist_Item.getChecklist_id(), inspectionId, checklistDTO.getCategoryId());

				if (attachments != null) {
					for (byte[] attachment : attachments) {
						attachmentBase64List.add(Base64.getEncoder().encodeToString(attachment));
					}
				}
				checklist_Item.setAttachments(attachmentBase64List);
			}

			ApproverReviewerCommentId id = new ApproverReviewerCommentId();
			id.setInspectionID(inspectionId);
			id.setCategoryID(checklistDTO.getCategoryId());
			checklistDTO.setReviewerComment(approver_Reviewer_Comments.stream().filter(e -> e.getId().equals(id))
					.findFirst().orElse(new Approver_Reviewer_Comment()).getReviewerComment());
			checklistDTO.setApproverComment(approver_Reviewer_Comments.stream().filter(e -> e.getId().equals(id))
					.findFirst().orElse(new Approver_Reviewer_Comment()).getApproverComment());
		}

		submitInspectionDTO.setChecklist(checklist_answers);

		InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionId);

		submitInspectionDTO.setInspectorComment(inspectionCase.getInspector_comments());
		submitInspectionDTO.setReviewerComment(inspectionCase.getReviewer_comments());
		submitInspectionDTO.setApproverComment(inspectionCase.getApprover_comments());
		if (inspectionCase.getLeadId() != null) {
			submitInspectionDTO.setLeadId(inspectionCase.getLeadId());
		}
		submitInspectionDTO.setGroupId(inspectionCase.getGroupId());

		System.out.println("ResponseDTO : " + submitInspectionDTO);
		return submitInspectionDTO;
	}

	// Get approver pool data

	public List<InspectionCase_EntityDTO> getApproverpool(String approverID, String dueDateFilter) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = new ArrayList<>();
			if (dueDateFilter.equalsIgnoreCase("all"))
				inspectionCases = inspectionCaseDAO.getApproverPool(approverID);
			else if (dueDateFilter.equalsIgnoreCase("month"))
				inspectionCases = inspectionCaseDAO.getApproverPoolByDate(approverID, LocalDate.now().minusDays(30),
						LocalDate.now());
			else if (dueDateFilter.equalsIgnoreCase("week"))
				inspectionCases = inspectionCaseDAO.getApproverPoolByDate(approverID, LocalDate.now().minusDays(7),
						LocalDate.now());
			else
				inspectionCases = inspectionCaseDAO.getApproverPoolByDate(approverID, LocalDate.now(), LocalDate.now());
			System.out.println(inspectionCases);
			inspection = inspectionCases.stream()
					// .filter(inspectionCase -> filterByDueDate(inspectionCase.getDueDate(),
					// dueDateFilter))
					.map(entityService::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	public Map<String, List<InspectionCase_EntityDTO>> getMyCasesForInspectorDate(String inspectorID, LocalDate date) {
	    Map<String, List<InspectionCase_EntityDTO>> newMap = new HashMap<>();
	    try {
	        List<InspectionCase> inspectionCases = inspectionCaseDAO.getMyCasesForInspectorDate(inspectorID, date);
	        System.out.println("Inspector ID: " + inspectorID);
	        System.out.println("Due Date: " + date);

	        LocalDate yesterday = date.minusDays(1);
	        LocalDate tomorrow = date.plusDays(1);
	        LocalDate dayaftertomorrow = date.plusDays(2);

	        // Convert to DTO
	        List<InspectionCase_EntityDTO> inspectionDTOs = inspectionCases.stream()
	                .map(entityService::toDto)
	                .collect(Collectors.toList());

	        // Categorize cases
	        newMap.put("Yesterday", new ArrayList<>());
	        newMap.put("Today", new ArrayList<>());
	        newMap.put("Tomorrow", new ArrayList<>());
	        newMap.put("Dayaftertomorrow", new ArrayList<>());

	        for (InspectionCase_EntityDTO dto : inspectionDTOs) {
	            if (dto.getDue_date().equals(yesterday)) {
	                newMap.get("Yesterday").add(dto);
	            } else if (dto.getDue_date().equals(date)) {
	                newMap.get("Today").add(dto);
	            } else if (dto.getDue_date().equals(tomorrow)) {
	                newMap.get("Tomorrow").add(dto);
	            } else if (dto.getDue_date().equals(dayaftertomorrow)) {
	                newMap.get("Dayaftertomorrow").add(dto);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return newMap;
	}


//////////////Save the inspection for approver//////////////////////

	public void addReviewComments(final ApproverCommentsDTO approverCommentRequestDTO) {
		InspectionCase inspectionCase = inspectionCaseDAO
				.findById(Long.parseLong(approverCommentRequestDTO.getInspectionId()));

		String status = determineStatusBasedOnAction(approverCommentRequestDTO.getInspectionStage(),
				approverCommentRequestDTO.getAction(), approverCommentRequestDTO.getInspectionId(),
				approverCommentRequestDTO.getRecommendedAction());
		inspectionCase.setStatus(status);

		if (approverCommentRequestDTO.getInspectionStage().equalsIgnoreCase("review"))
			inspectionCase.setReviewer_comments(approverCommentRequestDTO.getOverallComment());
		else
			inspectionCase.setApprover_comments(approverCommentRequestDTO.getOverallComment());

		inspectionCase.setRecommendedAction(approverCommentRequestDTO.getRecommendedAction());

		if ("Follow up".equalsIgnoreCase(approverCommentRequestDTO.getRecommendedAction())) {
			inspectionCase.setDateOfFollowUp(approverCommentRequestDTO.getDateOfFollowUp());
			inspectionCase.setStatus("followup");
		} else if ("Re-Inspection".equalsIgnoreCase(approverCommentRequestDTO.getRecommendedAction())) {
			inspectionCase.setDateOfReinspection(approverCommentRequestDTO.getDateOfReinspection());
			inspectionCase.setStatus("reinspection");
		}

		inspectionCaseDAO.updateInspectionCase(inspectionCase);

		// Save the category comments
		for (Approver_Reviewer_Comment approverReviewerComments : approverCommentRequestDTO
				.getApproverReviewerComments()) {
			approverReviewerDAO.save(approverReviewerComments);
		}
	}

	private String determineStatusBasedOnAction(String inspectionStage, String action, String inspectionId,
			String recommendedAction) {
		if ("submit".equalsIgnoreCase(action)) {
			String taskId = tasklistservice.getActiveTaskID(inspectionId);
			if (inspectionStage.equalsIgnoreCase("review")) {
				tasklistservice.CompleteTaskByID(taskId, new HashMap<String, Object>() {
					{
						put("reviewStatus", true);
					}
				});
				return "pending_approval";
			} else if (recommendedAction.equalsIgnoreCase("Re-Inspection")) {
				tasklistservice.CompleteTaskByID(taskId, new HashMap<String, Object>() {
					{
						put("approved", false);
						put("reinspection", true);
					}
				});
				return "completed";
			} else {
				tasklistservice.CompleteTaskByID(taskId, new HashMap<String, Object>() {
					{
						put("approved", true);

					}
				});
				return "completed";

			}
		} else if ("save".equalsIgnoreCase(action) && inspectionStage.equalsIgnoreCase("review")) {
			return "under_review";
		} else if ("save".equalsIgnoreCase(action) && inspectionStage.equalsIgnoreCase("approve")) {
			return "under_approval";
		}
		return "unknown";
	}

///////////Save the inspection for approver////////////////////

	// assigning Task to Approver
	// ////////////////////////////////////////////////////////////////////
	public void updateApproverIdinInspectionCase(Long inspectionId, String approverId, String status) {
		// This service is for updating new inspectorId and updating status to pending
		InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionId);
		System.out.println(inspectionCase);
		if (inspectionCase != null) {
			if (!approverId.equals(""))
				inspectionCase.setApproverID(approverId);
			inspectionCase.setStatus(status);
			inspectionCaseDAO.updateInspectionCase(inspectionCase);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	public void updateReviewerIdinInspectionCase(Long inspectionId, String approverId, String status) {
		// This service is for updating new inspectorId and updating status to pending
		InspectionCase inspectionCase = inspectionCaseDAO.findById(inspectionId);
		System.out.println(inspectionCase);
		if (inspectionCase != null) {
			if (!approverId.equals(""))
				inspectionCase.setReviewerID(approverId);
			inspectionCase.setStatus(status);
			inspectionCaseDAO.updateInspectionCase(inspectionCase);
		}
	}
//////////////////working days////////////////////

	public List<Users> getAvailableInspectors(long inspectionID) {
// Fetch the inspection case by ID using the DAO
		InspectionCase inspectionCase = inspectionCaseDAO.getInspectionCaseById(inspectionID);

		LocalDate dateOfInspection = LocalDate.parse(inspectionCase.getDateOfInspection());
		LocalDate dueDate = inspectionCase.getDueDate();

// Fetch all users with the role of "Inspector" using the DAO
		List<Users> inspectors = usersDAO.findUsersByRole("Inspector");

// Filter inspectors based on availability on all required days within the range
		return inspectors.stream()
				.filter(inspector -> isUserAvailableOnAllRequiredDays(dateOfInspection, dueDate, inspector))
				.collect(Collectors.toList());
	}

	private boolean isUserAvailableOnAllRequiredDays(LocalDate start, LocalDate end, Users inspector) {
// Get all the days of the week in the date range
		Set<DayOfWeek> requiredDays = start.datesUntil(end.plusDays(1)).map(LocalDate::getDayOfWeek)
				.filter(day -> !Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(day))
				.collect(Collectors.toSet());
		System.out.println(" the required days are " + requiredDays);

// Fetch the user's available days from the DAO
		List<DayOfWeek> availableDays = usersDAO.findUserAvailabilityByUserId(inspector.getUserID());
		System.out.println("user " + inspector.getUserName() + " is avaliable " + availableDays);

// Check if the user is available on all these days
		return requiredDays.stream().allMatch(availableDays::contains);
	}

///////////////working days/////////////////////////////

	public List<UsersDTO> getZoneUserDetailsByInspectionId(long inspectionId) {
		InspectionCase inspectionCase = inspectionCaseDAO.getInspectionCaseById(inspectionId);
		if (inspectionCase == null) {
			return null;
		}

		String entityId = inspectionCase.getEntity().getEntityid();
		String inspectionType = inspectionCase.getInspectionType();

		List<Users> users = inspectionCaseDAO.getUsersByInspectionType(inspectionType);
//	        Zone zone = inspectionCaseDAO.getZoneByEntityId(entityId);
		Set<Zone> zones = inspectionCase.getEntity().getZones();
		List<UsersDTO> userDTOs = users.stream()
//	            .filter(user -> user.getZones().stream()
//	                .map(Zone::getName)
//	                .collect(Collectors.toList())
//	                .contains(zone.getName())) // Assuming zones is a list of zone names
				.filter(user -> isUserAvailableOnAllRequiredDays(LocalDate.now(), inspectionCase.getDueDate(), user))
				.map(user -> new UsersDTO(user.getUserID(), user.getUserName(),

						user.getEmailID(),

						user.getPhoneNumber(), user.getRole()))

				.collect(Collectors.toList());

		return userDTOs;
	}

	public List<InspectionCase_EntityDTO> getReviewerpool(String reviewerID, String dueDateFilter) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = new ArrayList<>();
			if (dueDateFilter.equalsIgnoreCase("all"))
				inspectionCases = inspectionCaseDAO.getReviewerPool(reviewerID);
			else if (dueDateFilter.equalsIgnoreCase("month"))
				inspectionCases = inspectionCaseDAO.getReviewerPoolByDate(reviewerID, LocalDate.now().minusDays(30),
						LocalDate.now());
			else if (dueDateFilter.equalsIgnoreCase("week"))
				inspectionCases = inspectionCaseDAO.getReviewerPoolByDate(reviewerID, LocalDate.now().minusDays(7),
						LocalDate.now());
			else
				inspectionCases = inspectionCaseDAO.getReviewerPoolByDate(reviewerID, LocalDate.now(), LocalDate.now());
			System.out.println(inspectionCases);
			inspection = inspectionCases.stream()
					// .filter(inspectionCase -> filterByDueDate(inspectionCase.getDueDate(),
					// dueDateFilter))
					.map(entityService::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}
	//////////////////////////// Get case details based on
	//////////////////////////// caseId/////////////////////////////

	public Optional<Optional<InspectionCaseDetailsDTO>> getInspectionCaseDetails(long inspectionId) {
		return Optional.ofNullable(inspectionCaseDAO.getInspectionCaseDetailsById(inspectionId));
	}

	public Page<InspectionCaseDTO> getInspectionCasesWithEntityDetails(String entityid, String inspectorID,
			String status, String inspector_source, LocalDate start_date, LocalDate end_date, LocalDate dueStartDate,
			LocalDate dueEndDate, Pageable pageable) {

		return inspectionCaseDAO.fetchInspectionCasesWithEntityDetails(entityid, inspectorID, status, inspector_source,
				start_date, end_date, dueStartDate, dueEndDate, pageable);
	}

//	public CaseSummaryDTO getCaseSummary(String startDate, String endDate) {
//		return inspectionCaseDAO.getCaseSummary(startDate, endDate);
//	}

	public CaseSummaryDTO getCaseSummary(String startDate, String endDate, String processType) {
	    return inspectionCaseDAO.getCaseSummary(startDate, endDate, processType);
	}
	
	// Method to fetch case stats and map to DTO
//	public List<Map<String, Object>> getCaseStatsByMonth(LocalDate startDate, LocalDate endDate) {
//		return inspectionCaseDAO.getCaseStatsByMonth(startDate, endDate);
//	}

	public List<Map<String, Object>> getCaseStatsByMonth(LocalDate startDate, LocalDate endDate, String processType) {
	    return inspectionCaseDAO.getCaseStatsByMonth(startDate, endDate, processType);
	}
	
//	public InspectionHistoryDTO getCasesByFilters(InspectionFilters inspectionFilters) {
//		InspectionHistoryDTO inspectionHistoryDTO = new InspectionHistoryDTO();
//		try {
//			List<InspectionCase> inspectionCases = inspectionCaseDAO.getCasesCountByFilter(inspectionFilters);
//			Long casesCount = inspectionCases.stream().count();
//
//			// Get the no of entities
//			long entitiesCount = inspectionCases.stream().map(inspectionCase -> inspectionCase.getEntity()).distinct()
//					.count();
//
//			// Percentage of cases completed
//			long percentageCompleted = 0;
//			if (casesCount != 0) {
//				percentageCompleted = inspectionCases.stream()
//						.filter(inpsectionCase -> inpsectionCase.getStatus().equals("Completed")).count() * 100
//						/ casesCount;
//			}
//
//			// Inspection By Entities
//			Map<String, Long> inspectionByEntities = inspectionCases.stream().collect(Collectors
//					.groupingBy(inpsectionCase -> inpsectionCase.getEntity().getName(), Collectors.counting()));
//
//			// Inspection By Inspection Types
//			Map<String, Long> inspectionByInspectionType = inspectionCases.stream().collect(
//					Collectors.groupingBy(inspectionCase -> inspectionCase.getInspectionType(), Collectors.counting()));
//
//			// Inspection By Status
//			Map<String, Long> inspectionByStatus = inspectionCases.stream().collect(
//					Collectors.groupingBy(inspectionCase -> inspectionCase.getStatus(), Collectors.counting()));
//
//			// Inspections By Source
//			Map<String, Long> inspectionBySource = inspectionCases.stream().collect(Collectors
//					.groupingBy(inspectionCase -> inspectionCase.getInspector_source(), Collectors.counting()));
//			System.out.println(casesCount);
//			System.out.println(entitiesCount);
//			System.out.println(percentageCompleted);
//			System.out.println(inspectionByEntities);
//			System.out.println(inspectionByInspectionType);
//			System.out.println(inspectionBySource);
//
//			inspectionHistoryDTO.setCasesCount(casesCount);
//			inspectionHistoryDTO.setEntitiesCount(entitiesCount);
//			inspectionHistoryDTO.setPercentageCompleted(percentageCompleted);
//			inspectionHistoryDTO.setInspectionByEntities(inspectionByEntities);
//			inspectionHistoryDTO.setInspectionByInspectionType(inspectionByInspectionType);
//			inspectionHistoryDTO.setInspectionByStatus(inspectionByStatus);
//			inspectionHistoryDTO.setInspectionBySource(inspectionBySource);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return inspectionHistoryDTO;
//	}

	public InspectionHistoryDTO getCasesByFilters(InspectionFilters inspectionFilters) {
	    InspectionHistoryDTO inspectionHistoryDTO = new InspectionHistoryDTO();
	    try {
	        List<InspectionCase> inspectionCases = inspectionCaseDAO.getCasesCountByFilter(inspectionFilters);
	        Long casesCount = inspectionCases.stream().count();

	        long entitiesCount = inspectionCases.stream()
	                .map(inspectionCase -> inspectionCase.getEntity())
	                .distinct()
	                .count();

	        long percentageCompleted = 0;
	        if (casesCount != 0) {
	            percentageCompleted = inspectionCases.stream()
	                    .filter(caseObj -> caseObj.getStatus().equalsIgnoreCase("Completed"))
	                    .count() * 100 / casesCount;
	        }

	        Map<String, Long> inspectionByEntities =
	                inspectionCases.stream().collect(Collectors.groupingBy(
	                        c -> c.getEntity().getName(), Collectors.counting()));

	        Map<String, Long> inspectionByInspectionType =
	                inspectionCases.stream().collect(Collectors.groupingBy(
	                        InspectionCase::getInspectionType, Collectors.counting()));

	        Map<String, Long> inspectionByStatus =
	                inspectionCases.stream().collect(Collectors.groupingBy(
	                        InspectionCase::getStatus, Collectors.counting()));

	        Map<String, Long> inspectionBySource =
	                inspectionCases.stream().collect(Collectors.groupingBy(
	                        InspectionCase::getInspector_source, Collectors.counting()));

	        inspectionHistoryDTO.setCasesCount(casesCount);
	        inspectionHistoryDTO.setEntitiesCount(entitiesCount);
	        inspectionHistoryDTO.setPercentageCompleted(percentageCompleted);
	        inspectionHistoryDTO.setInspectionByEntities(inspectionByEntities);
	        inspectionHistoryDTO.setInspectionByInspectionType(inspectionByInspectionType);
	        inspectionHistoryDTO.setInspectionByStatus(inspectionByStatus);
	        inspectionHistoryDTO.setInspectionBySource(inspectionBySource);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return inspectionHistoryDTO;
	}

	
//	public List<InspectionCase_EntityDTO> getInspectionHistoryCases(InspectionFilters inspectionFilters) {
//		List<InspectionCase_EntityDTO> inspectionCase_EntityDTOs = new ArrayList<>();
//		List<InspectionCase> inspectionCases = inspectionCaseDAO.getCasesCountByFilter(inspectionFilters);
//
//		inspectionCases.stream().forEach(inspectionCase -> {
//			InspectionCase_EntityDTO inspectionCase_EntityDTO = new InspectionCase_EntityDTO();
//			inspectionCase_EntityDTO.setInspectionID(inspectionCase.getInspectionID());
//			inspectionCase_EntityDTO.setEntityid(inspectionCase.getEntity().getName());
//			inspectionCase_EntityDTO.setInspection_type(inspectionCase.getInspectionType());
//			inspectionCase_EntityDTO.setDateOfInspection(inspectionCase.getDateOfInspection());
//			inspectionCase_EntityDTO.setInspector_source(inspectionCase.getInspector_source());
//			inspectionCase_EntityDTO.setStatus(inspectionCase.getStatus());
//			// inspectionCase_EntityDTO.setLeadId(inspectionCase.getLeadId());
//			inspectionCase_EntityDTOs.add(inspectionCase_EntityDTO);
//		});
//
//		return inspectionCase_EntityDTOs;
//	}
	
	public List<InspectionCase_EntityDTO> getInspectionHistoryCases(InspectionFilters inspectionFilters) {
	    List<InspectionCase_EntityDTO> inspectionCase_EntityDTOs = new ArrayList<>();
	    List<InspectionCase> inspectionCases = inspectionCaseDAO.getCasesCountByFilter(inspectionFilters);

	    // read request parameter (sent from controller)
	    String reqProcessType = inspectionFilters.getProcessType();

	    /*
	     * includeRiskData = true when:
	     *   - processType missing or empty
	     *   - processType = ADP
	     * includeRiskData = false when:
	     *   - processType = manual / anything other than ADP
	     */
	    boolean includeRiskData = (reqProcessType == null || reqProcessType.trim().isEmpty()
	                               || reqProcessType.equalsIgnoreCase("ADP"));

	    inspectionCases.forEach(inspectionCase -> {

	        InspectionCase_EntityDTO dto = new InspectionCase_EntityDTO();
	        dto.setInspectionID(inspectionCase.getInspectionID());
	        dto.setEntityid(inspectionCase.getEntity().getName());
	        dto.setInspection_type(inspectionCase.getInspectionType());
	        dto.setDateOfInspection(inspectionCase.getDateOfInspection());
	        dto.setInspector_source(inspectionCase.getInspector_source());
	        dto.setStatus(inspectionCase.getStatus());

	        /* ⭐ Add riskScore & recommendation ONLY for ADP inspectionCase records
	              AND only if API request allows (includeRiskData = true) */
	        if ("ADP".equalsIgnoreCase(inspectionCase.getProcessType()) && includeRiskData) {

	            // recommendation from InspectionCase table
	            dto.setRecommendation(inspectionCase.getRecommendation());

	            try {
	                Integer riskScore = entityManager.createQuery(
	                        "SELECT s.riskScore FROM IDPSummary s " +
	                        "WHERE s.inspectionID = :insId ORDER BY s.id DESC",
	                        Integer.class)
	                    .setParameter("insId", inspectionCase.getInspectionID())
	                    .setMaxResults(1)
	                    .getSingleResult();

	                dto.setRiskScore(riskScore);

	            } catch (Exception e) {
	                dto.setRiskScore(null);
	            }
	        }

	        inspectionCase_EntityDTOs.add(dto);
	    });

	    return inspectionCase_EntityDTOs;
	}

	
	public boolean isInspectionSkipped(String entityId, String inspectionType, String dateOfInspection) {
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    Long count = 0L;
 
	    try {
	        tx = session.beginTransaction();
 
	        count = (Long) session.createQuery(
	                "SELECT COUNT(ic) FROM InspectionCase ic " +
	                        "WHERE ic.entity.entityid = :entityId " +   // ✅ adjust field
	                        "AND ic.inspectionType = :inspectionType " +
	                        "AND ic.dateOfInspection = :date " +
	                        "AND ic.status = 'inspectionSkipped'")
	                .setParameter("entityId", entityId)
	                .setParameter("inspectionType", inspectionType)
	                .setParameter("date", LocalDate.parse(dateOfInspection))
	                .uniqueResult();
 
	        tx.commit();
	    } catch (Exception e) {
	        if (tx != null) tx.rollback();
	        e.printStackTrace();
	    } finally {
	        session.close();
	    }
 
	    return count != null && count > 0;
	}
 
}


