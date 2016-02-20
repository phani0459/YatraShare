package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.BookedRides;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabsFragment extends Fragment  implements Callback<BookedRides> {
    private static final String TAG = TabsFragment.class.getSimpleName();

    @Bind(R.id.viewpager)
    public ViewPager viewPager;
    @Bind(R.id.tabs)
    public TabLayout tabLayout;
    private int mTitle;
    public static final int UPCOMING_BOOKED_RIDES = 0;
    public static final int PAST_BOOKED_RIDES = 1;
    public static final int UPCOMING_OFFERED_RIDES = 2;
    public static final int PAST_OFFERED_RIDES = 3;
    private Context mContext;
    @Bind(R.id.getRidesProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.retryButton)
    public Button retryButton;
    @Bind(R.id.bookedRidesProgressBGView)
    public View mProgressBGView;

    public TabsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, null, false);
        ButterKnife.bind(this, view);

        mContext = getActivity();
        mTitle = getArguments().getInt("TITLE");

        if (mTitle == HomeActivity.BOOKED_RIDES_SCREEN) {
            ((HomeActivity)mContext).setTitle("Rides I've booked");
            getBookedRides();
        } else if (mTitle == HomeActivity.OFFERED_RIDES_SCREEN) {
            ((HomeActivity)mContext).setTitle("Rides I've offered");
        }

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBookedRides();
            }
        });

        return view;
    }

    public void getBookedRides() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        android.util.Log.e("getBookedRides", userGuide);
        if (!TextUtils.isEmpty(userGuide)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YatraShareAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);

            Call<BookedRides> call = yatraShareAPI.bookedRides(userGuide);
            //asynchronous call
            call.enqueue(this);
        } else {
            ((HomeActivity)mContext).showSnackBar(getString(R.string.userguide_ratioanle));
        }

    }
    public Fragment getFragment(int arg, Object object) {
        Bundle bundle = new Bundle();
        BookedRidesFragment bookedRidesFragment = new BookedRidesFragment();
        bundle.putInt("TITLE", arg);
        bundle.putSerializable("BookedRides", (BookedRides) object);
        bookedRidesFragment.setArguments(bundle);
        return bookedRidesFragment;
    }

    private void setupViewPager(ViewPager viewPager, Object object) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        if (mTitle == HomeActivity.BOOKED_RIDES_SCREEN) {
            adapter.addFragment(getFragment(UPCOMING_BOOKED_RIDES, object), "Upcoming");
            adapter.addFragment(getFragment(PAST_BOOKED_RIDES, null), "Past");
        } else if (mTitle == HomeActivity.OFFERED_RIDES_SCREEN) {
            adapter.addFragment(getFragment(UPCOMING_OFFERED_RIDES, null), "Upcoming");
            adapter.addFragment(getFragment(PAST_OFFERED_RIDES, null), "Past Journeys");
        }
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
        android.util.Log.e("RESPONSE", response + "");
        android.util.Log.e("RESPONSE raw", response.raw() + "");
        if (response != null && response.body() != null) {
            try {
                BookedRides bookedRides = response.body();
                if (bookedRides != null && bookedRides.Data != null && bookedRides.Data.size() > 0) {
                    retryButton.setVisibility(View.GONE);
                    setupViewPager(viewPager, bookedRides);
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    retryButton.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                retryButton.setVisibility(View.VISIBLE);
            }
        }
        Utils.showProgress(false, mProgressView, mProgressBGView);
    }

    /*
     * Invoked when a network or unexpected exception occurred during the HTTP request.
     *
     * @param t
     * */
    @Override
    public void onFailure(Throwable t) {
        android.util.Log.e(TAG, t.getLocalizedMessage() +"");
        retryButton.setVisibility(View.VISIBLE);
        setupViewPager(viewPager, null);
        tabLayout.setupWithViewPager(viewPager);
        Utils.showProgress(false, mProgressView, mProgressBGView);
    }
}
