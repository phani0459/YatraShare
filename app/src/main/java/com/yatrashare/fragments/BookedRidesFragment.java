package com.yatrashare.fragments;

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
import com.yatrashare.adapter.BookedRidesRecyclerViewAdapter;
import com.yatrashare.dtos.BookedRides;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class BookedRidesFragment extends Fragment implements Callback<BookedRides> {
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

        setEmptyRidesTexts();
        getBookedRides();

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    public void toggleProgress(boolean visibility) {
        if (visibility) {
            mProgressView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyRidesLayout.setVisibility(View.GONE);
        } else {
            mProgressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyRidesLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bookedRides != null) {
            getArguments().putSerializable("RIDES", bookedRides);
        }
    }

    public void getBookedRides() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        android.util.Log.e("getBookedRides", userGuide);
        if (!TextUtils.isEmpty(userGuide)) {
            toggleProgress(true);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YatraShareAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);

            Call<BookedRides> call = yatraShareAPI.bookedRides(userGuide, "" + (mTitle + 1));
            //asynchronous call
            call.enqueue(this);
        } else {
            ((HomeActivity)mContext).showSnackBar(getString(R.string.userguide_ratioanle));
        }
    }

    public void loadBookedRides() {
        if (bookedRides != null && bookedRides.Data != null && bookedRides.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new BookedRidesRecyclerViewAdapter(bookedRides.Data));
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
        toggleProgress(false);
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
        toggleProgress(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            bookedRides = (BookedRides) getArguments().getSerializable("RIDES");
            loadBookedRides();
        }
    }

    public void setEmptyRidesTexts  () {
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
}