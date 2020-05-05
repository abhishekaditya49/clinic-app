package com.example.clinincapp;

import java.sql.Time;

public class patient {
    private String name;
    private int patientNo;
    private int slot;
    private int checkedUp;
    private String day;

    public patient(){

    }

    public patient( int patientNo,String name, String day,int slot, int checkedUp) {
        this.name = name;
        this.patientNo = patientNo;
        this.slot = slot;
        this.checkedUp = checkedUp;
        this.day = day;

    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPatientNo() {
        return patientNo;
    }

    public void setPatientNo(int patientNo) {
        this.patientNo = patientNo;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int isCheckedUp() {
        return checkedUp;
    }

    public void setCheckedUp(int checkedUp) {
        this.checkedUp = checkedUp;
    }
}