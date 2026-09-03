package com.tcm_management_system.dto;

import java.math.BigDecimal;

public class MedicineRequestDto {
    private String name;
    private String unit;
    private BigDecimal sellingPrice;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getUnit() {return unit;}
    public void setUnit(String unit) {this.unit = unit;}
    public BigDecimal getSellingPrice() {return sellingPrice;}
    public void setSellingPrice(BigDecimal sellingPrice) {this.sellingPrice = sellingPrice;}
}