package com.yatrashare.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.activities.OfferRideActivity;
import com.yatrashare.dtos.FoundRides;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.SerializedPlace;
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


public class HomeFragment extends Fragment implements View.OnTouchListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    @Bind(R.id.et_where_from)
    public EditText whereFromEditText;
    @Bind(R.id.et_where_to)
    public EditText whereToEditText;
    @Bind(R.id.et_date)
    public EditText dateEditText;
    private int year, month, day;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Context mContext;
    @Bind(R.id.findRideProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.rideProgress)
    public ProgressBar mProgressView;
    private SharedPreferences mSharedPreferences;
    private SerializedPlace whereFromPlace, wheretoPlace;


    @OnClick(R.id.swapAreas)
    public void swapLocations() {
        String whereFrom = whereFromEditText.getText().toString();
        String whereTo = whereToEditText.getText().toString();

        if (!TextUtils.isEmpty(whereFrom) && !TextUtils.isEmpty(whereTo)) {
            whereFromEditText.setText(whereTo);
            whereToEditText.setText(whereFrom);
        }

        if (whereFromPlace != null && wheretoPlace != null) {
            SerializedPlace tempPlace = whereFromPlace;
            whereFromPlace = wheretoPlace;
            wheretoPlace = tempPlace;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedLayout = inflater.inflate(R.layout.fragment_home, null, false);
        ButterKnife.bind(this, inflatedLayout);
        mContext = getActivity();

        ((HomeActivity) getActivity()).setTitle(getActivity().getString(R.string.app_name));

        whereFromEditText.setInputType(InputType.TYPE_NULL);
        whereToEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setInputType(InputType.TYPE_NULL);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        LogWrapper logWrapper = new LogWrapper();
        UtilsLog.setLogNode(logWrapper);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        whereFromEditText.setOnTouchListener(this);
        whereToEditText.setOnTouchListener(this);
        dateEditText.setOnTouchListener(this);
        return inflatedLayout;
    }

    @OnClick(R.id.searchRide)
    public void searchRide() {
        Utils.hideSoftKeyboard(whereToEditText);
        if (Utils.isInternetAvailable(mContext)) {
            final String whereFrom = whereFromEditText.getText().toString();
            final String whereTo = whereToEditText.getText().toString();
            final String date = dateEditText.getText().toString();

            if (TextUtils.isEmpty(whereFrom) && TextUtils.isEmpty(whereTo)) {
                whereFromEditText.setError("Enter Departure");
                whereToEditText.setError("Enter Arrival");
                return;
            }

            whereFromEditText.setError(null);
            whereToEditText.setError(null);

            /**
             * comfort
             * current page
             * start time
             * end time
             * ladies only
             * ride type
             * vehicle type
             * page size
             */
            Utils.showProgress(true, mProgressView, mProgressBGView);
            FindRide findRide = new FindRide(whereFrom, whereTo,
                    date, "ALLTYPES", "1", "1", "24", "All", "1", "1", "10", "0");

            Gson gson = new Gson();
            Log.e(TAG, "searchRide: " + gson.toJson(findRide));

            Call<SearchRides> call = Utils.getYatraShareAPI().FindRides(findRide);
            //asynchronous call
            call.enqueue(new Callback<SearchRides>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<SearchRides> response, Retrofit retrofit) {
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                    if (response.body() != null) {
                        android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                        SearchRides searchRides = response.body();
                        if (searchRides != null) {
                            FoundRides foundRides = new FoundRides();
                            foundRides.searchRides = searchRides;
                            foundRides.destinationPlace = whereFrom;
                            foundRides.arriavalPlace = whereTo;
                            foundRides.selectedDate = date;
                            ((HomeActivity) getActivity()).loadScreen(HomeActivity.SEARCH_RIDE_SCREEN, false, foundRides, null);
                        } else {
                            ((HomeActivity) mContext).showSnackBar("No rides available at this time, Try again!");
                        }
                    }
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t error
                 */
                @Override
                public void onFailure(Throwable t) {
                    android.util.Log.e(TAG, "FAILURE RESPONSE");
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    @OnClick(R.id.offerRide)
    public void offerRide() {
        if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
            if (whereFromPlace != null && wheretoPlace != null && whereFromPlace.address.equalsIgnoreCase(wheretoPlace.address)) {
                Utils.showToast(mContext, "Departure and Arrival should not be same");
                return;
            }
            Intent intent = new Intent(mContext, OfferRideActivity.class);
            intent.putExtra("DEPARTURE", whereFromPlace);
            intent.putExtra("ARRIVAL", wheretoPlace);
            intent.putExtra("DATE", dateEditText.getText().toString());
            startActivity(intent);
        } else {
            Utils.showLoginDialog(mContext, Constants.HOME_SCREEN_NAME);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setCurrentScreen(HomeActivity.HOME_SCREEN);
        ((HomeActivity) getActivity()).setTitle("");
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateEditText.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        };
    }

    public SerializedPlace preparePlace(Place place) {
        SerializedPlace serializedPlace = new SerializedPlace();
        serializedPlace.latitude = place.getLatLng().latitude;
        serializedPlace.longitude = place.getLatLng().longitude;
        serializedPlace.address = (String) place.getAddress();
        return serializedPlace;
    }

    public boolean whereFromhasFocus;
    public static final int REQUEST_CODE_AUTOCOMPLETE = 1;

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
                if (whereFromhasFocus) {
                    whereFromPlace = preparePlace(place);
                    whereFromEditText.setText(place.getAddress());
                } else {
                    wheretoPlace = preparePlace(place);
                    whereToEditText.setText(place.getAddress());
                }

                whereFromEditText.setError(null);
                whereToEditText.setError(null);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mContext, data);
                Log.e(TAG, "Error: Status = " + status.toString());
                if (whereFromhasFocus) {
                    whereFromEditText.setText("");
                    whereFromPlace = null;
                } else {
                    wheretoPlace = null;
                    whereToEditText.setText("");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(Utils.createBoundsWithMinDiagonal(mContext))
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
    public boolean onTouch(View v, MotionEvent event) {
        Utils.hideSoftKeyboard(whereFromEditText);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.et_where_from:
                    whereFromhasFocus = true;
                    openAutocompleteActivity();
                    break;
                case R.id.et_where_to:
                    whereFromhasFocus = false;
                    openAutocompleteActivity();
                    break;
                case R.id.et_date:
                    DatePickerDialog mDatePickerDialog = new DatePickerDialog(mContext, mDateSetListener, year, month, day);
                    mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
                            dateEditText.setHint(getString(R.string.ride_date));
                        }
                    });
                    break;
            }
        }
        return false;
    }

}
