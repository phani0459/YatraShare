package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserRating {

  @SerializedName("receiverGuid")
  String receiverGuid;

  @SerializedName("stars")
  String stars;

  @SerializedName("feedback")
  String feedback;

  @SerializedName("travellingType")
  String travellingType;

  public UserRating(String receiverGuid, String stars, String feedback, String travellingType) {
      this.receiverGuid = receiverGuid;
      this.stars = stars;
      this.feedback = feedback;
      this.travellingType = travellingType;
  }
}