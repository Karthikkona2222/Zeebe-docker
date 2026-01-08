package com.aaseya.AIS.Model;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
 
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
 
@Entity
@Table
public class Template {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "template_id")
	private long template_id;
	@Column(name = "template_name")
	private String template_name;
 
	public String getTemplate_name() {
		return template_name;
	}
 
	public void setTemplate_name(String template_name) {
		this.template_name = template_name;
	}
 
	@Column(name = "effective_from")
	private String effective_from;
	@Column(name = "version")
	private String version;
	public String getVersion() {
		return version;
	}
 
	public void setVersion(String version) {
		this.version = version;
	}
 
	@Column(name = "isActive")
	private boolean isActive;
 
	@ManyToMany
	private List<Checklist_Category> checklist_categorys;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Inspection_Type.class)
	@JoinTable(name = "template_inspection_type", joinColumns = {
			@JoinColumn(name = "template_id", referencedColumnName = "template_id") }, inverseJoinColumns = {
					@JoinColumn(name = "ins_type_id", referencedColumnName = "ins_type_id") })
	private List<Inspection_Type> inspection_types = new ArrayList<Inspection_Type>();
 
	public List<Inspection_Type> getInspection_types() {
		return inspection_types;
	}
 
	public void setInspection_types(List<Inspection_Type> inspection_types) {
		this.inspection_types = inspection_types;
	}
 
	public List<Checklist_Category> getChecklist_categorys() {
		return checklist_categorys;
	}
 
	public void setChecklist_categorys(List<Checklist_Category> checklist_categorys) {
		this.checklist_categorys = checklist_categorys;
	}
 
	public long getTemplate_id() {
		return template_id;
	}
 
	public void setTemplate_id(long template_id) {
		this.template_id = template_id;
	}
 
	public String getEffective_from() {
		return effective_from;
	}
 
	public void setEffective_from(String effective_from) {
		this.effective_from = effective_from;
	}
 
	public String getVersion_from() {
		return version;
	}
 
	public void setVersion_from(String version) {
		this.version = version;
	}
 
	public boolean isActive() {
		return isActive;
	}
 
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
 
	@Override
	public String toString() {
		return "Template [template_id=" + template_id + ", effective_from=" + effective_from + ", version_from="
				+ version + ", isActive=" + isActive + ", getTemplate_id()=" + getTemplate_id()
				+ ", getEffective_from()=" + getEffective_from() + ", getVersion_from()=" + getVersion_from()
				+ ", isActive()=" + isActive() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
 
}
 
 