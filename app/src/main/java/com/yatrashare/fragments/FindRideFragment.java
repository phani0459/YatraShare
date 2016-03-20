package com.yatrashare.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.AvailableRidesAdapter;
import com.yatrashare.dtos.FoundRides;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.pojos.FindRide;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindRideFragment extends Fragment implements AvailableRidesAdapter.OnItemClickListener{


    private static final String TAG = FindRideFragment.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.availableRidesList)
    public RecyclerView mRecyclerView;
    @Bind(R.id.emptyRidesLayout)
    public ScrollView emptyRidesLayout;
    @Bind(R.id.findRideProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.rideProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.emptyRidesHeading)
    public TextView emptyRidesHeading;
    @Bind(R.id.emptyRidesSubHeading)
    public TextView emptyRidesSubHeading;

    private AvailableRidesAdapter mAdapter;
    private SearchRides searchRides;
    public String date;
    private String whereFrom;
    private String whereTo;

    public FindRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null){
            return null;
        }
        View inflatedLayout = inflater.inflate(R.layout.fragment_find_rides, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, inflatedLayout);

        String mTitle = (String) getArguments().getSerializable("TITLE");

        ((HomeActivity)mContext).setTitle(mTitle);

        emptyRidesHeading.setText("There are no rides at present.");
        emptyRidesSubHeading.setText("Wait for some time and Try again!");
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

        return inflatedLayout;
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
            outRect.top = mVerticalSpaceHeight;
            outRect.left = mVerticalSpaceHeight;
            outRect.right = mVerticalSpaceHeight;

        }
    }

    public void searchRides() {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        FindRide findRide = new FindRide(whereFrom, whereTo,
                date, comfortLevel, "1", startTime, endTime, gender, rideType, vehicleType, "10");

        Call<SearchRides> call = Utils.getYatraShareAPI().FindRides(findRide);
        //asynchronous call
        call.enqueue(new Callback<SearchRides>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<SearchRides> response, Retrofit retrofit) {
                Utils.showProgress(false, mProgressView, mProgressBGView);
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null) {
                    android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                    FindRideFragment.this.searchRides = response.body();
                    if (searchRides != null) {
                        if (searchRides.Data != null && searchRides.Data.size() > 0) {
                            emptyRidesLayout.setVisibility(View.GONE);
                            mAdapter = new AvailableRidesAdapter(mContext, searchRides.Data, FindRideFragment.this);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            emptyRidesLayout.setVisibility(View.VISIBLE);
                            mRecyclerView.setAdapter(null);
                        }
                    } else {
                        ((HomeActivity)mContext).showSnackBar("No rides available at this time, Try again!");
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
                ((HomeActivity)mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    public String rideType = "2";
    public String vehicleType = "2";
    public String comfortLevel = "ALLTYPES";
    public String gender = "All";
    public String startTime = "1";
    public String endTime = "24";

    public static final int REQUEST_CODE_RIDE_FILTER = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_RIDE_FILTER) {
            rideType = data.getStringExtra("RIDE TYPE");
            vehicleType = data.getStringExtra("VEHICLE TYPE");
            comfortLevel = data.getStringExtra("COMFORT TYPE");
            gender = data.getStringExtra("GENDER");
            date = data.getStringExtra("DATE");
            startTime = data.getStringExtra("START TIME");
            endTime = data.getStringExtra("END TIME");
            searchRides();
        }
    }

    @Override
    public void onPause() {
        if (searchRides != null) {
            foundRides.searchRides = searchRides;
            foundRides.destinationPlace = whereFrom;
            foundRides.arriavalPlace = whereTo;
            foundRides.selectedDate = date;
            getArguments().putSerializable("Searched Rides", foundRides);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.SEARCH_RIDE_SCREEN);
        ((HomeActivity)mContext).prepareMenu();
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.foundRides = (FoundRides) bundle.getSerializable("Searched Rides");
            if (foundRides != null) {
                this.searchRides = foundRides.searchRides;
                whereFrom = foundRides.destinationPlace;
                whereTo = foundRides.arriavalPlace;
                date = foundRides.selectedDate;
            }
            if (searchRides != null) {
                if (searchRides.Data != null && searchRides.Data.size() > 0) {
                    emptyRidesLayout.setVisibility(View.GONE);
                    mAdapter = new AvailableRidesAdapter(mContext, searchRides.Data, FindRideFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    emptyRidesLayout.setVisibility(View.VISIBLE);
                    mRecyclerView.setAdapter(null);
                }
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        SearchRides.SearchData searchData = mAdapter.getItem(position);
        ((HomeActivity) mContext).loadScreen(HomeActivity.BOOK_a_RIDE_SCREEN, false, searchData, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
    }

    public FoundRides foundRides;
}
