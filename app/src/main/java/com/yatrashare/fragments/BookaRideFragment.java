package com.yatrashare.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.yatrashare.R;
import com.yatrashare.activities.EditRideActivity;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.RideDetails;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookaRideFragment extends Fragment {


    private static final String TAG = BookaRideFragment.class.getSimpleName();
    private Context mContext;
    private SearchRides.SearchData rideData;
    @Bind(R.id.bookRideProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.bookRideProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.rideTime)
    public TextView rideTime;
    @Bind(R.id.rideDate)
    public TextView rideDate;
    @Bind(R.id.rideFrom)
    public TextView rideFrom;
    @Bind(R.id.rideTo)
    public TextView rideTo;
    @Bind(R.id.seatsAvailable)
    public TextView seatsAvailable;
    @Bind(R.id.pricePerSeat)
    public TextView pricePerSeat;
    @Bind(R.id.usersPreferences)
    public TextView usersPreferences;
    @Bind(R.id.chatPrefernece)
    public ImageView mChatPreference;
    @Bind(R.id.musicPreference)
    public ImageView mMusicPreference;
    @Bind(R.id.smokePreference)
    public ImageView mSmokePreference;
    @Bind(R.id.foodPreference)
    public ImageView mFoodPreference;
    @Bind(R.id.im_vehicle_drawee)
    public SimpleDraweeView vehicleDrawee;
    @Bind(R.id.im_vehicle)
    public ImageView vehicleImageView;
    @Bind(R.id.tv_vehicleName)
    public TextView vehicleNameTextView;
    @Bind(R.id.tv_vehiclecolor)
    public TextView vehicleColorTextView;
    @Bind(R.id.rb_comfortLevel)
    public RatingBar comfortLevelRating;

    @Bind(R.id.tv_detour)
    public TextView detourTextView;
    @Bind(R.id.tv_luggage)
    public TextView luggageTextView;
    @Bind(R.id.tv_scheduleFlexi)
    public TextView timeFlexiTextView;
    @Bind(R.id.tv_lastLogin)
    public TextView lastLoginTextView;
    @Bind(R.id.tv_memberSince)
    public TextView memSinceTextView;
    @Bind(R.id.tv_fbFriends)
    public TextView fbFriendsTextView;

    @Bind(R.id.tv_owner_Name)
    public TextView vehicleOwnerName;
    @Bind(R.id.tv_owner_age)
    public TextView ownerAge;
    @Bind(R.id.tv_owner_phone_status)
    public TextView ownerPhoneStatus;
    @Bind(R.id.tv_owner_email_status)
    public TextView ownerEmailStatus;
    @Bind(R.id.tv_owner_licencestatus)
    public TextView ownerLicenceStatus;
    @Bind(R.id.im_vehicle_owner_drawee)
    public SimpleDraweeView vehicleOwnerDrawee;
    @Bind(R.id.fabEditProfile)
    public android.support.design.widget.FloatingActionButton editProfileFab;
    @Bind(R.id.fab_menu)
    public FloatingActionMenu fabMenu;

    private boolean canMessage;
    private RideDetails rideDetails;
    private String userGuid;


    public BookaRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View inflatedLayout = inflater.inflate(R.layout.fragment_book_ride, container, false);
        ButterKnife.bind(this, inflatedLayout);
        mContext = getActivity();

        FloatingActionButton mBookFab = (FloatingActionButton) inflatedLayout.findViewById(R.id.bookRideFab);
        FloatingActionButton mMessageFab = (FloatingActionButton) inflatedLayout.findViewById(R.id.messageRiderFab);

        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        rideData = (SearchRides.SearchData) getArguments().getSerializable("RIDE");
        if (rideData != null) {
            try {
                rideTime.setSelected(true);
                rideFrom.setSelected(true);
                rideTo.setSelected(true);
                usersPreferences.setSelected(true);
//                rideDate.setText(rideData.RideDate);
                rideTime.setText(rideData.RideDate);
                rideFrom.setText(rideData.DeparturePoint != null ? rideData.DeparturePoint : "");
                rideTo.setText(rideData.ArrivalPoint != null ? rideData.ArrivalPoint : "");
                seatsAvailable.setText(rideData.RemainingSeats + " Seat(s) left");
                pricePerSeat.setText("" + Utils.getCurrency(mContext) + " " + rideData.RoutePrice + " /Seat");
                usersPreferences.setText("Preferences");

                int chatPref = rideData.Chat;
                int musicPref = rideData.Music;
                int smokePref = rideData.Smoking;
                int foodPref = rideData.Food;

                mChatPreference.setImageResource(chatPref == 1 ? R.drawable.chat_allow : chatPref == 3 ? R.drawable.chat_not_allow : R.drawable.chat);
                mMusicPreference.setImageResource(musicPref == 1 ? R.drawable.music_allow : musicPref == 3 ? R.drawable.music_not_allow : R.drawable.music_not_much);
                mSmokePreference.setImageResource(smokePref == 1 ? R.drawable.smoke_allow : smokePref == 3 ? R.drawable.smoke_not_allow : R.drawable.smoke_not_much);
                mFoodPreference.setImageResource(foodPref == 1 ? R.drawable.food_allow : foodPref == 3 ? R.drawable.food_not_allow : R.drawable.food_not_much);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ((HomeActivity) mContext).setTitle("Book Ride");

        mBookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isLoggedIn(getActivity())) {
                    Utils.showLoginDialog(getActivity(), Constants.BOOK_a_RIDE_SCREEN_NAME);
                } else {
                    SharedPreferences sharedPreferences = Utils.getSharedPrefs(mContext);
                    String gender = sharedPreferences.getString(Constants.PREF_USER_GENDER, "");
                    if (!TextUtils.isEmpty(rideData.LadiesOnly) && rideData.LadiesOnly.equalsIgnoreCase("true")) {
                        if (gender.equalsIgnoreCase("Female")) {
                            if (mSharedPreferences.getBoolean(Constants.PREF_MOBILE_VERIFIED, false)) {
                                showSeatsDialog(userGuid, rideData.PossibleRideGuid, rideData.RemainingSeats);
                            } else {
//                              Utils.showMobileVerifyDialog(getActivity(), "Mobile Number has to be verified to book a seat", Constants.BOOK_a_RIDE_SCREEN_NAME);
                                ((HomeActivity) mContext).loadScreen(HomeActivity.UPDATE_MOBILE_SCREEN, false, null, Constants.BOOK_a_RIDE_SCREEN_NAME);
                            }
                        } else {
                            Utils.showToast(mContext, "Ride is only for ladies");
                        }
                    } else {
                        if (mSharedPreferences.getBoolean(Constants.PREF_MOBILE_VERIFIED, false)) {
                            showSeatsDialog(userGuid, rideData.PossibleRideGuid, rideData.RemainingSeats);
                        } else {
//                          Utils.showMobileVerifyDialog(getActivity(), "Mobile Number has to be verified to book a seat", Constants.BOOK_a_RIDE_SCREEN_NAME);
                            ((HomeActivity) mContext).loadScreen(HomeActivity.UPDATE_MOBILE_SCREEN, false, false, Constants.BOOK_a_RIDE_SCREEN_NAME);
                        }
                    }
                }
            }
        });

        mMessageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isLoggedIn(getActivity())) {
                    Utils.showLoginDialog(getActivity(), Constants.BOOK_a_RIDE_SCREEN_NAME);
                } else {
                    openMessage();
                }
            }
        });

        editProfileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isLoggedIn(getActivity())) {
                    if (rideDetails != null) {
                        Intent intent = new Intent(mContext, EditRideActivity.class);
                        intent.putExtra("USERGUIDE", userGuid);
                        intent.putExtra("RIDE DETAILS", rideDetails);
                        startActivity(intent);
                        ((HomeActivity) mContext).loadHomePage(false, "");
                    }
                }
            }
        });

        if (Utils.isInternetAvailable(mContext)) getRideDetails();

        return inflatedLayout;
    }

    public void openMessage() {
        if (canMessage && rideDetails != null) {
            ((HomeActivity) mContext).loadScreen(HomeActivity.MESSAGE_DETAILS_SCREEN, false, rideDetails.Data, Constants.BOOK_a_RIDE_SCREEN_NAME);
        }
    }

    public void getRideDetails() {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Call<RideDetails> call = Utils.getYatraShareAPI(mContext).getRideDetails(rideData.PossibleRideGuid, "");
        //asynchronous call
        call.enqueue(new Callback<RideDetails>() {
            /*
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
            */
            @Override
            public void onResponse(retrofit.Response<RideDetails> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null && response.isSuccess()) {
                    if (response.body().Data != null) {
                        rideDetails = response.body();
                        canMessage = true;
                        loadRideDetails(rideDetails);
                    }
                }
                Utils.showProgress(false, mProgressView, mProgressBGView);
            }

            /*
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    private void loadRideDetails(RideDetails rideDetails) {
        if (rideDetails != null && rideDetails.Data != null) {
            String vehicleName = "<font color=\"#818181\">Name: </font>" + "<font color=\"#000000\">" + rideDetails.Data.VehicleModel + "</font>";
            String vehicleColor = "<font color=\"#818181\">Color: </font>" + "<font color=\"#000000\">" + rideDetails.Data.Color + "</font>";
            String detour = "<font color=\"#818181\">Detour: </font>" + "<font color=\"#000000\">" + rideDetails.Data.MakeDetour + "</font>";
            String luggage = "<font color=\"#818181\">Luggage Size: </font>" + "<font color=\"#000000\">" + rideDetails.Data.MaxLuggageSize + "</font>";
            String scheduleFlexi = "<font color=\"#818181\">Schedule Flexibility: </font>" + "<font color=\"#000000\">" + rideDetails.Data.DepartureTimeFlexi + "</font>";

            String phoneStatus = "<font color=\"#818181\">Phone: </font>" + "<font color=\"#000000\">" + (rideDetails.Data.MobileNumberStatus == 2 ? "Verified" : "Not Verified") + "</font>";
            String emailStatus = "<font color=\"#818181\">Email: </font>" + "<font color=\"#000000\">" + (rideDetails.Data.EmailStatus == 2 ? "Verified" : "Not Verified") + "</font>";
            String licenceStatus = "<font color=\"#818181\">Licence: </font>" + "<font color=\"#000000\">" + (rideDetails.Data.LicenceStatus == 2 ? "Verified" : "Not Verified") + "</font>";

            String vehiclePic = rideDetails.Data.VehiclePicture;
            String profilePicture = rideDetails.Data.ProfilePicture;

            if (userGuid.equalsIgnoreCase(rideDetails.Data.OwnerGuid)) {
                fabMenu.setVisibility(View.GONE);
                editProfileFab.setVisibility(View.VISIBLE);
            }

            vehicleNameTextView.setText(Html.fromHtml(vehicleName));
            vehicleColorTextView.setText(Html.fromHtml(vehicleColor));

            rideDate.setText("Start Date: " + rideDetails.Data.DepartureDate);
            rideTime.setText("Departure Time: " + rideDetails.Data.DepartureTime);

            if (vehiclePic != null && !vehiclePic.isEmpty() && !vehiclePic.startsWith("../")) {
                vehicleDrawee.setVisibility(View.VISIBLE);
                vehicleImageView.setVisibility(View.GONE);
                Uri uri = Uri.parse(vehiclePic);
                vehicleDrawee.setImageURI(uri);
            } else {
                vehicleDrawee.setVisibility(View.GONE);
                vehicleImageView.setVisibility(View.VISIBLE);
                if (rideDetails.Data.VehicleType.equalsIgnoreCase("Bike")) {
                    vehicleImageView.setImageResource(R.drawable.bike_icon);
                } else {
                    vehicleImageView.setImageResource(R.drawable.car_icon);
                }
            }

            if (profilePicture != null && !profilePicture.isEmpty() && !profilePicture.startsWith("/")) {
                Uri uri = Uri.parse(profilePicture);
                vehicleOwnerDrawee.setImageURI(uri);
            } else {
                vehicleOwnerDrawee.setImageURI(Constants.getDefaultPicURI());
            }

            try {
                vehicleOwnerName.setText(rideDetails.Data.UserName);
                if (rideDetails.Data.Age <= 0) {
                    ownerAge.setVisibility(View.GONE);
                } else {
                    ownerAge.setText(rideDetails.Data.Age + " Years Old");
                }
                ownerPhoneStatus.setText(Html.fromHtml(phoneStatus));
                ownerEmailStatus.setText(Html.fromHtml(emailStatus));
                ownerLicenceStatus.setText(Html.fromHtml(licenceStatus));

                comfortLevelRating.setRating(Float.parseFloat(rideDetails.Data.ComfortRating));
                detourTextView.setText(Html.fromHtml(detour));
                luggageTextView.setText(Html.fromHtml(luggage));
                timeFlexiTextView.setText(Html.fromHtml(scheduleFlexi));
                lastLoginTextView.setText("Last Login: " + rideDetails.Data.LastLogin);
                memSinceTextView.setText("Member Since: " + rideDetails.Data.MemberSince);
                fbFriendsTextView.setText("Facebook Friends: " + rideDetails.Data.FacebookFriends);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void showSeatsDialog(final String userGuid, final String possibleRideGuid, String remainingSeats) {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.no_of_seats_item);
        Button resetPwdButton = (Button) dialog.findViewById(R.id.btnReset);
        Button cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
        final Spinner noOfSeatsSpinner = (Spinner) dialog.findViewById(R.id.noOfSeatsSpinner);

        int remainingSeatsInt = 0;
        ArrayList<String> strings = new ArrayList<>();

        if (!TextUtils.isEmpty(remainingSeats)) {
            try {
                remainingSeatsInt = Integer.parseInt(remainingSeats);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (remainingSeatsInt > 0) {
            for (int i = 0; i < remainingSeatsInt; i++) {
                strings.add("" + (i + 1));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, strings);
        noOfSeatsSpinner.setAdapter(adapter);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        resetPwdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Utils.isInternetAvailable(mContext)) {
                    Utils.showProgress(true, mProgressView, mProgressBGView);
                    bookRide(userGuid, possibleRideGuid, noOfSeatsSpinner.getSelectedItem().toString());
                }
            }
        });

        if (remainingSeatsInt > 0) {
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "There are no seats available", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.BOOK_a_RIDE_SCREEN);
        ((HomeActivity) mContext).prepareMenu();
    }

    private void bookRide(String userGuid, String possibleRideGuid, String passengers) {
        Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).bookRide(userGuid, possibleRideGuid, passengers);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {

            /* Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null && response.isSuccess()) {
                    if (response.body().Data.equalsIgnoreCase("1")) {
                        ((HomeActivity) mContext).showSnackBar("Successfully booked your seat");
                        ((HomeActivity) mContext).loadScreen(HomeActivity.RIDE_CONFIRM_SCREEN, false, rideDetails.Data, Constants.HOME_SCREEN_NAME);
                    }
                } else {
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                }
                Utils.showProgress(false, mProgressView, mProgressBGView);
            }


            /* Invoked when a network or unexpected exception occurred during the HTTP request.
            *
            * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

}
