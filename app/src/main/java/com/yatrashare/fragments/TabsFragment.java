package com.yatrashare.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabsFragment extends Fragment  {
    private static final String TAG = TabsFragment.class.getSimpleName();

    @Bind(R.id.viewpager)
    public ViewPager viewPager;
    @Bind(R.id.tabs)
    public TabLayout tabLayout;
    @Bind(R.id.fabChat)
    public FloatingActionButton floatingActionButton;
    private int mTitle;
    public static final int UPCOMING_BOOKED_RIDES = 0;
    public static final int PAST_BOOKED_RIDES = 1;
    public static final int UPCOMING_OFFERED_RIDES = 2;
    public static final int PAST_OFFERED_RIDES = 3;
    public static final int RECEIVED_RATINGS = 4;
    public static final int GIVEN_RATINGS = 5;
    private Context mContext;


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
            floatingActionButton.setVisibility(View.GONE);
        } else if (mTitle == HomeActivity.OFFERED_RIDES_SCREEN) {
            ((HomeActivity)mContext).setTitle("Rides I've offered");
            floatingActionButton.setVisibility(View.GONE);
        } else if (mTitle == HomeActivity.RATINGS_SCREEN) {
            ((HomeActivity)mContext).setTitle("Ratings");
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.PROVIDE_RATING_SCREEN, false, null, Constants.RATINGS_SCREEN_NAME);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public Fragment getFragment(int arg) {
        Bundle bundle = new Bundle();
        BookedRidesFragment bookedRidesFragment = new BookedRidesFragment();
        bundle.putInt("TITLE", arg);
        bookedRidesFragment.setArguments(bundle);
        return bookedRidesFragment;
    }

    public Fragment getOfferedFragment(int arg) {
        Bundle bundle = new Bundle();
        OfferedRidesFragment offeredRidesFragment = new OfferedRidesFragment();
        bundle.putInt("TITLE", arg);
        offeredRidesFragment.setArguments(bundle);
        return offeredRidesFragment;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        if (mTitle == HomeActivity.BOOKED_RIDES_SCREEN) {
            adapter.addFragment(getFragment(UPCOMING_BOOKED_RIDES), "Upcoming");
            adapter.addFragment(getFragment(PAST_BOOKED_RIDES), "Past");
        } else if (mTitle == HomeActivity.OFFERED_RIDES_SCREEN) {
            adapter.addFragment(getOfferedFragment(UPCOMING_OFFERED_RIDES), "Upcoming");
            adapter.addFragment(getOfferedFragment(PAST_OFFERED_RIDES), "Past Journeys");
        } else if (mTitle == HomeActivity.RATINGS_SCREEN) {
            adapter.addFragment(getRatingsFragment(RECEIVED_RATINGS), "Received Ratings");
            adapter.addFragment(getRatingsFragment(GIVEN_RATINGS), "Given Ratings");
        }
        viewPager.setAdapter(adapter);
    }

    private Fragment getRatingsFragment(int arg) {
        Bundle bundle = new Bundle();
        RatingsFragment ratingsFragment = new RatingsFragment();
        bundle.putInt("TITLE", arg);
        ratingsFragment.setArguments(bundle);
        return ratingsFragment;
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

}
