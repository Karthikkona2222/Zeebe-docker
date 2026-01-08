package com.aaseya.AIS.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aaseya.AIS.Model.ClaimRequestDocuments;
import com.aaseya.AIS.Model.HealthCarePreInspectionChecklist;
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dto.CSRDocumentDTO;
import com.aaseya.AIS.dto.ClaimCaseDTO;
import com.aaseya.AIS.dto.ClaimCaseResponseDTO;
import com.aaseya.AIS.dto.ClaimResponseDTO;
import com.aaseya.AIS.dto.DashboardCountsDTO;
import com.aaseya.AIS.dto.StartHealthcareRequestDTO;
import com.aaseya.AIS.service.AISService;
import com.aaseya.AIS.service.CategoriesSummaryReportService;
import com.aaseya.AIS.service.ChecklistCategoryService;
import com.aaseya.AIS.service.ChecklistItemService;
import com.aaseya.AIS.service.ChecklistService;
import com.aaseya.AIS.service.ClaimCaseService;
import com.aaseya.AIS.service.ClaimRequestDocumentsService;
import com.aaseya.AIS.service.ClaimReviewService;
import com.aaseya.AIS.service.ControlTypeService;
import com.aaseya.AIS.service.EntityInspectionReportService;
import com.aaseya.AIS.service.HealthCareChecklistService;
import com.aaseya.AIS.service.HealthcareService;
import com.aaseya.AIS.service.InspectionCaseService;
import com.aaseya.AIS.service.InspectionMappingService;
import com.aaseya.AIS.service.InspectionPeriodicityService;
import com.aaseya.AIS.service.InspectionPlanService;
import com.aaseya.AIS.service.InspectionSLAService;
import com.aaseya.AIS.service.InspectionService;
import com.aaseya.AIS.service.InspectionTypeService;
import com.aaseya.AIS.service.NewEntityService;
import com.aaseya.AIS.service.OperateService;
import com.aaseya.AIS.service.PolicyDetailsService;
import com.aaseya.AIS.service.PoolService;
import com.aaseya.AIS.service.PreInspectionChecklistService;
import com.aaseya.AIS.service.SaveSubmitPreInspectionChecklistService;
import com.aaseya.AIS.service.SegmentService;
import com.aaseya.AIS.service.SkillService;
import com.aaseya.AIS.service.SubProcessTaskService;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.zeebe.client.ZeebeClient;

@CrossOrigin("*")
@RestController
public class HealthClaimController {

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
	private ClaimRequestDocumentsService claimRequestDocumentsService;

	@Autowired
	private PolicyDetailsService policyDetailsService;

	@Autowired
	private HealthcareService healthcareService;

	@Autowired
	private OperateService operateService;

	@Autowired
	private PoolService poolService;
	
	@Autowired
	private ClaimCaseService claimCaseService;
	
	@Autowired
    private HealthCareChecklistService healthCareChecklistService;
	
	@Autowired
	private SubProcessTaskService subProcessTaskService;
	
	@Autowired
	private ClaimReviewService claimReviewService;

