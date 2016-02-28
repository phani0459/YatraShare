package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.pojos.UserLogin;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.TextDrawable;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
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
    public ImageView  mMusicPreference;
    @Bind(R.id.smokePreference)
    public ImageView mSmokePreference;
    @Bind(R.id.foodPreference)
    public ImageView mFoodPreference;


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

        FloatingActionButton mFab = (FloatingActionButton) inflatedLayout.findViewById(R.id.fab);

        mFab.setImageDrawable(new TextDrawable(mContext, "Book", ColorStateList.valueOf(Color.WHITE), 18.f, TextDrawable.VerticalAlignment.BASELINE));
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        rideData = (SearchRides.SearchData)getArguments().getSerializable("RIDE");
        if (rideData != null) {
            try {
                rideDate.setText(rideData.RideDate);
//                rideTime.setText(rideData.RideDate);
                rideFrom.setText(rideData.DepartureCity);
                rideTo.setText(rideData.ArrivalCity);
                seatsAvailable.setText(rideData.RemainingSeats);
                pricePerSeat.setText(rideData.RoutePrice);
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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showProgress(true, mProgressView, mProgressBGView);
                bookRide(userGuid, rideData.PossibleRideGuid, "1");
            }
        });

        return inflatedLayout;
    }

    private void bookRide(String userGuid, String possibleRideGuid, String passengers) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YatraShareAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);

        Call<String> call = yatraShareAPI.bookRide(userGuid, possibleRideGuid, passengers);
        //asynchronous call
        call.enqueue(new Callback<String>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response != null && response.body() != null && response.isSuccess()) {
                    android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                } else {
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.error_invalid_login));
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity) mContext).showSnackBar(getString(R.string.error_invalid_login));
            }
        });
    }

}
