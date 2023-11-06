package com.example.tg_patient_profile.model;

public class Patient {
    String patient_id;
    String address, dob, patient_name, phone, photo, underCare;
    String first_name, middle_name, last_name;
    String medicareNo, westwenAffairesNo;
    String nok_id1, nok_id2;
    String gp_id1, gp_id2;

    Patient() {

    }

    public Patient(final String patient_id, final String first_name, final String last_name) {
        this.patient_id = patient_id;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    //constructor for adding a patient
    public Patient(final String dob, final String first_name, final String middle_name, final String last_name, final String medicareNo, final String westwenAffairesNo, final String nok_id1, final String nok_id2, final String gp_id1, final String gp_id2) {
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


    public Patient(final String address, final String dob, final String name, final String phone, final String photo, final String underCare) {
        this.address = address;
        this.dob = dob;
        this.patient_name = name;
        this.phone = phone;
        this.photo = photo;
        this.underCare = underCare;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(final String patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(final String patient_name) {
        this.patient_name = patient_name;
    }

    public String getMedicareNo() {
        return medicareNo;
    }

    public void setMedicareNo(final String medicareNo) {
        this.medicareNo = medicareNo;
    }

    public String getWestwenAffairesNo() {
        return westwenAffairesNo;
    }

    public void setWestwenAffairesNo(final String westwenAffairesNo) {
        this.westwenAffairesNo = westwenAffairesNo;
    }

    public String getNok_id1() {
        return nok_id1;
    }

    public void setNok_id1(final String nok_id1) {
        this.nok_id1 = nok_id1;
    }

    public String getNok_id2() {
        return nok_id2;
    }

    public void setNok_id2(final String nok_id2) {
        this.nok_id2 = nok_id2;
    }

    public String getGp_id1() {
        return gp_id1;
    }

    public void setGp_id1(final String gp_id1) {
        this.gp_id1 = gp_id1;
    }

    public String getGp_id2() {
        return gp_id2;
    }

    public void setGp_id2(final String gp_id2) {
        this.gp_id2 = gp_id2;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(final String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(final String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(final String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(final String dob) {
        this.dob = dob;
    }

    public String getName() {
        return patient_name;
    }

    public void setName(final String name) {
        this.patient_name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(final String photo) {
        this.photo = photo;
    }

    public String getUnderCare() {
        return underCare;
    }

    public void setUnderCare(final String underCare) {
        this.underCare = underCare;
    }

    @Override
    public String toString() {
        return "Patient{" + "address='" + address + '\'' + ", dob='" + dob + '\'' + ", patient_name='" + patient_name + '\'' + ", phone='" + phone + '\'' + ", photo='" + photo + '\'' + ", underCare='" + underCare + '\'' + ", first_name='" + first_name + '\'' + ", middle_name='" + middle_name + '\'' + ", last_name='" + last_name + '\'' + ", medicareNo='" + medicareNo + '\'' + ", westwenAffairesNo='" + westwenAffairesNo + '\'' + ", nok_id1='" + nok_id1 + '\'' + ", nok_id2='" + nok_id2 + '\'' + ", gp_id1='" + gp_id1 + '\'' + ", gp_id2='" + gp_id2 + '\'' + '}';
    }
}