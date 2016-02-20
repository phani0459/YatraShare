package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserSignUp {

  @SerializedName("Email")
  String mEmail;

  @SerializedName("FirstName")
  String mFirstName;

  @SerializedName("Password")
  String mPassword;

  @SerializedName("PhoneNumber")
  String mPhoneNumber;

  public UserSignUp(String email, String firstName, String password, String phoneNumber) {
      this.mEmail = email;
      this.mFirstName = firstName;
      this.mPassword = password;
      this.mPhoneNumber = phoneNumber;
  }
}