package com.aaseya.AIS.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaseya.AIS.Model.InspectionCase;
import com.aaseya.AIS.dao.getGroupCasesForLeadDAO;
import com.aaseya.AIS.dto.InspectionCase_EntityDTO;

@Service
public class getGroupCasesForLeadService {
	
	@Autowired
	private NewEntityService entityService;
	
	@Autowired
	private getGroupCasesForLeadDAO getGroupCasesForLeadDAO;
	
	public List<InspectionCase_EntityDTO> getGroupCaseForLeadInspector(Long leadId) {
		List<InspectionCase_EntityDTO> inspection = new ArrayList<InspectionCase_EntityDTO>();
		try {
			List<InspectionCase> inspectionCases = getGroupCasesForLeadDAO.getGroupCaseForLeadInspector(leadId);
			
			inspection = inspectionCases.stream().map(entityService::toDto).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inspection; 
	}

}
