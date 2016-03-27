package com.yatrashare.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.activities.SubRidesActivity;
import com.yatrashare.adapter.OfferedRidesRecyclerViewAdapter;
import com.yatrashare.dtos.OfferedRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class OfferedRidesFragment extends Fragment implements Callback<OfferedRides>, OfferedRidesRecyclerViewAdapter.SetOnItemClickListener {
    private static final String TAG = OfferedRidesFragment.class.getSimpleName();

    private android.content.Context mContext;
    private int mTitle;
    @Bind(R.id.emptyRidesSubHeading)
    public TextView emptyRidesSubHeading;
    @Bind(R.id.emptyRidesHeading)
    public TextView emptyRidesHeading;
    @Bind(R.id.emptyRidesLayout)
    public ScrollView emptyRidesLayout;
    @Bind(R.id.ridesList)
    public RecyclerView recyclerView;
    private OfferedRides offeredRides;
    @Bind(R.id.getRidesProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.bukdRidesProgressBGView)
    public View mProgressBGView;
    private OfferedRidesRecyclerViewAdapter adapter;
    private String userGuide;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferedRidesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookedrides_list, null, false);
        mContext = getActivity();
        mTitle = getArguments().getInt("TITLE");
        ButterKnife.bind(this, view);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        setEmptyRidesTexts();
        getOfferedRides();

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (offeredRides != null) {
            getArguments().putSerializable("RIDES", offeredRides);
        }
    }

    public void getOfferedRides() {
        android.util.Log.e("getOfferedRides", userGuide);
        if (!TextUtils.isEmpty(userGuide)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            Call<OfferedRides> call = null;
            switch (mTitle) {
                case TabsFragment.UPCOMING_OFFERED_RIDES:
                    call = Utils.getYatraShareAPI().upComingOfferedRides(userGuide, "1", "10");
                    break;
                case TabsFragment.PAST_OFFERED_RIDES:
                    call = Utils.getYatraShareAPI().pastOfferedRides(userGuide, "1", "10");
                    break;
            }
            //asynchronous call
            if (call != null) {
                call.enqueue(this);
            }
        } else {
            ((HomeActivity) mContext).showSnackBar(getString(R.string.userguide_ratioanle));
        }
    }

    public void loadOfferedRides() {
        if (offeredRides != null && offeredRides.Data != null && offeredRides.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new OfferedRidesRecyclerViewAdapter(mContext, offeredRides.Data, mTitle, this);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyRidesLayout.setVisibility(View.VISIBLE);
        }
    }

    /*
        * Successful HTTP response.
        *
        * @param response
        * @param retrofit
        */
    @Override
    public void onResponse(retrofit.Response<OfferedRides> response, Retrofit retrofit) {
        android.util.Log.e("RESPONSE raw", response.raw() + "");
        Utils.showProgress(false, mProgressView, mProgressBGView);
        if (response.body() != null) {
            try {
                offeredRides = response.body();
                loadOfferedRides();
            } catch (Exception e) {
                e.printStackTrace();
                recyclerView.setVisibility(View.GONE);
                emptyRidesLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     * Invoked when a network or unexpected exception occurred during the HTTP request.
     *
     * @param t
     * */
    @Override
    public void onFailure(Throwable t) {
        android.util.Log.e(TAG, t.getLocalizedMessage() + "");
        Utils.showProgress(false, mProgressView, mProgressBGView);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.OFFERED_RIDES_SCREEN);
        if (getArguments() != null) {
            offeredRides = (OfferedRides) getArguments().getSerializable("RIDES");
            loadOfferedRides();
        }
    }

    public void setEmptyRidesTexts() {
        switch (mTitle) {
            case TabsFragment.UPCOMING_OFFERED_RIDES:
                emptyRidesHeading.setText("You don't have any upcoming Rides.");
                emptyRidesSubHeading.setText("Why not set one up now?");
                break;
            case TabsFragment.PAST_OFFERED_RIDES:
                emptyRidesHeading.setText("You have'nt offered any rides in the past.");
                emptyRidesSubHeading.setText("");
                break;
        }
    }

    Call<UserDataDTO> call = null;

    @Override
    public void onItemClick(final int clickedItem, final int position) {
        OfferedRides.OfferedRideData offeredRide = (OfferedRides.OfferedRideData) adapter.getItem(position);
        if (clickedItem == 1) {
            Intent intent = new Intent(mContext, SubRidesActivity.class);
            intent.putExtra("TITLE", mTitle);
            intent.putExtra("SELECTED RIDE", offeredRide);
            intent.putExtra("UserGuide", userGuide);
            startActivity(intent);
        } else if (clickedItem == 2) {
            deleteRide(offeredRide, position);
        }
    }

    private void deleteRide(OfferedRides.OfferedRideData offeredRide, final int position) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Call<UserDataDTO> call = Utils.getYatraShareAPI().deleteOfferedRide(userGuide, offeredRide.RideGuid);
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        adapter.remove(position);
                        ((HomeActivity) mContext).showSnackBar("Success");
                    }
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
            }
        });
    }
}