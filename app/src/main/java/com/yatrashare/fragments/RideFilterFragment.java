package com.yatrashare.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RideFilterFragment extends Fragment {


    private Context mContext;

    public RideFilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_filter, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        

        return view;
    }

}
