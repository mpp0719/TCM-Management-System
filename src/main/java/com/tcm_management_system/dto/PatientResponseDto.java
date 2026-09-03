package com.tcm_management_system.dto;

import java.time.LocalDate;

public class PatientResponseDto {
    private Long id;
    private String name;
    private String icNo; // formatted with dashes here, not in entity
    private String gender;
    private String address;
    private String phoneNum;
    private LocalDate lastVisitDate;
    private LocalDate nextAppointmentDate;

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getIcNo() {return icNo;}
    public void setIcNo(String icNo) {this.icNo = icNo;}
    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}
    public String getPhoneNum() {return phoneNum;}
    public void setPhoneNum(String phoneNum) {this.phoneNum = phoneNum;}
    public LocalDate getLastVisitDate() {return lastVisitDate;}
    public void setLastVisitDate(LocalDate lastVisitDate) {this.lastVisitDate = lastVisitDate;}
    public LocalDate getNextAppointmentDate() {return nextAppointmentDate;}
    public void setNextAppointmentDate(LocalDate nextAppointmentDate) {this.nextAppointmentDate = nextAppointmentDate;}
}