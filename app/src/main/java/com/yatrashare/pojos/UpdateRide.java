package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

public class UpdateRide {

    @SerializedName("PossibleRideGuid")
    String mPossibleRideGuid;

    @SerializedName("Seats")
    String mSeats;

    @SerializedName("DepartureDate")
    String mDepartureDate;

    @SerializedName("DepartureTime")
    String mDepartureTime;

    @SerializedName("TimeFlexi")
    String mTimeFlexi;

    @SerializedName("LadiesOnly")
    String mLadiesOnly;

    @SerializedName("Detour")
    String mDetour;

    @SerializedName("MaxLuggageSize")
    String mMaxLuggageSize;

    @SerializedName("OtherDetails")
    String mOtherDetails;

    @SerializedName("UserUpdatedPrice")
    String mUserUpdatedPrice;

    @SerializedName("UserVehicleModelId")
    String mUserVehicleModelId;

    public UpdateRide(String mPossibleRideGuid, String mSeats, String mDepartureDate, String mDepartureTime, String mTimeFlexi, String mLadiesOnly, String mDetour, String mMaxLuggageSize,
                      String mOtherDetails, String mUserUpdatedPrice, String mUserVehicleModelId) {
        this.mPossibleRideGuid = mPossibleRideGuid;
        this.mSeats = mSeats;
        this.mDepartureDate = mDepartureDate;
        this.mDepartureTime = mDepartureTime;
        this.mTimeFlexi = mTimeFlexi;
        this.mLadiesOnly = mLadiesOnly;
        this.mDetour = mDetour;
        this.mMaxLuggageSize = mMaxLuggageSize;
        this.mOtherDetails = mOtherDetails;
        this.mUserUpdatedPrice = mUserUpdatedPrice;
        this.mUserVehicleModelId = mUserVehicleModelId;
    }
}