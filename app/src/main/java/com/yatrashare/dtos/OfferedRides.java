package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 04-01-2016.
 */
public class OfferedRides implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<OfferedRideData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class OfferedRideData implements Serializable {
        public String DeparturePoint;
        public String ArrivalPoint;
        public String DepartureDate;
        public boolean PendingBooking;
        public String RideGuid;
        public String TotalUpcomingRides;
        public String TotalPastRides;
        public ArrayList<PossibleRouteData> PossibleRoutes;
    }

    public class PossibleRouteData implements Serializable {
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
