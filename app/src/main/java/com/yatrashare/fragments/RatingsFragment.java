package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.RatingsRecyclerViewAdapter;
import com.yatrashare.dtos.Rating;
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
public class RatingsFragment extends Fragment {


    private static final String TAG = RatingsFragment.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.ratingProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.rating_progress)
    public ProgressBar mProgressView;
    @Bind(R.id.ratingsList)
    public RecyclerView recyclerView;
    @Bind(R.id.emptyRidesLayout)
    public ScrollView emptyRidesLayout;
    private int mTitle;

    public RatingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        mTitle = getArguments().getInt("TITLE");

        TextView emptyRidesHeading = (TextView) view.findViewById(R.id.emptyRidesHeading);
        TextView emptyRidesSubHeading = (TextView) view.findViewById(R.id.emptyRidesSubHeading);
        ImageView emptyRidesImage = (ImageView) view.findViewById(R.id.emptyRideImage);

        emptyRidesHeading.setText("No ratings yet.");
        emptyRidesSubHeading.setText("Once you do, you'll find them here.");
        emptyRidesImage.setBackgroundResource(R.drawable.empty_rarings);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        getRatings(userGuide, mTitle);

        return view;
    }

    public void getRatings(String userGuide, final int title) {
        if (Utils.isInternetAvailable(mContext)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);

            Call<Rating> call = null;
            if (title == TabsFragment.RECEIVED_RATINGS) {
                call = Utils.getYatraShareAPI().userReceivedRatings(userGuide);
            } else {
                call = Utils.getYatraShareAPI().userGivenRatings(userGuide);
            }

            //asynchronous call
            if (call != null) {
                call.enqueue(new Callback<Rating>() {
                    /**
                     * Successful HTTP response.
                     *
                     * @param response server response
                     * @param retrofit adapter
                     */
                    @Override
                    public void onResponse(retrofit.Response<Rating> response, Retrofit retrofit) {
                        android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                        if (response != null && response.body() != null && response.body().Data != null) {
                            if (response.body().Data.size() > 0) {
                                emptyRidesLayout.setVisibility(View.GONE);
                                RatingsRecyclerViewAdapter adapter = new RatingsRecyclerViewAdapter(response.body().Data, title);
                                recyclerView.setAdapter(adapter);
                            } else {
                                emptyRidesLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            emptyRidesLayout.setVisibility(View.VISIBLE);
                        }
                        Utils.showProgress(false, mProgressView, mProgressBGView);
                    }

                    /**
                     * Invoked when a network or unexpected exception occurred during the HTTP request.
                     *
                     * @param t error
                     */
                    @Override
                    public void onFailure(Throwable t) {
                        Utils.showProgress(false, mProgressView, mProgressBGView);
                        android.util.Log.e(TAG, "FAILURE RESPONSE");
                        emptyRidesLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            emptyRidesLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.RATINGS_SCREEN);
    }
}
