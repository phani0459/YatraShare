package com.yatrashare.pojos;

import com.google.android.gms.location.places.Place;

import java.io.Serializable;
import java.util.ArrayList;

public class RideInfoDto implements Serializable {
    String mRideDeparture;
    String mRideArrival;
    String mRideType;
    String mLadiesOnly;
    String mTotalkilometers;
    ArrayList<String> mSelectedWeekdays;
    ArrayList<StopOverPointsDto> mStopOvers;
    ArrayList<PossibleRoutesDto> mMainPossibleRoutes;
    ArrayList<PossibleRoutesDto> mAllPossibleRoutes;
    String mTotalprice;
    public String mVehicleType;
    String mTimeFlexi;
    String mDetour;
    String mSeats;
    String mOtherDetails;
    String mCompanyDetails;
    String mUserVehicleModelId;
    String mDepartureTime;
    String mReturnTime;
    String mMaxLuggageSize;

    public String getmRideDeparture() {
        return mRideDeparture;
    }

    public void setmRideDeparture(String mRideDeparture) {
        this.mRideDeparture = mRideDeparture;
    }

    public String getmRideArrival() {
        return mRideArrival;
    }

    public void setmRideArrival(String mRideArrival) {
        this.mRideArrival = mRideArrival;
    }

    public String getmRideType() {
        return mRideType;
    }

    public void setmRideType(String mRideType) {
        this.mRideType = mRideType;
    }

    public String getmLadiesOnly() {
        return mLadiesOnly;
    }

    public void setmLadiesOnly(String mLadiesOnly) {
        this.mLadiesOnly = mLadiesOnly;
    }

    public String getmTotalkilometers() {
        return mTotalkilometers;
    }

    public void setmTotalkilometers(String mTotalkilometers) {
        this.mTotalkilometers = mTotalkilometers;
    }

    public ArrayList<String> getmSelectedWeekdays() {
        return mSelectedWeekdays;
    }

    public void setmSelectedWeekdays(ArrayList<String> mSelectedWeekdays) {
        this.mSelectedWeekdays = mSelectedWeekdays;
    }

    public ArrayList<StopOverPointsDto> getmStopOvers() {
        return mStopOvers;
    }

    public void setmStopOvers(ArrayList<StopOverPointsDto> mStopOvers) {
        this.mStopOvers = mStopOvers;
    }

    public ArrayList<PossibleRoutesDto> getmMainPossibleRoutes() {
        return mMainPossibleRoutes;
    }

    public void setmMainPossibleRoutes(ArrayList<PossibleRoutesDto> mMainPossibleRoutes) {
        this.mMainPossibleRoutes = mMainPossibleRoutes;
    }

    public ArrayList<PossibleRoutesDto> getmAllPossibleRoutes() {
        return mAllPossibleRoutes;
    }

    public void setmAllPossibleRoutes(ArrayList<PossibleRoutesDto> mAllPossibleRoutes) {
        this.mAllPossibleRoutes = mAllPossibleRoutes;
    }

    public String getmTotalprice() {
        return mTotalprice;
    }

    public void setmTotalprice(String mTotalprice) {
        this.mTotalprice = mTotalprice;
    }

    public String getmVehicleType() {
        return mVehicleType;
    }

    public void setmVehicleType(String mVehicleType) {
        this.mVehicleType = mVehicleType;
    }

    public String getmTimeFlexi() {
        return mTimeFlexi;
    }

    public void setmTimeFlexi(String mTimeFlexi) {
        this.mTimeFlexi = mTimeFlexi;
    }

    public String getmDetour() {
        return mDetour;
    }

    public void setmDetour(String mDetour) {
        this.mDetour = mDetour;
    }

    public String getmSeats() {
        return mSeats;
    }

    public void setmSeats(String mSeats) {
        this.mSeats = mSeats;
    }

    public String getmOtherDetails() {
        return mOtherDetails;
    }

    public void setmOtherDetails(String mOtherDetails) {
        this.mOtherDetails = mOtherDetails;
    }

    public String getmCompanyDetails() {
        return mCompanyDetails;
    }

    public void setmCompanyDetails(String mCompanyDetails) {
        this.mCompanyDetails = mCompanyDetails;
    }

    public String getmUserVehicleModelId() {
        return mUserVehicleModelId;
    }

    public void setmUserVehicleModelId(String mUserVehicleModelId) {
        this.mUserVehicleModelId = mUserVehicleModelId;
    }

    public String getmDepartureTime() {
        return mDepartureTime;
    }

    public void setmDepartureTime(String mDepartureTime) {
        this.mDepartureTime = mDepartureTime;
    }

    public String getmReturnTime() {
        return mReturnTime;
    }

    public void setmReturnTime(String returnTime) {
        if (returnTime.contains("Time")) this.mReturnTime = "";
        else this.mReturnTime = returnTime;
    }

    public String getmMaxLuggageSize() {
        return mMaxLuggageSize;
    }

    public void setmMaxLuggageSize(String mMaxLuggageSize) {
        this.mMaxLuggageSize = mMaxLuggageSize;
    }

    public class StopOverPointsDto implements Serializable {
        public Place getStopOves() {
            return stopOves;
        }

        public void setStopOves(Place stopOves) {
            this.stopOves = stopOves;
        }

        Place stopOves;
    }

    public class PossibleRoutesDto implements Serializable {
        Place departure;
        Place arrival;

        public Place getDeparture() {
            return departure;
        }

        public void setDeparture(Place departure) {
            this.departure = departure;
        }

        public Place getArrival() {
            return arrival;
        }

        public void setArrival(Place arrival) {
            this.arrival = arrival;
        }
    }
}
