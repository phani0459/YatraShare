package com.yatrashare.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class RideInfoDto implements Serializable {
    String mRideDeparture;
    String mRideArrival;
    String mRideType;
    String mLadiesOnly;
    String mTotalkilometers;
    ArrayList<String> mSelectedWeekdays;
    ArrayList<StopOvers> mStopOvers;
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

    public ArrayList<StopOvers> getmStopOvers() {
        return mStopOvers;
    }

    public void setmStopOvers(ArrayList<StopOvers> mStopOvers) {
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


    public class StopOvers implements Serializable {
        public double getStopOverLatitude() {
            return stopOverLatitude;
        }

        public void setStopOverLatitude(double stopOverLatitude) {
            this.stopOverLatitude = stopOverLatitude;
        }

        public double getStopOverLongitude() {
            return stopOverLongitude;
        }

        public void setStopOverLongitude(double stopOverLongitude) {
            this.stopOverLongitude = stopOverLongitude;
        }

        double stopOverLatitude;
        double stopOverLongitude;

        public String getStopOverLocation() {
            return stopOverLocation;
        }

        public void setStopOverLocation(String stopOverLocation) {
            this.stopOverLocation = stopOverLocation;
        }

        String stopOverLocation;
    }

    public class PossibleRoutesDto implements Serializable {
        double departureLatitude;
        double departureLongitude;
        String mainDeparturePlace;
        String mainArrivalPlace;

        public String getMainDeparturePlace() {
            return mainDeparturePlace;
        }

        public void setMainDeparturePlace(String mainDeparturePlace) {
            this.mainDeparturePlace = mainDeparturePlace;
        }

        public String getMainArrivalPlace() {
            return mainArrivalPlace;
        }

        public void setMainArrivalPlace(String mainArrivalPlace) {
            this.mainArrivalPlace = mainArrivalPlace;
        }

        public double getDepartureLatitude() {
            return departureLatitude;
        }

        public void setDepartureLatitude(double departureLatitude) {
            this.departureLatitude = departureLatitude;
        }

        public double getDepartureLongitude() {
            return departureLongitude;
        }

        public void setDepartureLongitude(double departureLongitude) {
            this.departureLongitude = departureLongitude;
        }

        public double getArrivalLatitude() {
            return arrivalLatitude;
        }

        public void setArrivalLatitude(double arrivalLatitude) {
            this.arrivalLatitude = arrivalLatitude;
        }

        public double getArrivalLongitude() {
            return arrivalLongitude;
        }

        public void setArrivalLongitude(double arrivalLongitude) {
            this.arrivalLongitude = arrivalLongitude;
        }

        double arrivalLatitude;
        double arrivalLongitude;

    }
}
