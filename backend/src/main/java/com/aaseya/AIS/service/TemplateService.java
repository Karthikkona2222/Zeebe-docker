package com.aaseya.AIS.service;
 
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
 
 
import com.aaseya.AIS.Model.Template;
import com.aaseya.AIS.dao.TemplateDAO;
import com.aaseya.AIS.dto.CategoryDTO;
import com.aaseya.AIS.dto.TemplateDTO;
import com.aaseya.AIS.dto.TemplateDetailsDTO;
import com.aaseya.AIS.dto.TemplateResponseDTO;
import com.aaseya.AIS.dto.TemplateSummaryDTO;

import jakarta.transaction.Transactional;
@Service
public class TemplateService {
	@Autowired
	private TemplateDAO templateDAO;

	@Transactional
	public void addTemplate(TemplateDTO templateDTO) {
		Template template = new Template();
		template.setTemplate_name(templateDTO.getTemplate_name());
		template.setEffective_from(templateDTO.getEffective_from());
		template.setVersion_from(templateDTO.getVersion());
		template.setActive(true);
		System.out.println(template);
		templateDAO.saveTemplate(template);
	}

	public List<TemplateDTO> getTemplateNameAndVersionByInspectionTypeName(String inspectionTypeName) {
		List<Template> templatelist = templateDAO.getTemplateNameAndVersionByInspectionTypeName(inspectionTypeName);
		List<TemplateDTO> templateDetails = new ArrayList<>();
		for (Template template : templatelist) {
			TemplateDTO templateDTO = new TemplateDTO();
			templateDTO.setTemplate_id(template.getTemplate_id());
			templateDTO.setTemplate_name(template.getTemplate_name());
			templateDTO.setVersion(template.getVersion());
			templateDetails.add(templateDTO);
		}
		return templateDetails;
	}
	
	public Page<TemplateResponseDTO> getTemplatesWithInspectionTypes(int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);

	    // Fetch paginated data from DAO
	    Page<Template> templates = templateDAO.getAllTemplatesWithInspectionTypes(pageable);

	    // Map entities to DTOs
	    return templates.map(template -> {
	        TemplateResponseDTO dto = new TemplateResponseDTO();
	        dto.setTemplateId(template.getTemplate_id());
	        dto.setTemplateName(template.getTemplate_name());
	        dto.setInspectionTypes(
	                template.getInspection_types().stream().map(insType -> {
	                    TemplateResponseDTO.InspectionTypeDTO insTypeDTO = new TemplateResponseDTO.InspectionTypeDTO();
	                    insTypeDTO.setInsTypeId(insType.getIns_type_id());
	                    insTypeDTO.setName(insType.getName());
	                    return insTypeDTO;
	                }).collect(Collectors.toList())
	        );
	        return dto;
	    });
	}
	  public TemplateDetailsDTO getTemplateDetails(Long template_id) {
	        Template template = templateDAO.getTemplateById(template_id);

	        // Map Template entity to TemplateDetailsDTO
	        TemplateDetailsDTO dto = new TemplateDetailsDTO();
	        dto.setTemplateId(template.getTemplate_id());
	        dto.setTemplateName(template.getTemplate_name());
	        
	        // Map Inspection Type (single entry)
	        template.getInspection_types()
	            .stream()
	            .findFirst()
	            .ifPresent(inspectionType -> {
	                dto.setInsTypeId(inspectionType.getIns_type_id());
	                dto.setInsTypeName(inspectionType.getName());
	            });
	       
	       
	        

	        // Map each ChecklistCategory to CategoryDTO
	        List<CategoryDTO> categoryDTOs = template.getChecklist_categorys().stream()
	                .map(category -> {
	                    CategoryDTO categoryDTO = new CategoryDTO();
	                    categoryDTO.setCategoryId(category.getChecklist_cat_id());
	                    categoryDTO.setCategoryName(category.getChecklist_category_name());
	                    categoryDTO.setStatus(category.getIsActive());
	                    return categoryDTO;
	                })
	                .collect(Collectors.toList());

	        dto.setCategories(categoryDTOs);

	        return dto;
	    }
	  
	  public List<TemplateSummaryDTO> getAllTemplates() {
	        return templateDAO.findAllTemplates();
	    }

	}
