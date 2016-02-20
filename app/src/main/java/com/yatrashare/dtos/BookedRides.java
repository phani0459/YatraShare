package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 04-01-2016.
 */
public class BookedRides implements Serializable{
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<BookedData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class BookedData implements Serializable{
        public long RideBookingId;
        public String OwnerName;
        public String OwnerGuid;
        public String PossibleRideGuid;
        public String OwnerPicture;
        public String Ride;
        public String RideTime;
        public long BookedSeats;
        public String BookingStatus;
        public String RideStatus;
    }
}
