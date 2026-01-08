package com.aaseya.AIS.Model;
 
import java.util.List;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "Checklist_Category")
 
public class Checklist_Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "checklist_cat_id")
	private long checklist_cat_id;
	@Column(name = "checklist_category_name")
	private String checklist_category_name;
	@Column(name = "isActive")
	private Boolean isActive;
 
	public String getChecklist_category_name() {
		return checklist_category_name;
	}
 
	public void setChecklist_category_name(String checklist_category_name) {
		this.checklist_category_name = checklist_category_name;
	}
 
	@Column(name = "version")
	private String version;
	@Column(name = "category_threshold_local")
	private Integer category_threshold_local;
	@Column(name = "category_weightage_local")
	private Integer category_weightage_local;
 
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Checklist_Item> checklist_items;
 
	@ManyToMany(mappedBy = "checklist_categorys")
	private List<Template> templates;
 
	public List<Template> getTemplates() {
		return templates;
	}
 
	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}
 
	public List<Checklist_Item> getChecklist_items() {
		return checklist_items;
	}
 
	public void setChecklist_items(List<Checklist_Item> checklist_items) {
		this.checklist_items = checklist_items;
	}
 
	public long getChecklist_cat_id() {
		return checklist_cat_id;
	}
 
	public void setChecklist_cat_id(long checklist_cat_id) {
		this.checklist_cat_id = checklist_cat_id;
	}
 
	public String getVersion() {
		return version;
	}
 
	public void setVersion(String version) {
		this.version = version;
	}
 
	

	public Integer getCategory_threshold_local() {
		return category_threshold_local;
	}

	public void setCategory_threshold_local(Integer category_threshold_local) {
		this.category_threshold_local = category_threshold_local;
	}

	public Integer getCategory_weightage_local() {
		return category_weightage_local;
	}

	public void setCategory_weightage_local(Integer category_weightage_local) {
		this.category_weightage_local = category_weightage_local;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Checklist_Category [checklist_cat_id=" + checklist_cat_id + ", checklist_category_name="
				+ checklist_category_name + ", isActive=" + isActive + ", version=" + version
				+ ", category_threshold_local=" + category_threshold_local + ", category_weightage_local="
				+ category_weightage_local + ", checklist_items=" + checklist_items + ", templates=" + templates + "]";
	}

	
	
 
	 
}
 