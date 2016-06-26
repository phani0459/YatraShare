package com.yatrashare.utils;

import android.net.Uri;

/**
 * Created by KANDAGATLAS on 29-12-2015.
 */
public class Constants {

    public static final String defaultPicURL = "http://www.mediafire.com/convkey/5b62/1bi4vdi37z3wfvczg.jpg?size_id=3";
    public static final String defaultFemaleURL = "http://www.mediafire.com/convkey/9110/ib8u5bpbdjr9frezg.jpg?size_id=3";
    public static final String noImageURL = "http://www.mediafire.com/convkey/f3c2/8h849nu7g5x89fzzg.jpg?size_id=3";
    public static final int PAGE_SIZE = 10;
    public static final int IMAGE_SIZE = 5 * 1000 * 1000;
    public static final int TYPE_ITEM = 3;
    public static final int TYPE_LOADING = 4;
    public static final String peferencesName = "Yatrashare_Prefs";

    public static final String allRides = "0";
    public static final String longRide = "1";
    public static final String dailyRide = "2";
    public static final String vehicleBike = "1";
    public static final String vehicleCar = "2";

    public static Uri getDefaultFemaleURI() {
        Uri uri = Uri.parse(defaultFemaleURL);
        return uri;
    }

    public static Uri getDefaultPicURI() {
        Uri uri = Uri.parse(defaultPicURL);
        return uri;
    }

    public static Uri getDefaultNoImageURI() {
        Uri uri = Uri.parse(noImageURL);
        return uri;
    }

    public static final String HOME_SCREEN_NAME = "HOME";
    public static final String LOGIN_SCREEN_NAME = "LOGIN";
    public static final String LOGIN_WITH_EMAIL_SCREEN_NAME = "LOGIN EMAIL";
    public static final String SIGNUP_SCREEN_NAME = "SIGN UP";
    public static final String PROFILE_SCREEN_NAME = "PROFILE";
    public static final String SEARCH_RIDE_SCREEN_NAME = "SEARCH RIDE";
    public static final String OFFER_RIDE_SCREEN_NAME = "OFFER RIDE";
    public static final String BOOKED_RIDES_SCREEN_NAME = "BOOKED RIDES";
    public static final String OFFERED_RIDES_SCREEN_NAME = "OFFERED RIDES";
    public static final String RATINGS_SCREEN_NAME = "Ratings";
    public static final String MORE_SCREEN_NAME = "MORE SCREEN";
    public static final String WEB_SCREEN_NAME = "WEB SCREEB";
    public static final String EDIT_PROFILE_SCREEN_NAME = "EDIT PROFILE";
    public static final String UPDATE_MOBILE_SCREEN_NAME = "UPDATE MOBILE";
    public static final String BOOK_a_RIDE_SCREEN_NAME = "BOOK A RIDE";
    public static final String MESSAGE_SCREEN_NAME = "MESSAGES SCREEN";
    public static final String MESSAGE_DETAILS_SCREEN_NAME = "MESSAGE Details SCREEN";
    public static final String PROVIDE_RATING_SCREEN_NAME = "Provide rating SCREEN";
    public static final String RIDE_CONFIRM_SCREEN_NAME = "Ride Confirm SCREEN";

    public static final String PREF_USER_EMAIL = "Email";
    public static final String PREF_USER_PHONE = "PhoneNo";
    public static final String PREF_USER_GENDER = "gender";
    public static final String PREF_USER_GUID = "UserGuid";
    public static final String PREF_USER_FIRST_NAME = "First Name";
    public static final String PREF_USER_LAST_NAME = "Last Name";
    public static final String PREF_USER_FB_ID = "id";
    public static final String PREF_USER_DOB = "dob";
    public static final String PREF_USER_COUNTRY = "country";
    public static final String PREF_USER_LICENCE_1 = "LicenceOne";
    public static final String PREF_USER_LICENCE_2 = "LicenceTwo";
    public static final String PREF_USER_TOKEN = "token";

    public static final String PREF_LOGGEDIN = "loggedIn";
    public static final String PREF_MOBILE_VERIFIED = "mobileVerificationStatus";
    public static final String PREF_USER_PROFILE_PIC = "profilePic";
    public static final String ORIGIN_SCREEN_KEY = "ORIGIN SCREEN";

}
