package com.tcm_management_system.dto;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentUpdateDto {
    private LocalDate paymentDate;
    private String paymentMethod;
    private BigDecimal treatmentFee;

    public LocalDate getPaymentDate() {return paymentDate;}
    public void setPaymentDate(LocalDate paymentDate) {this.paymentDate = paymentDate;}
    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}
    public BigDecimal getTreatmentFee() {return treatmentFee;}
    public void setTreatmentFee(BigDecimal treatmentFee) {this.treatmentFee = treatmentFee;}
}