	@PostMapping("/upload/{policyId}")
	public ResponseEntity<?> uploadDocuments(@PathVariable String policyId,
			@RequestParam("documents") MultipartFile[] files) {
		try {
			List<ClaimRequestDocuments> savedDocs = new ArrayList<>();
			for (MultipartFile file : files) {
				ClaimRequestDocuments doc = new ClaimRequestDocuments();
				doc.setDocumentType(file.getOriginalFilename()); // Use actual file name
				doc.setDocument(file.getBytes());
				ClaimRequestDocuments savedDoc = claimRequestDocumentsService.saveDocumentForPolicy(policyId, doc);
				savedDocs.add(savedDoc);
			}
			return ResponseEntity.ok(savedDocs);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// This is api is using in search functionality as well as for at Claimant
	// details screen//
	@GetMapping("/getAllPolicyBasedOnPolicyById")
	public ResponseEntity<List<PolicyDetails>> search(@RequestParam(required = false) String policyId,
			@RequestParam(required = false) String customerName) {
		List<PolicyDetails> policies = claimRequestDocumentsService.getPolicyByIdOrCustomerName(policyId, customerName);
		return ResponseEntity.ok(policies);
	}

	// This api is using at table to show all the policies with pagination//
	@GetMapping("/GetAllPolicyDetails")
	public ResponseEntity<Page<PolicyDetails>> getAllPolicies(
			@RequestParam(defaultValue = "0", required = false) int page,
			@RequestParam(defaultValue = "10", required = false) int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<PolicyDetails> policies = policyDetailsService.getAllPolicies(pageable);
		return ResponseEntity.ok(policies);
	}

	// Api is using at after CSR uploads documents data, input when submit//
	@PostMapping(value = "/starthealthclaimprocess", consumes = "multipart/form-data")
	public ResponseEntity<Map<String, Object>> startHealthcare(@RequestPart("policyData") String policyDataJson,
			@RequestPart("documents") List<MultipartFile> documents) {

		Map<String, Object> response = new HashMap<>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			StartHealthcareRequestDTO requestDTO = objectMapper.readValue(policyDataJson,
					StartHealthcareRequestDTO.class);

			List<CSRDocumentDTO> csrDocs = documents.stream().map(file -> {
				try {
					CSRDocumentDTO doc = new CSRDocumentDTO();
					doc.setDocumentType(file.getOriginalFilename());
					doc.setDocument(file.getBytes());
					return doc;
				} catch (Exception e) {
					throw new RuntimeException("Error reading file: " + file.getOriginalFilename(), e);
				}
			}).toList();

			requestDTO.setDocuments(csrDocs);

			Long businessKey = healthcareService.startHealthcareProcess(requestDTO);
			Long processInstanceKey = businessKey;

			// Fetch DischargeSummary from Operate by processInstanceKey (businessKey)
			Map<String, Object> dischargeSummary = operateService.getDischargeSummaryWithPolling(processInstanceKey,
					Duration.ofSeconds(8), Duration.ofMillis(800));

			response.put("DischargeSummary", dischargeSummary); // may be null if worker hasn’t written it or Operate
																// hasn’t indexed yet [1]
			response.put("status", "Success");
			response.put("message", "Healthcare process started successfully.");
			response.put("businessKey", businessKey);
			response.put("DischargeSummary", dischargeSummary); // will contain billedAmount, diagnosis, procedures,
																// meds, etc. [1]
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "Failure");
			response.put("message", "Process start failed: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

	@PostMapping("/completeByProcessInstanceKey")
	public String completeTaskByProcessInstanceKey(@RequestParam String processInstanceKey) {
		// Pass null or empty list since variables are not provided
		return tasklistservice.completeTaskByProcessInstanceKey(processInstanceKey, null);
	}

	@PostMapping(value = "/combine-zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> combineZipFiles(@RequestParam("file") MultipartFile zipFile) {
		if (zipFile.isEmpty()) {
			return ResponseEntity.badRequest().body("No file uploaded");
		}

		StringBuilder combinedData = new StringBuilder();

		try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream(), StandardCharsets.UTF_8)) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
					String line;
					while ((line = reader.readLine()) != null) {
						combinedData.append(line).append("\n");
					}
				}
				zis.closeEntry();
			}
		} catch (IOException e) {
			return ResponseEntity.status(500).body("Error processing zip file: " + e.getMessage());
		}

		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(combinedData.toString());
	}

	// get cases of that pool based on pool name works for all pools//
	@GetMapping("/Getclaimcasesforpool")
	public ResponseEntity<List<ClaimCaseDTO>> getClaimCasesByPoolName(@RequestParam String poolName) {
		List<ClaimCaseDTO> claimCaseDTOs = poolService.getClaimCasesByPoolName(poolName);
		if (claimCaseDTOs.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(claimCaseDTOs);
	}

	// Assign to him self based on username and also works for all pools//
	@PostMapping("/assigncasetomyself")
	public ResponseEntity<String> assignUserToClaim(@RequestParam Long claimId, @RequestParam String poolName,
			@RequestParam String username) {

		boolean updated = poolService.assignUserToClaimByPool(claimId, poolName, username);

		if (updated) {
			return ResponseEntity.ok("User assigned successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to assign user: Invalid poolName or claimId");
		}
	}

	@GetMapping("/dashboard")
	 public ResponseEntity<DashboardCountsDTO> getDashboardCountsByEmail(@RequestParam String email) {
        DashboardCountsDTO countsDTO = poolService.getDashboardCountsByEmail(email);
        return ResponseEntity.ok(countsDTO);
    }
	
	@GetMapping("/getAssignedcases")
    public ResponseEntity<List<ClaimCaseResponseDTO>> getClaimCases(@RequestParam String emailID) {
        List<ClaimCaseResponseDTO> claimCases = claimCaseService.fetchClaimCases(emailID);
        return ResponseEntity.ok(claimCases);
    }

	
	// Get all Pre-Inspection checklist items (flat list)
    @GetMapping("/preinspectionitems")
    public List<HealthCarePreInspectionChecklist> getAllPreInspectionItems() {
        return healthCareChecklistService.getAllPreInspectionItems();
    }
    
    @GetMapping("/checklistitems")
    public List<Object> getAllChecklistItems() {
        return healthCareChecklistService.getAllChecklistItems();
    }

    
    @GetMapping("/documentssummary/{claimId}")
    public Map<String, Object> getDischargeSummary(@PathVariable Long claimId) {
        Map<String, Object> dischargeSummary = claimCaseService.fetchDischargeSummary(claimId);
        if (dischargeSummary == null || dischargeSummary.isEmpty()) {
            return Map.of("error", "No discharge summary found for claimId: " + claimId);
        }
        return dischargeSummary;
    }
    
    
    
    @PostMapping("/completeByClaimIdatinspector")
    public String completeTaskByClaimId(
            @RequestParam Long claimId,
            @RequestBody Map<String, Object> payload) {

        return subProcessTaskService.completeTaskByClaimId(claimId, payload);
    }

    
    @GetMapping("/GetApproverandReviewerChecklists")
    public ClaimResponseDTO getClaimDetails(
            @RequestParam("claimId") Long claimId,
            @RequestParam(name = "pool", required = false, defaultValue = "inspector") String pool) {
 
        return claimReviewService.getClaimWithChecklist(claimId);
    }
    
    @PostMapping("/completeByClaimIdAtReviewer")
    public String completeTaskByClaimIdAtReviewer(
            @RequestParam Long claimId,
            @RequestBody Map<String, Object> payload) {

        return subProcessTaskService.completeTaskByClaimIdAtReviewer(claimId, payload);
    }
    
    @PostMapping("/completeByClaimIdAtApprover")
    public String completeTaskByClaimIdAtApprover(
            @RequestParam Long claimId,
            @RequestBody Map<String, Object> payload) {

        return subProcessTaskService.completeTaskByClaimIdAtApprover(claimId, payload);
    }


}
