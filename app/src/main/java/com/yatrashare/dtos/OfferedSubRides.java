package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 04-01-2016.
 */
public class OfferedSubRides implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<SubRideData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class SubRideData implements Serializable {
        public String DeparturePoint;
        public String ArrivalPoint;
        public String DepartureDate;
        public String PossibleRideGuid;
        public String DepartureTime;
        public String RoutePrice;
        public String AvailableSeats;
        public String DepartureHour;
        public String DepartureMins;
        public String BookedSeats;
        public String RideBookings;
    }

}
