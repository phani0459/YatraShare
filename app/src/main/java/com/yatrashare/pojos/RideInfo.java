package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RideInfo {
    @SerializedName("RideDeparture")
    String mRideDeparture;

    @SerializedName("RideArrival")
    String mRideArrival;

    @SerializedName("RideType")
    String mRideType;

    @SerializedName("LadiesOnly")
    String mLadiesOnly;

    @SerializedName("Totalkilometers")
    String mTotalkilometers;

    @SerializedName("SelectedWeekdays")
    ArrayList<String> mSelectedWeekdays;

    @SerializedName("StopOvers")
    ArrayList<StopOverPoints> mStopOvers;

    @SerializedName("MainPossibleRoutes")
    ArrayList<PossibleRoutes> mMainPossibleRoutes;

    @SerializedName("AllPossibleRoutes")
    ArrayList<PossibleRoutes> mAllPossibleRoutes;

    @SerializedName("Totalprice")
    String mTotalprice;

    @SerializedName("VehicleType")
    String mVehicleType;

    @SerializedName("TimeFlexi")
    String mTimeFlexi;

    @SerializedName("Detour")
    String mDetour;

    @SerializedName("Seats")
    String mSeats;

    @SerializedName("OtherDetails")
    String mOtherDetails;

    @SerializedName("CompanyDetails")
    String mCompanyDetails;

    @SerializedName("UserVehicleModelId")
    String mUserVehicleModelId;

    @SerializedName("DepartureTime")
    String mDepartureTime;

    @SerializedName("ReturnTime")
    String mReturnTime;

    @SerializedName("MaxLuggageSize")
    String mMaxLuggageSize;

    public RideInfo(String mRideDeparture, String mRideArrival, String mTotalkilometers, String mTotalprice, String mTimeFlexi, String mDetour, String mSeats,
                    String mOtherDetails, String mCompanyDetails,
                    String mUserVehicleModelId, String mDepartureTime,
                    String mReturnTime, String ladiesOnly, ArrayList<String> selectedWeekDays,
                    String rideType, String vehicleType, String mMaxLuggageSize,
                    ArrayList<PossibleRoutes> mMainPossibleRoutes, ArrayList<PossibleRoutes> mAllPossibleRoutes, ArrayList<StopOverPoints> mStopOvers) {
        this.mRideDeparture = mRideDeparture;
        this.mRideArrival = mRideArrival;
        this.mTotalkilometers = mTotalkilometers;
        this.mTotalprice = mTotalprice;
        this.mSelectedWeekdays = selectedWeekDays;
        this.mTimeFlexi = mTimeFlexi;
        this.mDetour = mDetour;
        this.mSeats = mSeats;
        this.mOtherDetails = mOtherDetails;
        this.mCompanyDetails = mCompanyDetails;
        this.mUserVehicleModelId = mUserVehicleModelId;
        this.mAllPossibleRoutes = mAllPossibleRoutes;
        this.mMainPossibleRoutes = mMainPossibleRoutes;
        this.mStopOvers = mStopOvers;
        this.mDepartureTime = mDepartureTime;
        this.mReturnTime = mReturnTime;
        this.mLadiesOnly = ladiesOnly;
        this.mRideType = rideType;
        this.mVehicleType = vehicleType;
        this.mMaxLuggageSize = mMaxLuggageSize;
    }

    public class StopOverPoints {
        @SerializedName("stopover_location")
        String mstopover_location;

        @SerializedName("StopOverState")
        String mStopOverState;

        @SerializedName("Order")
        String mOrder;

        @SerializedName("Latitude")
        String mLatitude;

        @SerializedName("Longitude")
        String mLongitude;

        @SerializedName("stopoverAddressDetails")
        String mstopoverAddressDetails;

        @SerializedName("StopOverCity")
        String mStopOverCity;

        public StopOverPoints(String mstopover_location, String mStopOverState, String mOrder,
                              String mLatitude, String mLongitude, String mstopoverAddressDetails, String mStopOverCity) {
            this.mstopover_location = mstopover_location;
            this.mStopOverState = mStopOverState;
            this.mOrder = mOrder;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
            this.mstopoverAddressDetails = mstopoverAddressDetails;
            this.mStopOverCity = mStopOverCity;
        }

    }

    public class PossibleRoutes {
        @SerializedName("Departure")
        String mDeparture;

        @SerializedName("Arrival")
        String mArrival;

        @SerializedName("DepartureCity")
        String mDepartureCity;

        @SerializedName("ArrivalCity")
        String mArrivalCity;

        @SerializedName("DepartureState")
        String mDepartureState;

        @SerializedName("ArrivalState")
        String mArrivalState;

        @SerializedName("RoutePrice")
        String mRoutePrice;

        @SerializedName("UserUpdatedPrice")
        String mUserUpdatedPrice;

        @SerializedName("readOnly")
        String mreadOnly;

        @SerializedName("kilometers")
        String mkilometers;

        @SerializedName("order")
        String morder;

        @SerializedName("MainRoute")
        String mMainRoute;

        @SerializedName("Timeframe")
        String mTimeframe;

        public PossibleRoutes(String mDeparture, String mArrival, String mDepartureCity, String mMainRoute, String mTimeframe,
                              String mArrivalCity, String mDepartureState, String mArrivalState, String mRoutePrice, String mUserUpdatedPrice, String mreadOnly, String mkilometers, String morder) {
            this.mDeparture = mDeparture;
            this.mArrival = mArrival;
            this.mDepartureCity = mDepartureCity;
            this.mArrivalCity = mArrivalCity;
            this.mDepartureState = mDepartureState;
            this.mArrivalState = mArrivalState;
            this.mRoutePrice = mRoutePrice;


            this.mUserUpdatedPrice = mUserUpdatedPrice;
            this.mreadOnly = mreadOnly;
            this.mkilometers = mkilometers;
            this.morder = morder;
            this.mMainRoute = mMainRoute;
            this.mTimeframe = mTimeframe;
        }

    }
}
