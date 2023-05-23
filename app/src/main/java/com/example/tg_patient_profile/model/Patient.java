package com.example.tg_patient_profile.model;

public class Patient {
    String address, dob, patient_name, phone, photo, underCare;
    String first_name, middle_name, last_name;

    Patient(){

    }

    public Patient(String first_name, String middle_name, String last_name) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
    }

    public Patient(String address, String dob, String name, String phone, String photo, String underCare) {
        this.address = address;
        this.dob = dob;
        this.patient_name = name;
        this.phone = phone;
        this.photo = photo;
        this.underCare = underCare;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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
        return patient_name;
    }

    public void setName(String name) {
        this.patient_name = name;
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

    @Override
    public String toString() {
        return "Patient{" +
                "name='" + patient_name + '\'' +
                '}';
    }
}