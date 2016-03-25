package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 25/3/16.
 */
public class GetUserBookings implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<UserBookingData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class UserBookingData implements Serializable {
        public int RideBookingId;
        public String BookedUserGuid;
        public String BookedUserName;
        public String BookedUserProfilePic;
        public String BookedUserMobileNo;
        public String BookedUserEmail;
        public String BookedUserNameFacebook;
        public int BookedSeats;
        public int BookingStatus;
        public String BookedTime;
        public String Route;
        public String DepartureTime;
        public String FacebookId;
    }
}
