package com.tcm_management_system.dto;

public class PatientRequestDto {
    private String name;
    private String icNo;
    private String gender;
    private String address;
    private String phoneNum;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getIcNo() {return icNo;}
    public void setIcNo(String icNo) {this.icNo = icNo;}
    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getPhoneNum() {return phoneNum;}
    public void setPhoneNum(String phoneNum) {this.phoneNum = phoneNum;}
}