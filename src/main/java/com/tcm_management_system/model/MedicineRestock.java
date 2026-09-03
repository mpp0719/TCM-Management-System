package com.tcm_management_system.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicine_restock")
public class MedicineRestock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "restock_qty", nullable = false)
    private Integer restockQty;

    @Column(name = "restock_date", nullable = false)
    private LocalDate restockDate;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    public MedicineRestock() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Medicine getMedicine() {return medicine;}
    public void setMedicine(Medicine medicine) {this.medicine = medicine;}
    public Integer getRestockQty() {return restockQty;}
    public void setRestockQty(Integer restockQty) {this.restockQty = restockQty;}
    public LocalDate getRestockDate() {return restockDate;}
    public void setRestockDate(LocalDate restockDate) {this.restockDate = restockDate;}
    public BigDecimal getUnitPrice() {return unitPrice;}
    public void setUnitPrice(BigDecimal unitPrice) {this.unitPrice = unitPrice;}
}