package com.yatrashare.interfaces;

import com.yatrashare.dtos.BookedRides;
import com.yatrashare.dtos.MessageDetails;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.Rating;
import com.yatrashare.dtos.RatingReceiverInfo;
import com.yatrashare.dtos.RideDetails;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.FindRide;
import com.yatrashare.pojos.UserFBLogin;
import com.yatrashare.pojos.UserFgtPassword;
import com.yatrashare.pojos.UserLogin;
import com.yatrashare.pojos.UserPreferences;
import com.yatrashare.pojos.UserProfile;
import com.yatrashare.pojos.UserRating;
import com.yatrashare.pojos.UserSignUp;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface YatraShareAPI {

    public static final String BASE_URL = "http://www.yatrashare.com";

    @GET("/api/Rides/GetUserConnectedRides")
    Call<BookedRides> bookedRides(@Query("userGuid") String userGuide, @Query("typeOfRide") String typeOfRide);

    @POST("/api/User/Login")
    Call<String> userLogin(@Body UserLogin userLogin);

    @POST("/api/Rides/BookYourSeat")
    Call<UserDataDTO> bookRide(@Query("userGuid") String userGuide, @Query("possibleRideGuid") String possibleRideGuid, @Query("passengers") String passengers);

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

    @POST("/api/Profile/ResendVerificationCode")
    Call<UserDataDTO> sendVerificationCode(@Query("userGuid") String userGuide);

    @POST("/api/Profile/VerifyMobile")
    Call<UserDataDTO> verifyMobileNumber(@Query("userGuid") String userGuide, @Query("mobileNumber") String mobileNumber, @Query("verificationCode") String verificationCode);

    @GET("/api/Profile/GetBasicInfo")
    Call<Profile> userProfile(@Query("userGuid") String userGuide);

    @GET("/api/Profile/GetReceivedRatings")
    Call<Rating> userReceivedRatings(@Query("userGuid") String userGuide);

    @GET("/api/Profile/GetGivenRatings")
    Call<Rating> userGivenRatings(@Query("userGuid") String userGuide);

    @GET("/api/Profile/GetRatingReceiverUserinfo")
    Call<RatingReceiverInfo> getRatingReceiverUserinfoId(@Query("userGuid") String userGuide, @Query("mobileNo") String mobileNumber);

    @GET("/api/Profile/GetRatingReceiverUserinfo")
    Call<UserDataDTO> getRatingReceiverUserinfoMobile(@Query("mobileNumber") String mobileNumber);

    @GET("/api/Messages/GetUserMessagesConversation")
    Call<MessageDetails> userMessageConversation(@Query("userGuid") String userGuide, @Query("messageGuid") String messageGuid);

    @GET("/api/Messages/GetInboxMessages")
    Call<MessagesList> userInboxMessages(@Query("userGuid") String userGuide, @Query("currentpage") String currentpage, @Query("pagesize") String pagesize);

    @GET("/api/Messages/DeleteMessage")
    Call<UserDataDTO> deleteMessage(@Query("userGuid") String userGuide, @Query("messageGuid") String messageGuid);

    @POST("/api/Profile/GiveRatingtoUser")
    Call<UserDataDTO> giveRatingtoUser(@Query("userGuid") String userGuide, @Body UserRating userRating);

    @POST("/api/Messages/SendMessage")
    Call<UserDataDTO> sendMessage(@Query("userGuid") String userGuide, @Query("receiverGuid") String receiverGuid, @Query("possibleRideGuid") String possibleRideGuid, @Query("message") String message);

    @POST("/api/Messages/SendReplyMessage")
    Call<UserDataDTO> SendReplyMessage(@Query("userGuid") String userGuide, @Query("messageGuid") String messageGuid, @Query("message") String message);

    @GET("/api/Rides/GetRideDetails")
    Call<RideDetails> getRideDetails(@Query("possibleRideGuid") String possibleRideGuid);

    @POST("/api/Messages/GetUserMessagesConversation")
    Call<MessageDetails> getMessageConversation(@Query("userGuid") String userGuide, @Query("messageGuid") String messageGuid);

    @POST("/api/Rides/DeleteSeatBooking")
    Call<UserDataDTO> deleteRide(@Query("userGuid") String userGuide, @Query("rideBookingId") String rideBookingId);

    @POST("/api/Rides/CancelSeatBooking")
    Call<UserDataDTO> cancelSeat(@Query("userGuid") String userGuide, @Query("rideBookingId") String rideBookingId);

    @POST("/api/Rides/SendJourneyDetails")
    Call<UserDataDTO> sendJourneyDetails(@Query("userGuid") String userGuide, @Query("rideBookingId") String rideBookingId);

}