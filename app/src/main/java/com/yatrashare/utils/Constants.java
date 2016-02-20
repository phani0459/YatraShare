package com.yatrashare.utils;

/**
 * Created by KANDAGATLAS on 29-12-2015.
 */
public class Constants {

    public static final String LOGIN_URL = "http://www.yatrashare.com/api/User/Login?";
    /**
     * {
     "Email": "bharathpendru@gmail.com",
     "Password": "abcdefgh"
     }
     */
    public static final String REGISTER_URL = "http://www.yatrashare.com/api/User/Register?";
    /**
     {
     "FirstName": "bharath",
     "Email": "bharath@gmail.com",
     "Password": "abcdefgh",
     "PhoneNumber":"9533393567"
     }
     */
    public static final String PROFILE_URL = "http://www.yatrashare.com/api/Profile/GetProfileInfo?";
    /**
     * userGuid = d64f79de-5745-44e8-b4bd-932838fe88cb
     */
    public static final String FORGOTPWD_URL = "http://www.yatrashare.com/api/User/ForgotPassword?";
    /**
     {
     "Email": "bharathP369@gmail.com",
     "PhoneNumber": "9533393567"
     }
     */

    public static final String SEARCH_RIDE_URL = "http://www.yatrashare.com/api/Rides/SearchRides?";
    /**
     *  {
     "DeparturePoint":"Hyderabad",
      "ArrivalPoint":"",
      "DepartureDate":"",
      "Comfort":"ALLTYPES",
      "currentPage":"1",
      "StartTime":"1",
      "EndTime":"24",
      "LadiesOnly":"All",
      "RideType":"1",
      "VehicleType":"1"
      }
     */

    public static final String BOOKED_RIDES_URL = "http://www.yatrashare.com/api/Rides/GetUserConnectedRides?";
    /**
     * userGuid=d64f79de-5745-44e8-b4bd-932838fe88cb&typeOfRide=2
     */

    public static final String LOGIN_WITH_FB = "http://www.yatrashare.com/api/User/LoginUsingFacebook?";
    /**
     *  FaceBookid:
        Name:
        FriendsCount:
        Email:
        ProfilePicture:

     */

    public static final String PREF_USER_EMAIL = "Email";
    public static final String PREF_USER_GUID = "UserGuid";
    public static final String PREF_USER_NAME = "Name";
    public static final String PREF_USER_FB_ID = "id";
    public static final String PREF_LOGGEDIN = "loggedIn";
    public static final String PREF_USER_PROFILE_PIC = "profilePic";
}
