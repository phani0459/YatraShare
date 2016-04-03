package com.yatrashare.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.yatrashare.R;
import com.yatrashare.pojos.RideInfoDto;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfferRideActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = OfferRideActivity.class.getSimpleName();
    @Bind(R.id.et_ofr_where_from)
    public EditText offerWhereFromEdit;
    @Bind(R.id.et_ofr_where_to)
    public EditText offerWhereToEdit;
    @Bind(R.id.rbtn_ofr_long_ride)
    public RadioButton longRideRB;
    @Bind(R.id.rbtn_ofr_daily_ride)
    public RadioButton dailyRideRB;
    @Bind(R.id.stopovers_layout)
    public LinearLayout stopOversLayout;
    @Bind(R.id.cb_roundtrip)
    public CheckBox roundTripCheckBox;
    @Bind(R.id.et_stopover_point)
    public EditText stopOverEdit;
    @Bind(R.id.bt_departuredate)
    public Button departureDateBtn;
    @Bind(R.id.bt_departuretime)
    public Button departureTimeBtn;
    @Bind(R.id.bt_arrivaldate)
    public Button arrivalDateBtn;
    @Bind(R.id.bt_arrivaltime)
    public Button arrivalTimeBtn;
    @Bind(R.id.btn_nxtStep)
    public Button offerRideButn;
    @Bind(R.id.return_date_layout)
    public LinearLayout returnDateLayout;
    private int clickedButton;
    private Place departurePlace;
    private Place arrivalPlace;
    @Bind(R.id.et_RidePrice)
    public EditText ridePriceEditText;
    private long totalPrice;
    private float totalKM;
    @Bind(R.id.weeksLayout)
    public LinearLayout weeksLinearLayout;
    @Bind(R.id.cb_monday)
    public CheckBox mondayCheckBox;
    @Bind(R.id.cb_tueday)
    public CheckBox tuesDayCheckBox;
    @Bind(R.id.cb_wedday)
    public CheckBox weddayCheckBox;
    @Bind(R.id.cb_thuday)
    public CheckBox thudayCheckBox;
    @Bind(R.id.cb_friday)
    public CheckBox fridayCheckBox;
    @Bind(R.id.cb_satday)
    public CheckBox satdayCheckBox;
    @Bind(R.id.cb_sunday)
    public CheckBox sundayCheckBox;


    public void updatePrice() {
        if (departurePlace != null && arrivalPlace != null) {
            float[] results = new float[1];
            Location.distanceBetween(departurePlace.getLatLng().latitude, departurePlace.getLatLng().longitude, arrivalPlace.getLatLng().latitude, arrivalPlace.getLatLng().longitude, results);
            totalKM = results[0] / 1000;
            if (totalKM > 0 && totalKM < 10) {
                totalPrice = 20;
            } else {
                totalPrice = Math.round(totalKM * 1.8 / 10) * 10;
            }
            ridePriceEditText.setText("" + totalPrice);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);
        ButterKnife.bind(this);

        String departure = getIntent().getExtras().getString("DEPARTURE", "");
        String arrival = getIntent().getExtras().getString("ARRIVAL", "");

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        offerWhereFromEdit.setInputType(InputType.TYPE_NULL);
        offerWhereToEdit.setInputType(InputType.TYPE_NULL);
        stopOverEdit.setInputType(InputType.TYPE_NULL);

        offerWhereFromEdit.setText(departure);
        offerWhereToEdit.setText(arrival);

        roundTripCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.expand(returnDateLayout);
                } else {
                    Utils.collapse(returnDateLayout);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Offer a Ride");

        longRideRB.setChecked(true);
        Utils.collapse(weeksLinearLayout);

        offerWhereFromEdit.setOnTouchListener(this);
        offerWhereToEdit.setOnTouchListener(this);
        stopOverEdit.setOnTouchListener(this);
        departureDateBtn.setOnClickListener(this);
        arrivalDateBtn.setOnClickListener(this);
        departureTimeBtn.setOnClickListener(this);
        arrivalTimeBtn.setOnClickListener(this);
        mondayCheckBox.setOnCheckedChangeListener(this);
        tuesDayCheckBox.setOnCheckedChangeListener(this);
        weddayCheckBox.setOnCheckedChangeListener(this);
        thudayCheckBox.setOnCheckedChangeListener(this);
        fridayCheckBox.setOnCheckedChangeListener(this);
        satdayCheckBox.setOnCheckedChangeListener(this);
        sundayCheckBox.setOnCheckedChangeListener(this);
    }

    int selectedEditText;

    /**
     * Long Ride = 1
     * Daily Ride  = 2
     */

    @OnClick(R.id.btn_nxtStep)
    public void nextStep() {
        RideInfoDto rideInfoDto = new RideInfoDto();
        Utils.hideSoftKeyboard(offerWhereFromEdit);
        String rideDeparture = offerWhereFromEdit.getText().toString();
        String rideArrival = offerWhereToEdit.getText().toString();
        String rideDepartureDate = departureDateBtn.getText().toString();
        String rideDepartureTime = departureTimeBtn.getText().toString();
        String rideArrivalDate = arrivalDateBtn.getText().toString();
        String rideArrivalTime = arrivalTimeBtn.getText().toString();

        if (!rideDeparture.isEmpty() && !rideArrival.isEmpty()) {
            if (!rideDepartureDate.isEmpty() && !rideDepartureTime.isEmpty()) {
                rideInfoDto.setmTotalprice(totalPrice + "");
                rideInfoDto.setmTotalkilometers(totalKM + "");
                rideInfoDto.setmRideType(longRideRB.isChecked() ? "1" : "2");
                rideInfoDto.setmReturnTime(rideArrivalTime);
                rideInfoDto.setmDepartureTime(rideDepartureTime);
                rideInfoDto.setmRideArrival(rideArrival);
                rideInfoDto.setmRideDeparture(rideDeparture);
                rideInfoDto.setmSelectedWeekdays(longRideRB.isChecked() ? null : weekDays);
            } else {
                Utils.showToast(this, "Enter Departure Date and Time");
            }
        } else {
            Utils.showToast(this, "Enter Departure and Arrival");
        }

        for (int i = 0; i < weekDays.size(); i++) {
            Log.e("safsafsafsa", "ertretretre" + weekDays.get(i));
        }

//        getSupportFragmentManager().beginTransaction().add(R.id.offerRideContent, new PublishRideFragment(), null).commit();
    }

    private void openAutocompleteActivity(int editTextId) {
        selectedEditText = editTextId;
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void setEditValues(Place selectedPlace) {
        switch (selectedEditText) {
            case R.id.et_ofr_where_from:
                if (selectedPlace != null) {
                    departurePlace = selectedPlace;
                    offerWhereFromEdit.setText(selectedPlace.getAddress());
                } else {
                    offerWhereFromEdit.setText("");
                }
                updatePrice();
                break;
            case R.id.et_ofr_where_to:
                if (selectedPlace != null) {
                    arrivalPlace = selectedPlace;
                    offerWhereToEdit.setText(selectedPlace.getAddress());
                } else {
                    offerWhereToEdit.setText("");
                }
                updatePrice();
                break;
            case R.id.et_stopover_point:
                if (selectedPlace != null) {
                    stopOverEdit.setText(selectedPlace.getAddress());
                } else {
                    stopOverEdit.setText("");
                }
                break;
            default:
                try {
                    EditText editText = (EditText) stopOversLayout.findViewById(selectedEditText);
                    if (selectedPlace != null) {
                        editText.setText(selectedPlace.getAddress());
                    } else {
                        editText.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @OnClick(R.id.tv_addStopOverPoints)
    public void addStopOverEditText() {
        final EditText editText = new EditText(this);
        final int editId = stopOversLayout.getChildCount() + 1;
        editText.setId(editId);
        editText.setHint(getString(R.string.ride_stopovers));
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ride_middle, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
        editText.setCompoundDrawablePadding(4);
        editText.setSingleLine();
        editText.setTextSize(16);
        editText.setPadding(4, 4, 4, 4);
        editText.setBackgroundResource(R.drawable.bg_input_group);
        stopOversLayout.addView(editText);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editText.getLayoutParams();
        layoutParams.setMargins(0, 12, 0, 0);
        editText.setInputType(InputType.TYPE_NULL);
        editText.setLayoutParams(layoutParams);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                        stopOversLayout.removeView(editText);
                        return true;
                    } else {
                        openAutocompleteActivity(editId);
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                switch (clickedButton) {
                    case R.id.bt_departuredate:
                        departureDateBtn.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        break;
                    case R.id.bt_arrivaldate:
                        arrivalDateBtn.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        break;
                }
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                switch (clickedButton) {
                    case R.id.bt_departuretime:
                        departureTimeBtn.setText("" + hourOfDay + ":" + minute);
                        break;
                    case R.id.bt_arrivaltime:
                        arrivalTimeBtn.setText("" + hourOfDay + ":" + minute);
                        break;
                }
            }
        };

        longRideRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrivalDateBtn.setVisibility(View.VISIBLE);
                    arrivalTimeBtn.setText(getString(R.string.time));
                    Utils.collapse(weeksLinearLayout);
                }
            }
        });

        dailyRideRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrivalDateBtn.setVisibility(View.GONE);
                    arrivalTimeBtn.setText(getString(R.string.returnTime));
                    Utils.expand(weeksLinearLayout);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that the result was from the autocomplete widget.
        if (resultCode == Activity.RESULT_OK) {
            // Get the user's selected place from the Intent.
            Place place = PlaceAutocomplete.getPlace(this, data);
            Log.e(TAG, "Place Selected: " + place.getAddress());

            // Format the place's details and display them in the TextView.
            setEditValues(place);

            stopOverEdit.setError(null);
            offerWhereFromEdit.setError(null);
            offerWhereToEdit.setError(null);
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.e(TAG, "Error: Status = " + status.toString());
            setEditValues(null);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            setEditValues(null);
        }
    }

    public static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.hideSoftKeyboard(offerWhereFromEdit);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.et_ofr_where_from:
                    openAutocompleteActivity(R.id.et_ofr_where_from);
                    break;
                case R.id.et_ofr_where_to:
                    openAutocompleteActivity(R.id.et_ofr_where_to);
                    break;
                case R.id.et_stopover_point:
                    if (event.getRawX() >= (stopOverEdit.getRight() - stopOverEdit.getCompoundDrawables()[2].getBounds().width())) {
                        stopOverEdit.setText("");
                        return true;
                    } else {
                        openAutocompleteActivity(R.id.et_stopover_point);
                    }
                    break;
            }
        }
        return false;
    }

    private int year, month, day, hour, minute;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    @Override
    public void onClick(final View v) {
        clickedButton = v.getId();
        switch (v.getId()) {
            case R.id.bt_departuredate:
            case R.id.bt_arrivaldate:
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, mDateSetListener, year, month, day);
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
                        switch (v.getId()) {
                            case R.id.bt_departuredate:
                                departureDateBtn.setText(getResources().getString(R.string.departuredate));
                                break;
                            case R.id.bt_arrivaldate:
                                arrivalDateBtn.setText(getResources().getString(R.string.returndate));
                                break;
                        }
                    }
                });
                break;
            case R.id.bt_departuretime:
            case R.id.bt_arrivaltime:
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, mTimeSetListener, hour, minute, false);
                timePickerDialog.show();

                timePickerDialog.setOnCancelListener(new DatePickerDialog.OnCancelListener() {

                    /**
                     * This method will be invoked when the dialog is canceled.
                     *
                     * @param dialog The dialog that was canceled will be passed into the
                     *               method.
                     */
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        switch (v.getId()) {
                            case R.id.bt_departuretime:
                                departureTimeBtn.setText(getResources().getString(R.string.time));
                                break;
                            case R.id.bt_arrivaltime:
                                if (arrivalDateBtn.getVisibility() == View.VISIBLE) {
                                    arrivalTimeBtn.setText(getResources().getString(R.string.time));
                                } else {
                                    arrivalTimeBtn.setText(getResources().getString(R.string.returnTime));
                                }
                                break;
                        }
                    }
                });
                break;
        }
    }

    ArrayList<String> weekDays = new ArrayList<>();

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_monday:
                if (isChecked) {
                    weekDays.add("Mon");
                } else {
                    if (weekDays.contains("Mon")) weekDays.remove("Mon");
                }
                break;
            case R.id.cb_tueday:
                if (isChecked) {
                    weekDays.add("Tue");
                } else {
                    if (weekDays.contains("Tue")) weekDays.remove("Tue");
                }
                break;
            case R.id.cb_wedday:
                if (isChecked) {
                    weekDays.add("Wed");
                } else {
                    if (weekDays.contains("Wed")) weekDays.remove("Wed");
                }
                break;
            case R.id.cb_thuday:
                if (isChecked) {
                    weekDays.add("Thu");
                } else {
                    if (weekDays.contains("Thu")) weekDays.remove("Thu");
                }
                break;
            case R.id.cb_friday:
                if (isChecked) {
                    weekDays.add("Fri");
                } else {
                    if (weekDays.contains("Fri")) weekDays.remove("Fri");
                }
                break;
            case R.id.cb_satday:
                if (isChecked) {
                    weekDays.add("Sat");
                } else {
                    if (weekDays.contains("Sat")) weekDays.remove("Sat");
                }
                break;
            case R.id.cb_sunday:
                if (isChecked) {
                    weekDays.add("Sun");
                } else {
                    if (weekDays.contains("Sun")) weekDays.remove("Sun");
                }
                break;
        }
    }
}
