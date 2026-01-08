package com.aaseya.AIS.dto;

import java.util.List;

public class TemplateResponseDTO {

    private long templateId;
    private String templateName;
    private List<InspectionTypeDTO> inspectionTypes;

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<InspectionTypeDTO> getInspectionTypes() {
        return inspectionTypes;
    }

    public void setInspectionTypes(List<InspectionTypeDTO> inspectionTypes) {
        this.inspectionTypes = inspectionTypes;
    }

    public static class InspectionTypeDTO {
        private long insTypeId;
        private String name;

        public long getInsTypeId() {
            return insTypeId;
        }

        public void setInsTypeId(long insTypeId) {
            this.insTypeId = insTypeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
