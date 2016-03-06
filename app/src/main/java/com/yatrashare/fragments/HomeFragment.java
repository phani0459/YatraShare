package com.yatrashare.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.utils.Utils;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedLayout = inflater.inflate(R.layout.fragment_home, null, false);
        ((HomeActivity)getActivity()).setTitle(getActivity().getString(R.string.app_name));

        Button mSearchRideBt = (Button) inflatedLayout.findViewById(R.id.searchRide);
        Button mOfferRideBt = (Button) inflatedLayout.findViewById(R.id.offerRide);

        mOfferRideBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).loadScreen(HomeActivity.OFFER_RIDE_SCREEN, false, null, null);
            }
        });

        mSearchRideBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).loadScreen(HomeActivity.SEARCH_RIDE_SCREEN, false, null, null);
            }
        });

        return inflatedLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).setCurrentScreen(HomeActivity.HOME_SCREEN);
    }
}
