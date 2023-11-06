package com.example.tg_patient_profile.model;

public class GP {
  String phone;
  String email;

  public GP(
      final String firstName,
      final String middleName,
      final String lastName,
      final String phone,
      final String email,
      final String fax,
      final String photo) {
    this.phone = phone;
    this.email = email;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public void setEmail(final String email) {
    this.email = email;
  }
}
