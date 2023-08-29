package com.example.tg_patient_profile.model;

public class Patient {
    String address, dob, patient_name, phone, photo, underCare;
    String first_name, middle_name, last_name;
    String medicareNo, westwenAffairesNo;
    String nok_id1, nok_id2;
    String gp_id1,gp_id2;

    Patient(){

    }

    public Patient(String first_name, String middle_name, String last_name) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
    }

    //constructor for adding a patient
    public Patient(String dob, String first_name, String middle_name, String last_name, String medicareNo, String westwenAffairesNo, String nok_id1, String nok_id2, String gp_id1, String gp_id2) {
        this.dob = dob;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.medicareNo = medicareNo;
        this.westwenAffairesNo = westwenAffairesNo;
        this.nok_id1 = nok_id1;
        this.nok_id2 = nok_id2;
        this.gp_id1 = gp_id1;
        this.gp_id2 = gp_id2;
    }



    public Patient(String address, String dob, String name, String phone, String photo, String underCare) {
        this.address = address;
        this.dob = dob;
        this.patient_name = name;
        this.phone = phone;
        this.photo = photo;
        this.underCare = underCare;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getMedicareNo() {
        return medicareNo;
    }

    public void setMedicareNo(String medicareNo) {
        this.medicareNo = medicareNo;
    }

    public String getWestwenAffairesNo() {
        return westwenAffairesNo;
    }

    public void setWestwenAffairesNo(String westwenAffairesNo) {
        this.westwenAffairesNo = westwenAffairesNo;
    }

    public String getNok_id1() {
        return nok_id1;
    }

    public void setNok_id1(String nok_id1) {
        this.nok_id1 = nok_id1;
    }

    public String getNok_id2() {
        return nok_id2;
    }

    public void setNok_id2(String nok_id2) {
        this.nok_id2 = nok_id2;
    }

    public String getGp_id1() {
        return gp_id1;
    }

    public void setGp_id1(String gp_id1) {
        this.gp_id1 = gp_id1;
    }

    public String getGp_id2() {
        return gp_id2;
    }

    public void setGp_id2(String gp_id2) {
        this.gp_id2 = gp_id2;
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
                "address='" + address + '\'' +
                ", dob='" + dob + '\'' +
                ", patient_name='" + patient_name + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                ", underCare='" + underCare + '\'' +
                ", first_name='" + first_name + '\'' +
                ", middle_name='" + middle_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", medicareNo='" + medicareNo + '\'' +
                ", westwenAffairesNo='" + westwenAffairesNo + '\'' +
                ", nok_id1='" + nok_id1 + '\'' +
                ", nok_id2='" + nok_id2 + '\'' +
                ", gp_id1='" + gp_id1 + '\'' +
                ", gp_id2='" + gp_id2 + '\'' +
                '}';
    }
}