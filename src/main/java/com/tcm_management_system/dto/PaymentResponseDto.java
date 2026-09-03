package com.tcm_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentResponseDto {
    private Long id;
    private Long visitId;
    private String patientName;
    private LocalDate paymentDate;
    private String paymentMethod;
    private BigDecimal treatmentFee;
    private BigDecimal medicineCost;
    private BigDecimal totalAmount;


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getVisitId() {return visitId;}
    public void setVisitId(Long visitId) {this.visitId = visitId;}
    public String getPatientName() {return patientName;}
    public void setPatientName(String patientName) {this.patientName = patientName;}
    public LocalDate getPaymentDate() {return paymentDate;}
    public void setPaymentDate(LocalDate paymentDate) {this.paymentDate = paymentDate;}
    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}
    public BigDecimal getTreatmentFee() {return treatmentFee;}
    public void setTreatmentFee(BigDecimal treatmentFee) {this.treatmentFee = treatmentFee;}
    public BigDecimal getMedicineCost() {return medicineCost;}
    public void setMedicineCost(BigDecimal medicineCost) {this.medicineCost = medicineCost;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}
}