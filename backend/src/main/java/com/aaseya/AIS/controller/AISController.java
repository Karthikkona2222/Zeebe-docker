package com.aaseya.AIS.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import com.aaseya.AIS.dto.*;
import com.aaseya.AIS.zeebe.worker.ValidateInspectionTypeWorker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.aaseya.AIS.Model.Checklist_Item;
import com.aaseya.AIS.Model.ClaimRequestDocuments;
import com.aaseya.AIS.Model.ControlType;
import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.InspectionPlan;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.PreInspectionChecklist;
import com.aaseya.AIS.Model.SaveSubmitPreInspectionChecklist;
import com.aaseya.AIS.Model.Skill;
import com.aaseya.AIS.Model.SubSegment;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dao.InspectionCaseDAO;
//import com.aaseya.AIS.dto.SkillInspectionTypeDTO;
import com.aaseya.AIS.service.AISService;
import com.aaseya.AIS.service.CategoriesSummaryReportService;
import com.aaseya.AIS.service.ChecklistCategoryService;
import com.aaseya.AIS.service.ChecklistItemService;
import com.aaseya.AIS.service.ChecklistService;
import com.aaseya.AIS.service.ClaimRequestDocumentsService;
import com.aaseya.AIS.service.ControlTypeService;
import com.aaseya.AIS.service.EntityInspectionReportService;
import com.aaseya.AIS.service.GetInspectionCasesService;
import com.aaseya.AIS.service.IDPAIService;
import com.aaseya.AIS.service.IDPSummaryService;
import com.aaseya.AIS.service.InspectionCaseService;
import com.aaseya.AIS.service.InspectionEscalationService;
import com.aaseya.AIS.service.InspectionMappingService;
import com.aaseya.AIS.service.InspectionPeriodicityService;
import com.aaseya.AIS.service.InspectionPlanService;
import com.aaseya.AIS.service.InspectionRiskService;
import com.aaseya.AIS.service.InspectionSLAService;
import com.aaseya.AIS.service.InspectionScheduleService;
import com.aaseya.AIS.service.InspectionService;
import com.aaseya.AIS.service.InspectionTypeScheduleService;
import com.aaseya.AIS.service.InspectionTypeService;
import com.aaseya.AIS.service.NewEntityService;
import com.aaseya.AIS.service.OperateService;
import com.aaseya.AIS.service.PdfReportService;
import com.aaseya.AIS.service.PeriodicityDetailsByInspectionTypeService;
import com.aaseya.AIS.service.PreInspectionChecklistService;
import com.aaseya.AIS.service.SaveSubmitPreInspectionChecklistService;
import com.aaseya.AIS.service.SegmentService;
import com.aaseya.AIS.service.SkillService;
import com.aaseya.AIS.service.SummaryGeneratorService;
import com.aaseya.AIS.service.TempCheckInspService;
import com.aaseya.AIS.service.TemplateService;
import com.aaseya.AIS.service.UpdateIsActiveService;
import com.aaseya.AIS.service.UserGroupForEditByIdService;
import com.aaseya.AIS.service.UserGroupService;
import com.aaseya.AIS.service.UserSkillService;
import com.aaseya.AIS.service.UsersService;
import com.aaseya.AIS.service.ZoneService;
import com.aaseya.AIS.service.getGroupCasesForLeadService;
import com.aaseya.AIS.zeebe.worker.FetchInspectionIdWithEntityDetails;
import com.aaseya.AIS.dao.InspectionTypeDAO;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import static com.aaseya.AIS.utility.Constants.*;

@CrossOrigin("*")
@RestController
public class AISController {

	@Autowired
	private ZeebeClient zeebeClient;

	@Autowired
	private ZoneService zoneService;

	@Autowired
	private InspectionMappingService inspectionMappingService;

	@Autowired
	private UserGroupForEditByIdService userGroupForEditByIdService;
	@Autowired
	private UsersService usersService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private InspectionSLAService inspectionSLAService;

	@Autowired
	private InspectionTypeService inspectionTypeService;

	@Autowired
	private NewEntityService newEntityService;

	@Autowired
	private TemplateService templateService;

	@Autowired
	private com.aaseya.AIS.service.TaskListService tasklistservice;

	@Autowired
	private InspectionCaseService inspectionCaseService;

	@Autowired
	private AISService aisService;

	@Autowired
	private ChecklistService checklistService;

	@Autowired
	private PreInspectionChecklistService preInspectionChecklistService;

	@Autowired
	private InspectionService inspectionService;

	@Autowired
	private SaveSubmitPreInspectionChecklistService saveSubmitPreInspectionChecklistService;

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;

	@Autowired
	private InspectionTypeDAO inspectionTypeDAO;

	@Autowired
	private SummaryGeneratorService summaryGeneratorService;

	@Autowired
	private SkillService skillsService;
	
	@PersistenceContext
    private EntityManager entityManager;

	@Autowired
	private ChecklistItemService checklistItemService;

	@Autowired
	private ChecklistCategoryService checklistCategoryService;

	@Autowired
	private TempCheckInspService tempCheckInspService;

	@Autowired
	private UserSkillService userSkillService;

	@Autowired
	private UsersService userService;

	@Autowired
	private UpdateIsActiveService updateIsActiveService;

	@Autowired
	private EntityInspectionReportService entityInspectionReportService;

	@Autowired
	private CategoriesSummaryReportService categoriesSummaryReportService;

	@Autowired
	private ControlTypeService controlTypeService;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private getGroupCasesForLeadService getGroupCasesForLeadService;

	@Autowired
	private InspectionPlanService inspectionPlanService;

	@Autowired
	private InspectionPeriodicityService periodicityService;

	@Autowired
	private ClaimRequestDocumentsService service;

	@Autowired
	private GetInspectionCasesService inspectionCasesService;

	@Autowired
	private PeriodicityDetailsByInspectionTypeService periodicityDetailsByInspectionTypeService;

	@Autowired
	private FetchInspectionIdWithEntityDetails jobWorker;

	@Autowired
	private InspectionScheduleService inspectionScheduleService;

	@Autowired
	private InspectionTypeScheduleService inspectionTypeScheduleService;

	@Autowired
	private ValidateInspectionTypeWorker worker;

	@Autowired
	private ValidateInspectionTypeWorker validateInspectionTypeWorker;
	
	@Autowired
	 private InspectionRiskService inspectionRiskService;
	 
	 @Autowired
	    private IDPSummaryService idpSummaryService;
	 
	 @Autowired
	    private IDPAIService idpaiService;
	 
	 @Autowired
	 private OperateService operateService;
	 
	 @Autowired
	 private PdfReportService pdfReportService;
	 
	 @Value("${developer.id}")
	 String developerId;



	Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping("/addZone")
	public ResponseDTO addZone(@RequestBody Zone zone) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			zoneService.addZone(zone);
			responseDTO.setStatus("Success");
			responseDTO.setMessage("Zone created");
		} catch (Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());
		}
		return responseDTO;
	}

/////////////add users in usertable
	@PostMapping("/addUsers")
	public ResponseDTO addUsers(@RequestBody UsersDTO users) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			usersService.addUsers(users);
			responseDTO.setStatus("Success");
			responseDTO.setMessage("Users created");
		} catch (Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());
		}
		return responseDTO;
	}

//	@GetMapping("/getUsers") 
//	public ResponseEntity<List<Users>>getAllUsers() {        
//	List<Users> users = usersService.getAllUsers();       
//	return ResponseEntity.ok(users); 
//	}

	//// get users details
//	@GetMapping("/getUsers")
//	public List<UsersDTO> getAllUsers() {
//		return usersService.getAllUsers();
//	}

	@GetMapping("/getInspectionTypeNames")
	public List<String> getAllInspectionType() {
		return inspectionTypeService.getInspectionTypeNames();
	}

	@PostMapping("/addSubSegment")
	public ResponseDTO addSubSegment(@RequestBody SubSegment subSegment) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			segmentService.addSubSegment(subSegment);
			responseDTO.setStatus("Success");
			responseDTO.setMessage("SubSegment created");
		} catch (Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());
		}
		return responseDTO;
	}

	@GetMapping("/getEntityNames")
	public ResponseEntity<List<EntitiesInspectionTypeDTO>> getEntities(
			@PathVariable(value = "ins_type_id", required = false) Long insTypeId) {
		List<EntitiesInspectionTypeDTO> response = newEntityService.fetchEntities(insTypeId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/addSegment")
	public ResponseDTO addSegment(@RequestBody SegmentDTO segmentDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			segmentService.addSegment(segmentDTO);
			responseDTO.setStatus("Success");
			responseDTO.setMessage("Segment created");
		} catch (Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());
		}
		return responseDTO;
	}

	@GetMapping("/getMycasesForManager/{createdBy}/{dueDateFilter}")
	public List<InspectionCase_EntityDTO> getMycasesForManager(@PathVariable("createdBy") String createdBy,
			@PathVariable("dueDateFilter") String dueDateFilter) {
		return newEntityService.getMycasesForManager(createdBy, dueDateFilter);
	}

	@GetMapping("/getUnassignedCasesForManager/{createdBy}/status/new")
	public List<InspectionCase_EntityDTO> getEntitiesByCreatedByAndStatusNew(
			@PathVariable("createdBy") String createdBy) {
		return newEntityService.getEntitiesByCreatedByAndStatus(createdBy, "new");
	}

	@GetMapping("/getTemplate/{inspectionTypeName}")
	public List<TemplateDTO> getTemplateNameAndVersionByInspectionTypeName(@PathVariable String inspectionTypeName) {
		return templateService.getTemplateNameAndVersionByInspectionTypeName(inspectionTypeName);
	}

	@GetMapping("/getInspectorPool/{date}")
	public Map<String, List<InspectionCase_EntityDTO>> getInspectorPool(@PathVariable("date") LocalDate date) {
		return inspectionCaseService.getInspectorPool(date);
	}

	@GetMapping("/getMycasesForInspector/{inspectorEmailId}")
	public List<InspectionCase_EntityDTO> getMycasesForInspector(
			@PathVariable("inspectorEmailId") String inspectorEmailId) {
		return inspectionCaseService.getMycasesForInspector(inspectorEmailId);
	}

	@GetMapping("/getUnassignedcasesForInspector/{inspectorEmailId}")
	public List<InspectionCase_EntityDTO> getUnasssignedcasesForInspector(
			@PathVariable("inspectorEmailId") String inspectorEmailId) {
		return inspectionCaseService.getUnasssignedcasesForInspector(inspectorEmailId);
	}

