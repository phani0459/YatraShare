package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserFgtPassword {

  @SerializedName("Email")
  String mEmail;

  @SerializedName("PhoneNumber")
  String mPhoneNumber;

  public UserFgtPassword(String email, String phoneNumber) {
    this.mEmail = email;
    this.mPhoneNumber = phoneNumber;
  }
}