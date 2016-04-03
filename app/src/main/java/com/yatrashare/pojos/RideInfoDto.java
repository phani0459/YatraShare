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
        String mstopover_location;
        String mStopOverState;
        String mOrder;
        String mLatitude;
        String mLongitude;
        String mstopoverAddressDetails;
        String mStopOverCity;

        public String getMstopover_location() {
            return mstopover_location;
        }

        public void setMstopover_location(String mstopover_location) {
            this.mstopover_location = mstopover_location;
        }

        public String getmStopOverState() {
            return mStopOverState;
        }

        public void setmStopOverState(String mStopOverState) {
            this.mStopOverState = mStopOverState;
        }

        public String getmOrder() {
            return mOrder;
        }

        public void setmOrder(String mOrder) {
            this.mOrder = mOrder;
        }

        public String getmLatitude() {
            return mLatitude;
        }

        public void setmLatitude(String mLatitude) {
            this.mLatitude = mLatitude;
        }

        public String getmLongitude() {
            return mLongitude;
        }

        public void setmLongitude(String mLongitude) {
            this.mLongitude = mLongitude;
        }

        public String getMstopoverAddressDetails() {
            return mstopoverAddressDetails;
        }

        public void setMstopoverAddressDetails(String mstopoverAddressDetails) {
            this.mstopoverAddressDetails = mstopoverAddressDetails;
        }

        public String getmStopOverCity() {
            return mStopOverCity;
        }

        public void setmStopOverCity(String mStopOverCity) {
            this.mStopOverCity = mStopOverCity;
        }
    }

    public class PossibleRoutesDto implements Serializable {
        String mDeparture;
        String mArrival;
        String mDepartureCity;
        String mArrivalCity;
        String mDepartureState;
        String mArrivalState;
        String mRoutePrice;
        String mUserUpdatedPrice;
        String mreadOnly;
        String mkilometers;
        String morder;
        String mMainRoute;
        String mTimeframe;

        public String getmDeparture() {
            return mDeparture;
        }

        public void setmDeparture(String mDeparture) {
            this.mDeparture = mDeparture;
        }

        public String getmArrival() {
            return mArrival;
        }

        public void setmArrival(String mArrival) {
            this.mArrival = mArrival;
        }

        public String getmDepartureCity() {
            return mDepartureCity;
        }

        public void setmDepartureCity(String mDepartureCity) {
            this.mDepartureCity = mDepartureCity;
        }

        public String getmArrivalCity() {
            return mArrivalCity;
        }

        public void setmArrivalCity(String mArrivalCity) {
            this.mArrivalCity = mArrivalCity;
        }

        public String getmDepartureState() {
            return mDepartureState;
        }

        public void setmDepartureState(String mDepartureState) {
            this.mDepartureState = mDepartureState;
        }

        public String getmArrivalState() {
            return mArrivalState;
        }

        public void setmArrivalState(String mArrivalState) {
            this.mArrivalState = mArrivalState;
        }

        public String getmRoutePrice() {
            return mRoutePrice;
        }

        public void setmRoutePrice(String mRoutePrice) {
            this.mRoutePrice = mRoutePrice;
        }

        public String getmUserUpdatedPrice() {
            return mUserUpdatedPrice;
        }

        public void setmUserUpdatedPrice(String mUserUpdatedPrice) {
            this.mUserUpdatedPrice = mUserUpdatedPrice;
        }

        public String getMreadOnly() {
            return mreadOnly;
        }

        public void setMreadOnly(String mreadOnly) {
            this.mreadOnly = mreadOnly;
        }

        public String getMkilometers() {
            return mkilometers;
        }

        public void setMkilometers(String mkilometers) {
            this.mkilometers = mkilometers;
        }

        public String getMorder() {
            return morder;
        }

        public void setMorder(String morder) {
            this.morder = morder;
        }

        public String getmMainRoute() {
            return mMainRoute;
        }

        public void setmMainRoute(String mMainRoute) {
            this.mMainRoute = mMainRoute;
        }

        public String getmTimeframe() {
            return mTimeframe;
        }

        public void setmTimeframe(String mTimeframe) {
            this.mTimeframe = mTimeframe;
        }
    }
}
