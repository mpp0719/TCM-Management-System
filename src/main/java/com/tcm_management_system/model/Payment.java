package com.tcm_management_system.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment", uniqueConstraints = @UniqueConstraint(columnNames = "visit_id"))
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private Visit visit;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "treatment_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal treatmentFee;

    @Column(name = "medicine_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal medicineCost;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    public Payment() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Visit getVisit() {return visit;}
    public void setVisit(Visit visit) {this.visit = visit;}
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