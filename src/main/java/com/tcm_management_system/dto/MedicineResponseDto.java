package com.tcm_management_system.dto;

import java.math.BigDecimal;

public class MedicineResponseDto {
    private Long id;
    private String name;
    private String unit;
    private int currentStock; // computed value included directly here
    private BigDecimal sellingPrice;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getUnit() {return unit;}
    public void setUnit(String unit) {this.unit = unit;}
    public int getCurrentStock() {return currentStock;}
    public void setCurrentStock(int currentStock) {this.currentStock = currentStock;}
    public BigDecimal getSellingPrice() {return sellingPrice;}
    public void setSellingPrice(BigDecimal sellingPrice) {this.sellingPrice = sellingPrice;}
}