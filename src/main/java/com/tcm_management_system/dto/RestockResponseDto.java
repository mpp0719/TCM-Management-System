package com.tcm_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RestockResponseDto {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private Integer restockQty;
    private LocalDate restockDate;
    private BigDecimal unitPrice;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getMedicineId() {return medicineId;}
    public void setMedicineId(Long medicineId) {this.medicineId = medicineId;}
    public String getMedicineName() {return medicineName;}
    public void setMedicineName(String medicineName) {this.medicineName = medicineName;}
    public Integer getRestockQty() {return restockQty;}
    public void setRestockQty(Integer restockQty) {this.restockQty = restockQty;}
    public LocalDate getRestockDate() {return restockDate;}
    public void setRestockDate(LocalDate restockDate) {this.restockDate = restockDate;}
    public BigDecimal getUnitPrice() {return unitPrice;}
    public void setUnitPrice(BigDecimal unitPrice) {this.unitPrice = unitPrice;}
}