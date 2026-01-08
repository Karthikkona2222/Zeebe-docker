package com.aaseya.AIS.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aaseya.AIS.Model.ClaimCase;
import com.aaseya.AIS.Model.CodeRangeCategory;
import com.aaseya.AIS.Model.PolicyDetails;
import com.aaseya.AIS.Model.Pool;
import com.aaseya.AIS.Model.Users;
import com.aaseya.AIS.dto.ClaimCaseResponseDTO;
import com.aaseya.AIS.dto.PolicyDetailsDTO;
import com.aaseya.AIS.utility.MapStringParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class ClaimCaseDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Persists a new ClaimCase entity into the database. The transaction is managed
	 * by Spring.
	 *
	 * @param claimCase The ClaimCase entity to save.
	 */
	@Transactional
	public void save(ClaimCase claimCase) {
		if (claimCase != null) {
			entityManager.persist(claimCase);
		}
	}

	public void updateClaimCase(Long claimId, String dischargeSummary, String claimCaseDetails) {
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaUpdate<ClaimCase> update = cb.createCriteriaUpdate(ClaimCase.class);
			Root<ClaimCase> root = update.from(ClaimCase.class);

			update.set(root.get("dischargeSummary"), dischargeSummary);
			update.set(root.get("claimCaseDetails"), claimCaseDetails);
			update.where(cb.equal(root.get("claimId"), claimId));

			session.createQuery(update).executeUpdate();
			tx.commit();

		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close(); // explicitly closing session
		}
	}

	public void saveOrUpdateClaimCase(ClaimCase claimCase) {
		org.hibernate.Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.saveOrUpdate(claimCase);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			throw e;
		}
	}

	public ClaimCase getClaimCaseById(Long claimId) {
		try (Session session = sessionFactory.openSession()) {
			return session.get(ClaimCase.class, claimId);
		}
	}

	public long countAssignedCases(String email, String assignedField, String statusFilter) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<ClaimCase> root = query.from(ClaimCase.class);

		query.select(cb.count(root));
		query.where(cb.and(cb.equal(root.get(assignedField), email), cb.equal(root.get("status"), statusFilter)));

		return entityManager.createQuery(query).getSingleResult();
	}

	@Transactional(readOnly = true)
	public List<ClaimCaseResponseDTO> getClaimCasesByEmail(String emailID) {
		Session session = entityManager.unwrap(Session.class);
		CriteriaBuilder cb = session.getCriteriaBuilder();

		// Find users
		CriteriaQuery<Users> userQuery = cb.createQuery(Users.class);
		Root<Users> userRoot = userQuery.from(Users.class);
		userQuery.select(userRoot).where(cb.equal(userRoot.get("emailID"), emailID));
		List<Users> users = session.createQuery(userQuery).getResultList();

		if (users.isEmpty())
			return List.of();

		Set<String> roles = users.stream().map(Users::getRole).collect(Collectors.toSet());
		if (roles.isEmpty())
			return List.of();

		// Query claim cases assigned to user (by inspector, reviewer, approver)
		CriteriaQuery<ClaimCase> cq = cb.createQuery(ClaimCase.class);
		Root<ClaimCase> claimRoot = cq.from(ClaimCase.class);

		List<Predicate> rolePredicates = new ArrayList<>();
		if (roles.contains("Inspector")) {
			rolePredicates.add(cb.and(cb.equal(claimRoot.get("status"), "pending"),
					cb.equal(claimRoot.get("assignedInspector"), emailID)));
		}
		if (roles.contains("Reviewer")) {
			rolePredicates.add(cb.and(cb.equal(claimRoot.get("status"), "pending_inreview"),
					cb.equal(claimRoot.get("assignedReviewer"), emailID)));
		}
		if (roles.contains("Approver")) {
			rolePredicates.add(cb.and(cb.equal(claimRoot.get("status"), "pending_inapproval"),
					cb.equal(claimRoot.get("assignedApprover"), emailID)));
		}
		cq.where(cb.or(rolePredicates.toArray(new Predicate[0])));

		List<ClaimCase> claimCases = session.createQuery(cq).getResultList();

		// Map entities to DTOs
		List<ClaimCaseResponseDTO> result = new ArrayList<>();
		for (ClaimCase claim : claimCases) {
			ClaimCaseResponseDTO dto = new ClaimCaseResponseDTO();
			dto.setClaimId(claim.getClaimId());
			dto.setClaimType(claim.getClaimType());
			dto.setCreatedBy(claim.getCreatedBy());
			dto.setDueDate(claim.getDueDate());
			dto.setStatus(claim.getStatus());
			dto.setCreatedTimestamp(claim.getCreatedTimestamp());

			// Map policy details
			PolicyDetailsDTO pdto = new PolicyDetailsDTO();
			if (claim.getPolicyDetails() != null) {
				pdto.setPolicyId(claim.getPolicyDetails().getPolicyId());
				pdto.setCustomerName(claim.getPolicyDetails().getCustomerName());
				pdto.setGender(claim.getPolicyDetails().getGender());
				pdto.setDateOfBirth(claim.getPolicyDetails().getDateOfBirth());
				pdto.setAddress(claim.getPolicyDetails().getAddress());
				pdto.setPhoneNumber(claim.getPolicyDetails().getPhoneNumber());
				pdto.setEmail(claim.getPolicyDetails().getEmail());
				pdto.setSocialSecurityNumber(claim.getPolicyDetails().getSocialSecurityNumber());
				pdto.setPolicyType(claim.getPolicyDetails().getPolicyType());
				pdto.setCoverageStartDate(claim.getPolicyDetails().getCoverageStartDate());
				pdto.setCoverageEndDate(claim.getPolicyDetails().getCoverageEndDate());
				pdto.setPremiumAmount(claim.getPolicyDetails().getPremiumAmount());
				pdto.setMedicalHistory(claim.getPolicyDetails().getMedicalHistory());
				pdto.setInsurancePlanName(claim.getPolicyDetails().getInsurancePlanName());
				pdto.setCreatedTimestamp(claim.getPolicyDetails().getCreatedTimestamp());
			}
			dto.setPolicyDetails(pdto);
			result.add(dto);
		}
		return result;
	}

	public Map<String, Object> getDischargeSummaryByClaimId(Long claimId) {
	    Session session = null;
	    Transaction tx = null;
	    Map<String, Object> response = new HashMap<>();

	    String disease = "";

	    try {
	        session = sessionFactory.openSession();
	        tx = session.beginTransaction();

	        ClaimCase claimCase = session.get(ClaimCase.class, claimId);
	        if (claimCase != null) {
	            response.put("claimId", claimCase.getClaimId());
	            response.put("claimType", claimCase.getClaimType());
	            response.put("policyType", claimCase.getPolicyType());
	            response.put("status", claimCase.getStatus());
	            response.put("createdTimestamp", claimCase.getCreatedTimestamp());

	            PolicyDetails policy = claimCase.getPolicyDetails();
	            if (policy != null) {
	                response.put("policyId", policy.getPolicyId());
	                response.put("customerName", policy.getCustomerName());
	                response.put("coverageStartDate", policy.getCoverageStartDate());
	                response.put("coverageEndDate", policy.getCoverageEndDate());
	                response.put("insurancePlanName", policy.getInsurancePlanName());
	            }

	            String dischargeSummaryStr = claimCase.getDischargeSummary();
	            Map<String, Object> dischargeSummaryMap = null;
	            Object diagnosisObj = null;
	            String category = "Unknown";
	            String formattedBillAmount = "";
	            
	            // --- ADDED PART: Manually extract the full summary string before parsing ---
	            String fullSummaryOfDischargeSummary = "Summary not available.";
	            if (dischargeSummaryStr != null && !dischargeSummaryStr.isEmpty()) {
	                String summaryKey = "summaryOfDischargeSummary=";
	                int startIndex = dischargeSummaryStr.indexOf(summaryKey);
	                if (startIndex != -1) {
	                    int valueStartIndex = startIndex + summaryKey.length();
	                    // The summary value ends just before the next key starts (e.g., ", patientDetails=")
	                    // or at the end of the map string.
	                    String nextKeyDelimiter = ", patientDetails=";
	                    int endIndex = dischargeSummaryStr.indexOf(nextKeyDelimiter, valueStartIndex);
	                    
	                    if (endIndex == -1) {
	                        // If it's the last entry, find the closing brace.
	                        endIndex = dischargeSummaryStr.lastIndexOf('}');
	                    }
	                    
	                    if (endIndex > valueStartIndex) {
	                        fullSummaryOfDischargeSummary = dischargeSummaryStr.substring(valueStartIndex, endIndex).trim();
	                    }
	                }
	            }
	            // --- END OF ADDED PART ---

	            
	            
	            if (dischargeSummaryStr != null && !dischargeSummaryStr.isEmpty()) {
	                try {
	                    dischargeSummaryMap = MapStringParser.parseJavaMapString(dischargeSummaryStr);
	                    diagnosisObj = dischargeSummaryMap.get("diagnosis");

	                 // Format billedAmount with Indian digit grouping (if present)
	                    Object billedAmountObj = dischargeSummaryMap.get("billedAmount");
	                    if (billedAmountObj != null) {
	                        String billedStr = billedAmountObj.toString().replaceAll("[^\\d]", ""); // Remove any non-digit chars
	                        if (!billedStr.isEmpty()) {
	                            try {
	                                long billed = Long.parseLong(billedStr);
	                                formattedBillAmount = "₹" + formatIndianNumber(billed);
	                            } catch (NumberFormatException ex) {
	                                formattedBillAmount = billedAmountObj.toString(); // fallback original string
	                            }
	                        }
	                        // Update map and later response usage
	                        dischargeSummaryMap.put("billedAmount", formattedBillAmount);
	                    }

	                    // Existing ICD-10 and category code...
	                    String icd10 = "";
	                    if (diagnosisObj instanceof Map<?, ?> diagnosisMap) {
                            Object icd10Obj = diagnosisMap.get("icd10DiagnosisCodes");
	                        if (icd10Obj == null) {
	                             icd10Obj = diagnosisMap.get("ICD10DiagnosisCodes"); // Fallback
	                        }
	                        if (icd10Obj instanceof List<?> list) {
                                if (!list.isEmpty() && list.get(0) != null) {
	                                icd10 = list.get(0).toString();
	                            }
	                        } else if (icd10Obj != null) {
	                            icd10 = icd10Obj.toString();
	                        }
	                    }

	                    if (!icd10.isEmpty()) {
	                        Query query = session.createQuery("SELECT c.category FROM CodeRangeCategory c "
	                                + "WHERE :icd10 BETWEEN substring(c.codeRange, 1, locate('-', c.codeRange)-1) "
	                                + "AND substring(c.codeRange, locate('-', c.codeRange)+1)");
	                        query.setParameter("icd10", icd10);
	                        Object result = query.uniqueResult();
	                        if (result != null) {
	                            category = result.toString();
	                        }
	                    }

	                    // Diagnosis vs Prescription Match
	                    String admittingDiagnosis = (String) dischargeSummaryMap.getOrDefault("admittingDiagnosis",
	                            dischargeSummaryMap.get("Admitting Diagnosis"));

	                        String doctorDiagnosis = (String) dischargeSummaryMap.getOrDefault("doctorPrescriptionDiagnosis",
	                            dischargeSummaryMap.get("Doctor Prescription Diagnosis"));

	                        if (admittingDiagnosis != null && doctorDiagnosis != null
	                                && admittingDiagnosis.trim().equalsIgnoreCase(doctorDiagnosis.trim())) {
	                            response.put("DiagnosisPrescriptionMatch", "Yes");
	                        } else {
	                            response.put("DiagnosisPrescriptionMatch", "No");
	                        }

	                    // Extract Discharge Date
	                    String dischargeDate = "";
	                    Object patientDetailsObj = dischargeSummaryMap.get("patientDetails");
	                    if (patientDetailsObj instanceof Map<?, ?> patientDetails) {
                            Object dateObj = patientDetails.get("dischargeDate");
	                        if (dateObj != null) {
	                            dischargeDate = dateObj.toString();
	                        }
	                    }

	                    response.put("ICD10Validation", !icd10.isEmpty() ? "Yes" : "No");
	                    response.put("CategoryValidation", !"Unknown".equals(category) ? "Yes" : "No");
	                    response.put("DischargeDateValidation", "No");
	                    response.put("Category", category);

	                    // Extract disease
	                    if (dischargeSummaryStr != null && !dischargeSummaryStr.isEmpty()) {
	                        Map<String, Object> dischargeSummaryMap1 = MapStringParser
	                                .parseJavaMapString(dischargeSummaryStr);
	                        Object diseaseObj = dischargeSummaryMap1.get("disease");
	                        if (diseaseObj != null) {
	                            disease = diseaseObj.toString();
	                        }
	                        String requiredDoc = null;
	                        if (disease.toLowerCase().contains("heart")) {
	                            requiredDoc = "ECG Report";
	                        } else if (disease.toLowerCase().contains("lung")) {
	                            requiredDoc = "Xray Report";
	                        } else if (disease.toLowerCase().contains("cancer")) {
	                            requiredDoc = "Biopsy Report";
	                        } else if (disease.toLowerCase().contains("pancreatic")) {
	                            requiredDoc = "Endoscopy Report";
	                        }

	                        if (requiredDoc != null) {
	                            final String requiredDocFinal = requiredDoc;
	                            boolean docUploaded = claimCase.getCsrDocuments() != null && claimCase.getCsrDocuments()
	                                    .stream().anyMatch(d -> d.getDocumentType() != null && d.getDocumentType()
	                                            .toLowerCase().contains(requiredDocFinal.split(" ")[0].toLowerCase()));
	                            response.put("additionalDocumentsRequired", docUploaded ? "No" : "Yes");
	                            response.put("RequiredDocument", requiredDoc);
	                        } else {
	                            response.put("additionalDocumentsRequired", "Not Applicable");
	                        }

	                        response.put("dischargeSummary", dischargeSummaryMap);
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    response.put("dischargeSummary",
	                            Collections.singletonMap("error", "Failed to parse discharge summary."));
	                    response.put("ICD10Validation", "No");
	                    response.put("CategoryValidation", "No");
	                    response.put("DischargeDateValidation", "No");
	                    response.put("additionalDocumentsRequired", "Not Applicable");
	                    response.put("Category", "Unknown");
	                    response.put("DiagnosisPrescriptionMatch", "No");
	                }
	            } else {
	                response.put("dischargeSummary", null);
	                response.put("ICD10Validation", "No");
	                response.put("CategoryValidation", "No");
	                response.put("DischargeDateValidation", "No");
	                response.put("additionalDocumentsRequired", "Not Applicable");
	                response.put("Category", "Unknown");
	                response.put("DiagnosisPrescriptionMatch", "No");
	            }

	            // --- New Part: Process validationDetails for discharge date validation ---
	            String validationDetailsStr = claimCase.getValidationDetails();
	            Map<String, Object> validationDetailsMap = new HashMap<>();
	            Map<String, Object> dischargeDateValidationEntry = null;

	            if (validationDetailsStr != null && !validationDetailsStr.isEmpty()) {
	                try {
	                    validationDetailsMap = MapStringParser.parseJavaMapString(validationDetailsStr);

	                    if (validationDetailsMap.containsKey("validations")) {
	                        List<Map<String, Object>> validationsList = (List<Map<String, Object>>) validationDetailsMap
	                                .get("validations");
	                        for (Map<String, Object> entry : validationsList) {
	                            if ("dischargeDate".equals(entry.get("type"))) {
	                                dischargeDateValidationEntry = entry;
	                                break;
	                            }
	                        }
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }

	            response.put("dischargeDateValidationEntry", dischargeDateValidationEntry);

	            String dischargeDateStr = "";
	            String coverageStartStr = "";
	            String coverageEndStr = "";

	            if (dischargeDateValidationEntry != null) {
	                Map<String, Object> details = (Map<String, Object>) dischargeDateValidationEntry.get("details");
	                if (details != null) {
	                    dischargeDateStr = (String) details.get("dischargeDate");
	                    coverageStartStr = (String) details.get("coverageStart");
	                    coverageEndStr = (String) details.get("coverageEnd");
	                }
	            } else {
	                Object patientDetailsObj = dischargeSummaryMap != null ? dischargeSummaryMap.get("patientDetails")
	                        : null;
	                if (patientDetailsObj instanceof Map<?, ?> patientDetails) {
                        Object dateObj = patientDetails.get("dischargeDate");
	                    if (dateObj != null) {
	                        dischargeDateStr = dateObj.toString();
	                    }
	                }
	                if (policy != null) {
	                    coverageStartStr = policy.getCoverageStartDate() != null
	                            ? policy.getCoverageStartDate().toString()
	                            : "";
	                    coverageEndStr = policy.getCoverageEndDate() != null ? policy.getCoverageEndDate().toString()
	                            : "";
	                }
	            }

	            boolean isDischargeDateValid = false;
	            try {
	                LocalDate dischargeDate = LocalDate.parse(dischargeDateStr);
	                LocalDate coverageStart = LocalDate.parse(coverageStartStr);
	                LocalDate coverageEnd = LocalDate.parse(coverageEndStr);

	                isDischargeDateValid = !dischargeDate.isBefore(coverageStart)
	                        && !dischargeDate.isAfter(coverageEnd);
	            } catch (Exception e) {
	                isDischargeDateValid = false;
	            }

	            response.put("DischargeDateValidation", isDischargeDateValid ? "Yes" : "No");

	            // --- Build simplified response with only required fields ---
	            Map<String, Object> simplifiedResponse = new HashMap<>();

	            // Extract admittingDiagnosis and doctorPrescriptionDiagnosis safely
	         // MODIFIED: Safely extract admittingDiagnosis and doctorPrescriptionDiagnosis
	            String admittingDiagnosis = "";
	            String doctorPrescriptionDiagnosis = "";
	            if (dischargeSummaryMap != null) {
	                admittingDiagnosis = dischargeSummaryMap.getOrDefault("admittingDiagnosis", dischargeSummaryMap.getOrDefault("Admitting Diagnosis", "")).toString();
	                doctorPrescriptionDiagnosis = dischargeSummaryMap.getOrDefault("doctorPrescriptionDiagnosis", dischargeSummaryMap.getOrDefault("Doctor Prescription Diagnosis", "")).toString();
	            }

	            String icd10DiagnosisCode = "";
	            if (diagnosisObj instanceof Map) {
	                // MODIFIED: Check for new 'icd10DiagnosisCodes' key first
	                Object icd10Obj = ((Map<?, ?>) diagnosisObj).get("icd10DiagnosisCodes");
	                 if (icd10Obj == null) {
	                    icd10Obj = ((Map<?, ?>) diagnosisObj).get("ICD10DiagnosisCodes"); // Fallback
	                }
	                if (icd10Obj instanceof List<?> list) {
                        if (!list.isEmpty())
	                        icd10DiagnosisCode = list.get(0).toString();
	                } else if (icd10Obj != null) {
	                    icd10DiagnosisCode = icd10Obj.toString();
	                }
	            }

	            simplifiedResponse.put("admittingDiagnosis", admittingDiagnosis);
	            simplifiedResponse.put("doctorPrescriptionDiagnosis", doctorPrescriptionDiagnosis);
	            simplifiedResponse.put("icd10DiagnosisCode", icd10DiagnosisCode);
	            simplifiedResponse.put("category", category);
	            simplifiedResponse.put("dischargeDate", dischargeDateStr);

	            // Add formatted bill amount to simplified response
	            simplifiedResponse.put("billAmount", formattedBillAmount);

	            Map<String, String> validations = new HashMap<>();
	            validations.put("Admitting Diagnosis matches with the Doctor's prescription or not",
	                    response.getOrDefault("DiagnosisPrescriptionMatch", "No").toString());
	            validations.put("ICD-10 Codes and Category Match or not", ("Yes".equals(response.get("ICD10Validation"))
	                    && "Yes".equals(response.get("CategoryValidation"))) ? "Yes" : "No");
	            String additionalDocsRequiredFlag = response.getOrDefault("additionalDocumentsRequired", "No")
	                    .toString();

	            validations.put("Additional Support Documents Required", additionalDocsRequiredFlag);

	            Map<String, String> documentMessages = new HashMap<>();
	            String claimDisease = (disease != null && !disease.isEmpty()) ? disease : "your disease";

	            if ("No".equalsIgnoreCase(additionalDocsRequiredFlag)) {
	                documentMessages.put("documentStatusMessage",
	                        "For your claim regarding " + claimDisease + ", the documents are present.");
	            } else if ("Yes".equalsIgnoreCase(additionalDocsRequiredFlag)) {
	                documentMessages.put("documentStatusMessage", "For your claim regarding " + claimDisease
	                        + ", the documents are not present. Kindly update.");
	            } else {
	                documentMessages.put("documentStatusMessage",
	                        "Document status not applicable for " + claimDisease + ".");
	            }

	            simplifiedResponse.put("validations", validations);
	            simplifiedResponse.put("documentMessages", documentMessages);

	            validations.put("Discharge dates are in range to apply for the claim",
	                    response.getOrDefault("DischargeDateValidation", "No").toString());

	            simplifiedResponse.put("validations", validations);

	            // --- MODIFIED PART: Use the manually extracted full summary ---
	            if (dischargeSummaryMap != null && !dischargeSummaryMap.isEmpty()) {
	                simplifiedResponse.put("DischargeSummary", dischargeSummaryMap);
	                // This now uses the complete summary string we extracted earlier.
	                simplifiedResponse.put("summaryOfDischargeSummary", fullSummaryOfDischargeSummary);
	            } else {
	                simplifiedResponse.put("DischargeSummary", Collections.emptyMap());
	                simplifiedResponse.put("summaryOfDischargeSummary", "Summary not available.");
	            }
	            // --- END OF MODIFIED PART ---
	            tx.commit();
	            return simplifiedResponse;

	        } else {
	            tx.commit();
	            return Map.of("error", "No claim case found for claimId: " + claimId);
	        }

	    } catch (Exception e) {
	        if (tx != null)
	            tx.rollback();
	        e.printStackTrace();
	        return Map.of("error", "Error fetching discharge summary for claimId: " + claimId);
	    } finally {
	        if (session != null)
	            session.close();
	    }
	}

	/* Indian Number formatting utility */
	public static String formatIndianNumber(long amount) {
	    String s = String.valueOf(amount);
	    int n = s.length();
	    if (n <= 3) return s;
	    String last3 = s.substring(n - 3);
	    String rest = s.substring(0, n - 3);
	    StringBuilder sb = new StringBuilder();
	    int mod = rest.length() % 2;
	    if (mod > 0) {
	        sb.append(rest, 0, mod).append(",");
	    }
	    for (int i = mod; i < rest.length(); i += 2) {
	        sb.append(rest, i, i + 2).append(",");
	    }
	    sb.append(last3);
	    return sb.toString();
	}


	private String findCategoryByICD10Code(Session session, String icd10Code, String policyId) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<CodeRangeCategory> cq = cb.createQuery(CodeRangeCategory.class);
		Root<CodeRangeCategory> root = cq.from(CodeRangeCategory.class);

		cq.select(root).where(cb.equal(root.get("policyDetails").get("policyId"), policyId));

		List<CodeRangeCategory> results = session.createQuery(cq).getResultList();

		for (CodeRangeCategory crc : results) {
			if (isCodeInRange(icd10Code, crc.getCodeRange())) {
				return crc.getCategory();
			}
		}
		return null;
	}

	private boolean isCodeInRange(String code, String range) {
		if (range == null || range.isEmpty()) {
			return false;
		}
		if (!range.contains("-")) {
			return range.equalsIgnoreCase(code);
		}
		String[] parts = range.split("-");
		if (parts.length != 2)
			return false;

		String start = parts[0].trim();
		String end = parts[1].trim();

		return code.compareToIgnoreCase(start) >= 0 && code.compareToIgnoreCase(end) <= 0;
	}
	
//	public static String formatIndianNumber(long amount) {
//	    // Handle small numbers normally
//	    String s = String.valueOf(amount);
//	    int n = s.length();
//	    if (n <= 3) return s;
//
//	    String last3 = s.substring(n - 3);
//	    String rest = s.substring(0, n - 3);
//
//	    StringBuilder sb = new StringBuilder();
//	    int mod = rest.length() % 2;
//	    if (mod > 0) {
//	        sb.append(rest.substring(0, mod)).append(",");
//	    }
//	    for (int i = mod; i < rest.length(); i += 2) {
//	        sb.append(rest.substring(i, i + 2)).append(",");
//	    }
//	    sb.append(last3);
//	    return sb.toString();
//	}


	
	
	public ClaimCase findByClaimId(Long claimId) {
	    return entityManager.find(ClaimCase.class, claimId);
	}

	public Pool findPoolById(Long poolId) {
	    return entityManager.find(Pool.class, poolId);
	}

	  public ClaimCase getById(Long claimId) {
	        Session session = sessionFactory.openSession();
	        ClaimCase claimCase = session.get(ClaimCase.class, claimId);
	        session.close();
	        return claimCase;
	    }
	


}
