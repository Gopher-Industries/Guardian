package com.example.tg_patient_profile.model;

import java.util.Date;

public class Patient {
    String name, id, medicare, caretaker, dob, photo;
//    Date dob;
//    byte[] photo;

    public Patient() {
    }

    public Patient(String photo, String name, String id, String dob, String medicare, String caretaker) {
        this.name = name;
        this.id = id;
        this.dob = dob;
        this.medicare = medicare;
        this.caretaker = caretaker;
        this.photo = photo;
    }

//    public Patient(byte[] photo, String name, String id, Date dob, String medicare, String caretaker) {
//        this.name = name;
//        this.id = id;
//        this.dob = dob;
//        this.medicare = medicare;
//        this.caretaker = caretaker;
//        this.photo = photo;
//    }


    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMedicare() {
        return medicare;
    }

    public void setMedicare(String medicare) {
        this.medicare = medicare;
    }

    public String getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(String caretaker) {
        this.caretaker = caretaker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
