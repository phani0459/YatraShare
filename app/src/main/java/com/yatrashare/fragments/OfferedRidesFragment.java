package com.yatrashare.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.yatrashare.activities.EditRideActivity;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.activities.OfferRideActivity;
import com.yatrashare.activities.SubRidesActivity;
import com.yatrashare.adapter.OfferedRidesRecyclerViewAdapter;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.dtos.OfferedRides;
import com.yatrashare.dtos.RideDetails;
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
    private LinearLayoutManager mLayoutManager;
    private int currentPage = 1;


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

        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(mContext);
        userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        setEmptyRidesTexts();
        getOfferedRides();

        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        if (getArguments() != null) {
            offeredRides = (OfferedRides) getArguments().getSerializable("RIDES");
            adapter = null;
            currentPage = 1;
            loadOfferedRides();
        }

        return view;
    }

    public void getOfferedRides() {
        android.util.Log.e("getOfferedRides", userGuide);
        if (Utils.isInternetAvailable(mContext)) {
            if (!TextUtils.isEmpty(userGuide)) {
                Utils.showProgress(true, mProgressView, mProgressBGView);
                Call<OfferedRides> call = null;
                switch (mTitle) {
                    case TabsFragment.UPCOMING_OFFERED_RIDES:
                        call = Utils.getYatraShareAPI(mContext).upComingOfferedRides(userGuide, currentPage + "", Constants.PAGE_SIZE + "");
                        break;
                    case TabsFragment.PAST_OFFERED_RIDES:
                        call = Utils.getYatraShareAPI(mContext).pastOfferedRides(userGuide, currentPage + "", Constants.PAGE_SIZE + "");
                        break;
                }
                //asynchronous call
                if (call != null) {
                    call.enqueue(this);
                }
            } else {
                if (mContext instanceof HomeActivity) {
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.userguide_ratioanle));
                } else {
                    ((OfferRideActivity) mContext).showSnackBar(getString(R.string.userguide_ratioanle));
                }
            }
        }
    }

    public void loadOfferedRides() {
        if (offeredRides != null && offeredRides.Data != null && offeredRides.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            mIsLoading = false;
            if (offeredRides.Data.size() < Constants.PAGE_SIZE) {
                mIsLastPage = true;
            }

            if (adapter != null) {
                adapter.removeLoading();
                for (int i = 0; i < offeredRides.Data.size(); i++) {
                    adapter.addItem(offeredRides.Data.get(i));
                }
            } else {
                adapter = new OfferedRidesRecyclerViewAdapter(mContext, offeredRides.Data, mTitle, this);
                recyclerView.setAdapter(adapter);
            }
        } else {
            if (adapter != null && adapter.getItemCount() > 0) {
                mIsLastPage = true;
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyRidesLayout.setVisibility(View.VISIBLE);
            }
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

    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mIsLoading && !mIsLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= Constants.PAGE_SIZE) {
                    loadMoreItems();
                }
            }
        }
    };

    private void loadMoreItems() {
        mIsLoading = true;
        if (adapter != null) adapter.addLoading();
        currentPage = currentPage + 1;
        getOfferedRides();
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
        if (mContext instanceof HomeActivity) {
            ((HomeActivity) mContext).setCurrentScreen(HomeActivity.OFFERED_RIDES_SCREEN);
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
            areYouSureDialog(offeredRide, position);
        } else if (clickedItem == 3) {
            if (Utils.isInternetAvailable(mContext)) editRide(offeredRide);
        }
    }

    public void areYouSureDialog(final OfferedRides.OfferedRideData offeredRideData, final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (Utils.isInternetAvailable(mContext)) {
                            Utils.showProgress(true, mProgressView, mProgressBGView);
                            deleteRide(offeredRideData, position);
                        }
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void editRide(final OfferedRides.OfferedRideData offeredRide) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Call<RideDetails> call = Utils.getYatraShareAPI(mContext).getRideDetails("", offeredRide.RideGuid);
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
                        RideDetails rideDetails = response.body();
                        Intent intent = new Intent(mContext, EditRideActivity.class);
                        intent.putExtra("USERGUIDE", userGuide);
                        intent.putExtra("RIDE DETAILS", rideDetails);
                        startActivity(intent);
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
            }
        });
    }

    private void deleteRide(OfferedRides.OfferedRideData offeredRide, final int position) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).deleteOfferedRide(userGuide, offeredRide.RideGuid);
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        adapter.remove(position);
                        if (mContext instanceof HomeActivity) {
                            ((HomeActivity) mContext).showSnackBar("Success");
                        } else {
                            ((OfferRideActivity) mContext).showSnackBar("Success");
                        }

                        if (adapter.getItemCount() == 0) {
                            emptyRidesLayout.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
            }
        });
    }
}