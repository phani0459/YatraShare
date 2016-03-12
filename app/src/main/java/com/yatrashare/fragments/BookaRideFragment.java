package com.yatrashare.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.yatrashare.R;
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
    @Bind(R.id.bookARideLinear)
    public LinearLayout bookARideLinearLayout;
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
    @Bind(R.id.rideDeparturePoint)
    public TextView rideDeparturePoint;
    @Bind(R.id.rideArrivalPoint)
    public TextView rideArrivalPoint;
    @Bind(R.id.chatPrefernece)
    public ImageView mChatPreference;
    @Bind(R.id.musicPreference)
    public ImageView  mMusicPreference;
    @Bind(R.id.smokePreference)
    public ImageView mSmokePreference;
    @Bind(R.id.foodPreference)
    public ImageView mFoodPreference;
    private boolean canMessage;
    private RideDetails rideDetails;


    public BookaRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null){
            return null;
        }
        View inflatedLayout = inflater.inflate(R.layout.fragment_book_ride, container, false);
        ButterKnife.bind(this, inflatedLayout);
        mContext = getActivity();

        FloatingActionButton mBookFab = (FloatingActionButton) inflatedLayout.findViewById(R.id.bookRideFab);
        FloatingActionButton mMessageFab = (FloatingActionButton) inflatedLayout.findViewById(R.id.messageRiderFab);

        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        rideData = (SearchRides.SearchData)getArguments().getSerializable("RIDE");
        if (rideData != null) {
            try {

                rideTime.setSelected(true);
                rideFrom.setSelected(true);
                rideTo.setSelected(true);
                rideArrivalPoint.setSelected(true);
                rideDeparturePoint.setSelected(true);
                usersPreferences.setSelected(true);
//                rideDate.setText(rideData.RideDate);
                rideTime.setText(rideData.RideDate);
                rideFrom.setText(rideData.DepartureCity);
                rideTo.setText(rideData.ArrivalCity);
                rideDeparturePoint.setText(rideData.DeparturePoint != null ? rideData.DeparturePoint : "");
                rideArrivalPoint.setText(rideData.ArrivalPoint != null ? rideData.ArrivalPoint : "");
                seatsAvailable.setText(rideData.RemainingSeats + " Seat(s)");
                pricePerSeat.setText("" + mContext.getResources().getString(R.string.Rs) + " " + rideData.RoutePrice + " /Seat");
                usersPreferences.setText(rideData.UserName + "'s Preferences");

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
                    if (mSharedPreferences.getBoolean(Constants.PREF_MOBILE_VERIFIED, false)) {
                        showSeatsDialog(userGuid, rideData.PossibleRideGuid, "1");
                    } else {
                        Utils.showMobileVerifyDialog(getActivity(), "Mobile Number has to be verified to book a seat", Constants.BOOK_a_RIDE_SCREEN_NAME);
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

        getRideDetails();

        return inflatedLayout;
    }

    public void openMessage() {
        if (canMessage && rideDetails != null) {
            ((HomeActivity)mContext).loadScreen(HomeActivity.MESSAGE_DETAILS_SCREEN, false, rideDetails.Data, Constants.BOOK_a_RIDE_SCREEN_NAME);
        }
    }

    public void getRideDetails() {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Call<RideDetails> call = Utils.getYatraShareAPI().getRideDetails(rideData.PossibleRideGuid);
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

        if (remainingSeats != null && !remainingSeats.isEmpty()) {
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
                Utils.showProgress(true, mProgressView, mProgressBGView);
                bookRide(userGuid, possibleRideGuid, noOfSeatsSpinner.getSelectedItem().toString());
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
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.BOOK_a_RIDE_SCREEN);
        ((HomeActivity)mContext).prepareMenu();
    }

    private void bookRide(String userGuid, String possibleRideGuid, String passengers) {
        Call<UserDataDTO> call = Utils.getYatraShareAPI().bookRide(userGuid, possibleRideGuid, passengers);
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
                if (response != null && response.body() != null && response.isSuccess()) {
                    if (response.body().Data.equalsIgnoreCase("1")) {
                        ((HomeActivity) mContext).showSnackBar("Successfully booked your seat");
                        ((HomeActivity) mContext).loadScreen(HomeActivity.PROVIDE_RATING_SCREEN, false, rideDetails.Data.OwnerGuid, Constants.HOME_SCREEN_NAME);
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
