package com.aaseya.AIS.Model;
 
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
 
@Entity
public class SaveSubmitPreInspectionChecklist implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
    private SaveSubmitPreInspectionId id;
       
    private String selected_answer;
    
    private String Comment;
 
    // Getters and Setters
    public SaveSubmitPreInspectionId getId() {
        return id;
    }
 
    public void setId(SaveSubmitPreInspectionId id) {
        this.id = id;
    }
 
    public String getSelected_answer() {
        return selected_answer;
    }
 
    public void setSelected_answer(String selected_answer) {
        this.selected_answer = selected_answer;
    }
    
 
    public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}


	@Embeddable
    public static class SaveSubmitPreInspectionId implements Serializable {
       
		private static final long serialVersionUID = 1L;
		private long inspectionid;  // Changed to long
        private long preinspectionchecklistid;  // Changed to long

        public SaveSubmitPreInspectionId() {
        }

        public SaveSubmitPreInspectionId(long inspectionid, long preinspectionchecklistid) {
            this.inspectionid = inspectionid;
            this.preinspectionchecklistid = preinspectionchecklistid;
        }

        // Getters and Setters
        public long getInspectionid() {
            return inspectionid;
        }

        public void setInspectionid(long inspectionid) {
            this.inspectionid = inspectionid;
        }

        public long getPreinspectionchecklistid() {
            return preinspectionchecklistid;
        }

        public void setPreinspectionchecklistid(long preinspectionchecklistid) {
            this.preinspectionchecklistid = preinspectionchecklistid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SaveSubmitPreInspectionId that = (SaveSubmitPreInspectionId) o;
            return inspectionid == that.inspectionid && preinspectionchecklistid == that.preinspectionchecklistid;
        }

        @Override
        public int hashCode() {
            return Objects.hash(inspectionid, preinspectionchecklistid);
        }
    }
    
}
 
 