package com.aaseya.AIS.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.NewEntity;
import com.aaseya.AIS.Model.Segment;
import com.aaseya.AIS.Model.SubSegment;
import com.aaseya.AIS.Model.Zone;
import com.aaseya.AIS.dao.EntityDAO;
import com.aaseya.AIS.dao.InspectionCaseDAO;
import com.aaseya.AIS.dao.InspectionTypeDAO;
import com.aaseya.AIS.dao.ZoneDAO;
import com.aaseya.AIS.dto.ApproverDashboardDTO;
import com.aaseya.AIS.dto.EntitiesInspectionTypeDTO;
import com.aaseya.AIS.dto.EntityDTO;
import com.aaseya.AIS.dto.EntityDetailsDTO;
import com.aaseya.AIS.dto.EntityInformationDTO;
import com.aaseya.AIS.dto.EntityRegistrationDTO;
import com.aaseya.AIS.dto.EntityResponseDTO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;
import com.aaseya.AIS.dto.InspectionTypeDTO;
import com.aaseya.AIS.dto.InspectorSourceStatusCountDTO;
import com.aaseya.AIS.dto.StatusCountResponseDTO;
import com.aaseya.AIS.dto.ZoneDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.aaseya.AIS.Model.InspectionCase;

@Service
public class NewEntityService {

	@Autowired
	private EntityDAO entityDAO;

	@Autowired
	private ZoneDAO zoneDAO;

	@Autowired
	private InspectionCaseDAO inspectionCaseDAO;

	@Autowired
	private InspectionTypeDAO inspectionTypeDAO;

	public List<EntityDTO> getName() {
		List<EntityDTO> EntityNameList = new ArrayList<EntityDTO>();
		List<NewEntity> name = entityDAO.getAllEntityName();

		for (NewEntity entity : name) {
			EntityDTO entityDTO = new EntityDTO();
			entityDTO.setEntityid(entity.getEntityid());
			entityDTO.setName(entity.getName());
			entityDTO.setAddress(entity.getAddress());
			entityDTO.setFacility(entity.getFacility());
			entityDTO.setType(entity.getType());

			EntityNameList.add(entityDTO);
		}
		return EntityNameList;
	}

	public List<String> getEntityNames() {
		return entityDAO.getAllEntityNames();
	}

	// public Inspection_Type getInspectionTypeById(Long id) {
	// TODO Auto-generated method stub
	// return null;
	// }

