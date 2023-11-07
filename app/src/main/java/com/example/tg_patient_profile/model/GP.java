package com.example.tg_patient_profile.model;

public class GP {
  String phone;
  String email;
  private String firstName;
  private String middleName;
  private String lastName;
  private String fax;
  private final String photo;

  public GP(
      final String firstName,
      final String middleName,
      final String lastName,
      final String phone,
      final String email,
      final String fax,
      final String photo) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.phone = phone;
    this.email = email;
    this.fax = fax;
    this.photo = photo;
  }

  public void setMiddleName(final String middleName) {
    this.middleName = middleName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public void setFax(final String fax) {
    this.fax = fax;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public void setEmail(final String email) {
    this.email = email;
  }
}
