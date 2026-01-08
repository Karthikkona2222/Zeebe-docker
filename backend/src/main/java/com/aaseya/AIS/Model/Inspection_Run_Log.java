package com.aaseya.AIS.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Inspection_Run_Log")
public class Inspection_Run_Log {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_type_id", referencedColumnName = "ins_type_id")
    private Inspection_Type inspectionType;

    @Column(name = "run_date", nullable = false)
    private LocalDate runDate;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Inspection_Type getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(Inspection_Type inspectionType) {
        this.inspectionType = inspectionType;
    }

    public LocalDate getRunDate() {
        return runDate;
    }

    public void setRunDate(LocalDate runDate) {
        this.runDate = runDate;
    }
}