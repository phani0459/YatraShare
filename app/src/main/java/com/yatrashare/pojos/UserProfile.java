package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserProfile {

    @SerializedName("Email")
    String mEmail;

    @SerializedName("FirstName")
    String mFirstName;

    @SerializedName("LastName")
    String mLastName;

    @SerializedName("PhoneNo")
    String mPhoneNo;

    @SerializedName("Dob")
    String mDob;

    @SerializedName("Gender")
    String mGender;

    @SerializedName("AboutMe")
    String mAboutMe;

  public UserProfile(String email, String firstName, String lastName, String phoneNumber, String dob, String gender, String aboutMe) {
      this.mEmail = email;
      this.mFirstName = firstName;
      this.mLastName = lastName;
      this.mPhoneNo = phoneNumber;
      this.mDob = dob;
      this.mGender = gender;
      this.mAboutMe = aboutMe;
  }
}