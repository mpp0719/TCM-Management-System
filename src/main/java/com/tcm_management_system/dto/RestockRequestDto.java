package com.tcm_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RestockRequestDto {
    private Long medicineId;
    private Integer restockQty;
    private LocalDate restockDate;
    private BigDecimal unitPrice;

    public Long getMedicineId() {return medicineId;}
    public void setMedicineId(Long medicineId) {this.medicineId = medicineId;}
    public Integer getRestockQty() {return restockQty;}
    public void setRestockQty(Integer restockQty) {this.restockQty = restockQty;}
    public LocalDate getRestockDate() {return restockDate;}
    public void setRestockDate(LocalDate restockDate) {this.restockDate = restockDate;}
    public BigDecimal getUnitPrice() {return unitPrice;}
    public void setUnitPrice(BigDecimal unitPrice) {this.unitPrice = unitPrice;}
}