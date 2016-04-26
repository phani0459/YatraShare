package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserSignUp {

    @SerializedName("Email")
    String mEmail;

    @SerializedName("FirstName")
    String mFirstName;

    @SerializedName("LastName")
    String userLastName;

    @SerializedName("Password")
    String mPassword;

    @SerializedName("CountryCode")
    String mCountryCode;

    @SerializedName("Gender")
    String mGender;

    @SerializedName("PhoneNumber")
    String mPhoneNumber;

    public UserSignUp(String email, String firstName, String password, String phoneNumber, String mCountryCode, String mGender, String userLastName) {
        this.mEmail = email;
        this.mFirstName = firstName;
        this.mPassword = password;
        this.mPhoneNumber = phoneNumber;
        this.mCountryCode = mCountryCode;
        this.mGender = mGender;
        this.userLastName = userLastName;
    }
}