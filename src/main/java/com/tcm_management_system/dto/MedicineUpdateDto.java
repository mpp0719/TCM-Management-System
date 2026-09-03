package com.tcm_management_system.dto;

import java.math.BigDecimal;

public class MedicineUpdateDto {
    private String name;
    private BigDecimal sellingPrice;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public BigDecimal getSellingPrice() {return sellingPrice;}
    public void setSellingPrice(BigDecimal sellingPrice) {this.sellingPrice = sellingPrice;}
}