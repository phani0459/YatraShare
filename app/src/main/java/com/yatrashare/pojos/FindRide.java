package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by KANDAGATLAS on 26-01-2016.
 */
public class FindRide {


    @SerializedName("DeparturePoint")
    String mDeparturePoint;

    @SerializedName("ArrivalPoint")
    String mArrivalPoint;

    @SerializedName("DepartureDate")
    String mDepartureDate;

    @SerializedName("Comfort")
    String mComfort;

    @SerializedName("currentPage")
    String mcurrentPage;

    @SerializedName("StartTime")
    String mStartTime;

    @SerializedName("EndTime")
    String mEndTime;

    @SerializedName("LadiesOnly")
    String mLadiesOnly;

    @SerializedName("RideType")
    String mRideType;

    @SerializedName("VehicleType")
    String mVehicleType;

    @SerializedName("PageSize")
    String mPageSize;

    public FindRide(String departurePoint, String arrivalPoint,
                    String departureDate, String comfort,
                    String currentPage, String startTime,
                    String endTime, String ladiesOnly,
                    String rideType, String vehicleType, String pageSize) {
        this.mDeparturePoint = departurePoint;
        this.mArrivalPoint = arrivalPoint;
        this.mDepartureDate = departureDate;
        this.mComfort = comfort;
        this.mcurrentPage = currentPage;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mLadiesOnly = ladiesOnly;
        this.mRideType = rideType;
        this.mVehicleType = vehicleType;
        this.mPageSize = pageSize;
    }
}
