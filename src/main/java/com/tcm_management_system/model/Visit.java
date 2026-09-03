package com.tcm_management_system.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visit")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(columnDefinition = "TEXT")
    private String complaint;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "next_appointment_date")
    private LocalDate nextAppointmentDate; // nullable

    @OneToMany(mappedBy = "visit")
    private List<VisitMedication> medications = new ArrayList<>();

    public Visit() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Patient getPatient() {return patient;}
    public void setPatient(Patient patient) {this.patient = patient;}
    public LocalDate getVisitDate() {return visitDate;}
    public void setVisitDate(LocalDate visitDate) {this.visitDate = visitDate;}
    public String getComplaint() {return complaint;}
    public void setComplaint(String complaint) {this.complaint = complaint;}
    public String getTreatmentPlan() {return treatmentPlan;}
    public void setTreatmentPlan(String treatmentPlan) {this.treatmentPlan = treatmentPlan;}
    public LocalDate getNextAppointmentDate() {return nextAppointmentDate;}
    public void setNextAppointmentDate(LocalDate nextAppointmentDate) {this.nextAppointmentDate = nextAppointmentDate;}
    public List<VisitMedication> getMedications() {return medications;}
    public void setMedications(List<VisitMedication> medications) {this.medications = medications;}
}