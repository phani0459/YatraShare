package com.yatrashare.interfaces;

import com.yatrashare.dtos.BookedRides;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.Rating;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.FindRide;
import com.yatrashare.pojos.UserFBLogin;
import com.yatrashare.pojos.UserFgtPassword;
import com.yatrashare.pojos.UserLogin;
import com.yatrashare.pojos.UserPreferences;
import com.yatrashare.pojos.UserProfile;
import com.yatrashare.pojos.UserSignUp;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface YatraShareAPI {

    public static final String BASE_URL = "http://www.yatrashare.com";

    @GET("/api/Rides/GetUserConnectedRides?typeOfRide=2")
    Call<BookedRides> bookedRides(@Query("userGuid") String userGuide);

    @POST("/api/User/Login")
    Call<String> userLogin(@Body UserLogin userLogin);

    @POST("/api/Rides/BookYourSeat")
    Call<String> bookRide(@Query("userGuid") String userGuide, @Query("possibleRideGuid") String possibleRideGuid, @Query("passengers") String passengers);

    @POST("/api/Rides/SearchRides")
    Call<SearchRides> FindRides(@Body FindRide findRide);

    @POST("/api/User/ForgotPassword")
    Call<UserDataDTO> userFgtPwd(@Body UserFgtPassword userFgtPwd);

    @POST("/api/User/LoginUsingFacebook")
    Call<String> userFBLogin(@Body UserFBLogin userFBLogin);

    @POST("/api/User/Register")
    Call<UserDataDTO> userRegistration(@Body UserSignUp userSignUp);

    @POST("/api/Profile/UpdatePreferences")
    Call<UserDataDTO> updateUserPreferences(@Query("userGuid") String userGuide, @Body UserPreferences userPreferences);

    @POST("/api/Profile/ChangePassword")
    Call<UserDataDTO> changePassword(@Query("userGuid") String userGuide, @Query("newPassword") String newPassword);

    @GET("/api/Profile/GetPublicProfileInfo")
    Call<Profile> userPublicProfile(@Query("publicUserGuid") String userGuide);

    @GET("/api/Profile/GetProfileInfo")
    Call<Profile> userBasicProfile(@Query("userGuid") String userGuide);

    @POST("/api/Profile/UpdateUserProfile")
    Call<UserDataDTO> updateProfile(@Query("userGuid") String userGuide, @Body UserProfile userProfile);

    @GET("/api/Profile/GetBasicInfo")
    Call<Profile> userProfile(@Query("userGuid") String userGuide);

    @GET("/api/Profile/GetReceivedRatings")
    Call<Rating> userRatings(@Query("userGuid") String userGuide);

}