package com.yatrashare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.adapter.UserBookingsAdapter;
import com.yatrashare.dtos.GetUserBookings;
import com.yatrashare.dtos.OfferedSubRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserBookingsActivity extends AppCompatActivity implements UserBookingsAdapter.SetOnItemClickListener, Callback<GetUserBookings> {

    private static final String TAG = UserBookingsActivity.class.getSimpleName();
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
    @Bind(R.id.getRidesProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.bukdRidesProgressBGView)
    public View mProgressBGView;
    private OfferedSubRides.SubRideData selectedSubRideData;
    private String userGuide;
    private GetUserBookings userBookings;
    private UserBookingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bookedrides_list);
        ButterKnife.bind(this);
        mContext = this;

        mTitle = getIntent().getExtras().getInt("TITLE");
        selectedSubRideData = (OfferedSubRides.SubRideData) getIntent().getExtras().getSerializable("SELECTED SUB RIDE");
        userGuide = getIntent().getExtras().getString("UserGuide", "");

        setEmptyBookingsTexts();
        getUserBookings();

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Bookings");

    }

    public void showSnackBar(String msg) {
        Snackbar.make(recyclerView, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void getUserBookings() {
        if (Utils.isInternetAvailable(mContext)) {
            if (!TextUtils.isEmpty(userGuide)) {
                Utils.showProgress(true, mProgressView, mProgressBGView);
                Call<GetUserBookings> call = Utils.getYatraShareAPI().getUserBookings(userGuide, selectedSubRideData.PossibleRideGuid);
                //asynchronous call
                call.enqueue(this);
            } else {
                showSnackBar(getString(R.string.userguide_ratioanle));
            }
        }
    }

    public void loadUserBookings() {
        if (userBookings != null && userBookings.Data != null && userBookings.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new UserBookingsAdapter(mContext, userBookings.Data, mTitle, this);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyRidesLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setEmptyBookingsTexts() {
        switch (mTitle) {
            case TabsFragment.UPCOMING_OFFERED_RIDES:
                emptyRidesHeading.setText("You have no bookings yet.");
                emptyRidesSubHeading.setText("");
                break;
            case TabsFragment.PAST_OFFERED_RIDES:
                emptyRidesHeading.setText("There are no bookings for the ride.");
                emptyRidesSubHeading.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(int clickedItem, int position) {
        final GetUserBookings.UserBookingData data = adapter.getItem(position);
        if (clickedItem == R.id.sendMessage) {
            Intent intent = new Intent(this, MessageDetailsActivity.class);
            intent.putExtra(Constants.ORIGIN_SCREEN_KEY, "User Bookings");
            intent.putExtra("Message", data);
            intent.putExtra("PossibleRideGuid", selectedSubRideData.PossibleRideGuid);
            startActivity(intent);
        } else {
            if (Utils.isInternetAvailable(mContext)) {
                Call<UserDataDTO> call = null;
                switch (clickedItem) {
                    case R.id.approveSeat:
                        call = Utils.getYatraShareAPI().approveSeat(userGuide, data.RideBookingId + "");
                        break;
                    case R.id.rejectSeat:
                        call = Utils.getYatraShareAPI().rejectSeat(userGuide, data.RideBookingId + "");
                        break;
                }
                if (call != null) {
                    Utils.showProgress(true, mProgressView, mProgressBGView);
                    call.enqueue(new Callback<UserDataDTO>() {
                        /**
                         * Successful HTTP response.
                         *
                         * @param response server response
                         * @param retrofit adapter
                         */
                        @Override
                        public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                            android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                            if (response.body() != null && response.body().Data != null) {
                                if (response.body().Data.equalsIgnoreCase("Success")) {
                                    Snackbar.make(recyclerView, response.body().Data, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    getUserBookings();
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
        }
    }

    @Override
    public void onResponse(Response<GetUserBookings> response, Retrofit retrofit) {
        android.util.Log.e("RESPONSE raw", response.raw() + "");
        Utils.showProgress(false, mProgressView, mProgressBGView);
        if (response.body() != null) {
            try {
                userBookings = response.body();
                loadUserBookings();
            } catch (Exception e) {
                e.printStackTrace();
                recyclerView.setVisibility(View.GONE);
                emptyRidesLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {
        android.util.Log.e(TAG, t.getLocalizedMessage() + "");
        Utils.showProgress(false, mProgressView, mProgressBGView);
    }
}
