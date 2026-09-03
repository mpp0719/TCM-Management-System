package com.tcm_management_system.dto;

public class DispensedMedicineDto {
    private Long medicineId;
    private String medicineName;
    private Integer quantityDispensed;
    private String unit;

    public Long getMedicineId() {return medicineId;}
    public void setMedicineId(Long medicineId) {this.medicineId = medicineId;}
    public String getMedicineName() {return medicineName;}
    public void setMedicineName(String medicineName) {this.medicineName = medicineName;}
    public Integer getQuantityDispensed() {return quantityDispensed;}
    public void setQuantityDispensed(Integer quantityDispensed) {this.quantityDispensed = quantityDispensed;}
    public String getUnit() {return unit;}
    public void setUnit(String unit) {this.unit = unit;}
}