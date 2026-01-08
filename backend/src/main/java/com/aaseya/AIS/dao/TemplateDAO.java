package com.aaseya.AIS.dao;
 
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
 
import com.aaseya.AIS.Model.Inspection_Type;
import com.aaseya.AIS.Model.Template;
import com.aaseya.AIS.dto.TemplateSummaryDTO;
import com.aaseya.AIS.utility.CriteriaPaginationUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.EntityNotFoundException;

 
@Repository
public class TemplateDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@PersistenceContext
    private final EntityManager entityManager;
	private final CriteriaPaginationUtil paginationUtil;

	@Autowired
	public TemplateDAO(EntityManager entityManager, CriteriaPaginationUtil paginationUtil) {
		this.entityManager = entityManager;
		this.paginationUtil = paginationUtil;
	}

	public void saveTemplate(Template template) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();

		session.persist(template);
		transaction.commit();
		session.close();
	}

	public List<Template> getTemplateNameAndVersionByInspectionTypeName(String inspectionTypeName) {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Inspection_Type> cr = cb.createQuery(Inspection_Type.class);
		Root<Inspection_Type> root = cr.from(Inspection_Type.class);
		cr.select(root).where(cb.equal(root.get("name"), inspectionTypeName));
		Inspection_Type inspection_type = session.createQuery(cr).getSingleResultOrNull();

		if (inspection_type == null) {
			System.out.println("No Inspection_Type found with name: " + inspectionTypeName);
			return new ArrayList<>();
		}

		long inspectionTypeId = inspection_type.getIns_type_id();
		System.out.println("Result: " + inspectionTypeId);

		CriteriaBuilder cb2 = session.getCriteriaBuilder();
		CriteriaQuery<Template> cr2 = cb2.createQuery(Template.class);
		Root<Template> root2 = cr2.from(Template.class);
		cr2.where(cb2.equal(root2.join("inspection_types").get("ins_type_id"), inspectionTypeId));
		List<Template> templatelist = session.createQuery(cr2).getResultList();
		System.out.println("Template list: " + templatelist);

		session.close();
		return templatelist;
	}
	public Page<Template> getAllTemplatesWithInspectionTypes(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Template> criteriaQuery = criteriaBuilder.createQuery(Template.class);
      // Root<Template> root = criteriaQuery.from(Template.class);

        // Use fetch to eagerly load inspection_types
    //   root.fetch("inspection_types", jakarta.persistence.criteria.JoinType.LEFT);

     //  criteriaQuery.select(root).distinct(true);

        return paginationUtil.getPaginatedData(
	            criteriaQuery,
	            pageable,
	            Template.class,
	            entity -> entity 
	    );
    }
	 public Template getTemplateById(Long template_id) {
	        Session session = sessionFactory.openSession();

	        CriteriaBuilder cb = session.getCriteriaBuilder();
	        CriteriaQuery<Template> query = cb.createQuery(Template.class);
	        Root<Template> root = query.from(Template.class);

	        // Add where condition for templateId
	        query.select(root).where(cb.equal(root.get("template_id"), template_id));

	        Template template = session.createQuery(query).uniqueResult();

	        if (template == null) {
	            throw new EntityNotFoundException("Template not found for ID: " + template_id);
	        }

	        // Since fetch type is EAGER, associated categories should already be loaded
	        Hibernate.initialize(template.getChecklist_categorys());

	        return template;
	    }
	 public List<TemplateSummaryDTO> findAllTemplates() {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<Template> cq = cb.createQuery(Template.class);
	        Root<Template> root = cq.from(Template.class);
	        cq.select(root);

	        List<Template> templates = entityManager.createQuery(cq).getResultList();

	        // Map only required fields
	        return templates.stream().map(t -> {
	            TemplateSummaryDTO dto = new TemplateSummaryDTO();
	            dto.setTemplate_id(t.getTemplate_id());
	            dto.setTemplate_name(t.getTemplate_name());
	            dto.setVersion(t.getVersion());
	            dto.setActive(t.isActive());
	            return dto;
	        }).collect(Collectors.toList());
	    }

	 public Template findLatestTemplateByInspectionType(long inspectionTypeId) {

	        String query = "SELECT t FROM Template t JOIN t.inspection_types it " +

	                       "WHERE it.ins_type_id = :inspectionTypeId " +

	                       "ORDER BY t.effective_from DESC";

	        List<Template> templates = entityManager.createQuery(query, Template.class)

	                .setParameter("inspectionTypeId", inspectionTypeId)

	                .setMaxResults(1)

	                .getResultList();

	        return templates.isEmpty() ? null : templates.get(0);

	    }

	


}