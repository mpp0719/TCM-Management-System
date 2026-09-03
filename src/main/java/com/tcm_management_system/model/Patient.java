package com.tcm_management_system.model;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "ic_no", nullable = false, unique = true)
    private String icNo;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone_num")
    private String phoneNum;

    @OneToMany(mappedBy = "patient")
    private List<Visit> visits = new ArrayList<>();

    public Patient() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
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
    public List<Visit> getVisits() {return visits;}
    public void setVisits(List<Visit> visits) {this.visits = visits;}
}