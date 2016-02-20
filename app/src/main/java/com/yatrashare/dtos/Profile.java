package com.yatrashare.dtos;

import java.io.Serializable;

/**
 * Created by KANDAGATLAS on 03-01-2016.
 */
public class Profile implements Serializable {

    public String ContentEncoding;
    public String ContentType;
    public UserData Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class UserData implements Serializable{

        /**
         * Public Profile Info?public user Guide
         */
        public String UserName;
        public String ProfilePicture;
        public String UserAvgRating;
        public String UserRatingsCount;
        public String VehicleType;
        public String VehicleModel;
        public String ComfortRating;
        public String Color;
        public String CarPicture;
        public int Chat;
        public int Food;
        public int Smoking;
        public int Music;
        public String ChatTooltip;
        public String MusicTooltip;
        public String SmokingTooltip;
        public String FoodTooltip;
        public String MobileNumberStatus;
        public String EmailStatus;
        public String FacebookFriends;
        public String OfferedRides;
        public String VehiclePicture;
        public String Age;

        /**
         * Profile Info? user Guide
         */
        public String UserId;
        public String FirstName;
        public String LastName;
        public String FullName;
        public String MemberSince;
        public String Email;
        public String PhoneNo;
        public String Dob;
        public String Gender;
        public String AboutMe;
        public String ProfilePhoto;
        public String Licence1;
        public String Licence2;
        public String LicenceStatus;

        public AdditionalInfo Additionalinfo;
        public VerificationStatus VerificationStatus;

        public String LastLogin;
        public String UserStatus;
    }

    public class AdditionalInfo implements Serializable{
        public String AddressLine1;
        public String AddressLine2;
        public String City;
        public String Country;
        public String State;
        public String Postcode;
    }

    public class VerificationStatus implements Serializable{
        public String EmailStatus;
        public String AboutMeStatus;
        public String ProfilePictureStatus;
        public String LicenceStatus;
        public String MobileNumberStatus;
        public String MobileVerificationCode;
        public String MobileVerificationTime;
        public String VehicleCount;
        public String FacebookExists;
        public String UserPreferences;
    }
}
