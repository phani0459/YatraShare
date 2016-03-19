package com.yatrashare.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.AvailableRidesAdapter;
import com.yatrashare.dtos.FoundRides;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.pojos.FindRide;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.LogWrapper;
import com.yatrashare.utils.Utils;
import com.yatrashare.utils.UtilsLog;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindRideFragment extends Fragment implements AvailableRidesAdapter.OnItemClickListener{


    private static final String TAG = FindRideFragment.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.where_from)
    public AutoCompleteTextView mWhereFromView;
    @Bind(R.id.where_to)
    public AutoCompleteTextView mWhereToView;
    @Bind(R.id.whereToLayout)
    public TextInputLayout mWhereToLayout;
    @Bind(R.id.whereFramLayout)
    public TextInputLayout mWhereFromLayout;
    private int year, month, day;
    private DatePickerDialog mDatePickerDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Bind(R.id.rideDateButton)
    public Button mDateButton;
    @Bind(R.id.availableRidesList)
    public RecyclerView mRecyclerView;
    @Bind(R.id.emptyRidesLayout)
    public ScrollView emptyRidesLayout;
    @Bind(R.id.rideTypeButton)
    public Button mRideTypeButton;
    @Bind(R.id.findRideProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.rideProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.emptyRidesHeading)
    public TextView emptyRidesHeading;
    @Bind(R.id.emptyRidesSubHeading)
    public TextView emptyRidesSubHeading;

    public boolean whereFromhasFocus;

    private AvailableRidesAdapter mAdapter;
    private SearchRides searchRides;
    public String date;
    private String whereFrom;
    private String whereTo;

    public FindRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null){
            return null;
        }
        View inflatedLayout = inflater.inflate(R.layout.fragment_find_rides, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, inflatedLayout);

        String mTitle = (String) getArguments().getSerializable("TITLE");

        ((HomeActivity)mContext).setTitle(mTitle);

        emptyRidesHeading.setText("There are no rides at present.");
        emptyRidesSubHeading.setText("Wait for some time and Try again!");
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        LogWrapper logWrapper = new LogWrapper();
        UtilsLog.setLogNode(logWrapper);

        mWhereFromView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    whereFromhasFocus = true;
                    openAutocompleteActivity();
                }
                return false;
            }
        });
        mWhereToView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    whereFromhasFocus = false;
                    openAutocompleteActivity();
                }
                return false;
            }
        });

        mWhereFromView.setInputType(InputType.TYPE_NULL);
        mWhereToView.setInputType(InputType.TYPE_NULL);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mWhereToView);
                mDatePickerDialog = new DatePickerDialog(mContext, mDateSetListener, year, month, day);
                mDatePickerDialog.show();

                mDatePickerDialog.setOnCancelListener(new DatePickerDialog.OnCancelListener() {

                    /**
                     * This method will be invoked when the dialog is canceled.
                     *
                     * @param dialog The dialog that was canceled will be passed into the
                     *               method.
                     */
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDateButton.setText(getString(R.string.ride_date));
                        date = "";
                    }
                });

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = "" + dayOfMonth + "/" + (monthOfYear + 1)+ "/" + year;
                mDateButton.setText(date);
            }
        };

        return inflatedLayout;
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
            outRect.top = mVerticalSpaceHeight;
            outRect.left = mVerticalSpaceHeight;
            outRect.right = mVerticalSpaceHeight;

        }
    }

    @OnClick(R.id.rideTypeButton)
    public void showRideType(final Button rideTypeBtn) {
        Utils.hideSoftKeyboard(mWhereToView);
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
        builderSingle.setTitle("Select Ride Type");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Long Ride");
        arrayAdapter.add("Daily Rides");

        builderSingle.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        rideTypeBtn.setText("Ride Type");
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        rideTypeBtn.setText(strName);
                    }
                });
        builderSingle.show();
    }

    @OnClick(R.id.findaRideBtn)
    public void getRideDetails() {
        Utils.hideSoftKeyboard(mWhereToView);
        whereFrom = mWhereFromView.getText().toString();
        whereTo = mWhereToView.getText().toString();
        String rideTypeString = mRideTypeButton.getText().toString();
        date = mDateButton.getText().toString();

        if (rideTypeString.equalsIgnoreCase(getString(R.string.ride_type))){
            rideType = "2";
        } else if (rideTypeString.equalsIgnoreCase("Long Ride")){
            rideType = "1";
        } else if (rideTypeString.equalsIgnoreCase("Daily Rides")){
            rideType = "2";
        }

        if (date.equalsIgnoreCase(getString(R.string.ride_date))){
            date = "";
        }

        if (TextUtils.isEmpty(whereFrom) && TextUtils.isEmpty(whereTo)) {
            cancel = true;
            mWhereFromLayout.setError("Enter Departure");
            mWhereToLayout.setError("Enter Arrival");
        } else {
            cancel = false;
            mWhereFromLayout.setErrorEnabled(false);
            mWhereToLayout.setErrorEnabled(false);
        }

        if (!cancel) {
            searchRides();
        }
    }

    boolean cancel = true;

    public void searchRides() {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        FindRide findRide = new FindRide(whereFrom, whereTo,
                date, comfortLevel, "1", startTime, endTime, gender, rideType, vehicleType, "10");

        Call<SearchRides> call = Utils.getYatraShareAPI().FindRides(findRide);
        //asynchronous call
        call.enqueue(new Callback<SearchRides>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<SearchRides> response, Retrofit retrofit) {
                Utils.showProgress(false, mProgressView, mProgressBGView);
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null) {
                    android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                    FindRideFragment.this.searchRides = response.body();
                    if (searchRides != null) {
                        if (searchRides.Data != null && searchRides.Data.size() > 0) {
                            emptyRidesLayout.setVisibility(View.GONE);
                            mAdapter = new AvailableRidesAdapter(mContext, searchRides.Data, FindRideFragment.this);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            emptyRidesLayout.setVisibility(View.VISIBLE);
                            mRecyclerView.setAdapter(null);
                        }
                    } else {
                        ((HomeActivity)mContext).showSnackBar("No rides available at this time, Try again!");
                    }
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity)mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    public String rideType = "2";
    public String vehicleType = "2";
    public String comfortLevel = "ALLTYPES";
    public String gender = "All";
    public String startTime = "1";
    public String endTime = "24";

    public static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static final int REQUEST_CODE_RIDE_FILTER = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                Log.e(TAG, "Place Selected: " + place.getAddress());

                // Format the place's details and display them in the TextView.
                if (whereFromhasFocus) mWhereFromView.setText(place.getAddress());
                else mWhereToView.setText(place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mContext, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        } else if (requestCode == REQUEST_CODE_RIDE_FILTER) {
            rideType = data.getStringExtra("RIDE TYPE");
            vehicleType = data.getStringExtra("VEHICLE TYPE");
            comfortLevel = data.getStringExtra("COMFORT TYPE");
            gender = data.getStringExtra("GENDER");
            date = data.getStringExtra("DATE");
            startTime = data.getStringExtra("START TIME");
            endTime = data.getStringExtra("END TIME");

            if (date.equalsIgnoreCase("")){
                mDateButton.setText(getString(R.string.ride_date));
            } else {
                mDateButton.setText(date);
            }

            if (rideType.equalsIgnoreCase("1")){
                mRideTypeButton.setText("Long Ride");
            } else if (rideType.equalsIgnoreCase("2")){
                mRideTypeButton.setText("Daily Rides");
            }

            searchRides();

        }
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        if (searchRides != null) {
            foundRides.searchRides = searchRides;
            foundRides.destinationPlace = whereFrom;
            foundRides.arriavalPlace = whereTo;
            foundRides.selectedDate = date;
            getArguments().putSerializable("Searched Rides", foundRides);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.SEARCH_RIDE_SCREEN);
        ((HomeActivity)mContext).prepareMenu();
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.foundRides = (FoundRides) bundle.getSerializable("Searched Rides");
            this.searchRides = foundRides.searchRides;
            if (searchRides != null) {
                if (searchRides.Data != null && searchRides.Data.size() > 0) {
                    emptyRidesLayout.setVisibility(View.GONE);
                    mAdapter = new AvailableRidesAdapter(mContext, searchRides.Data, FindRideFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    emptyRidesLayout.setVisibility(View.VISIBLE);
                    mRecyclerView.setAdapter(null);
                }
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        SearchRides.SearchData searchData = mAdapter.getItem(position);
        ((HomeActivity) mContext).loadScreen(HomeActivity.BOOK_a_RIDE_SCREEN, false, searchData, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
    }

    public FoundRides foundRides;
}
