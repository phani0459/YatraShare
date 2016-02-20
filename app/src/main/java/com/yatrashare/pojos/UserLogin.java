package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserLogin {

  @SerializedName("Email")
  String mEmail;

  @SerializedName("Password")
  String mPassword;

  public UserLogin(String email, String password) {
    this.mEmail = email;
    this.mPassword = password;
  }
}