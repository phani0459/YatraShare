package com.yatrashare.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yatrashare.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishRideFragment extends Fragment {


    private Context mContext;

    public PublishRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View inflatedLayout = inflater.inflate(R.layout.fragment_offer_nxt_step, null);
        mContext = getActivity();

//        ButterKnife.bind(this, inflatedLayout);
        return inflatedLayout;
    }

}
