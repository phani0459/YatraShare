package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserFBLogin {

  @SerializedName("Email")
  String mEmail;

  @SerializedName("ProfilePicture")
  String mProfilePicture;

  @SerializedName("FaceBookid")
  String mFaceBookid;

  @SerializedName("Name")
  String mName;

  public UserFBLogin(String email, String profilePicture, String name, String fbId) {
      this.mEmail = email;
      this.mProfilePicture = profilePicture;
      this.mFaceBookid = fbId;
      this.mName = name;
  }
}