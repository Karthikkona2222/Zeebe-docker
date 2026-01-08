package com.aaseya.AIS.dto;

import java.util.List;

public class InspectionTypeGetAdminDTO {
    private long inspectionId;
    private String inspectionType;
    private String threshold;
    private String enity_size;
    private List<SlaDetailDTO> slaDetails;

    // Getters and Setters

    public long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(long inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getInspectionType() {
        return inspectionType;
    }
    

    public String getEnity_size() {
		return enity_size;
	}

	public void setEnity_size(String enity_size) {
		this.enity_size = enity_size;
	}

	public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public List<SlaDetailDTO> getSlaDetails() {
        return slaDetails;
    }

    public void setSlaDetails(List<SlaDetailDTO> slaDetails) {
        this.slaDetails = slaDetails;
    }

    public static class SlaDetailDTO {
        private String entitySize;
        private GoalDeadlineDTO inspector;
        private GoalDeadlineDTO reviewer;
        private GoalDeadlineDTO approver;

        // Getters and Setters

        public String getEntitySize() {
            return entitySize;
        }

        public void setEntitySize(String entitySize) {
            this.entitySize = entitySize;
        }

        public GoalDeadlineDTO getInspector() {
            return inspector;
        }

        public void setInspector(GoalDeadlineDTO inspector) {
            this.inspector = inspector;
        }

        public GoalDeadlineDTO getReviewer() {
            return reviewer;
        }

        public void setReviewer(GoalDeadlineDTO reviewer) {
            this.reviewer = reviewer;
        }

        public GoalDeadlineDTO getApprover() {
            return approver;
        }

        public void setApprover(GoalDeadlineDTO approver) {
            this.approver = approver;
        }
    }

    public static class GoalDeadlineDTO {
        private int goal;
        private int deadline;

        // Getters and Setters

        public int getGoal() {
            return goal;
        }

        public void setGoal(int goal) {
            this.goal = goal;
        }

        public int getDeadline() {
            return deadline;
        }

        public void setDeadline(int deadline) {
            this.deadline = deadline;
        }
    }
}
