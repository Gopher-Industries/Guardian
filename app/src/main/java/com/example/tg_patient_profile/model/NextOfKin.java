package com.example.tg_patient_profile.model;

import androidx.annotation.NonNull;

public class NextOfKin {
  String firstName;
  String middleName;
  String lastName;
  String homeAddress;
  String mobilePhone;
  String emailAddress;
  String photo;

  public NextOfKin(
      final String firstName,
      final String middleName,
      final String lastName,
      final String homeAddress,
      final String mobilePhone,
      final String emailAddress,
      final String photo) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.homeAddress = homeAddress;
    this.mobilePhone = mobilePhone;
    this.emailAddress = emailAddress;
    this.photo = photo;
  }

  public void setHomeAddress(final String homeAddress) {
    this.homeAddress = homeAddress;
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

  public void setMobilePhone(final String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public void setEmailAddress(final String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @NonNull
  @Override
  public String toString() {
    return "NextofKin{"
        + "first_name='"
        + firstName
        + '\''
        + ", middle_name='"
        + middleName
        + '\''
        + ", last_name='"
        + lastName
        + '\''
        + ", home_address='"
        + homeAddress
        + '\''
        + ", mobile_phone='"
        + mobilePhone
        + '\''
        + ", email_address='"
        + emailAddress
        + '\''
        + ", photo='"
        + photo
        + '\''
        + '}';
  }
}