//	@GetMapping("/getAddressByEntity/{name}")
//	public EntityInformationDTO getAllAddressByEntity(@PathVariable String name) {
//		return newEntityService.getAddressByEntity(name);
//	}

	// assigning Task to user
	@PostMapping("/assignInspection")
	public String assignInspection(@RequestBody AssignDTO assignDTO) {
		String status = "Success";
		try {
			String processId = assignDTO.getInspectionId().toString();
			String taskId = tasklistservice.getActiveTaskID(processId);

			if (assignDTO.getInspectorId() != null && !assignDTO.getInspectorId().isEmpty()) {
				// Assign inspector
				tasklistservice.assignTask(assignDTO.getInspectionId().toString(), taskId, assignDTO.getInspectorId());
				zeebeClient.newSetVariablesCommand(assignDTO.getInspectionId())
						.variables(new HashMap<String, Object>() {
							{
								put("inspectorId", assignDTO.getInspectorId());
							}
						}).send().join();

				// Update the inspection case with inspectorId
				InspectionCase inspectionCase = inspectionCaseService.updateInspectionCase(assignDTO.getInspectionId(),
						assignDTO.getInspectorId(), "pending", null);
				inspectionCase.setCaseCreationType("individual");
				inspectionCaseDAO.updateInspectionCase(inspectionCase);

				// Save Inspection Mapping for inspectorId
				inspectionMappingService.savesInspectionMapping(assignDTO.getInspectionId(),
						assignDTO.getInspectorId());
			} else if (assignDTO.getLeadId() != null) {
				// Assign leadId
				tasklistservice.assignTask(assignDTO.getInspectionId().toString(), taskId, null);
				zeebeClient.newSetVariablesCommand(assignDTO.getInspectionId())
						.variables(new HashMap<String, Object>() {
							{
								put("leadId", assignDTO.getLeadId());

							}
						}).send().join();

				// Update the inspection case with leadId and groupId
				InspectionCase inspectionCase = inspectionCaseService.updateInspectionCase(assignDTO.getInspectionId(),
						null, "pending", null);
				inspectionCase.setLeadId(assignDTO.getLeadId());
				inspectionCase.setGroupId(assignDTO.getGroupId());
				inspectionCase.setCaseCreationType("group");
				inspectionCaseDAO.updateInspectionCase(inspectionCase);
			} else {
				status = "Failure: Either inspectorId or both leadId and groupId must be provided.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "Failure";
		}
		return status;
	}

	@PostMapping(value = "/startAISProcess", consumes = { "application/json", "multipart/form-data" })
	public ResponseEntity<?> startAISProcess(
	        @RequestParam("processType") String processType,
	        @RequestPart(value = "startAISRequest", required = false) StartAISRequestDTO startAISRequest,
	        @RequestPart(value = "files", required = false) MultipartFile[] inspectorUploadedDocument) {
	    
	    try {
	        /* ============================================================
	         * PROCESS TYPE = "manual"
	         * ============================================================ */
	        if ("manual".equalsIgnoreCase(processType)) {
	            AISResponseDTO startAISResponseDTO = new AISResponseDTO();
	            try {
	                String businessKey = aisService.startAISProcess(startAISRequest, processType);
	                startAISResponseDTO.setStatus("Success");
	                startAISResponseDTO.setMessage("AIS process started successful.");
	                startAISResponseDTO.setBusinessKey(businessKey);
	                System.out.println(startAISResponseDTO);
 
	                // Store new entity if required
	                if (startAISRequest.isAddEntity()) {
	                    String entityId = newEntityService.saveEntity(startAISRequest.getNewEntity());
	                    startAISRequest.setEntityId(entityId);
	                }
 
	                // Store inspection details
	                inspectionCaseService.saveInspectionCase(startAISRequest, businessKey);
	            } catch (Exception e) {
	                e.printStackTrace();
	                startAISResponseDTO.setStatus("Failure");
	                startAISResponseDTO.setMessage("Process is not started =" + e.getMessage());
	            }
	            return ResponseEntity.ok(startAISResponseDTO);
	        }
	        
	        /* ============================================================
	         * PROCESS TYPE = "ADP"
	         * ============================================================ */
	        else if ("ADP".equalsIgnoreCase(processType)) {
	            Map<String, Object> response = new HashMap<>();
	            ObjectMapper mapper = new ObjectMapper();
	            ArrayNode infoArray = mapper.createArrayNode();
 
	            // 1) Handle uploaded documents
	            if (inspectorUploadedDocument != null) {
	                for (MultipartFile file : inspectorUploadedDocument) {
	                    if (file != null && !file.isEmpty()) {
	                        ObjectNode fileNode = mapper.createObjectNode();
	                        fileNode.put("documentType", file.getOriginalFilename());
	                        try {
	                            fileNode.put("document", Base64.getEncoder().encodeToString(file.getBytes()));
	                        } catch (IOException e) {
	                            throw new RuntimeException("Error reading file: " + file.getOriginalFilename(), e);
	                        }
	                        infoArray.add(fileNode);
	                    }
	                }
	            }
 
	            try {
	                // 2) Prepare Zeebe variables
	                Map<String, Object> variables = new HashMap<>();
	                variables.put("informationForTheEntity", infoArray);
	               // variables.put("processType", "ADP");
	                variables.put("processType", processType);
	           	 variables.put("developerId", developerId);


 
	                // 3) Start main process AISProcessV3
	                ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
	                        .bpmnProcessId("AISProcessV3SK")
	                        .latestVersion()
	                        .variables(variables)
	                        .send()
	                        .join();
 
	                long businessKey = processInstanceEvent.getProcessInstanceKey();
 
	                // 4) Set BusinessKey variable
	                zeebeClient.newSetVariablesCommand(businessKey)
	                        .variables(Map.of("BusinessKey", businessKey))
	                        .send()
	                        .join();
 
	                response.put("businessKey", businessKey);
	                logger.info("Started AISProcessV3 with key: {}", businessKey);
 
	                // ----------------------------------------------------
	                // 5) Find subprocess AISADPUserCases with retry (Operate indexing delay)
	                // ----------------------------------------------------
	                String subProcessInstanceKeyStr = null;
	                int maxRetries = 10;  // 20 seconds total
	                for (int i = 0; i < maxRetries; i++) {
	                    subProcessInstanceKeyStr = operateService.searchProcessInstances(
	                            String.valueOf(businessKey), null);
	                    if (subProcessInstanceKeyStr != null) {
	                        logger.info("Found subprocess AISADPUserCases: {}", subProcessInstanceKeyStr);
	                        break;
	                    }
	                    logger.info("Subprocess not indexed yet, retry {}/{}", i+1, maxRetries);
	                    try {
	                        Thread.sleep(2000);  // 2s between retries
	                    } catch (InterruptedException ie) {
	                        Thread.currentThread().interrupt();
	                        break;
	                    }
	                }
 
	                Long subProcessInstanceKey = subProcessInstanceKeyStr != null
	                        ? Long.valueOf(subProcessInstanceKeyStr) : null;
 
	                String validationStatus = null;
 
	                if (subProcessInstanceKey != null) {
	                    // 6) Poll for validationStatus on subprocess
	                    long timeoutMs = 240_000;    // 2 minutes total
	                    long intervalMs = 2000;      // 2 seconds
	                    long deadline = System.currentTimeMillis() + timeoutMs;
 
	                    logger.info("Polling validationStatus for subprocess: {}", subProcessInstanceKey);
	                    
	                    while (System.currentTimeMillis() < deadline) {
	                        String value = operateService.getVariableByName(
	                                subProcessInstanceKey, "validationStatus");
	                        if (value != null) {
	                            validationStatus = value.replace("\"", "");
	                            logger.info("Found validationStatus: '{}' for subprocess {}",
	                                      validationStatus, subProcessInstanceKey);
	                            break;
	                        }
	                        logger.info("Waiting for validationStatus on subprocess {}...", subProcessInstanceKey);
	                        try {
	                            Thread.sleep(intervalMs);
	                        } catch (InterruptedException ie) {
	                            Thread.currentThread().interrupt();
	                            break;
	                        }
	                    }
	                } else {
	                    logger.warn("Could NOT find AISADPUserCases subprocess for parent {}", businessKey);
	                }
 
	                // ----------------------------------------------------
	                // 7) Build final response
	                // ----------------------------------------------------
	                if ("success".equalsIgnoreCase(validationStatus)) {
	                	response.put("status", "success");
	                    response.put("message", "Process completed successfully");
	                } else {
	                	response.put("status", "failure");
	                    response.put("message", "Process failed");
	                    // Debug info (remove in production)
	                    response.put("debug", Map.of(
	                            "subProcessFound", subProcessInstanceKey != null,
	                            "validationStatus", validationStatus,
	                            "businessKey", businessKey
	                    ));
	                }
 
	                logger.info("ADP Response: {}", response);
	                return ResponseEntity.ok(response);
 
	            } catch (Exception e) {
	                e.printStackTrace();
	                response.put("message", "Process failed");
	                response.put("error", e.getMessage());
	                return ResponseEntity.status(500).body(response);
	            }
	        }
 
	        /* ============================================================
	         * INVALID PROCESS TYPE
	         * ============================================================ */
	        else {
	            return ResponseEntity.badRequest().body("Invalid processType! Use: manual or ADP");
	        }
 
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return ResponseEntity.status(500).body("Unexpected Error: " + ex.getMessage());
	    }
	}
 

	///// Re-inspection

	@GetMapping("/managerPool/{createdBy}/{dateFilter}")
	public List<InspectionCase_EntityDTO> getManagerpoolbyDate(@PathVariable("createdBy") String createdBy,
			@PathVariable("dateFilter") String dateFilter) {
		try {
			return inspectionCaseService.getManagerPool(createdBy, dateFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<InspectionCase_EntityDTO>();
	}

	// To get individual status names and its count and total count of status for
	// particular createdby//
	@GetMapping("/getStatusCountForManager/{createdBy}")
	public ResponseEntity<StatusCountResponseDTO> getStatusCountByCreatedBy(
			@PathVariable("createdBy") String createdBy) {
		try {
			StatusCountResponseDTO statusCountResponseDTO = newEntityService.getStatusCountByCreatedBy(createdBy);
			return ResponseEntity.ok(statusCountResponseDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(null);
		}
	}
	//////////////////////////////////////////////////////////////////////////

	// To get count and names of status, Inspector source based on createBy//
	@GetMapping("/getInspectorSourceCountForManager/{createdBy}")
	public ResponseEntity<List<InspectorSourceStatusCountDTO>> getStatusAndInspectorSourceCount(
			@PathVariable("createdBy") String createdBy) {
		try {
			List<InspectorSourceStatusCountDTO> responseList = newEntityService
					.getStatusAndInspectorSourceCount(createdBy);
			return ResponseEntity.ok(responseList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(null);
		}
	}

	////////////////////////////////////////////////////////
	// To get individual status names and its count and total count of status for
	//////////////////////////////////////////////////////// particular
	//////////////////////////////////////////////////////// inspectorID//
	@GetMapping("/getStatusCountForInspector/{inspectorID}")
	public StatusCountResponseDTO getStatusCountByInspectorID(@PathVariable("inspectorID") String inspectorID) {
		return newEntityService.getStatusCountByInspectorID(inspectorID);
	}

	///////////////////////////////////////////////////////////
	// To get count and names of status, Inspector source based on inspectorID//
	@GetMapping("/getInspectorSourceCountForInspector/{inspectorID}")
	public List<InspectorSourceStatusCountDTO> getStatusAndInspectorSourceCountByInspectorID(
			@PathVariable("inspectorID") String inspectorID) {
		return newEntityService.getStatusAndInspectorSourceCountByInspectorID(inspectorID);
	}
	///////////////////////////////////////////

	@GetMapping("/getInspectionChecklist/{inspectionId}")
	public List<ChecklistDTO> getInspectionChecklist(@PathVariable("inspectionId") String inspectionId) {
		List<ChecklistDTO> checklist = new ArrayList<ChecklistDTO>();
		checklist = checklistService.getInspectionChecklist(inspectionId);
		return checklist;
	}

	@PostMapping("/saveInspection")
	public ResponseDTO saveInspection(@RequestBody SaveInspectionDTO inspectionDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			System.out.println(inspectionDTO);
			long insectionId = inspectionDTO.getInspectionChecklistandAnswers().get(0).getId().getInspectionID();
			inspectionCaseService.saveInspectionAnswers(inspectionDTO);

			// Update the overall comments
			InspectionCase inspectionCase = inspectionCaseService.updateInspectionComments(insectionId,
					inspectionDTO.getInspectorComment(), inspectionDTO.getReviewerComment(),
					inspectionDTO.getApproverComment());
			// updating InspectorId and changing status to pending in Database
			inspectionCase = inspectionCaseService.updateInspectionCase(insectionId, "", "in_progress", inspectionCase);
			inspectionCaseDAO.updateInspectionCase(inspectionCase);
			responseDTO.setStatus("Success");
			responseDTO.setMessage("Inspection saved successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setErrorMessage(e.getMessage());
		}
		return responseDTO;

	}

	@GetMapping("/getSubmittedInspection/{inspectionId}")
	public SubmitInspectionDTO getSubmittedInspection(@PathVariable("inspectionId") String inspectionId,
			@RequestParam(value = "inspectorId", required = false) String inspectorId) {
		SubmitInspectionDTO saveInspectionDTO = new SubmitInspectionDTO();
		try {
			saveInspectionDTO = inspectionCaseService.getSubmittedInspection(Long.parseLong(inspectionId), inspectorId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saveInspectionDTO;
	}

	/*
	 * Deprecated code reference purpose//
	 * 
	 * @PostMapping("/submitInspection/{inspectionId}") public ResponseDTO
	 * submitInspection(@PathVariable("inspectionId") String inspectionId,
	 * 
	 * @RequestBody SaveInspectionDTO inspectionDTO) { ResponseDTO responseDTO = new
	 * ResponseDTO(); try { String taskId =
	 * tasklistservice.getActiveTaskID(inspectionId);
	 * tasklistservice.CompleteTaskByID(taskId, new HashMap<String, Object>() { {
	 * put("checklistFilled", "true"); } });
	 * 
	 * inspectionCaseService.saveInspectionAnswers(inspectionDTO);
	 * 
	 * // Update the overall comments InspectionCase inspectionCase =
	 * inspectionCaseService.updateInspectionComments(Long.parseLong(inspectionId),
	 * inspectionDTO.getInspectorComment(), inspectionDTO.getReviewerComment(),
	 * inspectionDTO.getApproverComment());
	 * 
	 * // updating InspectorId and changing status to pending in Database
	 * inspectionCase =
	 * inspectionCaseService.updateInspectionCase(Long.parseLong(inspectionId), "",
	 * "pending_review", inspectionCase);
	 * 
	 * // Check due date and update dueDateSatisfiedByInspector boolean
	 * dueDateSatisfied =
	 * inspectionCaseService.isDueDateSatisfied(Long.parseLong(inspectionId));
	 * inspectionCase.setDueDateSatisfiedByInspector(dueDateSatisfied);
	 * inspectionCaseDAO.updateInspectionCase(inspectionCase);
	 * 
	 * responseDTO.setStatus("Success");
	 * responseDTO.setMessage("Inspection checklist submitted."); } catch (Exception
	 * e) { e.printStackTrace(); responseDTO.setStatus("Failure");
	 * responseDTO.setErrorMessage(e.getMessage()); } return responseDTO; }
	 */
	
	@PostMapping("/submitInspection/{inspectionId}")
	public ResponseDTO submitInspection(
	        @PathVariable("inspectionId") String inspectionId,
	        @RequestBody SaveInspectionDTO inspectionDTO) {
 
	    ResponseDTO responseDTO = new ResponseDTO();
 
	    try {
	        // 1. Complete active task (only for task completion, NOT for riskScore)
	        String taskId = tasklistservice.getActiveTaskID(inspectionId);
	        tasklistservice.CompleteTaskByID(
	                taskId,
	                Map.of("checklistFilled", true)
	        );
 
	        // 2. Save inspection answers
	        inspectionCaseService.saveInspectionAnswers(inspectionDTO);
 
	        // 3. Fetch riskScore using OperateService ONLY
	        long processInstanceKey = Long.parseLong(inspectionId);
	        Integer riskScore = operateService.getRiskScore(processInstanceKey);
 
	        Map<String, Object> completeVariables = new HashMap<>();
	        completeVariables.put("checklistFilled", true);
 
	        if (riskScore != null && riskScore > 80) {
 
	            String notificationMsg =
	                "As per AI Recommendation this case has been assigned to Inspector Manager, to Schedule follow up.";
 
	            responseDTO.setNotificationName("RISK_SCORE_HIGH_FOLLOW_UP_REQUIRED");
	            responseDTO.setNotificationMessage(notificationMsg);
 
	            // optional: store as process variable if needed later
	            completeVariables.put("aiNotification", notificationMsg);
	        }
 
	        // 4. Update comments
	        InspectionCase inspectionCase =
	                inspectionCaseService.updateInspectionComments(
	                        Long.parseLong(inspectionId),
	                        inspectionDTO.getInspectorComment(),
	                        inspectionDTO.getReviewerComment(),
	                        inspectionDTO.getApproverComment()
	                );
 
	        // 5. Update status
	        inspectionCase =
	                inspectionCaseService.updateInspectionCase(
	                        Long.parseLong(inspectionId),
	                        "",
	                        "pending_review",
	                        inspectionCase
	                );
 
	        // 6. Due date check
	        boolean dueDateSatisfied =
	                inspectionCaseService.isDueDateSatisfied(Long.parseLong(inspectionId));
	        inspectionCase.setDueDateSatisfiedByInspector(dueDateSatisfied);
	        inspectionCaseDAO.updateInspectionCase(inspectionCase);
 
	        responseDTO.setStatus("Success");
	        responseDTO.setMessage("Inspection checklist submitted.");
 
	    } catch (Exception e) {
	        logger.error("Error while submitting inspection", e);
	        responseDTO.setStatus("Failure");
	        responseDTO.setErrorMessage(e.getMessage());
	    }
 
	    return responseDTO;
	}
 
	
//	@PostMapping("/submitInspection/{inspectionId}")
//	public ResponseDTO submitInspection(
//	        @PathVariable("inspectionId") String inspectionId,
//	        @RequestParam(value = "AIrecommendation", required = false) String AIrecommendation,
//	        @RequestBody SaveInspectionDTO inspectionDTO) {
//
//	    ResponseDTO responseDTO = new ResponseDTO();
//
//	    try {
//
//	        String taskId = tasklistservice.getActiveTaskID(inspectionId);
//
//	        // ---- Prepare default Camunda variables ----
//	        Map<String, Object> camundaVars = new HashMap<>();
//	        camundaVars.put("checklistFilled", "true");
//
//	     // ---- Add new AIrecommendation logic ----
//	        if (AIrecommendation != null) {
//	            if (AIrecommendation.equalsIgnoreCase("continue")) {
//	                // flow where AI suggests to continue
//	                camundaVars.put("AIrecommendation", "true");   // hits the "true" branch in gateway
//	            } else if (AIrecommendation.equalsIgnoreCase("skip")) {
//	                // flow where AI suggests to skip
//	                camundaVars.put("AIrecommendation", "false");  // hits the "false" branch in gateway
//	            }
//	        }
//
//
//	        // ---- Complete task with required variables ----
//	        tasklistservice.CompleteTaskByID(taskId, camundaVars);
//
//
//	        // ---- OLD LOGIC (same as before) ----
//	        inspectionCaseService.saveInspectionAnswers(inspectionDTO);
//
//	        InspectionCase inspectionCase = inspectionCaseService.updateInspectionComments(
//	                Long.parseLong(inspectionId),
//	                inspectionDTO.getInspectorComment(),
//	                inspectionDTO.getReviewerComment(),
//	                inspectionDTO.getApproverComment()
//	        );
//
//	        inspectionCase = inspectionCaseService.updateInspectionCase(
//	                Long.parseLong(inspectionId),
//	                "",
//	                "pending_review",
//	                inspectionCase
//	        );
//	        
//	     // --- Set recommendation column based on AIrecommendation ---
//	        if (AIrecommendation != null) {
//	            if (AIrecommendation.equalsIgnoreCase("continue")) {
//	                inspectionCase.setRecommendation("yes");
//	            } else if (AIrecommendation.equalsIgnoreCase("skip")) {
//	                inspectionCase.setRecommendation("no");
//	            }
//	        }
//
//
//	        boolean dueDateSatisfied = inspectionCaseService.isDueDateSatisfied(Long.parseLong(inspectionId));
//	        inspectionCase.setDueDateSatisfiedByInspector(dueDateSatisfied);
//	        inspectionCaseDAO.updateInspectionCase(inspectionCase);
//
//	        responseDTO.setStatus("Success");
//	        responseDTO.setMessage("Inspection checklist submitted.");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        responseDTO.setStatus("Failure");
//	        responseDTO.setErrorMessage(e.getMessage());
//	    }
//
//	    return responseDTO;
//	}


	
	@GetMapping("/getMycasesForInspectorDate/{inspectorID}/{date}")
	public Map<String, List<InspectionCase_EntityDTO>> getMycasesForInspectorDate(
			@PathVariable("inspectorID") String inspectorID, @PathVariable("date") LocalDate due_date) {
		try {
			return inspectionCaseService.getMyCasesForInspectorDate(inspectorID, due_date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String, List<InspectionCase_EntityDTO>>();
	}

	/////// Get status count dashboard data for approver
	@GetMapping("/statusCountsByApproverID")
	public Map<String, Object> getStatusCountsByApproverID(@RequestParam String approverID) {
		List<ApproverDashboardDTO> statusCounts = newEntityService.getStatusCountsByApproverID(approverID);

		Map<String, Object> response = new HashMap<>();
		Map<String, Long> statusCountsMap = new HashMap<>();
		long totalCount = 0;

		for (ApproverDashboardDTO statusCount : statusCounts) {
			String status = statusCount.getStatus().toLowerCase();
			long count = statusCount.getCount();

			if (status.equals("pending_approval") || status.equals("under_approval")) {
				statusCountsMap.merge("pending", count, Long::sum);
			} else if (!status.equals("new") && !status.equals("in_progress")) {
				statusCountsMap.merge(status, count, Long::sum);
			}
		}

		for (long count : statusCountsMap.values()) {
			totalCount += count;
		}

		response.put("statusCounts", statusCountsMap);
		response.put("totalCount", totalCount);

		return response;
	}
	////// Get the pre-inspection checklist for inspection type

	@GetMapping("/getAllPreInspectionChecklists")
	public List<PreInspectionChecklist> getAllPreInspectionChecklists(@RequestParam String name) {
		return preInspectionChecklistService.getPreInspectionChecklist(name);
	}

	///////////// Get source count for approver
	@GetMapping("/approver-source-count")
	public ResponseEntity<List<InspectorSourceStatusCountDTO>> getApproverSourceCount(@RequestParam String approverID) {
		List<InspectorSourceStatusCountDTO> result = newEntityService.getApproverSourceCountForApproverID(approverID);
		return ResponseEntity.ok(result);
	}

	////// get user details with skills based on the Inspection type
	@GetMapping("/name/{name}")
	public InspectionTypeDTO getInspectionTypeDetailsByName(@PathVariable String name) {
		return inspectionTypeService.getInspectionTypeDetailsByName(name);
	}

	////
	@GetMapping("/role/{role}")
	public List<UsersDTO> getUsersByRole(@PathVariable String role) {
		return usersService.getUsersByRole(role);
	}

	@GetMapping("/getPreInspectionChecklists")
	public PreInspectionChecklistDTO getPreInspectionChecklists(@RequestParam long inspection_case_id) {
		return preInspectionChecklistService.getPreInspectionChecklistDTO(inspection_case_id);
	}

//////////////Save  the inspection for approver////////////////////

	@PostMapping("/reviewInspection")
	public ResponseEntity<String> reviewInspection(@RequestBody ApproverCommentsDTO approverCommentRequestDTO) {
		System.out.println(approverCommentRequestDTO);
		inspectionCaseService.addReviewComments(approverCommentRequestDTO);
		return ResponseEntity.ok("Success");
	}
///////////////Save the inspection for approver//////////////////////////

	// assigning Task to Approver
	// ////////////////////////////////////////////////////////////////////
	@PostMapping("/assignInspectionToApprover")
	public String assignApproverInspection(@RequestBody AssignApproverDTO assignApproverDTO) {
		String status = "Success";
		try {
			String processId = assignApproverDTO.getInspectionId().toString();
			String taskId = tasklistservice.getActiveTaskID(processId);
			tasklistservice.assignTask(assignApproverDTO.getInspectionId().toString(), taskId,
					assignApproverDTO.getApproverId());
			zeebeClient.newSetVariablesCommand(assignApproverDTO.getInspectionId())
					.variables(new HashMap<String, Object>() {
						{
							put("approverId", assignApproverDTO.getApproverId());
						}
					}).send().join();
			// updating InspectorId and changing status to pending in Database
			inspectionCaseService.updateApproverIdinInspectionCase(assignApproverDTO.getInspectionId(),
					assignApproverDTO.getApproverId(), "pending_approval");

		} catch (Exception e) {
			e.printStackTrace();
			status = "Failure";
		}
		return status;
	}

	//////////////////////////////////////////////////////////////////////////////////

	///// fetch all zone details based on the inspectionID//
	@GetMapping("/zonedetails/{inspectionId}")
	public ResponseEntity<ZoneUserDTO> getZoneUserDetails(@PathVariable long inspectionId) {
		ZoneUserDTO zoneUserDTO = inspectionService.getZoneUserDetailsByInspectionId(inspectionId);
		if (zoneUserDTO == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(zoneUserDTO);
	}

	@PostMapping("/savesubmit")
	public ResponseEntity<List<SaveSubmitPreInspectionChecklist>> saveOrUpdateChecklists(
			@RequestBody List<SaveSubmitPreInspectionChecklist> checklists) {
		List<SaveSubmitPreInspectionChecklist> savedChecklists = checklists.stream()
				.map(saveSubmitPreInspectionChecklistService::saveOrUpdateChecklist).toList();
		return new ResponseEntity<>(savedChecklists, HttpStatus.CREATED);
	}

	@GetMapping("/getMycasesForApprover/{approverID}/{dateFilter}")
	public List<InspectionCase_EntityDTO> getMycasesForApproverByDate(@PathVariable("approverID") String approverID,
			@PathVariable("dateFilter") String dateFilter) {
		return newEntityService.getMycasesForApprover(approverID, dateFilter);
	}

	// Get approver pool data
	@GetMapping("/getApproverPool/{approverID}/{dateFilter}")
	public List<InspectionCase_EntityDTO> getApproverpoolByDate(@PathVariable("approverID") String approverID,
			@PathVariable("dateFilter") String dateFilter) {
		try {
			return inspectionCaseService.getApproverpool(approverID, dateFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<InspectionCase_EntityDTO>();
	}

	//////////// get Available User//////////////////
	@GetMapping("/getavailableusers/{inspectionId}")
	public List<UsersDTO> getZoneUserDetailsByInspectionId(@PathVariable long inspectionId) {
		List<UsersDTO> usersDTO = inspectionCaseService.getZoneUserDetailsByInspectionId(inspectionId);
		return usersDTO;
	}
	//////////// get Available User//////////////////

	//////// SaveSubmitPreInspection//////
	@PostMapping("/saveSubmitPreInspection")
	public ResponseEntity<List<SaveSubmitPreInspectionChecklist>> saveOrUpdateChecklists(
			@RequestBody SavePreInspectionDTO dto) {

		List<SaveSubmitPreInspectionChecklist> savedChecklists = saveSubmitPreInspectionChecklistService
				.saveOrUpdateChecklists(dto);
		return new ResponseEntity<>(savedChecklists, HttpStatus.CREATED);
	}
	/////// SavedPreInspection//////////

	@GetMapping("/savedPreInspection")
	public ResponseEntity<SavedPreInspectionDTO> getSaveSubmitPreInspectionChecklist(@RequestParam long inspectionid) {
		SavedPreInspectionDTO checklists = checklistService.getChecklistsByInspectionId(inspectionid);
		return ResponseEntity.ok(checklists);
	}

/////// Get status count dashboard data for approver
	@GetMapping("/getStatusCountsForReviewer")
	public Map<String, Object> getStatusCountsForReviewer(@RequestParam String reviewerId) {
		List<ApproverDashboardDTO> statusCounts = newEntityService.getStatusCountsForReviewer(reviewerId);

		Map<String, Object> response = new HashMap<>();
		Map<String, Long> statusCountsMap = new HashMap<>();
		long totalCount = 0;

		for (ApproverDashboardDTO statusCount : statusCounts) {
			String status = statusCount.getStatus().toLowerCase();
			long count = statusCount.getCount();

			if (status.equals("pending_review") || status.equals("under_review")) {
				statusCountsMap.merge("pending", count, Long::sum);
			} else if (!status.equals("new") && !status.equals("in_progress")) {
				statusCountsMap.merge(status, count, Long::sum);
			}
		}

		for (long count : statusCountsMap.values()) {
			totalCount += count;
		}

		response.put("statusCounts", statusCountsMap);
		response.put("totalCount", totalCount);

		return response;
	}

///////////// Get source count for reviewer
	@GetMapping("/getSourceCountForReviewer")
	public ResponseEntity<List<InspectorSourceStatusCountDTO>> getSourceCountForReviewer(
			@RequestParam String approverID) {
		List<InspectorSourceStatusCountDTO> result = newEntityService.getSourceCountForReviewer(approverID);
		return ResponseEntity.ok(result);
	}

	// assigning Task to Reviewer
	// ////////////////////////////////////////////////////////////////////
	@PostMapping("/assignInspectionToReviewer")
	public String assignInspectionToReviewer(@RequestBody AssignApproverDTO assignApproverDTO) {
		String status = "Success";
		try {
			String processId = assignApproverDTO.getInspectionId().toString();
			String taskId = tasklistservice.getActiveTaskID(processId);
			tasklistservice.assignTask(assignApproverDTO.getInspectionId().toString(), taskId,
					assignApproverDTO.getApproverId());
			zeebeClient.newSetVariablesCommand(assignApproverDTO.getInspectionId())
					.variables(new HashMap<String, Object>() {
						{
							put("reviewerId", assignApproverDTO.getApproverId());
						}
					}).send().join();
			// updating InspectorId and changing status to pending in Database
			inspectionCaseService.updateReviewerIdinInspectionCase(assignApproverDTO.getInspectionId(),
					assignApproverDTO.getApproverId(), "pending_review");

		} catch (Exception e) {
			e.printStackTrace();
			status = "Failure";
		}
		return status;
	}

	@GetMapping("/getMycasesForReviewer/{reviewerId}/{dateFilter}")
	public List<InspectionCase_EntityDTO> getMycasesForReviewerByDate(@PathVariable("reviewerId") String reviewerID,
			@PathVariable("dateFilter") String dateFilter) {
		return newEntityService.getMycasesForReviewer(reviewerID, dateFilter);
	}
	
	@GetMapping("/getMycasesForCompliance/{complianceId}/{dateFilter}")
	public List<InspectionCase_EntityDTO> getMycasesForComplianceByDate(
	        @PathVariable("complianceId") String complianceId,
	        @PathVariable("dateFilter") String dateFilter) {

	    return newEntityService.getMycasesForCompliance(complianceId, dateFilter);
	}


	// Get approver pool data
	@GetMapping("/getReviewerPool/{reviewerId}/{dateFilter}")
	public List<InspectionCase_EntityDTO> getReviewerpoolByDate(@PathVariable("reviewerId") String reviewerID,
			@PathVariable("dateFilter") String dateFilter) {
		try {
			return inspectionCaseService.getReviewerpool(reviewerID, dateFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<InspectionCase_EntityDTO>();
	}
//////Co-relation message////

//////Co-relation message////
	@PostMapping("/askCopilot")
	public ResponseEntity<ResponseDTO> correlateMessage(@RequestBody Map<String, String> payload) {
		String businessKey = payload.get("businessKey");
		String prompt = payload.get("prompt");

		ResponseDTO responseDTO = new ResponseDTO();
		try {
			// Call the service to correlate the message with businessKey and prompt
			String chatGPTResponse = aisService.correlateChatGPTMessage(businessKey, prompt);

			// Set success response
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage("Message correlated successfully with businessKey:prompt: " + prompt);
			responseDTO.setBusinessKey(businessKey);
			responseDTO.setChatGPTResponse(chatGPTResponse); // Include the ChatGPT response

			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			e.printStackTrace();

			// Set failure response
			responseDTO.setStatus("FAILURE");
			responseDTO.setMessage("Message correlation failed");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	// Log the escalation in escalation table

	@Autowired
	private InspectionEscalationService inspectionEscalationService;

	@PostMapping("/saveInspectionEscalations")
	public ResponseEntity<String> saveInspectionEscalation(@RequestBody InspectionEscalationDTO requestDTO) {
		inspectionEscalationService.saveInspectionEscalation(requestDTO);
		return ResponseEntity.ok("Inspection Escalation saved successfully.");
	}

	///// PDF report///
	@PostMapping("/generatePDFreport")
	public ResponseEntity<Void> generateReport(@RequestBody Map<String, Long> requestBody) {
		Long inspectionId = requestBody.get("inspectionId"); // Ensure you're using "inspectionId"
		summaryGeneratorService.generateAndStoreReport(inspectionId);
		return ResponseEntity.status(HttpStatus.CREATED).build(); // Return 201 Created status
	}

	@GetMapping("/generate/{id}")
	public String generateSummary(@PathVariable("id") Long id) {
		// Call the service method to generate the PDF
		summaryGeneratorService.summaryGenerator(id);
		return "PDF generated successfully for Inspection ID: " + id;
	}

	private <R> R saveOrUpdateChecklist(SaveSubmitPreInspectionChecklist savesubmitpreinspectionchecklist1) {
		return null;
	}

	@PostMapping("/addSkills")
	public ResponseEntity<Skill> createSkill(@RequestBody SkillDTO skillDTO) {
		Skill savedSkill = skillsService.saveSkill(skillDTO);
		return ResponseEntity.ok(savedSkill);
	}

	@PostMapping("/addCheckListItems")
	public ResponseEntity<ResponseDTO> saveChecklistItemAndCorrectiveActions(
			@RequestBody Checklist_Item checklist_Item) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			// Call the service to save checklist items and corrective actions
			checklistService.addCheckListItems(checklist_Item);

			// Set success response
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage("Checklist Item and Corrective Actions saved successfully!");

			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			e.printStackTrace();

			// Set failure response
			responseDTO.setStatus("FAILURE");
			responseDTO.setMessage("Failed to save Checklist Item and Corrective Actions.");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);

		}

	}

	// addNewInspectionTypeWithSkills//
	@PostMapping("/addNewInspectionTypeWithSkills")
	public ResponseEntity<AISResponseDTO> processInspectionType(
			@RequestBody InspectionTypePrimaryDetailsDTO inspectionTypePrimaryDetailsDTO,
			@RequestParam("action") String action) {
		InspectionTypeIdDTO responseDTO = new InspectionTypeIdDTO();
		try {
			// Call the service to process the Inspection Type
			long inspectionTypeId = inspectionTypeService.processInspectionType(inspectionTypePrimaryDetailsDTO,
					action);

			// Populate common success response logic
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage("Inspection type successfully processed.");
			responseDTO.setInspectionTypeId(inspectionTypeId);
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			// Handle specific validation errors
			responseDTO = buildErrorResponse("Invalid request: " + e.getMessage());
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			// Handle general errors
			responseDTO = buildErrorResponse("Failed to process the request: " + e.getMessage());
			return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Helper method to build an error response
	private InspectionTypeIdDTO buildErrorResponse(String errorMessage) {
		InspectionTypeIdDTO errorResponse = new InspectionTypeIdDTO();
		errorResponse.setStatus("ERROR");
		errorResponse.setMessage(errorMessage);
		return errorResponse;
	}
	// addNewInspectionTypeWithSkills//

	@PostMapping("/addChecklistCategory")
	public ResponseEntity<ResponseDTO> saveChecklistCategory(@RequestBody ChecklistCategoryDTO checklistCategoryDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			// Call the service to save checklist category and items
			checklistService.addChecklistCategory(checklistCategoryDTO);

			// Set success response
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage("Checklist Category and Items saved successfully!");

			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			e.printStackTrace();

			// Set failure response
			responseDTO.setStatus("FAILURE");
			responseDTO.setMessage("Failed to save Checklist Category and Items.");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	@PostMapping("/addInspectionTypeToEntities")
	public void addInspectionTypeToEntities(@RequestBody Map<String, Object> request) {
		String inspectionTypeId = (String) request.get("inspectionTypeId");
		@SuppressWarnings("unchecked")
		List<String> entityIds = (List<String>) request.get("entityIds");

		inspectionTypeService.addInspectionTypeToEntities(inspectionTypeId, entityIds);
	}

	@GetMapping("/getUsers")
	public Page<UsersDTO> getAllUserDetails(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return usersService.getSelectedUserDetails(page, size);
	}

//	  Get Userdetails by emailID///
	@GetMapping("/UserDetails")
	public Map<String, Object> getUserDetails(@RequestParam("emailID") String emailID) {
		return usersService.getUserDetails(emailID);
	}

	@GetMapping("/getAllSkillNames")
	public List<SkillsDTO> getSkillsBySkillName() {
		return skillsService.getSkillsBySkillName();
	}

	@PostMapping("/saveZone")
	public ResponseEntity<String> saveZone(@RequestBody Zone zone) {
		try {
			zoneService.saveZone(zone);
			return ResponseEntity.ok("Zone saved successfully!");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(500).body("An error occurred while saving the zone.");
		}
	}

	@GetMapping("/getIdAndNames")
	public ResponseEntity<List<ZoneDTO>> getAllZones() {
		List<ZoneDTO> zones = zoneService.getAllZones();
		return ResponseEntity.ok(zones);
	}

	@GetMapping("/getAllCheckListitems")
	public Page<CheckList_ItemDTO> getChecklistItems(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return checklistItemService.getChecklistItems(pageable);
	}

	@GetMapping("/getAllCheckListCategory")
	public Page<ChecklistCategoriesDTO> getAllChecklistCategories(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return checklistCategoryService.getAllChecklistCategories(page, size);
	}

	// Get all skills with inspection type for add inspection type
	@GetMapping("/getSkills")
	public ResponseEntity<Page<SkillInspectionTypeDTO>> getSkills(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<SkillInspectionTypeDTO> skills = skillsService.getSkillandRelatedInspectionTypes(page, size);
		return ResponseEntity.ok(skills);
	}

	// Get category data for edit category
	@GetMapping("getChecklistCategory/{id}")
	public ResponseEntity<ChecklistCategoryDTO> getChecklistCategoryById(@PathVariable("id") long id) throws Exception {
		ChecklistCategoryDTO checklistCategoryDTO = checklistCategoryService.getChecklistCategoryById(id);
		return ResponseEntity.ok(checklistCategoryDTO);
	}

	// Edit the checklist category
	@PutMapping("/updateChecklistCategory")
	public ResponseEntity<ResponseDTO> updateChecklistCategory(@RequestBody ChecklistCategoryDTO request) {

		ResponseDTO responseDTO = new ResponseDTO();
		try {
			// Call the service to save checklist category and items
			checklistCategoryService.updateChecklistCategory(request.getChecklist_cat_id(),
					request.getChecklist_category_name(), request.getCategory_threshold_local(),
					request.getCategory_weightage_local(), request.getChecklist_ids());

			// Set success response
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage("Checklist Category and Items updated successfully!");

			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			e.printStackTrace();

			// Set failure response
			responseDTO.setStatus("FAILURE");
			responseDTO.setMessage("Failed to save Checklist Category and Items.");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage(e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	@PostMapping("/addSlaWithInspectionName")
	public ResponseEntity<AISResponseDTO> createInspectionSLA(@RequestBody InspectionTypeSLADTO request) {
		String name = request.getName();
		String action = request.getAction();
		Map<String, InspectionTypeSLADTO.SLAEntityDetails> entitySizes = request.getEntitySizes();

		AISResponseDTO response = inspectionSLAService.createInspectionSLA(name, entitySizes, action);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/addChecklistitems")
	public ResponseEntity<List<addChecklistitemsDTO>> getAllChecklistItems() {
		List<addChecklistitemsDTO> checklistItems = checklistService.getAllChecklistItems();
		return ResponseEntity.ok(checklistItems);
	}

	@GetMapping("/checklistItemsBy/{checklistId}")
	public ResponseEntity<?> getChecklistItemById(@PathVariable long checklistId) {
		Checklist_Item checklistItem = checklistService.getChecklistItemById(checklistId);
		if (checklistItem != null) {
			return ResponseEntity.ok(checklistItem);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Checklist item with ID " + checklistId + " not found.");
		}

	}

	@PostMapping("/AddEditTemplate")
	public AISResponseDTO saveOrUpdateTemplate(@RequestBody TempCheckInspDTO tempCheckInspDTO) {
		AISResponseDTO responseDTO = new AISResponseDTO();

		try {
			if ("add".equalsIgnoreCase(tempCheckInspDTO.getAction())) {
				tempCheckInspService.addTemplate(tempCheckInspDTO); // Add new template
				responseDTO.setStatus("SUCCESS");
				responseDTO.setMessage("Template added successfully.");
			} else if ("edit".equalsIgnoreCase(tempCheckInspDTO.getAction())) {
				tempCheckInspService.editTemplate(tempCheckInspDTO); // Edit existing template
				responseDTO.setStatus("SUCCESS");
				responseDTO.setMessage("Template updated successfully.");
			} else {
				responseDTO.setStatus("FAILURE");
				responseDTO.setMessage("Invalid action.");
			}

		} catch (Exception e) {
			responseDTO.setStatus("ERROR");
			responseDTO.setMessage("Error occurred: " + e.getMessage());

		}

		return responseDTO;
	}

	@GetMapping("/getAllInspectionTypes")
	public List<GetAllInspection_TypeDTO> getAllInspectionIdsAndNames() {
		return inspectionTypeService.getAllInspectionIdsAndNames();
	}

	@GetMapping("/getUserSkills")
	public ResponseEntity<?> getUserSkills(@RequestParam Long userId) {
		try {
			List<UserSkillDTO> userSkills = userSkillService.getUserSkillsByUserId(userId);
			return ResponseEntity.ok(userSkills);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/getalltemplatesandinspectiontypes")
	public ResponseEntity<Page<TemplateResponseDTO>> getAllTemplatesWithInspectionTypes(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Page<TemplateResponseDTO> response = templateService.getTemplatesWithInspectionTypes(page, size);
		return ResponseEntity.ok(response);
	}

	// Get all inspection types Ids and names for add entity and edit entity

	@GetMapping("/getAllUsersWithSkills")
	public Page<GetAllSkillDTO> getAllUsersWithSkills(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String role) { // Accept role as
																										// a query
																										// parameter
		Pageable pageable = PageRequest.of(page, size);
		return userSkillService.getAllUsersWithSkills(pageable, role);
	}

	@GetMapping("/getEntityDetails/{entityId}")
	public ResponseEntity<EntityDetailsDTO> getEntityById(@PathVariable("entityId") String entityId) {
		EntityDetailsDTO entityDetails = newEntityService.getEntityDetailsById(entityId);

		if (entityDetails != null) {
			return ResponseEntity.ok(entityDetails);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/inspection-type/{id}")
	public ResponseEntity<?> getInspectionTypeWithSkills(@PathVariable long id) {
		// Call the service layer to retrieve the Inspection_Type with Skills
		InspectionTypeAdminSkillDTO inspectionType = inspectionTypeService.getInspectionTypeById(id);

		// If the inspectionType is found, return it with HTTP status 200 OK
		if (inspectionType != null) {
			return ResponseEntity.ok(inspectionType);
		} else {
			// If not found, return a 404 NOT_FOUND with an appropriate message
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inspection Type with ID " + id + " not found.");
		}
	}

	@GetMapping("/getSlaWithInspection/{id}")
	public ResponseEntity<InspectionTypeGetAdminDTO> getInspectionTypeDetailsWithSLA(@PathVariable("id") long id) {
		InspectionTypeGetAdminDTO dto = inspectionTypeService.getInspectionTypeDetails(id);
		return ResponseEntity.ok(dto);
	}

	// Get all users with id,name, role and skills for add Skill

	@GetMapping("/getAllInspectionTypesWithSkills")
	public Page<InspectionTypeSkillAdminDTO> getAllInspectionTypesWithSkills(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return inspectionTypeService.getAllInspectionTypesWithSkills(pageable);
	}

	@PostMapping("/updateUserDetails")
	public ResponseEntity<Map<String, String>> updateUserDetails(@RequestBody UsersDTO usersDTO) {
		Map<String, String> response = new HashMap<>();

		try {
			userService.updateUserDetails(usersDTO);
			response.put("message", "User details have been updated successfully.");
			response.put("status", "SUCCESS");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("message", e.getMessage());
			e.printStackTrace();
			response.put("status", "FAILURE");
			return ResponseEntity.badRequest().body(response);
		}
	}

	// Edit the Existing skill or Update the skill and ADD the new skill
	@PostMapping("/AddEditSkill")
	public ResponseDTO manageSkill(@RequestBody SkillRequestDTO skillRequest) {
		ResponseDTO responseDTO = new ResponseDTO();

		try {
			String result = skillsService.handleSkillAction(skillRequest);
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage(result);
		} catch (IllegalArgumentException e) {
			responseDTO.setStatus("FAILURE");
			responseDTO.setMessage(e.getMessage());
		} catch (Exception e) {
			responseDTO.setStatus("ERROR");
			responseDTO.setMessage("An unexpected error occurred: " + e.getMessage());
		}

		return responseDTO;
	}

	//// Get the entity mappings by inspectionId for edit////
	@GetMapping("/getEntityMappingsForInspections/{insTypeId}")
	public InspectionTypeDTO getInspectionType(@PathVariable long insTypeId) {
		return inspectionTypeService.getInspectionTypewithEntity(insTypeId);
	}

	@GetMapping("/getChecklist/{checklistId}")
	public ResponseEntity<Checklist_Item> getChecklistDetails(@PathVariable long checklistId) {
		Checklist_Item checklistItem = checklistItemService.getChecklistDetails(checklistId);
		if (checklistItem == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(checklistItem);
	}

	@GetMapping("/getAlltheEntitiesDetails")
	public Page<Map<String, Object>> getAllTheEntities(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return newEntityService.getPaginatedEntitiesWithInspectionTypes(pageable);
	}

	@PostMapping("/editEntityMapping")
	public ResponseEntity<Map<String, String>> updateInspectionType(@RequestBody InspectionTypeDTO dto) {
		Map<String, String> response = new HashMap<>();
		try {
			inspectionTypeService.updateInspectionType(dto);
			response.put("message", "InspectionType updated successfully.");
			response.put("status", "SUCCESS");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("message", "Error: " + e.getMessage());
			response.put("status", "FAILURE");
			return ResponseEntity.badRequest().body(response);
		}
	}

////Add and edit a preinspection checklist///
	@PostMapping("/addEditPreInspectionChecklist")
	public ResponseEntity<ResponseDTO> addChecklist(@RequestBody PreInspectionChecklistDTO checklistDTO) {
		ResponseDTO response = new ResponseDTO();
		try {
			PreInspectionChecklist checklist = preInspectionChecklistService.saveOrUpdateChecklist(checklistDTO);
			response.setStatus("success");
			response.setMessage("Checklist created successfully.");

			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Error creating checklist.");
			response.setErrorCode("500");
			response.setErrorMessage(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/updateChecklistItem/{checklistId}")
	public ResponseEntity<Map<String, String>> updateChecklistItem(@PathVariable Long checklistId,
			@RequestBody addChecklistitemsDTO checklistItemDTO) {

		Map<String, String> response = new HashMap<>();
		try {
			Checklist_Item updatedChecklistItem = checklistItemService.updateChecklistItem(checklistId,
					checklistItemDTO);

			if (updatedChecklistItem != null) {
				response.put("message", "Checklist Item updated successfully.");
				response.put("status", "SUCCESS");
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.put("message", "Checklist Item not found.");
				response.put("status", "FAILURE");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.put("message", "An error occurred: " + e.getMessage());
			response.put("status", "FAILURE");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// using templateId retrive the inpsections details and categories associated to
	// templateID
	@GetMapping("/getTemplateDetails/{template_id}")
	public TemplateDetailsDTO getTemplateDetails(@PathVariable long template_id) {
		return templateService.getTemplateDetails(template_id);
	}

	@GetMapping("/getAllPreInspections")
	public Page<PreInspectionChecklistDTO> getAllPreInspectionChecklists(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return preInspectionChecklistService.getAllPreInspectionChecklists(page, size);
	}

	@GetMapping("/api/pre-inspection-checklist/{id}")
	public PreInspectionChecklistResponseDTO getChecklistById(@PathVariable("id") long id) {
		return preInspectionChecklistService.getChecklistById(id);
	}

	@GetMapping("/getSkillDetails/{skillId}")
	public SkillDetailDTO getSkillDetails(@PathVariable("skillId") long skillId) {
		SkillDetailDTO skillDetailDTO = new SkillDetailDTO();
		try {
			skillDetailDTO = skillsService.getSkillDetails(skillId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return skillDetailDTO;
	}

	@PostMapping("/addEditInspectionType")
	public ResponseEntity<AISResponseDTO> addInspectionTypeWithSkillsAndSLA(
			@RequestBody InspectionTypeRequestDTO inspectionTypeRequestDTO, @RequestParam("action") String action) {

		AISResponseDTO responseDTO = new AISResponseDTO();

		try {
			long inspectionTypeId = 0L;

			// ----- Defensive checks & logging (helps find where NPE occurs) -----
			if (inspectionTypeRequestDTO == null) {
				throw new IllegalArgumentException("Request body is null");
			}

			// 1. Process Inspection Type Primary Details
			InspectionTypePrimaryDetailsDTO primary = inspectionTypeRequestDTO.getInspectionTypePrimaryDetails();
			if (primary == null) {
				throw new IllegalArgumentException("inspectionTypePrimaryDetails is null");
			}

			inspectionTypeId = inspectionTypeService.processInspectionType(primary, action);

			// 2. Add entities to the inspection type (defensive)
			List<String> entityIds = new ArrayList<>();
			if (inspectionTypeRequestDTO.getInspectionTypeEntity() != null
					&& inspectionTypeRequestDTO.getInspectionTypeEntity().getEntityIds() != null) {
				entityIds = inspectionTypeRequestDTO.getInspectionTypeEntity().getEntityIds();
			}

			inspectionTypeService.addInspectionTypeToEntitiesPeriodicty(String.valueOf(inspectionTypeId), entityIds);

			// 3. Process SLA details (defensive)
			InspectionTypeSLADTO inspectionTypeSLA = inspectionTypeRequestDTO.getInspectionTypeSLA();
			if (inspectionTypeSLA != null && inspectionTypeSLA.getEntitySizes() != null) {
				inspectionSLAService.createInspectionSLA(
						inspectionTypeRequestDTO.getInspectionTypePrimaryDetails().getName(),
						inspectionTypeSLA.getEntitySizes(), action);
			} else {
				// no SLA passed — log but proceed
				System.out.println("No inspectionSLA provided or empty.");
			}

			// 4. Process Periodicity details
			if (inspectionTypeRequestDTO.getPeriodicity() != null) {
				periodicityService.processPeriodicity(inspectionTypeId, inspectionTypeRequestDTO.getPeriodicity(),
						inspectionTypeRequestDTO.getInspectionTypeEntity() == null ? new ArrayList<>()
								: (inspectionTypeRequestDTO.getInspectionTypeEntity().getEntityIds() == null
										? new ArrayList<>()
										: inspectionTypeRequestDTO.getInspectionTypeEntity().getEntityIds()),
						action);
			} else {
				throw new IllegalArgumentException("periodicity is null");
			}

			// 5. Update template mapping
			inspectionTypeService.updateTemplateMapping(inspectionTypeId, inspectionTypeRequestDTO.getPeriodicity());

			// success response
			responseDTO.setStatus("SUCCESS");
			responseDTO.setMessage("Inspection type successfully processed with action: " + action);
			responseDTO.setInspectionTypeId(inspectionTypeId);
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			responseDTO = buildErrorResponse("Invalid request: " + safeMessage(e));
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			// Print and return stack trace lines (DEBUG only)
			e.printStackTrace();

			StringBuilder sb = new StringBuilder();
			sb.append(e.getClass().getName());
			sb.append(": ").append(safeMessage(e)).append("\n");
			StackTraceElement[] st = e.getStackTrace();
			for (int i = 0; i < Math.min(10, st.length); i++) {
				sb.append("\tat ").append(st[i].toString()).append("\n");
			}

			responseDTO = buildErrorResponse("Failed to process the request: " + sb.toString());
			return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// helper to avoid null messages
	private String safeMessage(Throwable e) {
		if (e == null)
			return "";
		if (e.getMessage() != null)
			return e.getMessage();
		if (e.getCause() != null)
			return e.getCause().toString();
		return "";
	}

//	@PostMapping("/addEditPeriodicInspectionType")
//	public ResponseEntity<AISResponseDTO> addInspectionTypeWithSkillsAndSLA1(
//	        @RequestBody InspectionTypeRequestDTO inspectionTypeRequestDTO1,
//	        @RequestParam("action") String action1) {
//
//	    AISResponseDTO responseDTO = new AISResponseDTO();
//
//	    try {
//	        long inspectionTypeId = 0L;
//
//	            // 🔹 Run all steps for save/edit
//
//	            // 1. Process Inspection Type Primary Details
//	            inspectionTypeId = inspectionTypeService
//	                    .processPeriodicInspectionType(inspectionTypeRequestDTO1.getInspectionTypePrimaryDetails(), action1);
//
//	            // 2. Add entities to the inspection type
//	            List<String> entityIds = inspectionTypeRequestDTO1.getInspectionTypeEntity().getEntityIds();
//	            inspectionTypeService.addInspectionTypeToEntitiesPeriodicty(String.valueOf(inspectionTypeId), entityIds);
//
//	            // 3. Process SLA details
//	            InspectionTypeSLADTO inspectionTypeSLA = inspectionTypeRequestDTO1.getInspectionTypeSLA();
//	            inspectionSLAService.createInspectionSLA(
//	                    inspectionTypeRequestDTO1.getInspectionTypePrimaryDetails().getName(),
//	                    inspectionTypeSLA.getEntitySizes(), action1);
//
//	            // 4. Process Periodicity details
//	            if (inspectionTypeRequestDTO1.getPeriodicity() != null) {
//	                periodicityService.processPeriodicity(
//	                        inspectionTypeId,
//	                        inspectionTypeRequestDTO1.getPeriodicity(),
//	                        inspectionTypeRequestDTO1.getInspectionTypeEntity().getEntityIds(),
//	                        action1
//	                );
//	        } else {
//	            throw new IllegalArgumentException("Invalid action: " + action1);
//	        }
//
//	        // ✅ Populate success response
//	        responseDTO.setStatus("SUCCESS");
//	        responseDTO.setMessage("Inspection type successfully processed with action: " + action1);
//	        responseDTO.setInspectionTypeId(inspectionTypeId);
//
//	        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//
//	    } catch (IllegalArgumentException e) {
//	        responseDTO = buildErrorResponse("Invalid request: " + e.getMessage());
//	        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        responseDTO = buildErrorResponse("Failed to process the request: " + e.getMessage());
//	        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	}

	@PostMapping("/deleteInspectionScheduleType")
	public void deleteInspectionScheduleType(@RequestBody InspectionTypeRequestDTO inspectionTypeRequestDTO2) {
		List<String> entityIds = inspectionTypeRequestDTO2.getInspectionTypeEntity().getEntityIds();

		long inspectionTypeId = inspectionTypeRequestDTO2.getInspectionTypePrimaryDetails().getIns_type_id();

		// long inspectionTypeId =
		// inspectionTypeService.processInspectionType(inspectionTypeRequestDTO2.getInspectionTypePrimaryDetails(),
		// "edit");
		System.out.println("###############");

		System.out.println(Arrays.toString(entityIds.toArray()));
		System.out.println(inspectionTypeId);

		System.out.println("#####################");
		inspectionTypeService.DeleteInspectionTypeToEntitiesPeriodicty(String.valueOf(inspectionTypeId), entityIds);

	}

//	 @PostMapping("/startAISProcessSAP")
//	    public ResponseEntity<AISResponseDTO> startAISProcess(@RequestBody startAISFromSAPAPIDTO fromSAPAPIDTO) {
//	        AISResponseDTO responseDTO = new AISResponseDTO();
//	 
//	        try {
//	            // Call the service method and get the business key
//	            String businessKey = aisService.processStartAIS(fromSAPAPIDTO);
//	 
//	            // Set success response details
//	            responseDTO.setStatus("Success");
//	            responseDTO.setBusinessKey(businessKey);
//	            responseDTO.setMessage("AIS process started successfully.");
//	            responseDTO.setSapNotificationID(fromSAPAPIDTO.getSapNotificationID());
//	 
//	            return ResponseEntity.ok(responseDTO);
//	        } catch (Exception e) {
//	            // Set failure response details
//	            responseDTO.setStatus("Failure");
//	            responseDTO.setBusinessKey(null);
//	            responseDTO.setMessage("Error starting AIS process: " + e.getMessage());
//	            responseDTO.setSapNotificationID(fromSAPAPIDTO.getSapNotificationID());
//	 
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
//	        }
//	    }

	@GetMapping("/getInspectionTypeById/{inspectionTypeId}")
	public InspectionTypeRequestDTO getInspectionTypeById(@PathVariable("inspectionTypeId") long inspectionTypeId) {
		InspectionTypeRequestDTO inspectionTypeRequestDTO = new InspectionTypeRequestDTO();
		try {
			inspectionTypeRequestDTO = inspectionTypeService.getInspectionTypeByIdForEdit(inspectionTypeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspectionTypeRequestDTO;
	}

	// EntityRegistration based on Entity_Type & Edit EntityRegistration details
	// based on entityId

	@PostMapping(value = "/entityRegistration", consumes = { "multipart/form-data" })
	public ResponseDTO postEntity(
			@RequestPart(value = "entityRegistrationDTO", required = true) EntityRegistrationDTO entityRegistrationDTO,
			@RequestPart(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "deleteAttachmentIds", required = false) List<Long> deleteAttachmentIds) {

		ResponseDTO responseDTO = new ResponseDTO();

		// Attach files to DTO
		if (files != null) {
			entityRegistrationDTO.setAttachments(Arrays.asList(files));
		}

		// Attachments to delete
		if (deleteAttachmentIds != null && !deleteAttachmentIds.isEmpty()) {
			entityRegistrationDTO.setDeleteAttachmentIds(deleteAttachmentIds);
		}

		try {
			// Validate action
			String action = entityRegistrationDTO.getAction();
			if (action == null || (!action.equalsIgnoreCase("save") && !action.equalsIgnoreCase("edit"))) {
				responseDTO.setStatus("Failure");
				responseDTO.setErrorCode("400");
				responseDTO.setErrorMessage("Invalid action: " + action + ". Allowed actions are 'save' or 'edit'.");
				return responseDTO;
			}

			// Process entity registration
			String responseMessage = newEntityService.entityRegistration(entityRegistrationDTO);

			responseDTO.setStatus("Success");
			responseDTO.setMessage(responseMessage);

		} catch (RuntimeException e) {
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("400");
			responseDTO.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage("Error during entity registration: " + e.getMessage());
		}

		return responseDTO;
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO loginRequest) {
		try {
			// Decrypt the password using AESDecryptionUtil
			// String decryptedPassword =
			// AESDecryptionUtil.decrypt(loginRequest.getPassword());

			// Authenticate the user
			UsersDTO userDTO = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());

			return ResponseEntity.ok(userDTO);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid input provided: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Status: " + e.getMessage());
		}
	}

	// Activate and Deactivate any record in any table

	@PostMapping("/updateIsActive")
	public ResponseDTO updateIsActive(@RequestBody UpdateIsActiveDTO request) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			// Call the service to update the status
			updateIsActiveService.updateStatus(request);

			// Set success response
			responseDTO.setStatus("Success");
			responseDTO.setMessage("Status updated successfully");
			return responseDTO;
		} catch (IllegalArgumentException e) {
			// Set failure response for bad input
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("400");
			responseDTO.setErrorMessage(e.getMessage());
			return responseDTO;
		} catch (Exception e) {
			// Set failure response for internal server errors
			responseDTO.setStatus("Failure");
			responseDTO.setErrorCode("500");
			responseDTO.setErrorMessage("Error updating status: " + e.getMessage());
			return responseDTO;
		}
	}

	@GetMapping("/getAllInspectorNames")
	public ResponseEntity<List<UsersRoleDTO>> getUserByRole(@RequestParam String role) {
		List<UsersRoleDTO> users = userService.getUserByRole(role);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/getAllCasesBasedOnFilters")
	public Page<InspectionCaseDTO> getInspectionCasesWithEntityDetails(@RequestParam(required = false) String entityid,
			@RequestParam(required = false) String inspectorID, @RequestParam(required = false) String status,
			@RequestParam(required = false) String inspector_source,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueStartDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueEndDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

		Pageable pageable = PageRequest.of(page, size);
		return inspectionCaseService.getInspectionCasesWithEntityDetails(entityid, inspectorID, status,
				inspector_source, start_date, end_date, dueStartDate, dueEndDate, pageable);
	}

	// Get entity details by entity id checking for entity type
	@GetMapping("/getEntityRegistrationDetailsbyEntityID")
	public Object getEntityDetailsbyEntityID(@RequestParam("entityId") String entityId) {
		try {
			// Fetch the result from the service
			return newEntityService.getEntityDetailsbyEntityID(entityId);
		} catch (EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching entity details", e);
		}
	}

	@GetMapping("/getCaseDetailsById/{inspectionId}")
	public Optional<Optional<InspectionCaseDetailsDTO>> getInspectionCaseDetailsById(
			@PathVariable("inspectionId") long inspectionId) {
		return inspectionCaseService.getInspectionCaseDetails(inspectionId);
	}

	@GetMapping("/getEntityNames/{ins_type_id}")
	public ResponseEntity<List<EntitiesInspectionTypeDTO>> getEntities1(
			@PathVariable(value = "ins_type_id", required = false) Long insTypeId) {
		List<EntitiesInspectionTypeDTO> response = newEntityService.fetchEntities(insTypeId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/getEntityReportById")
	public ResponseEntity<?> getEntityReportById(@RequestParam String entityId) {
		try {
			// Fetch the entity report by ID using the service
			EntityReportDTO reportDTO = entityInspectionReportService.getEntityReportById(entityId);

			// Log the response data for debugging
			System.out.println("Returning Entity Report DTO: " + reportDTO);

			// Return 200 OK with the reportDTO data
			return ResponseEntity.ok(reportDTO);
		} catch (EntityNotFoundException e) {
			// Return a 404 if the entity is not found
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}

//	@PostMapping("/getEntityInspectionReport")
//	public ResponseEntity<EntityInspectionReportDTO> getInspectionReport(@RequestBody EntityRequestDTO requestDTO) {
//
//		// Call the service to get the report
//		EntityInspectionReportDTO reportDTO = entityInspectionReportService
//				.getInspectionReportByEntityAndDate(requestDTO);
//
//		// Return the report as the response
//		return ResponseEntity.ok(reportDTO);
//	}
	
	@PostMapping("/getEntityInspectionReport")
    public ResponseEntity<EntityInspectionReportDTO> getInspectionReport(
            @RequestParam(required = false) String processType, // optional
            @RequestBody EntityRequestDTO requestDTO) {

        // attach the query param inside the DTO
        requestDTO.setProcessType(processType);

        EntityInspectionReportDTO reportDTO =
                entityInspectionReportService.getInspectionReportByEntityAndDate(requestDTO);

        return ResponseEntity.ok(reportDTO);
    }

//	@PostMapping("/getEntityInspectionCasesReport")
//	public ResponseEntity<List<EntityInspectionCasesReportResponseDTO>> getInspectionCases(
//			@RequestBody EntityRequestDTO requestDTO) {
//		List<EntityInspectionCasesReportResponseDTO> cases = entityInspectionReportService
//				.getInspectionCases(requestDTO);
//		return ResponseEntity.ok(cases);
//	}
	
	@PostMapping("/getEntityInspectionCasesReport")
	public ResponseEntity<Page<EntityInspectionCasesReportResponseDTO>> getInspectionCases(
	        @RequestBody EntityRequestDTO requestDTO,
	        @RequestParam(defaultValue = "0") int pageNumber,
	        @RequestParam(defaultValue = "10") int pageSize) {

	    // processType already comes inside requestDTO from UI

	    List<EntityInspectionCasesReportResponseDTO> fullList =
	            entityInspectionReportService.getInspectionCases(requestDTO);

	    Pageable pageable = PageRequest.of(pageNumber, pageSize);

	    int fromIndex = (int) pageable.getOffset();
	    int toIndex = Math.min(fromIndex + pageable.getPageSize(), fullList.size());

	    List<EntityInspectionCasesReportResponseDTO> paginatedContent =
	            fromIndex >= fullList.size()
	                    ? List.of()
	                    : fullList.subList(fromIndex, toIndex);

	    Page<EntityInspectionCasesReportResponseDTO> page =
	            new PageImpl<>(paginatedContent, pageable, fullList.size());

	    return ResponseEntity.ok(page);
	}


//	@GetMapping("/getTransactionsSummaryReport")
//	public CaseSummaryDTO getCaseSummary(@RequestParam(required = false) String startDate,
//			@RequestParam(required = false) String endDate) {
//		return inspectionCaseService.getCaseSummary(startDate, endDate);
//	}
	
	@GetMapping("/getTransactionsSummaryReport")
	public CaseSummaryDTO getCaseSummary(@RequestParam(required = false) String startDate,
	                                     @RequestParam(required = false) String endDate,
	                                     @RequestParam(required = false) String processType) {
	    return inspectionCaseService.getCaseSummary(startDate, endDate, processType);
	}


//	@GetMapping("/getTransactions")
//	public List<Map<String, Object>> getCaseStats(
//			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//		return inspectionCaseService.getCaseStatsByMonth(startDate, endDate);
//	}
	
	@GetMapping("/getTransactions")
	public ResponseEntity<Page<Map<String, Object>>> getCaseStats(
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	        @RequestParam(required = false) String processType,
	        @RequestParam(name = "page", defaultValue = "0") int pageNumber,
	        @RequestParam(name = "size", defaultValue = "10") int pageSize) {

	    // Call existing logic (NO CHANGE)
	    List<Map<String, Object>> fullList =
	            inspectionCaseService.getCaseStatsByMonth(startDate, endDate, processType);

	    Pageable pageable = PageRequest.of(pageNumber, pageSize);

	    int fromIndex = (int) pageable.getOffset();
	    int toIndex = Math.min(fromIndex + pageable.getPageSize(), fullList.size());

	    List<Map<String, Object>> paginated =
	            (fromIndex >= fullList.size()) ? List.of() : fullList.subList(fromIndex, toIndex);

	    Page<Map<String, Object>> page =
	            new PageImpl<>(paginated, pageable, fullList.size());

	    return ResponseEntity.ok(page);
	}


//	@PostMapping("/inspectionHistory")
//	public InspectionHistoryDTO getInspectionHistory(@RequestBody InspectionFilters inspectionFilters) {
//		InspectionHistoryDTO inspectionHistoryDTO = new InspectionHistoryDTO();
//		try {
//			inspectionHistoryDTO = inspectionCaseService.getCasesByFilters(inspectionFilters);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return inspectionHistoryDTO;
//	}
	
	@PostMapping("/inspectionHistory")
	public InspectionHistoryDTO getInspectionHistory(
	        @RequestBody InspectionFilters inspectionFilters,
	        @RequestParam(required = false) String processType) {

	    try {
	        inspectionFilters.setProcessType(processType);  // optional
	        return inspectionCaseService.getCasesByFilters(inspectionFilters);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new InspectionHistoryDTO();
	    }
	}

//	@PostMapping("/inspectionHistory/getCases")
//	public List<InspectionCase_EntityDTO> getInspectionHistoryCases(@RequestBody InspectionFilters inspectionFilters) {
//		List<InspectionCase_EntityDTO> inspectionCaseList = new ArrayList<>();
//		try {
//			inspectionCaseList = inspectionCaseService.getInspectionHistoryCases(inspectionFilters);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return inspectionCaseList;
//	}
	
	@PostMapping("/inspectionHistory/getCases")
	public ResponseEntity<Page<InspectionCase_EntityDTO>> getInspectionHistoryCases(
	        @RequestParam(required = false) String processType,     // optional
	        @RequestBody InspectionFilters inspectionFilters,
	        @RequestParam(defaultValue = "0") int pageNumber,
	        @RequestParam(defaultValue = "10") int pageSize) {

	    // attach request param to filter object (existing logic)
	    inspectionFilters.setProcessType(processType);

	    // Call existing service (NO CHANGE)
	    List<InspectionCase_EntityDTO> fullList =
	            inspectionCaseService.getInspectionHistoryCases(inspectionFilters);

	    // Pageable object
	    Pageable pageable = PageRequest.of(pageNumber, pageSize);

	    int fromIndex = (int) pageable.getOffset();
	    int toIndex = Math.min(fromIndex + pageable.getPageSize(), fullList.size());

	    // Apply pagination without modifying the list from service
	    List<InspectionCase_EntityDTO> paginated =
	            (fromIndex >= fullList.size()) ? List.of() : fullList.subList(fromIndex, toIndex);

	    Page<InspectionCase_EntityDTO> page =
	            new PageImpl<>(paginated, pageable, fullList.size());

	    return ResponseEntity.ok(page);
	}

//	@GetMapping("/topFive-categories")
//	public ResponseEntity<Map<String, Long>> getTopNegativeCategories(
//			@RequestParam(value = "ins_Type_Id", required = false) Long ins_Type_Id) {
//		Map<String, Long> response = categoriesSummaryReportService.getTopNegativeCategories(ins_Type_Id);
//		return ResponseEntity.ok(response);
//	}
	
	@GetMapping("/topFive-categories")
	public ResponseEntity<Map<String, Long>> getTopNegativeCategories(
	        @RequestParam(value = "ins_Type_Id", required = false) Long ins_Type_Id,
	        @RequestParam(value = "processType", required = false) String processType) {

	    Map<String, Long> response = categoriesSummaryReportService
	            .getTopNegativeCategories(ins_Type_Id, processType);

	    return ResponseEntity.ok(response);
	}

//	@GetMapping("/topTen-negative-observations")
//	public ResponseEntity<List<TopTenNegativeObservationsDTO>> getTopTenNegativeObservations(
//			@RequestParam(value = "ins_Type_Id", required = false) Long ins_Type_Id) {
//
//		List<TopTenNegativeObservationsDTO> response = categoriesSummaryReportService
//				.getTop10NegativeObservations(ins_Type_Id);
//		return ResponseEntity.ok(response);
//	}
	
	@GetMapping("/topTen-negative-observations")
	public ResponseEntity<Page<TopTenNegativeObservationsDTO>> getTopTenNegativeObservations(
	        @RequestParam(value = "ins_Type_Id", required = false) Long ins_Type_Id,
	        @RequestParam(value = "processType", required = false) String processType,
	        @RequestParam(defaultValue = "0") int pageNumber,
	        @RequestParam(defaultValue = "10") int pageSize) {

	    // Fetch full list from service — NO CHANGE
	    List<TopTenNegativeObservationsDTO> fullList =
	            categoriesSummaryReportService.getTop10NegativeObservations(ins_Type_Id, processType);

	    // Pagination creation — SAME LOGIC AS FIRST API
	    Pageable pageable = PageRequest.of(pageNumber, pageSize);

	    int fromIndex = (int) pageable.getOffset();
	    int toIndex = Math.min(fromIndex + pageable.getPageSize(), fullList.size());

	    List<TopTenNegativeObservationsDTO> paginated =
	            (fromIndex >= fullList.size()) ? List.of() : fullList.subList(fromIndex, toIndex);

	    Page<TopTenNegativeObservationsDTO> page =
	            new PageImpl<>(paginated, pageable, fullList.size());

	    return ResponseEntity.ok(page);
	}

	@GetMapping("/getAllControlType")
	public Page<ControlType> getAllControlTypes(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return controlTypeService.getAllControlTypes(pageable);

	}

	@PostMapping("/AddorEditControlType")
	public ResponseEntity<AISResponseDTO> addControlType(@RequestBody ControlTypeDTO controlTypeDTO) {
		AISResponseDTO response = new AISResponseDTO();

		try {
			if ("add".equalsIgnoreCase(controlTypeDTO.getAction())) {
				ControlType existingControlType = controlTypeService
						.findByControlTypeName(controlTypeDTO.getControlTypeName());

				if (existingControlType != null) {
					response.setStatus("Failure");
					response.setMessage(
							"Control Type with the name '" + controlTypeDTO.getControlTypeName() + "' already exists.");
				} else {
					ControlType savedControlType = controlTypeService.saveControlType(controlTypeDTO);
					response.setStatus("Success");
					response.setMessage(
							"Control Type '" + savedControlType.getControlTypeName() + "' added successfully.");
				}
			} else if ("edit".equalsIgnoreCase(controlTypeDTO.getAction())) {
				if (controlTypeDTO.getControlTypeId() == null) {
					response.setStatus("Failure");
					response.setMessage("Control Type ID must be provided for editing.");
				} else {
					ControlType updatedControlType = controlTypeService.updateControlType(controlTypeDTO);
					if (updatedControlType != null) {
						response.setStatus("Success");
						response.setMessage(
								"Control Type '" + updatedControlType.getControlTypeName() + "' updated successfully.");
					} else {
						response.setStatus("Failure");
						response.setMessage(
								"Control Type with ID '" + controlTypeDTO.getControlTypeId() + "' not found.");
					}
				}
			} else {
				response.setStatus("Failure");
				response.setMessage("Invalid action. Please specify 'add' or 'edit'.");
			}
		} catch (IllegalArgumentException e) {
			response.setStatus("Failure");
			response.setMessage(e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/AddEditUserGroup")
	public ResponseEntity<Map<String, String>> processUserGroup(@RequestBody UserGroupDTO userGroupDTO) {
		Map<String, String> response = new HashMap<>();
		try {
			String message = userGroupService.processUserGroup(userGroupDTO);
			response.put("status", "success");
			response.put("message", message);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("status", "failure");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getAllUserGroups")
	public ResponseEntity<Page<AllUserGroupDTO>> getAllUserGroups(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<AllUserGroupDTO> userGroups = userGroupService.getAllUserGroups(pageable);
		return ResponseEntity.ok(userGroups);
	}

	@GetMapping("/getUsersByGroupId/{groupId}")
	public List<UserGroupDTO> getUsersByGroupId(@PathVariable Long groupId) {
		return userGroupService.getUsersByGroupId(groupId);
	}

	@GetMapping("/getControlTypeWithInspectionDetails/{controlTypeId}")
	public ResponseEntity<Map<String, Object>> getControlTypeDetails(@PathVariable long controlTypeId) {
		return ResponseEntity.ok(controlTypeService.getControlTypeWithInspectionDetails(controlTypeId));
	}

	@GetMapping("/getUserGroupForEditById/{groupId}")
	public ResponseEntity<UsersGroupDTO> getUserGroupDetails(@PathVariable Long groupId) {
		UsersGroupDTO dto = userGroupForEditByIdService.getUserGroupDetails(groupId);
		if (dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}

	// Get all the UserGroups with UserGroupID, UserGroupName, isActive and get the
	// count of Users linked to that UserGroup

	@GetMapping("/getUserGroupsForAssign")
	public List<UserGroupDTO> getUserGroupSummary() {
		return userGroupService.getUserGroupSummary();
	}

	// Get All ControlTypes Without Pagination
	@GetMapping("/getAllControlTypesForCase")
	public List<ControlType> getAllControlTypes() {
		return controlTypeService.getControlTypes();
	}

	@GetMapping("/getGroupcasesForLeadInspector/{leadId}")
	public List<InspectionCase_EntityDTO> getGroupCaseForLeadInspector(@PathVariable("leadId") Long leadId) {
		return getGroupCasesForLeadService.getGroupCaseForLeadInspector(leadId);
	}

	// get All InspectionPlan details
	@GetMapping("/getAllInspectionPlanDetails")
	public Page<InspectionPlanDTO> getAllInspectionPlans(@RequestParam(defaultValue = "0", required = false) int page,
			@RequestParam(defaultValue = "10", required = false) int size) {

		Pageable pageable = PageRequest.of(page, size);
		return inspectionPlanService.getAllInspectionPlans(pageable);
	}

	@PostMapping("assignCategory/{inspectionID}/{action}")
	public ResponseEntity<String> handleInspectionMapping(@PathVariable Long inspectionID, @PathVariable String action,
			@RequestBody List<AssignRequestDTO> requestDTOList) {

		// Validate action (must be "SAVE" or "SUBMIT")
		if (!action.equalsIgnoreCase("SAVE") && !action.equalsIgnoreCase("SUBMIT")) {
			return ResponseEntity.badRequest().body("Invalid action! Use 'SAVE' or 'SUBMIT'.");
		}

		// Call service to process the list of requests
		String response = inspectionMappingService.assignInspection(inspectionID, action, requestDTOList);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getAllInspectionTypes/{controlTypeId}")
	public List<GetAllInspection_TypeDTO> getInspectionIdsAndNames(@PathVariable long controlTypeId) {
		return inspectionTypeService.getInspectionIdsAndNamesByControlTypeId(controlTypeId);
	}

	@PostMapping("/createInspectionPlan")
	public ResponseEntity<Map<String, Object>> createInspectionPlan(
			@RequestBody CreateInspectionPlanRequestDTO requestDTO) {
		Map<String, Object> response = new HashMap<>();

		try {
			if ("edit".equalsIgnoreCase(requestDTO.getAction())) {
				// Handle edit action
				response = inspectionPlanService.editInspectionPlan(requestDTO);
				return ResponseEntity.ok(response);
			} else {
				// Handle create action (default)
				if (inspectionPlanService.existsByInspectionPlanName(requestDTO.getInspectionPlanName())) {
					response.put("status", "error");
					response.put("message",
							"Inspection Plan with the same name already exists: " + requestDTO.getInspectionPlanName());
					return ResponseEntity.badRequest().body(response);
				}

				InspectionPlan inspectionPlan = new InspectionPlan();
				inspectionPlan.setInspectionPlanName(requestDTO.getInspectionPlanName());
				inspectionPlan.setReasonForInspectionPlan(requestDTO.getReasonForInspectionPlan());
				inspectionPlan.setDescription(requestDTO.getDescription());
				inspectionPlan.setInspectorType(requestDTO.getInspectorType());

				List<SelectedEntityDTO> selectedEntities = requestDTO.getSelectedEntities();
				LocalDate dateOfInspection = requestDTO.getDateOfInspection();
				String createdBy = requestDTO.getCreatedBy();

				response = inspectionPlanService.createInspectionPlan(inspectionPlan, selectedEntities,
						dateOfInspection, createdBy);

				return ResponseEntity.ok(response);
			}
		} catch (IllegalArgumentException e) {
			response.put("status", "error");
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "An unexpected error occurred: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Get the assigned inspector with checklist
	@GetMapping("/getAssignedInspectorChecklist/{inspectionID}")
	public ResponseEntity<Object> getChecklistByInspectionID(@PathVariable Long inspectionID,
			@RequestParam(required = false) String inspectorID) {
		Object response = checklistService.getChecklistByInspectionID(inspectionID, inspectorID);
		return ResponseEntity.ok(response);
	}

	// Get the Checklists based on inspectorId
	@GetMapping("/getChecklistForInspector/{inspectorID}")
	public ResponseEntity<List<ChecklistDTO>> getChecklistByInspectorID(@PathVariable String inspectorID) {
		List<ChecklistDTO> response = checklistService.getChecklistByInspectorID(inspectorID);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/getAddressByEntity/{entityId}")
	public ResponseEntity<EntityResponseDTO> getAllEntityById(@PathVariable String entityId) {
		EntityResponseDTO response = newEntityService.getEntityAllDetailsById(entityId);
		if (response != null) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/getInspectionplan/{inspectionPlanId}")
	public ResponseEntity<CreateInspectionPlanRequestDTO> getInspectionPlanWithCasesById(
			@PathVariable String inspectionPlanId) {
		CreateInspectionPlanRequestDTO responseDTO = inspectionPlanService
				.getInspectionPlanWithCasesById(inspectionPlanId);
		if (responseDTO == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(responseDTO);
	}

	// getInspectionCases for Periodicity//////

	@GetMapping("/getInspectionCases")
	public List<InspectionCaseResponseDTO> getInspectionCases(
			@RequestParam(value = "createdBy", required = false, defaultValue = "all") String createdBy,
			@RequestParam(value = "inspectorSource", required = false, defaultValue = "all") String inspectorSource,
			@RequestParam(value = "createdDateFilter", required = false, defaultValue = "all") String createdDateFilter) {
		return inspectionCasesService.getInspectionCases(createdBy, inspectorSource, createdDateFilter);
	}

	@GetMapping("/periodicity-details")
	public ResponseEntity<PeriodicityDetailsDTO> getPeriodicityDetails(@RequestParam("entityId") String entityId,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {

		PeriodicityDetailsDTO dto = periodicityDetailsByInspectionTypeService.getPeriodicityDetailsByEntityId(entityId);

		// Compute ETag using a stable hash (consider upgrading to SHA-256 if needed)
		String eTag = "\"" + Integer.toHexString(dto.hashCode()) + "\"";

		logger.info("periodicity-details - entityId: {}, computed ETag: {}, received If-None-Match: {}", entityId, eTag,
				ifNoneMatch);

		// Conditional response based on ETag match
		if (eTag.equals(ifNoneMatch)) {
			logger.info("ETag matched for entityId {}. Returning 304 Not Modified.", entityId);
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
		}

		logger.info("ETag mismatch or absent. Returning fresh data for entityId {}.", entityId);
		return ResponseEntity.ok().eTag(eTag).body(dto);
	}

	@GetMapping("/getScheduleDetailsWithNextScheduledDate")
	public ResponseEntity<PeriodicityDTO> getNextSchedule(@RequestParam("ins_type_id") long insTypeId,
			@RequestParam("entityId") String entityId) {
		try {
			PeriodicityDTO dto = inspectionTypeService.getNextScheduleForEntity(insTypeId, entityId);
			return ResponseEntity.ok(dto);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/getSkipDetails")
	public Map<String, Object> runJobManually() {
		return jobWorker.handleInspectionScheduleJob();
	}

	@PostMapping("/updateSchedule")
	public ResponseEntity<AISResponseDTO> updateSchedule(@RequestParam("ins_type_id") Long inspectionTypeId,
			@RequestParam("entityId") String entityId, @RequestBody ScheduleUpdateDTO scheduleUpdateDTO) {

		AISResponseDTO response = new AISResponseDTO();
		try {
			inspectionScheduleService.updateSchedule(inspectionTypeId, entityId, scheduleUpdateDTO);

			response.setStatus("SUCCESS");
			response.setMessage("Schedule updated successfully for entityId: " + entityId);
			response.setInspectionTypeId(inspectionTypeId);
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			response.setStatus("FAILURE");
			response.setMessage("Invalid request: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);

		} catch (Exception e) {
			response.setStatus("FAILURE");
			response.setMessage("Error updating schedule: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getskipdetailsbyInspectiontype/{id}")
	public ScheduleDTO getSchedule(@PathVariable("id") Long id) {
		return inspectionTypeScheduleService.getScheduleById(id);
	}

	@GetMapping("/api/templates")
	public List<TemplateSummaryDTO> getTemplates() {
		return templateService.getAllTemplates();
	}

	@PostMapping(value = "/adp/startADPAISProcess", consumes = "multipart/form-data")
	public ResponseEntity<Map<String, Object>> startADPAISProcess(
			@RequestPart(value = "files", required = false) MultipartFile[] inspectorUploadedDocument) {

		Map<String, Object> response = new HashMap<>();
		
		// Read all the files and add into GCP.
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode infoArray = mapper.createArrayNode();

		// 🔹 Process uploaded files
		if (inspectorUploadedDocument != null) {
			for (MultipartFile file : inspectorUploadedDocument) {
				if (file != null && !file.isEmpty()) {
					ObjectNode fileNode = mapper.createObjectNode();
					fileNode.put("documentType", file.getOriginalFilename());
					try {
						fileNode.put("document", Base64.getEncoder().encodeToString(file.getBytes()));
					} catch (IOException e) {
						throw new RuntimeException("Error reading file: " + file.getOriginalFilename(), e);
					}
					infoArray.add(fileNode);
				}
			}
		}

		try {
			// Prepare variables for Zeebe
			Map<String, Object> variables = new HashMap<>();
			variables.put("informationForTheEntity", infoArray);

			ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
					.bpmnProcessId("AISProcessV3").latestVersion().variables(variables).send().join();

			long businessKey = processInstanceEvent.getProcessInstanceKey();

			// Also store businessKey back into the process instance
			zeebeClient.newSetVariablesCommand(businessKey).variables(Map.of("BusinessKey", businessKey)).send().join();

			response.put("businessKey", businessKey);

			//  Conditional message using utility methods
			if (infoArray.isEmpty()) {
				response.putAll(failure("No files uploaded", "Upload and verification failed"));
			} else {
				response.putAll(success("Documents uploaded successfully", "Upload and verification completed"));
			}

			logger.info("Got all the details from the Response object {}", response);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.putAll(failure("Process start failed", e.getMessage()));
			return ResponseEntity.status(500).body(response);
		}
	}

	// Utility for consistent failure response 
	private Map<String, Object> failure(String message, String reason) {
		Map<String, Object> fail = new HashMap<>();
		fail.put("status", "Failure");
		fail.put("ErrorDescription", message);
		fail.put("previousOutcome", reason);
		return fail;
	}

	// ✅ Utility for consistent success response 
	private Map<String, Object> success(String message, String outcome) {
		Map<String, Object> success = new HashMap<>();
		success.put("status", "Success");
		success.put("message", message);
		success.put("outcome", outcome);
		return success;
	}

//	@PostMapping("/validateInspection")
//	public Map<String, Object> validateInspection(@RequestBody List<Map<String, Object>> idpOutputs) {
//		return validateInspectionTypeWorker.performValidation(idpOutputs);
//	}
	
	@GetMapping("/getIDPAISummary")
	 public List<IDPSummaryResponseDTO> getSummary(
	            @RequestParam(required = false) String entityName,
	            @RequestParam(required = false) String inspectionType,
	            @RequestParam(required = false) Long inspectionID,
	            @RequestParam(required = false) String source
	    ) {
	        return idpaiService.search(entityName, inspectionType, inspectionID, source);
	    }

	 
	 @GetMapping("/api/ai/recommendations")
	 public ResponseEntity<Map<String, Object>> getAiRecommendations(
	         @RequestParam("inspectionId") Long inspectionId) {

	     Map<String, Object> response = new HashMap<>();

	     // --------------------------------------------
	     // 1️⃣ inspectionId → used for DB operations
	     // --------------------------------------------
	     Long dbInspectionId = inspectionId;

	     // Example: If you want to check DB status (optional)
	     /*
	     boolean active = inspectionCaseService.isActive(dbInspectionId);
	     if (!active) {
	         response.put("inspectionId", dbInspectionId);
	         response.put("active", false);
	         response.put("message", "Inspection is not active");
	         return ResponseEntity.ok(response);
	     }
	     */

	     // --------------------------------------------
	     // 2️⃣ inspectionId → also used as Zeebe processInstanceKey
	     // --------------------------------------------
	     Long processInstanceKey = inspectionId;

	     // Fetch from Camunda Operate
	     Integer riskScore = operateService.getRiskScore(processInstanceKey);

	     if (riskScore == null) {
	         response.put("inspectionId", dbInspectionId);
	         response.put("active", true);
	         response.put("message", "riskScore variable not found or invalid");
	         return ResponseEntity.ok(response);
	     }

	     // --------------------------------------------
	     // 3️⃣ AI Logic
	     // --------------------------------------------
	     String recommendation;
	     if (riskScore >= 80) {
	         recommendation = "Schedule Follow up Inspection";
	     } else if (riskScore >= 70) {
	         recommendation = "Assign case to compliance officer";
	     } else {
	         recommendation = "No AI recommendation";
	     }

	     // --------------------------------------------
	     // 4️⃣ Response
	     // --------------------------------------------
	     response.put("inspectionId", dbInspectionId);
	     response.put("active", true);
	     response.put("riskScore", riskScore);
	     response.put("recommendation", recommendation);

	     return ResponseEntity.ok(response);
	 }

	 @PostMapping("/inspection/{inspectionId}/ai-decision")
	 public ResponseEntity<?> updateAiDecision(
	         @PathVariable Long inspectionId,
	         @RequestParam String action) {

	     Map<String, Object> response = new HashMap<>();

	     try {

	         // ---------------------------
	         // 1️⃣ Set AIrecommendation variable
	         // ---------------------------
	         boolean aiValue = action.equalsIgnoreCase("continue");

	         // ---------------------------
	         // 2️⃣ Fetch risk score from Operate
	         // ---------------------------
	         Integer riskScore = operateService.getRiskScore(inspectionId);

	         String recommendation;

	         if (aiValue) { // continue
	             if (riskScore == null) {
	                 recommendation = "No AI recommendation";
	             } else if (riskScore >= 80) {
	                 recommendation = "Schedule Follow up Inspection";
	             } else if (riskScore >= 70) {
	                 recommendation = "Assign case to compliance officer";
	             } else {
	                 recommendation = "No AI recommendation";
	             }
	         } else { // skip
	             recommendation = "Did not followed the recommendations";
	         }

	         // ---------------------------
	         // 3️⃣ Update variables in Camunda
	         // ---------------------------
	         zeebeClient
	             .newSetVariablesCommand(inspectionId)
	             .variables(Map.of(
	                 "AIrecommendation", aiValue,
	                 "recommendation", recommendation   // <---- NEW LOGIC
	             ))
	             .send()
	             .join();

	         // ---------------------------
	         // 4️⃣ Update DB recommendation
	         // ---------------------------
	         InspectionCase inspectionCase =
	        	        inspectionCaseDAO.getInspectionCaseById(inspectionId.longValue());
	         inspectionCase.setRecommendation(recommendation);
	         inspectionCaseDAO.updateInspectionCase(inspectionCase);

	         // ---------------------------
	         // 5️⃣ Response
	         // ---------------------------
	         response.put("inspectionId", inspectionId);
	         response.put("AIrecommendation", aiValue);
	         response.put("riskScore", riskScore);
	         response.put("savedRecommendation", recommendation);

	         return ResponseEntity.ok(response);

	     } catch (Exception e) {
	         response.put("error", e.getMessage());
	         return ResponseEntity.internalServerError().body(response);
	     }
	 }
	 
	 
	 @PostMapping(value = "/download", produces = "application/pdf")
	    public ResponseEntity<byte[]> downloadReport(@RequestBody ReportRequestDTO request) {

	        byte[] pdfBytes = pdfReportService.generatePdf(request);

	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=AI_Inspection_Report.pdf")
	                .contentType(MediaType.APPLICATION_PDF)
	                .body(pdfBytes);
	    }
	    

	 
	    

	    @GetMapping("/PDFReport/{inspectionId}")
	    public ResponseEntity<byte[]> getPdfByInspectionId(
	            @PathVariable Long inspectionId) {

	        try {
	            // 🔹 Fetch only pdf_data column
	            byte[] pdfData = entityManager.createQuery(
	                    "SELECT p.pdfData FROM PdfReport p " +
	                    "WHERE p.inspectionCase.inspectionID = :id",
	                    byte[].class
	            )
	            .setParameter("id", inspectionId)
	            .getSingleResult();

	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_PDF)
	                    .contentLength(pdfData.length)
	                    .header(
	                        HttpHeaders.CONTENT_DISPOSITION,
	                        "inline; filename=inspection-" + inspectionId + ".pdf"
	                    )
	                    .body(pdfData);

	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	    }
	    
	    @PostMapping("/CompleteComplianceOfficer/{inspectionId}/complete")
	    public ResponseEntity<?> completeComplianceOfficerTask(@PathVariable long inspectionId) {

	        Map<String, Object> response = new HashMap<>();

	        try {
	            // 1️⃣ Get active task ID
	            String taskId = tasklistservice.getActiveTaskID(String.valueOf(inspectionId));

	            if (taskId == null || taskId.isEmpty()) {
	                throw new RuntimeException("No active task found for inspectionId: " + inspectionId);
	            }

	            // 2️⃣ Complete task (reuse existing method)
	            String result = tasklistservice.CompleteTaskByID(taskId, new HashMap<>());

	            response.put("inspectionId", inspectionId);
	            response.put("taskId", taskId);
	            response.put("message", "Compliance Officer task completed successfully");
	            response.put("taskResponse", result);

	            return ResponseEntity.ok(response);

	        } catch (Exception e) {
	            response.put("error", e.getMessage());
	            return ResponseEntity.internalServerError().body(response);
	        }
	    }



	  
}
