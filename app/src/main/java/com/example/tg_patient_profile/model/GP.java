package com.example.tg_patient_profile.model;

public class GP {
    String first_name, middle_name, last_name, clinic_address, phone, email, fax, photo;

    GP(){

    }

    public GP(String first_name, String middle_name, String last_name, String clinic_address, String phone, String email, String fax, String photo) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.clinic_address = clinic_address;
        this.phone = phone;
        this.email = email;
        this.fax = fax;
        this.photo = photo;
    }

    public String getClinic_address() {
        return clinic_address;
    }

    public void setClinic_address(String clinic_address) {
        this.clinic_address = clinic_address;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
