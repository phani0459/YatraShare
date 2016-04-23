package com.yatrashare.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.AvailableRidesAdapter;
import com.yatrashare.dtos.FoundRides;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.FindRide;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindRideFragment extends Fragment implements AvailableRidesAdapter.OnItemClickListener {


    private static final String TAG = FindRideFragment.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.availableRidesList)
    public RecyclerView mRecyclerView;
    @Bind(R.id.emptyRidesLayout)
    public ScrollView emptyRidesLayout;
    @Bind(R.id.findRideProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.rideProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.emptyRidesHeading)
    public TextView emptyRidesHeading;
    @Bind(R.id.emptyRidesSubHeading)
    public TextView emptyRidesSubHeading;
    @Bind(R.id.btn_createEmailAlert)
    public Button createEmailAlertBtn;

    private AvailableRidesAdapter mAdapter;
    private SearchRides searchRides;
    public String date;
    private String whereFrom;
    private String whereTo;
    private Button yesButton;
    private Button noButton;
    private ProgressBar mCreateAlertProgressBar;

    public FindRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View inflatedLayout = inflater.inflate(R.layout.fragment_find_rides, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, inflatedLayout);

        String mTitle = (String) getArguments().getSerializable("TITLE");

        ((HomeActivity) mContext).setTitle(mTitle);

        emptyRidesHeading.setText("There are no rides at present.");
        emptyRidesSubHeading.setText("Wait for some time and Try again!");
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));

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

    public void searchRides() {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        FindRide findRide = new FindRide(whereFrom, whereTo,
                date, comfortLevel, "1", startTime, endTime, gender, rideType, vehicleType, "10", vehicleRegdType);

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
                            createEmailAlertBtn.setVisibility(View.GONE);
                            mAdapter = new AvailableRidesAdapter(mContext, searchRides.Data, FindRideFragment.this);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            emptyRidesLayout.setVisibility(View.VISIBLE);
                            mRecyclerView.setAdapter(null);
                            createEmailAlertBtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ((HomeActivity) mContext).showSnackBar("No rides available at this time, Try again!");
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
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    public String rideType = "1";
    public String vehicleType = "1";
    public String comfortLevel = "ALLTYPES";
    public String gender = "All";
    public String startTime = "1";
    public String endTime = "24";
    public String vehicleRegdType = "0";

    final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    Calendar newCalendar = Calendar.getInstance();

    @OnClick(R.id.btn_createEmailAlert)
    public void showEmailAlertDialog() {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_forgotpwd);
        dialog.setTitle("Create an email alert");
        final EditText mEmailIdEdit = (EditText) dialog.findViewById(R.id.fgt_emailId);
        final EditText mDateEdit = (EditText) dialog.findViewById(R.id.fgt_phone);
        final TextInputLayout emailLayout = (TextInputLayout) dialog.findViewById(R.id.fgt_emailIdLayout);
        yesButton = (Button) dialog.findViewById(R.id.btnReset);
        noButton = (Button) dialog.findViewById(R.id.btnCancel);

        mDateEdit.setInputType(InputType.TYPE_NULL);

        yesButton.setText("Create");
        noButton.setText(getString(android.R.string.cancel));
        mDateEdit.setHint("");
        mDateEdit.setHint("MM/DD/YYYY");

        mCreateAlertProgressBar = (ProgressBar) dialog.findViewById(R.id.fgtPwdProgress);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String alertDate = dateFormatter.format(newDate.getTime());
                mDateEdit.setText(alertDate);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDateEdit.setText("");
                dialog.dismiss();
            }
        });

        mDateEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Utils.hideSoftKeyboard(mEmailIdEdit);
                    datePickerDialog.show();
                }
                return false;
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = mEmailIdEdit.getText().toString();
                String date = mDateEdit.getText().toString();

                boolean cancel = false;

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError(getString(R.string.error_field_required));
                    cancel = true;
                } else if (!Utils.isEmailValid(email)) {
                    emailLayout.setError(getString(R.string.error_invalid_email));
                    cancel = true;
                }

                if (!cancel) {
                    // Show a progress bar, and kick off a background task to
                    // perform the user forgot password attempt.
                    Utils.hideSoftKeyboard(mEmailIdEdit);
                    emailLayout.setErrorEnabled(false);
                    showEmailAlertProgress(true);
                    createanEmailAlert(email, date, dialog);
                    dialog.setCancelable(false);
                }
            }
        });

        dialog.show();
    }

    private void showEmailAlertProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mCreateAlertProgressBar.setIndeterminate(show);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateAlertProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mCreateAlertProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateAlertProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mCreateAlertProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        yesButton.setEnabled(!show);
        noButton.setEnabled(!show);
    }

    public void createanEmailAlert(String email, String alertDate, final Dialog dialog) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        Call<UserDataDTO> call = Utils.getYatraShareAPI().createEmailAlert(userGuid, email, alertDate, whereFrom, whereTo, rideType, vehicleType);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null) {
                    android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        dialog.dismiss();
                        ((HomeActivity) mContext).showSnackBar(getString(R.string.emailalertCreated_rationale));
                    } else {
                        Utils.showToast(mContext, response.body().Data);
                    }
                    showEmailAlertProgress(false);
                    dialog.setCancelable(true);
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
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                showEmailAlertProgress(false);
                dialog.setCancelable(true);
            }
        });
    }

    public static final int REQUEST_CODE_RIDE_FILTER = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_RIDE_FILTER) {
            rideType = data.getStringExtra("RIDE TYPE");
            vehicleType = data.getStringExtra("VEHICLE TYPE");
            comfortLevel = data.getStringExtra("COMFORT TYPE");
            gender = data.getStringExtra("GENDER");
            date = data.getStringExtra("DATE");
            startTime = data.getStringExtra("START TIME");
            endTime = data.getStringExtra("END TIME");
            vehicleRegdType = data.getStringExtra("VEHICLE REGD");
            searchRides();
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
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.SEARCH_RIDE_SCREEN);
        ((HomeActivity) mContext).prepareMenu();
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.foundRides = (FoundRides) bundle.getSerializable("Searched Rides");
            if (foundRides != null) {
                this.searchRides = foundRides.searchRides;
                whereFrom = foundRides.destinationPlace;
                whereTo = foundRides.arriavalPlace;
                date = foundRides.selectedDate;
            }
            if (searchRides != null) {
                if (searchRides.Data != null && searchRides.Data.size() > 0) {
                    emptyRidesLayout.setVisibility(View.GONE);
                    createEmailAlertBtn.setVisibility(View.GONE);
                    mAdapter = new AvailableRidesAdapter(mContext, searchRides.Data, FindRideFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    emptyRidesLayout.setVisibility(View.VISIBLE);
                    createEmailAlertBtn.setVisibility(View.VISIBLE);
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
