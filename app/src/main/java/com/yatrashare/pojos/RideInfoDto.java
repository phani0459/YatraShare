package com.yatrashare.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class RideInfoDto implements Serializable {
    String mRideDeparture;
    String mRideArrival;
    String mRideType;
    String mTotalkilometers;
    ArrayList<String> mSelectedWeekdays;
    ArrayList<StopOvers> mStopOvers;
    ArrayList<PossibleRoutesDto> mMainPossibleRoutes;
    ArrayList<PossibleRoutesDto> mAllPossibleRoutes;
    String mTotalprice;
    String mCompanyDetails;
    String mDepartureTime;
    String mReturnTime;
    String mDepartureDate;

    public String getUserUpdatedPrice() {
        return userUpdatedPrice;
    }

    public void setUserUpdatedPrice(String userUpdatedPrice) {
        this.userUpdatedPrice = userUpdatedPrice;
    }

    String userUpdatedPrice;

    public String getmReturnDate() {
        return mReturnDate;
    }

    public void setmReturnDate(String mReturnDate) {
        this.mReturnDate = mReturnDate;
    }

    public String getmDepartureDate() {
        return mDepartureDate;
    }

    public void setmDepartureDate(String mDepartureDate) {
        this.mDepartureDate = mDepartureDate;
    }

    String mReturnDate;

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


    public String getmCompanyDetails() {
        return mCompanyDetails;
    }

    public void setmCompanyDetails(String mCompanyDetails) {
        this.mCompanyDetails = mCompanyDetails;
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

        public boolean isMainRoute() {
            return isMainRoute;
        }

        public void setIsMainRoute(boolean isMainRoute) {
            this.isMainRoute = isMainRoute;
        }

        boolean isMainRoute;

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

        String arrival;
        String departure;

        public String getArrival() {
            return arrival;
        }

        public void setArrival(String arrival) {
            this.arrival = arrival;
        }

        public String getDeparture() {
            return departure;
        }

        public void setDeparture(String departure) {
            this.departure = departure;
        }
    }
}
