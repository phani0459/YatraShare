package com.yatrashare.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.BookedRides;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class BookedRidesFragment extends Fragment {
    private static final String TAG = BookedRidesFragment.class.getSimpleName();

    private android.content.Context mContext;
    private int mTitle;
    @Bind(R.id.emptyRidesSubHeading)
    public TextView emptyRidesSubHeading;
    @Bind(R.id.emptyRidesHeading)
    public TextView emptyRidesHeading;
    @Bind(R.id.emptyRidesLayout)
    public LinearLayout emptyRidesLayout;
    @Bind(R.id.ridesList)
    public RecyclerView recyclerView;
    private BookedRides bookedRides;

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
        bookedRides = (BookedRides) getArguments().getSerializable("BookedRides");
        ButterKnife.bind(this, view);

        setEmptyRidesTexts();

        ((HomeActivity)mContext).setTitle("Booked Rides");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bookedRides != null && bookedRides.Data != null && bookedRides.Data.size() > 0) {
            emptyRidesLayout.setVisibility(View.GONE);
        } else {
            emptyRidesLayout.setVisibility(View.VISIBLE);
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