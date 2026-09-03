package com.tcm_management_system.dto;

import java.time.LocalDate;
import java.util.List;

public class VisitResponseDto {
    private Long id;
    private Long patientId;
    private LocalDate visitDate;
    private String complaint;
    private String treatmentPlan;
    private LocalDate nextAppointmentDate;
    private List<DispensedMedicineDto> medications;
    private String patientName;
    private String patientPhoneNum;
    private boolean hasPayment;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getPatientId() {return patientId;}
    public void setPatientId(Long patientId) {this.patientId = patientId;}
    public LocalDate getVisitDate() {return visitDate;}
    public void setVisitDate(LocalDate visitDate) {this.visitDate = visitDate;}
    public String getComplaint() {return complaint;}
    public void setComplaint(String complaint) {this.complaint = complaint;}
    public String getTreatmentPlan() {return treatmentPlan;}
    public void setTreatmentPlan(String treatmentPlan) {this.treatmentPlan = treatmentPlan;}
    public LocalDate getNextAppointmentDate() {return nextAppointmentDate;}
    public void setNextAppointmentDate(LocalDate nextAppointmentDate) {this.nextAppointmentDate = nextAppointmentDate;}
    public List<DispensedMedicineDto> getMedications() {return medications;}
    public void setMedications(List<DispensedMedicineDto> medications) {this.medications = medications;}
    public String getPatientName() {return patientName;}
    public void setPatientName(String patientName) {this.patientName = patientName;}
    public String getPatientPhoneNum() {return patientPhoneNum;}
    public void setPatientPhoneNum(String patientPhoneNum) {this.patientPhoneNum = patientPhoneNum;}
    public boolean isHasPayment() {return hasPayment;}
    public void setHasPayment(boolean hasPayment) {this.hasPayment = hasPayment;}
}