package com.example.tg_patient_profile.model;

public class GP {
  String firstName;
  String middleName;
  String lastName;
  String clinicAddress;
  String phone;
  String email;
  String fax;
  String photo;

  public GP(
      final String firstName,
      final String middleName,
      final String lastName,
      final String clinicAddress,
      final String phone,
      final String email,
      final String fax,
      final String photo) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.clinicAddress = clinicAddress;
    this.phone = phone;
    this.email = email;
    this.fax = fax;
    this.photo = photo;
  }

  public void setClinicAddress(final String clinicAddress) {
    this.clinicAddress = clinicAddress;
  }

  public void setFax(final String fax) {
    this.fax = fax;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public void setMiddleName(final String middleName) {
    this.middleName = middleName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }
}
