package com.tcm_management_system.dto;

import java.time.LocalDate;

public class VisitRequestDto {
    private LocalDate visitDate;
    private String complaint;
    private String treatmentPlan;
    private LocalDate nextAppointmentDate; // nullable

    public LocalDate getVisitDate() {return visitDate;}
    public void setVisitDate(LocalDate visitDate) {this.visitDate = visitDate;}
    public String getComplaint() {return complaint;}
    public void setComplaint(String complaint) {this.complaint = complaint;}
    public String getTreatmentPlan() {return treatmentPlan;}
    public void setTreatmentPlan(String treatmentPlan) {this.treatmentPlan = treatmentPlan;}
    public LocalDate getNextAppointmentDate() {return nextAppointmentDate;}
    public void setNextAppointmentDate(LocalDate nextAppointmentDate) {this.nextAppointmentDate = nextAppointmentDate;}
}