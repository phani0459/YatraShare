package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 7/3/16.
 */
public class RideDetails implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public RideDetailData Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;


    public class RideDetailData implements Serializable {
        public String PossibleRideGuid;
        public String UserName;
        public String UserGuid;
        public String SameUser;
        public String ProfilePicture;
        public String Departure;
        public String Arrival;
        public String DepartureCity;
        public String ArrivalCity;
        public String DepartureState;
        public String ArrivalState;
        public String RoutePrice;
        public String DepartureDate;
        public String DepartureTime;
        public String StopOvers;
        public String Route;
        public String Views;
        public String OfferPublishedDate;
        public String Chat;
        public String Music;
        public String Smoking;
        public String Food;
        public String ChatTooltip;
        public String MusicTooltip;
        public String SmokingTooltip;
        public String FoodTooltip;
        public String OtherDetails;
        public String RemainingSeats;
        public String MaxLuggageSize;
        public String DepartureTimeFlexi;
        public String MakeDetour;
        public String LadiesOnly;
        public String LicenceStatus;
        public String MobileNumberStatus;
        public String EmailStatus;
        public String FacebookFriends;
        public String Age;
        public String ComfortRating;
        public String VehicleType;
        public String VehicleModel;
        public String Color;
        public String VehiclePicture;
        public String OfferedRides;
        public String MemberSince;
        public String LastLogin;
        public String OwnerGuid;
        public String MobileNumber;
        public String Email;
        public String RideType;
        public ArrayList<RouteCity> RouteCities;
    }

    public class RouteCity implements  Serializable {
        public String CityName;
        public String IsDeparture;
        public String IsDestination;
    }

}
