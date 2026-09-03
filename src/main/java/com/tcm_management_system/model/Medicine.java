package com.tcm_management_system.model;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String unit; // fixed per medicine, e.g. "g", "pack", "bottle"

    @OneToMany(mappedBy = "medicine")
    private List<MedicineRestock> restocks = new ArrayList<>();

    @OneToMany(mappedBy = "medicine")
    private List<VisitMedication> dispensedRecords = new ArrayList<>();

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    public Medicine() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getUnit() {return unit;}
    public void setUnit(String unit) {this.unit = unit;}
    public List<MedicineRestock> getRestocks() {return restocks;}
    public void setRestocks(List<MedicineRestock> restocks) {this.restocks = restocks;}
    public List<VisitMedication> getDispensedRecords() {return dispensedRecords;}
    public void setDispensedRecords(List<VisitMedication> dispensedRecords) {this.dispensedRecords = dispensedRecords;}
    public BigDecimal getSellingPrice() {return sellingPrice;}
    public void setSellingPrice(BigDecimal sellingPrice) {this.sellingPrice = sellingPrice;}
}