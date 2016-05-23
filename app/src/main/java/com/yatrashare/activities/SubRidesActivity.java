package com.yatrashare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.adapter.OfferedRidesRecyclerViewAdapter;
import com.yatrashare.dtos.OfferedRides;
import com.yatrashare.dtos.OfferedSubRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class SubRidesActivity extends AppCompatActivity implements Callback<OfferedSubRides>, OfferedRidesRecyclerViewAdapter.SetOnItemClickListener {
    private static final String TAG = SubRidesActivity.class.getSimpleName();

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
    private OfferedSubRides offeredSubRides;
    @Bind(R.id.getRidesProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.bukdRidesProgressBGView)
    public View mProgressBGView;
    private OfferedRidesRecyclerViewAdapter adapter;
    private String userGuide;
    private OfferedRides.OfferedRideData offeredRideData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bookedrides_list);

        ButterKnife.bind(this);
        mContext = this;

        mTitle = getIntent().getExtras().getInt("TITLE");
        offeredRideData = (OfferedRides.OfferedRideData) getIntent().getExtras().getSerializable("SELECTED RIDE");
        userGuide = getIntent().getExtras().getString("UserGuide", "");

        setEmptyRidesTexts();
        getOfferedSubRides();

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Stopover Points");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable("RIDES", offeredSubRides);
    }

    public void getOfferedSubRides() {
        if (Utils.isInternetAvailable(this)) {
            android.util.Log.e("getOfferedRides", userGuide);
            if (!TextUtils.isEmpty(userGuide)) {
                Utils.showProgress(true, mProgressView, mProgressBGView);
                Call<OfferedSubRides> call = null;
                switch (mTitle) {
                    case TabsFragment.UPCOMING_OFFERED_RIDES:
                        call = Utils.getYatraShareAPI(this).upComingSubRides(userGuide, offeredRideData.RideGuid);
                        break;
                    case TabsFragment.PAST_OFFERED_RIDES:
                        call = Utils.getYatraShareAPI(this).pastSubRides(userGuide, offeredRideData.RideGuid);
                        break;
                }
                //asynchronous call
                if (call != null) {
                    call.enqueue(this);
                }
            } else {
                Snackbar.make(recyclerView, getString(R.string.userguide_ratioanle), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    public void loadOfferedRides() {
        if (offeredSubRides != null && offeredSubRides.Data != null && offeredSubRides.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new OfferedRidesRecyclerViewAdapter(this, offeredSubRides.Data, mTitle, this, "Sample");
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
    public void onResponse(retrofit.Response<OfferedSubRides> response, Retrofit retrofit) {
        android.util.Log.e("RESPONSE raw", response.raw() + "");
        Utils.showProgress(false, mProgressView, mProgressBGView);
        if (response.body() != null) {
            try {
                offeredSubRides = response.body();
                loadOfferedRides();
            } catch (Exception e) {
                e.printStackTrace();
                recyclerView.setVisibility(View.GONE);
                emptyRidesLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (call != null) {
                Log.e(TAG, "onDestroy: ");
                call.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            offeredSubRides = (OfferedSubRides) savedInstanceState.getSerializable("RIDES");
            loadOfferedRides();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setEmptyRidesTexts() {
        switch (mTitle) {
            case TabsFragment.UPCOMING_OFFERED_RIDES:
                emptyRidesHeading.setText("You have'nt offered any sub rides.");
                emptyRidesSubHeading.setText("Why not set one up now?");
                break;
            case TabsFragment.PAST_OFFERED_RIDES:
                emptyRidesHeading.setText("You have'nt offered any sub rides.");
                emptyRidesSubHeading.setText("Why not set one up now?");
                break;
        }
    }

    Call<UserDataDTO> call = null;

    @Override
    public void onItemClick(final int clickedItem, final int position) {
        OfferedSubRides.SubRideData subRide = (OfferedSubRides.SubRideData) adapter.getItem(position);
        Intent intent = new Intent(mContext, UserBookingsActivity.class);
        intent.putExtra("TITLE", mTitle);
        intent.putExtra("SELECTED SUB RIDE", subRide);
        intent.putExtra("UserGuide", userGuide);
        startActivity(intent);
    }
}
