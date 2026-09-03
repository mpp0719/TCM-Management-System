package com.tcm_management_system.model;
import jakarta.persistence.*;

@Entity
@Table(name = "visit_medication")
public class VisitMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    public VisitMedication() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Visit getVisit() {return visit;}
    public void setVisit(Visit visit) {this.visit = visit;}
    public Medicine getMedicine() {return medicine;}
    public void setMedicine(Medicine medicine) {this.medicine = medicine;}
    public Integer getQuantityDispensed() {return quantityDispensed;}
    public void setQuantityDispensed(Integer quantityDispensed) {this.quantityDispensed = quantityDispensed;}
}