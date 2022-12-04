package com.example.tg_patient_profile.model;

public class Patient {
    String address, dob, name, phone, photo, underCare;

    Patient(){

    }
    public Patient(String address, String dob, String name, String phone, String photo, String underCare) {
        this.address = address;
        this.dob = dob;
        this.name = name;
        this.phone = phone;
        this.photo = photo;
        this.underCare = underCare;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUnderCare() {
        return underCare;
    }

    public void setUnderCare(String underCare) {
        this.underCare = underCare;
    }
}
