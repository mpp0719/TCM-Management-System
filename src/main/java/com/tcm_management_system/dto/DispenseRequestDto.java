package com.tcm_management_system.dto;

public class DispenseRequestDto {
    private Long visitId;
    private Long medicineId;
    private Integer quantityDispensed;

    public Long getVisitId() {return visitId;}
    public void setVisitId(Long visitId) {this.visitId = visitId;}
    public Long getMedicineId() {return medicineId;}
    public void setMedicineId(Long medicineId) {this.medicineId = medicineId;}
    public Integer getQuantityDispensed() {return quantityDispensed;}
    public void setQuantityDispensed(Integer quantityDispensed) {this.quantityDispensed = quantityDispensed;}
}