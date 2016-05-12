package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 07-01-2016.
 */
public class SearchRides implements Serializable {

    public String ContentEncoding;
    public String ContentType;
    public ArrayList<SearchData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class SearchData implements Serializable {
        public String RideGuid;
        public String PossibleRideGuid;
        public String RideId;
        public String UserName;
        public String ProfilePicture;
        public String DeparturePoint;
        public String ArrivalPoint;
        public String Route;
        public String DepartureCity;
        public int Chat;
        public String ComfortRating;
        public String Comfort;
        public String RoutePrice;
        public String RemainingSeats;
        public String RideDate;
        public String VehicleModel;
        public String VehicleType;
        public String PossibleRideId;
        public String ArrivalCity;
        public int Food;
        public int Music;
        public int Smoking;
        public String ChatTooltip;
        public String MusicTooltip;
        public String SmokingTooltip;
        public String FoodTooltip;
        public String TotalRides;
        public String LadiesOnly;
        public String Age;
        public String VehicleRegdType;
    }
}
