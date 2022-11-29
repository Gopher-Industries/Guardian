package com.example.tg_patient_profile;

public class PatientStatusChoice {

    private int image;
    private String statusText;

    public PatientStatusChoice(int image, String statusText) {
        this.image = image;
        this.statusText = statusText;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
