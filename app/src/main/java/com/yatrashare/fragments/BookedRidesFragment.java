package com.yatrashare.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.BookedRidesRecyclerViewAdapter;
import com.yatrashare.dtos.BookedRides;
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
public class BookedRidesFragment extends Fragment implements Callback<BookedRides>, BookedRidesRecyclerViewAdapter.SetOnItemClickListener {
    private static final String TAG = BookedRidesFragment.class.getSimpleName();

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
    private BookedRides bookedRides;
    @Bind(R.id.getRidesProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.bukdRidesProgressBGView)
    public View mProgressBGView;
    private BookedRidesRecyclerViewAdapter adapter;
    private String userGuide;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookedRidesFragment() {
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

        if (Utils.isInternetAvailable(mContext)) {
            getBookedRides();
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyRidesLayout.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bookedRides != null) {
            getArguments().putSerializable("RIDES", bookedRides);
        }
    }

    public void getBookedRides() {
        android.util.Log.e("getBookedRides", userGuide);
        if (!TextUtils.isEmpty(userGuide)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            Call<BookedRides> call = Utils.getYatraShareAPI(mContext).bookedRides(userGuide, "" + (mTitle + 1));
            //asynchronous call
            call.enqueue(this);
        } else {
            ((HomeActivity) mContext).showSnackBar(getString(R.string.userguide_ratioanle));
        }
    }

    public void loadBookedRides() {
        if (bookedRides != null && bookedRides.Data != null && bookedRides.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new BookedRidesRecyclerViewAdapter(bookedRides.Data, mTitle, this);
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
    public void onResponse(retrofit.Response<BookedRides> response, Retrofit retrofit) {
        android.util.Log.e("RESPONSE raw", response.raw() + "");
        Utils.showProgress(false, mProgressView, mProgressBGView);
        if (response.body() != null) {
            try {
                bookedRides = response.body();
                loadBookedRides();
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
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.BOOKED_RIDES_SCREEN);
        if (getArguments() != null) {
            bookedRides = (BookedRides) getArguments().getSerializable("RIDES");
            loadBookedRides();
        }
    }

    public void setEmptyRidesTexts() {
        switch (mTitle) {
            case TabsFragment.UPCOMING_BOOKED_RIDES:
                emptyRidesHeading.setText("You have'nt arranged any rides yet.");
                emptyRidesSubHeading.setText("Once you do, you'll find them here.");
                break;
            case TabsFragment.PAST_BOOKED_RIDES:
                emptyRidesHeading.setText("You have'nt arranged any rides.");
                emptyRidesSubHeading.setText("Once you do, you'll find them here.");
                break;
            case TabsFragment.UPCOMING_OFFERED_RIDES:
                emptyRidesHeading.setText("You have'nt offered any rides yet.");
                emptyRidesSubHeading.setText("Why not set one up now?");
                break;
            case TabsFragment.PAST_OFFERED_RIDES:
                emptyRidesHeading.setText("You have'nt offered any rides yet.");
                emptyRidesSubHeading.setText("Why not set one up now?");
                break;
        }
    }

    Call<UserDataDTO> call = null;

    public void areYouSureDialog(final int clickedItem, final String rideBookingId, final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Utils.showProgress(true, mProgressView, mProgressBGView);
                        switch (clickedItem) {
                            case BookedRidesRecyclerViewAdapter.cancelRide:
                                call = Utils.getYatraShareAPI(mContext).cancelSeat(userGuide, "" + rideBookingId);
                                break;
                            case BookedRidesRecyclerViewAdapter.deleteRide:
                                call = Utils.getYatraShareAPI(mContext).deleteRide(userGuide, "" + rideBookingId);
                                break;
                        }

                        if (call != null) {
                            call.enqueue(new Callback<UserDataDTO>() {
                                /**
                                 * Successful HTTP response.
                                 *
                                 * @param response response from server
                                 * @param retrofit adapter
                                 */
                                @Override
                                public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                                    android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                                    if (response.body() != null && response.body().Data != null) {
                                        if (response.body().Data.equalsIgnoreCase("Success")) {
                                            switch (clickedItem) {
                                                case BookedRidesRecyclerViewAdapter.cancelRide:
                                                    ((HomeActivity) mContext).showSnackBar("Ride Cancelled");
                                                    getBookedRides();
                                                    break;
                                                case BookedRidesRecyclerViewAdapter.deleteRide:
                                                    ((HomeActivity) mContext).showSnackBar("Ride Deleted successfully");
                                                    adapter.remove(position);
                                                    break;
                                                case BookedRidesRecyclerViewAdapter.getOwnerDetailsbySMS:
                                                    ((HomeActivity) mContext).showSnackBar("Message Successfully sent");
                                                    break;
                                            }
                                        } else {
                                            ((HomeActivity) mContext).showSnackBar(response.body().Data);
                                        }
                                    }
                                    Utils.showProgress(false, mProgressView, mProgressBGView);
                                }

                                /**
                                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                                 *
                                 * @param t exception
                                 */
                                @Override
                                public void onFailure(Throwable t) {
                                    android.util.Log.e(TAG, "FAILURE RESPONSE");
                                    Utils.showProgress(false, mProgressView, mProgressBGView);
                                    ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                                }
                            });
                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        call = null;
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onItemClick(final int clickedItem, final int position) {
        if (clickedItem != BookedRidesRecyclerViewAdapter.viewRide) {
            if (Utils.isInternetAvailable(mContext)) {
                Call<UserDataDTO> call = null;
                final BookedRides.BookedData data = adapter.getItem(position);
                switch (clickedItem) {
                    case BookedRidesRecyclerViewAdapter.cancelRide:
                    case BookedRidesRecyclerViewAdapter.deleteRide:
                        areYouSureDialog(clickedItem, "" + data.RideBookingId, position);
                        call = null;
                        break;
                    case BookedRidesRecyclerViewAdapter.getOwnerDetailsbySMS:
                        Utils.showProgress(true, mProgressView, mProgressBGView);
                        call = Utils.getYatraShareAPI(mContext).sendJourneyDetails(userGuide, "" + data.RideBookingId);
                        break;
                }

                //asynchronous call
                if (call != null) {
                    call.enqueue(new Callback<UserDataDTO>() {
                        /**
                         * Successful HTTP response.
                         *
                         * @param response response from server
                         * @param retrofit adapter
                         */
                        @Override
                        public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                            android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                            if (response.body() != null && response.body().Data != null) {
                                if (response.body().Data.equalsIgnoreCase("Success")) {
                                    switch (clickedItem) {
                                        case BookedRidesRecyclerViewAdapter.cancelRide:
                                            ((HomeActivity) mContext).showSnackBar("Ride Cancelled");
                                            break;
                                        case BookedRidesRecyclerViewAdapter.deleteRide:
                                            ((HomeActivity) mContext).showSnackBar("Ride Deleted successfully");
                                            adapter.remove(position);
                                            break;
                                        case BookedRidesRecyclerViewAdapter.getOwnerDetailsbySMS:
                                            ((HomeActivity) mContext).showSnackBar("Message Successfully sent");
                                            break;
                                    }
                                } else {
                                    ((HomeActivity) mContext).showSnackBar(response.body().Data);
                                }
                            }
                            Utils.showProgress(false, mProgressView, mProgressBGView);
                        }

                        /**
                         * Invoked when a network or unexpected exception occurred during the HTTP request.
                         *
                         * @param t exception
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
        } else {
        }
    }
}