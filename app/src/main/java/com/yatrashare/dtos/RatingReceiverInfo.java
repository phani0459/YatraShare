package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 04-01-2016.
 */
public class RatingReceiverInfo implements Serializable{
    public String ContentEncoding;
    public String ContentType;
    public RatingReceiverInfoData Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class RatingReceiverInfoData implements Serializable{
        public String RatingId;
        public String ProviderId;
        public String ReceiverId;
        public String ProviderGuid;
        public String ReceiverGuid;
        public String Stars;
        public String TravellingType;
        public String Feedback;
        public String RatingStatus;
        public String ProviderName;
        public String ProviderProfilePic;
        public String ReceiverName;
        public String ReceiverProfilePic;
        public String RatingGivenDate;
        public String RatingGivenTime;
        public String AvgRating;
        public String TotalRatingsCount;
        public String PossibleRideGuid;
        public String UserMobileNumber;
        public String CountryCode;
        public String Email;
        public String MobileNumber;
    }
}
