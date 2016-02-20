package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UserPreferences {

  @SerializedName("Chat")
  String mChat;

  @SerializedName("Music")
  String mMusic;

  @SerializedName("Food")
  String mFood;

  @SerializedName("Smoking")
  String mSmoking;

  @SerializedName("Pets")
  String mPets;

  public UserPreferences(String chat, String music, String smoking, String food, String pets) {
      this.mChat = chat;
      this.mMusic = music;
      this.mFood = food;
      this.mPets = pets;
      this.mSmoking = smoking;
  }
}