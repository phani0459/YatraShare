package com.yatrashare.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yatrashare.R;
import com.yatrashare.activities.OfferRideActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishedConfirmationFragment extends Fragment {


    private Context mContext;

    public PublishedConfirmationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_published_confirmation, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        return view;
    }

    @OnClick(R.id.btn_ViewPublishedRides)
    public void viewRides() {
//        ((HomeActivity) mContext).loadScreen(HomeActivity.OFFERED_RIDES_SCREEN, false, null, Constants.RIDE_CONFIRM_SCREEN_NAME);
        ((OfferRideActivity) mContext).loadMyRidesFragment();
    }

}
