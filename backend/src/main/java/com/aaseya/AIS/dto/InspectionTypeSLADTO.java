package com.aaseya.AIS.dto;

import java.util.Map;

public class InspectionTypeSLADTO {

    private String name;
    private Map<String, SLAEntityDetails> entitySizes;
    private String action; // New field for action


    // Getters and setters
    public String getName() {
        return name;
    }
    
    

    public String getAction() {
		return action;
	}



	public void setAction(String action) {
		this.action = action;
	}



	public void setName(String name) {
        this.name = name;
    }

    public Map<String, SLAEntityDetails> getEntitySizes() {
        return entitySizes;
    }

    public void setEntitySizes(Map<String, SLAEntityDetails> entitySizes) {
        this.entitySizes = entitySizes;
    }

    // Nested class to represent SLA details for each entity size
    public static class SLAEntityDetails {
        private int inspectorGoal;
        private int inspectorDeadline;
        private int reviewerGoal;
        private int reviewerDeadline;
        private int approverGoal;
        private int approverDeadline;

        // Getters and setters
        public int getInspectorGoal() {
            return inspectorGoal;
        }

        public void setInspectorGoal(int inspectorGoal) {
            this.inspectorGoal = inspectorGoal;
        }

        public int getInspectorDeadline() {
            return inspectorDeadline;
        }

        public void setInspectorDeadline(int inspectorDeadline) {
            this.inspectorDeadline = inspectorDeadline;
        }

        public int getReviewerGoal() {
            return reviewerGoal;
        }

        public void setReviewerGoal(int reviewerGoal) {
            this.reviewerGoal = reviewerGoal;
        }

        public int getReviewerDeadline() {
            return reviewerDeadline;
        }

        public void setReviewerDeadline(int reviewerDeadline) {
            this.reviewerDeadline = reviewerDeadline;
        }

        public int getApproverGoal() {
            return approverGoal;
        }

        public void setApproverGoal(int approverGoal) {
            this.approverGoal = approverGoal;
        }

        public int getApproverDeadline() {
            return approverDeadline;
        }

        public void setApproverDeadline(int approverDeadline) {
            this.approverDeadline = approverDeadline;
        }
    }
}
