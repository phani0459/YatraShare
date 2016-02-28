package com.yatrashare.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.AvailableRidesAdapter;
import com.yatrashare.adapter.PlaceAutocompleteAdapter;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.pojos.FindRide;
import com.yatrashare.utils.Log;
import com.yatrashare.utils.LogWrapper;
import com.yatrashare.utils.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindRideFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, AvailableRidesAdapter.OnItemClickListener{


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
    @Bind(R.id.rideProgress)
    public ProgressBar mProgressView;
    private int hour, minute, year, month, day;
    private PlaceAutocompleteAdapter mPlacesAdapter;
    protected GoogleApiClient mGoogleApiClient;
    private DatePickerDialog mDatePickerDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final LatLngBounds BOUNDS_HYDERABAD = new LatLngBounds(
            new LatLng(17.3700, 78.4800), new LatLng(17.3700, 78.4800));
    @Bind(R.id.rideDateButton)
    public Button mDateButton;
    @Bind(R.id.availableRidesList)
    public RecyclerView mRecyclerView;
    @Bind(R.id.emptyRidesLayout)
    public View emptyRidesLayout;
    @Bind(R.id.rideTypeButton)
    public Button mRideTypeButton;
    @Bind(R.id.findRideProgressBGView)
    public View mProgressBGView;
    private AvailableRidesAdapter mAdapter;

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

        Button mRideSubmitBt = (Button) inflatedLayout.findViewById(R.id.findaRideBtn);
        TextView emptyRidesHeading = (TextView) emptyRidesLayout.findViewById(R.id.emptyRidesHeading);
        TextView emptyRidesSubHeading = (TextView) emptyRidesLayout.findViewById(R.id.emptyRidesSubHeading);

        emptyRidesHeading.setText("There are no rides at present.");
        emptyRidesSubHeading.setText("Wait for some time and Try again!");

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        mWhereFromView.setOnItemClickListener(mAutocompleteClickListener);
        mWhereToView.setOnItemClickListener(mAutocompleteClickListener);

        try {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                        .enableAutoManage(this.getActivity(), 0, this)
                        .addApi(Places.GEO_DATA_API)
                        .addOnConnectionFailedListener(this)
                        .build();
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPlacesAdapter = new PlaceAutocompleteAdapter(mContext, mGoogleApiClient, BOUNDS_HYDERABAD, null);
        mWhereFromView.setAdapter(mPlacesAdapter);
        mWhereToView.setAdapter(mPlacesAdapter);

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
                    }
                });

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDateButton.setText("" + dayOfMonth + " / " + (monthOfYear + 1)+ " / " + year);
            }
        };

        mRideSubmitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mWhereToView);
                getRideDetails();
            }
        });

        mRideTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mWhereToView);
                showRideType((Button)v);
            }
        });

        return inflatedLayout;
    }

    private void showRideType(final Button rideTypeBtn) {
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

    private void getRideDetails() {
        String whereFrom = mWhereFromView.getText().toString();
        String whereTo = mWhereToView.getText().toString();
        String rideTypeString = mRideTypeButton.getText().toString();
        String date = mDateButton.getText().toString();

        if (rideTypeString.equalsIgnoreCase(getString(R.string.ride_type))){
            rideTypeString = "2";
        } else if (rideTypeString.equalsIgnoreCase("Long Ride")){
            rideTypeString = "1";
        } else if (rideTypeString.equalsIgnoreCase("Daily Rides")){
            rideTypeString = "2";
        }

        if (date.equalsIgnoreCase(getString(R.string.ride_date))){
            date = "";
        }

        boolean cancel = false;
        if (TextUtils.isEmpty(whereFrom) && TextUtils.isEmpty(whereTo)) {
            cancel = true;
            mWhereFromLayout.setError("Enter Departure");
            mWhereToLayout.setError("Enter Arrival");
        }

        if (!cancel) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YatraShareAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
            FindRide findRide = new FindRide(whereFrom, whereTo,
                    date, "ALLTYPES", "1", "1", "24", "All", rideTypeString, "2", "10");

            Call<SearchRides> call = yatraShareAPI.FindRides(findRide);
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
                    if (response != null && response.body() != null) {
                        android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                        SearchRides searchRides = response.body();
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .enableAutoManage(this.getActivity(), 0, this)
                        .addApi(Places.GEO_DATA_API)
                        .addOnConnectionFailedListener(this)
                        .build();
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this.getActivity());
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = null;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mPlacesAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i("Places : ", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback  = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("Places : ", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));
            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
//               / mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i("Places", "Place details received: " + place.getName());

            places.release();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onItemClick(int position) {
        SearchRides.SearchData searchData = (SearchRides.SearchData)mAdapter.getItem(position);
        ((HomeActivity) mContext).loadScreen(HomeActivity.BOOK_a_RIDE_SCREEN, false, searchData);
    }
}