	public Optional<NewEntity> getAllEntityNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<InspectionCase_EntityDTO> getMycasesForManager(String createdBy, String dueDateFilter) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = new ArrayList<>();
			if (dueDateFilter.equalsIgnoreCase("all"))
				inspectionCases = inspectionCaseDAO.getMycasesForManager(createdBy);
			else if (dueDateFilter.equalsIgnoreCase("month"))
				inspectionCases = inspectionCaseDAO.getMyCasesForManagerByDate(createdBy, LocalDate.now().minusDays(30),
						LocalDate.now());
			else if (dueDateFilter.equalsIgnoreCase("week"))
				inspectionCases = inspectionCaseDAO.getMyCasesForManagerByDate(createdBy, LocalDate.now().minusDays(7),
						LocalDate.now());
			else
				inspectionCases = inspectionCaseDAO.getMyCasesForManagerByDate(createdBy, LocalDate.now(),
						LocalDate.now());
			System.out.println(inspectionCases);
			inspection = inspectionCases.stream()
					// .filter(inspectionCase -> filterByDueDate(inspectionCase.getDueDate(),
					// dueDateFilter))
					.map(this::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	private boolean filterByDueDate(LocalDate caseDueDate, String dueDateFilter) {
		if (caseDueDate == null) {
			return false;
		}
		LocalDate today = LocalDate.now();
		System.out.println(dueDateFilter);
		System.out.println(dueDateFilter.equals("week"));
		return dueDateFilter.equals("today") ? caseDueDate.equals(today)
				: dueDateFilter.equals("week") ? isWithinWeek(caseDueDate, today)
						: !dueDateFilter.equals("month") || isWithinMonth(caseDueDate, today); // no filtering for
																									// "ALL"
	}

	private boolean isWithinWeek(LocalDate date, LocalDate today) {
		LocalDate startOfWeek = getStartOfWeek(today);
		return date.isAfter(startOfWeek.minusDays(1)) && date.isBefore(startOfWeek.plusDays(7));
	}

	private boolean isWithinMonth(LocalDate date, LocalDate today) {
		LocalDate startOfMonth = getStartOfMonth(today);
		return date.isAfter(startOfMonth.minusDays(1)) && date.isBefore(startOfMonth.plusMonths(1));
	}

	private LocalDate getStartOfWeek(LocalDate date) {
		System.out.println(date.minusDays(date.getDayOfWeek().getValue() - 1));
		return date.minusDays(date.getDayOfWeek().getValue() - 1);
	}

	private LocalDate getStartOfMonth(LocalDate date) {
		return date.withDayOfMonth(1);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<InspectionCase_EntityDTO> getEntitiesByCreatedByAndStatus(String createdBy, String status) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = inspectionCaseDAO.findInspectionCasesByCreatedByAndStatus(createdBy,
					status);
			inspection = inspectionCases.stream().map(this::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)

	public InspectionCase_EntityDTO toDto(InspectionCase inspectionCase) {
		NewEntity entity = inspectionCase.getEntity();
		System.out.println(entity);
		if (entity == null) {
	        
	        System.out.println("Entity is null for InspectionCase ID: " + inspectionCase.getInspectionID());
	        
	        return new InspectionCase_EntityDTO(); 
	    }
		InspectionCase_EntityDTO dto = new InspectionCase_EntityDTO();
		dto.setInspectionID(inspectionCase.getInspectionID());
		dto.setInspector_source(inspectionCase.getInspector_source());
		dto.setStatus(inspectionCase.getStatus());
		dto.setDateOfInspection(inspectionCase.getDateOfInspection());
		dto.setEntityid(entity.getEntityid());
		dto.setName(entity.getName());
		dto.setReason(inspectionCase.getReason());
		dto.setRepresentative_email(entity.getRepresentativeEmail());
		dto.setEfforts("2.00");
		dto.setAssigned_inspector(inspectionCase.getInspectorID());
		dto.setReference_case("");
		dto.setCaseCreationType(inspectionCase.getCaseCreationType());
		dto.setLeadId(inspectionCase.getLeadId() != null ? inspectionCase.getLeadId() : null);
	    Long groupId = inspectionCase.getGroupId();
		if(null != groupId) {
		dto.setGroupId(inspectionCase.getGroupId());
		}
		
		Long leadId = inspectionCase.getLeadId(); 
	    if (leadId != null) {
	        dto.setLeadId(leadId); 
	    }
		dto.setInspection_type(inspectionCase.getInspectionType());
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// LocalDate date = LocalDate.parse(inspectionCase.getDue_date(), dateFormat);
		// try {
		// Parsing the string representation of date to Date object

		// dto.setDue_date((date.plusDays(2)));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		dto.setDue_date(inspectionCase.getDueDate());
		dto.setSize(entity.getSize());
		dto.setRepresentative_name(entity.getRepresentativeName());
		dto.setRepresentative_phoneno(entity.getRepresentativePhoneNo());
		SubSegment subSegement = entity.getSubSegment();
		if (subSegement != null) {
			dto.setSubSegment(subSegement.getName());
		}
		Segment segment = entity.getSegment();
		if (segment != null) {
			dto.setSegment(segment.getSegment_name());
		}
		if (inspectionCase.getIs_preinspection() != null) {

			dto.setIs_preinspection(inspectionCase.getIs_preinspection());
		}
		if (inspectionCase.getIs_preinspection_submitted() != null) {
			dto.setIs_preinspection_submitted(inspectionCase.getIs_preinspection_submitted());
		}

		return dto;
	}

	public EntityInformationDTO getAddressByEntity(String name) {
		return entityDAO.getAllAddressByEntity(name);
	}

	public List<String> getEntityNames1() {
		return entityDAO.getAllEntityNames();
	}

	public String saveEntity(NewEntity newEntity) {

		return entityDAO.saveEntity(newEntity);
	}

	//////
	// To get individual status names and its count and total count of status for
	// particular createdby//

	public StatusCountResponseDTO getStatusCountByCreatedBy(String createdBy) {
		Map<String, Long> statusCountMap = inspectionCaseDAO.getStatusCountByCreatedBy(createdBy);
		Long totalCount = statusCountMap.values().stream().mapToLong(Long::longValue).sum();
		// Merge the statuses
		Long pendingCount = statusCountMap.getOrDefault("in_progress", 0L)
				+ statusCountMap.getOrDefault("pending_approval", 0L) + statusCountMap.getOrDefault("new", 0L)
				+ statusCountMap.getOrDefault("reopened", 0L) + statusCountMap.getOrDefault("pending", 0L);
		Long reopenedCount = statusCountMap.getOrDefault("reopened", 0L);
		Long completedCount = statusCountMap.getOrDefault("completed", 0L);
		Map<String, Long> mergedStatusCountMap = new HashMap<>();
		mergedStatusCountMap.put("pending", pendingCount);
		mergedStatusCountMap.put("reopened", reopenedCount);
		mergedStatusCountMap.put("completed", completedCount);
		return new StatusCountResponseDTO(mergedStatusCountMap, totalCount);
	}
	/////////////////////////////////////////////////////////// //////// //// /

	// To get count and names of status, Inspector source based on createBy//
	public List<InspectorSourceStatusCountDTO> getStatusAndInspectorSourceCount(String createdBy) {
		List<InspectorSourceStatusCountDTO> responseList = new ArrayList<>();
		List<String> inspectorSources = inspectionCaseDAO.getInspectorSourcesByCreatedBy(createdBy);

		for (String inspectorSource : inspectorSources) {
			Long inspectorSourceCount = inspectionCaseDAO
					.getInspectorSourceCountByCreatedByAndInspectorSource(createdBy, inspectorSource);
			Map<String, Long> originalStatusCounts = inspectionCaseDAO
					.getStatusCountsByCreatedByAndInspectorSource(createdBy, inspectorSource);

			Map<String, Long> mergedStatusCounts = new HashMap<>();
			long notStarted = 0;
			long inProgressCount = 0;
			long pendingApproval = 0;
			long underApproval = 0;
			long completedCount = 0;
			long reopenedCount = 0;

			for (Map.Entry<String, Long> entry : originalStatusCounts.entrySet()) {
				String status = entry.getKey();
				Long count = entry.getValue();

				if ("pending".equalsIgnoreCase(status) || "new".equalsIgnoreCase(status)) {
					notStarted += count;
				}

				if ("in_progress".equalsIgnoreCase(status)) {
					inProgressCount += count;
				}

				if ("completed".equalsIgnoreCase(status)) {
					completedCount += count;
				}

				if ("reopened".equalsIgnoreCase(status)) {
					reopenedCount += count;
				}

				if ("pending_approval".equalsIgnoreCase(status)) {
					pendingApproval += count;
				}

				if ("under_approval".equalsIgnoreCase(status)) {
					pendingApproval += count;
				}
			}

			mergedStatusCounts.put("pending_approval", pendingApproval);
			mergedStatusCounts.put("under_approval", underApproval);
			mergedStatusCounts.put("not_started", notStarted);
			mergedStatusCounts.put("in_progress", inProgressCount);
			mergedStatusCounts.put("completed", completedCount);
			mergedStatusCounts.put("reopened", reopenedCount);

			InspectorSourceStatusCountDTO dto = new InspectorSourceStatusCountDTO(inspectorSource, inspectorSourceCount,
					mergedStatusCounts);
			responseList.add(dto);
		}

		return responseList;
	}

	///////////////////////////////////////////////////////////////////////////////////
	// To get individual status names and its count and total count of status for
	/////////////////////////////////////////////////////////////////////////////////// particular
	/////////////////////////////////////////////////////////////////////////////////// inspectorID//
	public StatusCountResponseDTO getStatusCountByInspectorID(String inspectorID) {
		Map<String, Long> statusCountMap = inspectionCaseDAO.getStatusCountByInspectorID(inspectorID);
		System.out.println("statusCountMap : " + statusCountMap);
		// Define the new categories
		Map<String, String> statusMapping = new HashMap<>();
		statusMapping.put("in_progress", "pending");
		statusMapping.put("pending", "pending");
		statusMapping.put("new", "pending");
		statusMapping.put("pending_approval", "completed");
		statusMapping.put("under_approval", "completed");
		statusMapping.put("completed", "completed");
		statusMapping.put("reopened", "reopened");
		System.out.println("statusMapping : " + statusMapping);
		// Initialize the new status counts map
		Map<String, Long> newStatusCountMap = new HashMap<>();
		for (String newStatus : statusMapping.values()) {
			newStatusCountMap.put(newStatus, 0L);
		}
		System.out.println("newStatusCountMap : " + newStatusCountMap);
		// Aggregate the counts according to the new categories
		for (Map.Entry<String, Long> entry : statusCountMap.entrySet()) {
			String originalStatus = entry.getKey();
			System.out.println(originalStatus);
			Long count = entry.getValue();
			String newStatus = statusMapping.get(originalStatus);
			if (newStatusCountMap.get(newStatus) != null)
				newStatusCountMap.put(newStatus, newStatusCountMap.get(newStatus) + count);
		}
		System.out.println("newStatusCountMap : " + newStatusCountMap);
		Long totalCount = newStatusCountMap.values().stream().mapToLong(Long::longValue).sum();
		return new StatusCountResponseDTO(newStatusCountMap, totalCount);
	}

	////////////////////////////////////////////////////////////////////////////////////
	// To get count and names of status, Inspector source based on inspectorID//
	public List<InspectorSourceStatusCountDTO> getStatusAndInspectorSourceCountByInspectorID(String inspectorID) {
		List<InspectorSourceStatusCountDTO> responseList = new ArrayList<>();
		List<String> inspectorSources = inspectionCaseDAO.getInspectorSourcesByInspectorID(inspectorID);

		for (String inspectorSource : inspectorSources) {
			Long inspectorSourceCount = inspectionCaseDAO
					.getInspectorSourceCountByInspectorIDAndInspectorSource(inspectorID, inspectorSource);
			Map<String, Long> statusCounts = inspectionCaseDAO
					.getStatusCountsByInspectorIDAndInspectorSource(inspectorID, inspectorSource);

			// Initialize merged status counts
			long notStarted = 0;
			long inProgressCount = 0;
			long completedCount = 0;
			long reopenedCount = statusCounts.getOrDefault("reopened", 0L);

			// Merge the statuses
			for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
				switch (entry.getKey()) {
				case "in_progress":
					inProgressCount += entry.getValue();
					break;
				case "pending":
				case "new":
					notStarted += entry.getValue();
					break;
				case "completed":
				case "pending_approval":
				case "under_approval":
					completedCount += entry.getValue();
					break;
				case "reopened":
					// Already handled
					break;
				default:
					// Handle unknown statuses if necessary
					break;
				}
			}

			// Create a new map for the merged status counts
			Map<String, Long> mergedStatusCounts = new HashMap<>();
			mergedStatusCounts.put("not_started", notStarted);
			mergedStatusCounts.put("in_progress", inProgressCount);
			mergedStatusCounts.put("completed", completedCount);
			mergedStatusCounts.put("reopened", reopenedCount);

			InspectorSourceStatusCountDTO dto = new InspectorSourceStatusCountDTO(inspectorSource, inspectorSourceCount,
					mergedStatusCounts);
			responseList.add(dto);
		}

		return responseList;
	}

////////////////////
	public NewEntity getEntityById(String entityId) {
		return entityDAO.getEntityByEntityId(entityId);
	}

	public List<InspectionCase_EntityDTO> getMycasesForApprover(String approverID, String dueDateFilter) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = new ArrayList<>();
			if (dueDateFilter.equalsIgnoreCase("all"))
				inspectionCases = inspectionCaseDAO.getMycasesForApprover(approverID);
			else if (dueDateFilter.equalsIgnoreCase("month"))
				inspectionCases = inspectionCaseDAO.getMyCasesForApproverByDate(approverID,
						LocalDate.now().minusDays(30), LocalDate.now());
			else if (dueDateFilter.equalsIgnoreCase("week"))
				inspectionCases = inspectionCaseDAO.getMyCasesForApproverByDate(approverID,
						LocalDate.now().minusDays(7), LocalDate.now());
			else
				inspectionCases = inspectionCaseDAO.getMyCasesForApproverByDate(approverID, LocalDate.now(),
						LocalDate.now());
			System.out.println(inspectionCases);
			inspection = inspectionCases.stream()
					// .filter(inspectionCase -> filterByDueDate(inspectionCase.getDueDate(),
					// dueDateFilter))
					.map(this::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	////// Get status count dashboard data for approver
	public List<ApproverDashboardDTO> getStatusCountsByApproverID(String approverID) {
		List<Object[]> results = inspectionCaseDAO.getStatusCountsByApproverID(approverID);
		return results.stream().map(result -> new ApproverDashboardDTO((String) result[0], (Long) result[1]))
				.collect(Collectors.toList());
	}

	public long getTotalCountByApproverID(String approverID) {
		return inspectionCaseDAO.getStatusCountsByApproverID(approverID).stream().mapToLong(result -> (Long) result[1])
				.sum();
	}

	///////// Get source count for approver
	public List<InspectorSourceStatusCountDTO> getApproverSourceCountForApproverID(String approverID) {
		List<InspectorSourceStatusCountDTO> responseList = new ArrayList<>();
		List<String> inspectorSources = inspectionCaseDAO.getApproverSourceCountForApproverID(approverID);

		for (String inspectorSource : inspectorSources) {
			Long approverSourceCount = inspectionCaseDAO
					.getInspectorSourceCountByApproverIdAndInspectorSource(approverID, inspectorSource);
			Map<String, Long> rawStatusCounts = inspectionCaseDAO.getApproverSourceStatusCountForApproverID(approverID,
					inspectorSource);

			Map<String, Long> mergedStatusCounts = new HashMap<>();
			long pendingApprovalCount = 0;
			long underApprovalCount = 0;
			long completedCount = 0;
			long reopenedCount = 0;

			for (Map.Entry<String, Long> entry : rawStatusCounts.entrySet()) {
				String status = entry.getKey();
				Long count = entry.getValue();

				if ("pending_approval".equalsIgnoreCase(status)) {
					pendingApprovalCount += count;
				}

				if ("under_approval".equalsIgnoreCase(status)) {
					underApprovalCount += count;
				}

				if ("completed".equalsIgnoreCase(status)) {
					completedCount += count;
				}

				if ("reopened".equalsIgnoreCase(status)) {
					reopenedCount += count;
				}
			}

			mergedStatusCounts.put("pending_approval", pendingApprovalCount);
			mergedStatusCounts.put("under_approval", underApprovalCount);
			mergedStatusCounts.put("completed", completedCount);
			mergedStatusCounts.put("reopened", reopenedCount);

			InspectorSourceStatusCountDTO dto = new InspectorSourceStatusCountDTO(inspectorSource, approverSourceCount,
					mergedStatusCounts);
			responseList.add(dto);
		}

		return responseList;
	}

//////Get status count dashboard data for reviewer
	public List<ApproverDashboardDTO> getStatusCountsForReviewer(String reveiwerId) {
		List<Object[]> results = inspectionCaseDAO.getStatusCountsForReviewer(reveiwerId);
		return results.stream().map(result -> new ApproverDashboardDTO((String) result[0], (Long) result[1]))
				.collect(Collectors.toList());
	}

/////////Get source count for approver
	public List<InspectorSourceStatusCountDTO> getSourceCountForReviewer(String reveiwerId) {
		List<InspectorSourceStatusCountDTO> responseList = new ArrayList<>();
		List<String> inspectorSources = inspectionCaseDAO.getSourceCountForReviewer(reveiwerId);

		for (String inspectorSource : inspectorSources) {
			Long approverSourceCount = inspectionCaseDAO.getSourceCountForInspectionSource(reveiwerId, inspectorSource);
			Map<String, Long> rawStatusCounts = inspectionCaseDAO.getSourceStatusCountForReviewer(reveiwerId,
					inspectorSource);

			Map<String, Long> mergedStatusCounts = new HashMap<>();
			long pendingApprovalCount = 0;
			long underApprovalCount = 0;
			long completedCount = 0;
			long reopenedCount = 0;

			for (Map.Entry<String, Long> entry : rawStatusCounts.entrySet()) {
				String status = entry.getKey();
				Long count = entry.getValue();

				if ("pending_review".equalsIgnoreCase(status)) {
					pendingApprovalCount += count;
				}

				if ("under_review".equalsIgnoreCase(status)) {
					underApprovalCount += count;
				}

				if ("completed".equalsIgnoreCase(status)) {
					completedCount += count;
				}

				if ("reopened".equalsIgnoreCase(status)) {
					reopenedCount += count;
				}
			}

			mergedStatusCounts.put("pending_review", pendingApprovalCount);
			mergedStatusCounts.put("under_review", underApprovalCount);
			mergedStatusCounts.put("completed", completedCount);
			mergedStatusCounts.put("reopened", reopenedCount);

			InspectorSourceStatusCountDTO dto = new InspectorSourceStatusCountDTO(inspectorSource, approverSourceCount,
					mergedStatusCounts);
			responseList.add(dto);
		}

		return responseList;
	}

	public List<InspectionCase_EntityDTO> getMycasesForReviewer(String reviewerID, String dueDateFilter) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = new ArrayList<>();
			if (dueDateFilter.equalsIgnoreCase("all"))
				inspectionCases = inspectionCaseDAO.getMycasesForReviewer(reviewerID);
			else if (dueDateFilter.equalsIgnoreCase("month"))
				inspectionCases = inspectionCaseDAO.getMyCasesForReviewerByDate(reviewerID,
						LocalDate.now().minusDays(30), LocalDate.now());
			else if (dueDateFilter.equalsIgnoreCase("week"))
				inspectionCases = inspectionCaseDAO.getMyCasesForReviewerByDate(reviewerID,
						LocalDate.now().minusDays(7), LocalDate.now());
			else
				inspectionCases = inspectionCaseDAO.getMyCasesForReviewerByDate(reviewerID, LocalDate.now(),
						LocalDate.now());
			System.out.println(inspectionCases);
			inspection = inspectionCases.stream()
					// .filter(inspectionCase -> filterByDueDate(inspectionCase.getDueDate(),
					// dueDateFilter))
					.map(this::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection;
	}

	public List<NewEntity> getEntitiesWithInspectionTypes() {
		return entityDAO.getAllEntitiesWithInspectionTypes();

	}
	
	public List<InspectionCase_EntityDTO> getMycasesForCompliance(String complianceId, String dueDateFilter) {
	    List<InspectionCase_EntityDTO> inspection = new ArrayList<>();
	    try {
	        List<InspectionCase> inspectionCases;

	        if ("all".equalsIgnoreCase(dueDateFilter)) {
	            inspectionCases = inspectionCaseDAO.getMycasesForCompliance(complianceId);
	        } else if ("month".equalsIgnoreCase(dueDateFilter)) {
	            inspectionCases = inspectionCaseDAO.getMyCasesForComplianceByDate(
	                    complianceId, LocalDate.now().minusDays(30), LocalDate.now());
	        } else if ("week".equalsIgnoreCase(dueDateFilter)) {
	            inspectionCases = inspectionCaseDAO.getMyCasesForComplianceByDate(
	                    complianceId, LocalDate.now().minusDays(7), LocalDate.now());
	        } else {
	            inspectionCases = inspectionCaseDAO.getMyCasesForComplianceByDate(
	                    complianceId, LocalDate.now(), LocalDate.now());
	        }

	        inspection = inspectionCases.stream()
	                .map(this::toDto)
	                .collect(Collectors.toList());

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return inspection;
	}


	public EntityDetailsDTO getEntityDetailsById(String entityId) {
		NewEntity entity = entityDAO.findEntityById(entityId);

		if (entity == null) {
			throw new IllegalArgumentException("Entity not found for ID: " + entityId);
		}

		Set<String> uniqueZoneIds = new HashSet<>();
		List<ZoneDTO> zones = entity.getZones().stream().map(zone -> new ZoneDTO(zone.getZoneId(), zone.getName()))
				.collect(Collectors.toList());

		System.out.println(zones);
		System.out.println(entity.getZones());

		List<InspectionTypeDTO> inspectionTypes = entity.getInspectionTypes() != null
				&& !entity.getInspectionTypes().isEmpty()
						? entity.getInspectionTypes().stream()
								.map(type -> new InspectionTypeDTO(type.getIns_type_id(), type.getName())).collect(
										Collectors.toList())
						: new ArrayList<>();

		return new EntityDetailsDTO(entity.getEntityid(), entity.getName(), entity.getFloor(), entity.getAddress(),
				entity.getFacility(), entity.getRepresentativePhoneNo(), entity.getRepresentativeName(), // Correct
																											// mapping
																											// for name
				entity.getRepresentativeEmail(), // Correct mapping for email
				zones, inspectionTypes);

	}

	public boolean addUpdateEntity(EntityDetailsDTO entityDetailsDTO) {
		boolean response = false;

		if (entityDetailsDTO.getAction().equalsIgnoreCase("add")) {
			NewEntity entity = new NewEntity();
			entity.setName(entityDetailsDTO.getName());
			entity.setAddress(entityDetailsDTO.getAddress());
			entity.setFacility(entityDetailsDTO.getFacility());
			entity.setFloor(entityDetailsDTO.getFloor());
			entity.setActive(true);
			entity.setRepresentativeEmail(entityDetailsDTO.getRepresentative_email());
			entity.setRepresentativeName(entityDetailsDTO.getRepresentative_name());
			entity.setRepresentativePhoneNo(entityDetailsDTO.getRepresentative_phoneno());

			// Set the zones
			Set<Zone> zones = zoneDAO.findByIds(entityDetailsDTO.getZoneIds());
			zones.stream().forEach(zone -> zone.getNewEntities().add(entity));
			entity.setZones(zones);

			Set<Inspection_Type> inspection_Types = inspectionTypeDAO
					.findByIds(entityDetailsDTO.getInspectionTypeIds());
			inspection_Types.stream().forEach(inspectionType -> inspectionType.getNewEntities().add(entity));
			entity.setInspectionTypes(inspection_Types);

			response = entityDAO.addUpdateEntity(entity);
		} else {
			NewEntity entity = entityDAO.findEntityById(entityDetailsDTO.getEntityId());

			entity.setName(entityDetailsDTO.getName() != null ? entityDetailsDTO.getName() : entity.getName());
			entity.setFloor(entityDetailsDTO.getFloor() != null ? entityDetailsDTO.getFloor() : entity.getFloor());
			entity.setAddress(
					entityDetailsDTO.getAddress() != null ? entityDetailsDTO.getAddress() : entity.getAddress());
			entity.setFacility(
					entityDetailsDTO.getFacility() != null ? entityDetailsDTO.getFacility() : entity.getFacility());
			entity.setActive(true);
			entity.setRepresentativeEmail(
					entityDetailsDTO.getRepresentative_email() != null ? entityDetailsDTO.getRepresentative_email()
							: entity.getRepresentativeEmail());
			entity.setRepresentativeName(
					entityDetailsDTO.getRepresentative_name() != null ? entityDetailsDTO.getRepresentative_name()
							: entity.getRepresentativeName());
			entity.setRepresentativePhoneNo(
					entityDetailsDTO.getRepresentative_phoneno() != null ? entityDetailsDTO.getRepresentative_phoneno()
							: entity.getRepresentativePhoneNo());
			// Set the zones
			Set<Zone> zones = zoneDAO.findByIds(entityDetailsDTO.getZoneIds());
			entity.setZones(null);
			zones.stream().forEach(zone -> zone.getNewEntities().add(entity));
			entity.setZones(zones);

			// Set the inspection types
			Set<Inspection_Type> oldInspectionTypes = entity.getInspectionTypes();
			oldInspectionTypes.parallelStream().forEach(e -> {
				e.getNewEntities().remove(entity);
				inspectionTypeDAO.save(e);
			});
			System.out.println(oldInspectionTypes);
			Set<Inspection_Type> inspection_Types = inspectionTypeDAO
					.findByIds(entityDetailsDTO.getInspectionTypeIds());
			System.out.println("Inspection types : " + inspection_Types);
			inspection_Types.stream().forEach(inspectionType -> inspectionType.getNewEntities().add(entity));
			System.out.println("**********************************************************");
			entity.setInspectionTypes(inspection_Types);
			entity.getInspectionTypes().stream().forEach(e -> System.out.println(e.getIns_type_id()));

			response = entityDAO.addUpdateEntity(entity);
		}

		return response;
	}

	public Page<Map<String, Object>> getPaginatedEntitiesWithInspectionTypes(Pageable pageable) {
		Page<NewEntity> paginatedEntities = entityDAO.getPaginatedEntitiesWithInspectionTypes(pageable);

		// Map entities to a paginated response with formatted output
		return paginatedEntities.map(entity -> {
			Map<String, Object> entityMap = new HashMap<>();
			entityMap.put("id", entity.getEntityid());
			entityMap.put("name", entity.getName());
			entityMap.put("address", entity.getAddress());
			entityMap.put("entity_type",entity.getType());

			List<String> inspectionTypeNames = entity.getInspectionTypes().stream().map(Inspection_Type::getName)
					.collect(Collectors.toList());
			entityMap.put("inspectionTypes", inspectionTypeNames);

			return entityMap;
		});
	}

	// EntityRegistration based on Entity_Type  & Edit EntityRegistration details based on entityId
			public String entityRegistration(EntityRegistrationDTO entityRegistrationDTO) {
			    if (entityRegistrationDTO.getAction() == null || 
			        (!entityRegistrationDTO.getAction().equalsIgnoreCase("save") && 
			         !entityRegistrationDTO.getAction().equalsIgnoreCase("edit"))) {
			        throw new IllegalArgumentException("Invalid action: " + entityRegistrationDTO.getAction() + 
			                                           ". Allowed actions are 'save' or 'edit'.");
			    }
			    return entityDAO.saveEntityDetails(entityRegistrationDTO);
			}

		// Get entity details by entity id checking for entity type

		public Object getEntityDetailsbyEntityID(String entityId) throws Exception {
			// Fetch entity details from DAO
			EntityRegistrationDTO entityDetails = entityDAO.getEntityRegistration(entityId);

			// Determine and return the appropriate DTO based on EntityType
			
			//return entityDetails.getEntityType();
//			if ("site".equalsIgnoreCase(entityType)) {
//				return entityDetails.getSite();
//			} else if ("product".equalsIgnoreCase(entityType)) {
//				return entityDetails.getProduct();
//			} else if ("license".equalsIgnoreCase(entityType)) {
//				return entityDetails.getLicense();
//			} 
			if(entityDetails!=null) {
				return entityDetails;
			}
			else {
				throw new IllegalArgumentException("Invalid Id: " + entityId);
			}
		}
		
	// Get EntityNames associated with inspectionTypes
	
	public List<EntitiesInspectionTypeDTO> fetchEntities(Long insTypeId) {
        return entityDAO.getEntitiesWithOptionalInspectionType(insTypeId);
    }
	
	public EntityResponseDTO getEntityAllDetailsById(String entityId) {
        NewEntity entity = entityDAO.getEntityByEntityId(entityId);
        EntityResponseDTO responseDTO = new EntityResponseDTO();

        if (entity != null) {
            responseDTO.setEntityId(entity.getEntityid());

            // Check entity type and set the appropriate address field
            if (entity.getType() == null || entity.getType().isEmpty()) {
                // Full response when entity type is null, empty, or blank
                responseDTO.setName(entity.getName());
                responseDTO.setAddress(entity.getAddress());
                responseDTO.setFacility(entity.getFacility());
                responseDTO.setFloor(entity.getFloor());
                
            } else {
            	JsonNode entityDetails = entity.getEntityDetails();
            	responseDTO.setName(entity.getName()); // always set name

            	if (entityDetails != null) {
            	    switch (entity.getType().toLowerCase()) {
            	        case "site":
            	            responseDTO.setAddress(entityDetails.path("siteAddress").asText(null));
            	            break;
            	        case "product":
            	            responseDTO.setAddress(entityDetails.path("productAddress").asText(null));
            	            break;
            	        case "license":
            	            responseDTO.setAddress(entityDetails.path("associateLocationOrSite").asText(null));
            	            break;
            	        default:
            	            responseDTO.setAddress(null);
            	            break;
            	    }
            	} else {
            	    // fallback if entityDetails is missing
            	    responseDTO.setAddress(entity.getAddress());
            	}

            }
        }

        return responseDTO;
    }
}
