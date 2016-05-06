package com.yatrashare.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
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
import android.widget.ProgressBar;
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
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.SerializedPlace;
import com.yatrashare.fragments.PublishRideFragment;
import com.yatrashare.pojos.RideInfoDto;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

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
    @Bind(R.id.return_date_layout)
    public LinearLayout returnDateLayout;
    private int clickedButton;
    private SerializedPlace departurePlace;
    private SerializedPlace arrivalPlace;
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
    @Bind(R.id.offerRideView)
    public View mProgressBGView;
    @Bind(R.id.offerRideProgressBar)
    public ProgressBar mProgressBar;
    @Bind(R.id.et_PriceSymbol)
    public EditText priceSymbolEditText;


    private ArrayList<Place> stopOverPlaces;
    private PublishRideFragment publishRideFragment;
    private CountryData countryData;

    public void possibleRoutes() {
        allPossibleRoutes = new ArrayList<>();
        mainPossibleRoutes = new ArrayList<>();
        if (departurePlace != null && arrivalPlace != null) {
            RideInfoDto.PossibleRoutesDto possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
            possibleRoutesDto.setDepartureLatitude(departurePlace.latitude);
            possibleRoutesDto.setDepartureLongitude(departurePlace.longitude);
            possibleRoutesDto.setArrivalLatitude(arrivalPlace.latitude);
            possibleRoutesDto.setArrivalLongitude(arrivalPlace.longitude);
            possibleRoutesDto.setIsMainRoute(true);
            allPossibleRoutes.add(possibleRoutesDto);
            mainPossibleRoutes.add(possibleRoutesDto);
            if (stopOverPlacesHashMap != null && stopOverPlacesHashMap.size() > 0) {
                stopOverPlaces = new ArrayList<>();
                for (Iterator<Integer> iterator = stopOverPlacesHashMap.keySet().iterator(); iterator.hasNext(); ) {
                    stopOverPlaces.add(stopOverPlacesHashMap.get(iterator.next()));
                }
                if (stopOverPlaces.size() > 0) {
                    for (int i = 0; i < stopOverPlaces.size(); i++) {

                        possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
                        possibleRoutesDto.setDepartureLatitude(departurePlace.latitude);
                        possibleRoutesDto.setDepartureLongitude(departurePlace.longitude);
                        possibleRoutesDto.setArrivalLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                        possibleRoutesDto.setArrivalLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                        possibleRoutesDto.setIsMainRoute(false);
                        allPossibleRoutes.add(possibleRoutesDto);

                        possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
                        possibleRoutesDto.setDepartureLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                        possibleRoutesDto.setDepartureLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                        possibleRoutesDto.setArrivalLatitude(arrivalPlace.latitude);
                        possibleRoutesDto.setArrivalLongitude(arrivalPlace.longitude);
                        possibleRoutesDto.setIsMainRoute(false);
                        allPossibleRoutes.add(possibleRoutesDto);

                        if (i < stopOverPlaces.size() - 1) {
                            possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
                            possibleRoutesDto.setDepartureLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                            possibleRoutesDto.setDepartureLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                            possibleRoutesDto.setArrivalLatitude(stopOverPlaces.get(i + 1).getLatLng().latitude);
                            possibleRoutesDto.setIsMainRoute(false);
                            possibleRoutesDto.setArrivalLongitude(stopOverPlaces.get(i + 1).getLatLng().longitude);
                            allPossibleRoutes.add(possibleRoutesDto);
                        }

                        if (i == 0) {
                            possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
                            possibleRoutesDto.setDepartureLatitude(departurePlace.latitude);
                            possibleRoutesDto.setDepartureLongitude(departurePlace.longitude);
                            possibleRoutesDto.setArrivalLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                            possibleRoutesDto.setArrivalLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                            possibleRoutesDto.setIsMainRoute(false);
                            mainPossibleRoutes.add(possibleRoutesDto);
                        }
                        if (i == stopOverPlaces.size() - 1) {
                            possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
                            possibleRoutesDto.setDepartureLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                            possibleRoutesDto.setDepartureLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                            possibleRoutesDto.setArrivalLatitude(arrivalPlace.latitude);
                            possibleRoutesDto.setArrivalLongitude(arrivalPlace.longitude);
                            possibleRoutesDto.setIsMainRoute(false);
                            mainPossibleRoutes.add(possibleRoutesDto);
                        }
                        if (i < stopOverPlaces.size() - 1) {
                            possibleRoutesDto = new RideInfoDto().new PossibleRoutesDto();
                            possibleRoutesDto.setDepartureLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                            possibleRoutesDto.setDepartureLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                            possibleRoutesDto.setArrivalLatitude(stopOverPlaces.get(i + 1).getLatLng().latitude);
                            possibleRoutesDto.setArrivalLongitude(stopOverPlaces.get(i + 1).getLatLng().longitude);
                            possibleRoutesDto.setIsMainRoute(false);
                            mainPossibleRoutes.add(possibleRoutesDto);
                        }
                    }
                }
            }
        }
    }

    ArrayList<RideInfoDto.PossibleRoutesDto> allPossibleRoutes = new ArrayList<>();
    ArrayList<RideInfoDto.PossibleRoutesDto> mainPossibleRoutes = new ArrayList<>();

    public void updatePrice() {
        if (departurePlace != null && arrivalPlace != null) {
            float[] results = new float[1];
            if (stopOverPlacesHashMap != null && stopOverPlacesHashMap.size() > 0) {
                ArrayList<Place> stopOverPlaces = new ArrayList<>();
                for (Integer integer : stopOverPlacesHashMap.keySet()) {
                    stopOverPlaces.add(stopOverPlacesHashMap.get(integer));
                }
                if (stopOverPlaces.size() > 0) {
                    Location.distanceBetween(departurePlace.latitude, departurePlace.longitude,
                            stopOverPlaces.get(0).getLatLng().latitude, stopOverPlaces.get(0).getLatLng().longitude, results);
                    totalKM = results[0] / 1000;

                    for (int i = 0; i < stopOverPlaces.size() - 1; i++) {
                        results = new float[1];
                        Location.distanceBetween(stopOverPlaces.get(i).getLatLng().latitude, stopOverPlaces.get(i).getLatLng().longitude,
                                stopOverPlaces.get(i + 1).getLatLng().latitude, stopOverPlaces.get(i + 1).getLatLng().longitude, results);
                        totalKM = totalKM + results[0] / 1000;
                    }

                }
                results = new float[1];
                Location.distanceBetween(stopOverPlaces.get(stopOverPlaces.size() - 1).getLatLng().latitude, stopOverPlaces.get(stopOverPlaces.size() - 1).getLatLng().longitude,
                        arrivalPlace.latitude, arrivalPlace.longitude, results);
                totalKM = totalKM + (results[0] / 1000);
            } else {
                Location.distanceBetween(departurePlace.latitude, departurePlace.longitude, arrivalPlace.latitude, arrivalPlace.longitude, results);
                totalKM = results[0] / 1000;
            }

            totalPrice = getPrice(totalKM);
        } else {
            totalKM = 0;
            totalPrice = 0;
        }
        ridePriceEditText.setText("" + totalPrice);
    }

    public long getPrice(float km) {
        double fareRate = 1.8;
        if (countryData != null) fareRate = countryData.FarePerDistance;
        long price;
        if (km > 0 && km < 10) {
            price = 20;
        } else {
            price = Math.round(km * fareRate / 10) * 10;
        }
        return price;
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

        ridePriceEditText.setText("0");

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        countryData = Utils.getCountryInfo(OfferRideActivity.this, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null)
            priceSymbolEditText.setText(Html.fromHtml(countryData.CurrencySymbol));

        offerWhereFromEdit.setInputType(InputType.TYPE_NULL);
        offerWhereToEdit.setInputType(InputType.TYPE_NULL);
        stopOverEdit.setInputType(InputType.TYPE_NULL);

        offerWhereFromEdit.setText(departure);
        offerWhereToEdit.setText(arrival);

        departurePlace = (SerializedPlace) getIntent().getExtras().getSerializable("DEPARTURE");
        arrivalPlace = (SerializedPlace) getIntent().getExtras().getSerializable("ARRIVAL");
        String date = getIntent().getExtras().getString("DATE", "");

        offerWhereFromEdit.setText(departurePlace != null ? departurePlace.address : "");
        offerWhereToEdit.setText(arrivalPlace != null ? arrivalPlace.address : "");
        departureDateBtn.setText(TextUtils.isEmpty(date) ? getString(R.string.departuredate) : date);

        updatePrice();

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
        Utils.hideSoftKeyboard(offerWhereFromEdit);

        RideInfoDto rideInfoDto = new RideInfoDto();
        String rideDeparture = offerWhereFromEdit.getText().toString();
        String rideArrival = offerWhereToEdit.getText().toString();
        String rideDepartureDate = departureDateBtn.getText().toString().equalsIgnoreCase(getString(R.string.departuredate)) ? "" : departureDateBtn.getText().toString();
        String rideDepartureTime = departureTimeBtn.getText().toString().equalsIgnoreCase(getString(R.string.time)) ? "" : departureTimeBtn.getText().toString();
        String rideArrivalDate = arrivalDateBtn.getText().toString().equalsIgnoreCase(getString(R.string.returndate)) ? "" : arrivalDateBtn.getText().toString();
        String rideArrivalTime = arrivalTimeBtn.getText().toString();

        if (rideArrivalTime.equalsIgnoreCase(getString(R.string.time)) || rideArrivalTime.equalsIgnoreCase(getString(R.string.returnTime))) {
            rideArrivalTime = "";
        }

        if (TextUtils.isEmpty(rideDeparture)) {
            Utils.showToast(this, "Enter Departure");
            return;
        }

        if (TextUtils.isEmpty(rideArrival)) {
            Utils.showToast(this, "Enter Arrival");
            return;
        }

        if (rideDeparture.equalsIgnoreCase(rideArrival)) {
            Utils.showToast(this, "Departure and Arrival should not be same");
            return;
        }

        if (TextUtils.isEmpty(rideDepartureDate)) {
            Utils.showToast(this, "Enter Departure Date");
            return;
        }

        if (TextUtils.isEmpty(rideDepartureTime)) {
            Utils.showToast(this, "Enter Departure Time");
            return;
        }

        if (roundTripCheckBox.isChecked()) {
            if (longRideRB.isChecked()) {
                if (TextUtils.isEmpty(rideArrivalDate)) {
                    Utils.showToast(this, "Enter Return Date");
                    return;
                }

                if (rideDepartureDate.equalsIgnoreCase(rideArrivalDate) && rideArrivalTime.equalsIgnoreCase(rideDepartureTime)) {
                    Utils.showToast(this, "Return Time and departure time cannot be same");
                    return;
                }

                Date departureDate = new Date();
                Date arrivalDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    departureDate = format.parse(rideDepartureDate);
                    arrivalDate = format.parse(rideArrivalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (departureDate.after(arrivalDate)) {
                    Utils.showToast(this, "Return Date cannot be before Departure Date");
                    return;
                }

            }

            if (TextUtils.isEmpty(rideArrivalTime)) {
                Utils.showToast(this, "Enter Return Time");
                return;
            }

            if (dailyRideRB.isChecked()) {
                if (rideArrivalTime.equalsIgnoreCase(rideDepartureTime)) {
                    Utils.showToast(this, "Return Time and departure time cannot be same");
                    return;
                }
            }

        }
        rideInfoDto.setmReturnDate(rideArrivalDate);
        rideInfoDto.setmDepartureDate(rideDepartureDate);
        rideInfoDto.setmTotalprice(totalPrice + "");
        rideInfoDto.setmTotalprice(totalPrice + "");
        rideInfoDto.setmTotalkilometers(totalKM + "");
        rideInfoDto.setmRideType(longRideRB.isChecked() ? "1" : "2");
        rideInfoDto.setmReturnTime(rideArrivalTime);
        rideInfoDto.setmDepartureTime(rideDepartureTime);
        rideInfoDto.setmRideArrival(rideArrival);
        rideInfoDto.setmRideDeparture(rideDeparture);
        rideInfoDto.setUserUpdatedPrice(ridePriceEditText.getText().toString());
        rideInfoDto.setmSelectedWeekdays(longRideRB.isChecked() ? null : weekDays);

        possibleRoutes();

        rideInfoDto.setmAllPossibleRoutes(allPossibleRoutes);
        rideInfoDto.setmMainPossibleRoutes(mainPossibleRoutes);

        ArrayList<RideInfoDto.StopOvers> latLngs = new ArrayList<>();
        if (stopOverPlaces != null && stopOverPlaces.size() > 0) {
            for (int i = 0; i < stopOverPlaces.size(); i++) {
                RideInfoDto.StopOvers stopOver = new RideInfoDto().new StopOvers();
                stopOver.setStopOverLatitude(stopOverPlaces.get(i).getLatLng().latitude);
                stopOver.setStopOverLongitude(stopOverPlaces.get(i).getLatLng().longitude);
                stopOver.setStopOverLocation(stopOverPlaces.get(i).getAddress() + "");
                latLngs.add(stopOver);
            }
        }
        rideInfoDto.setmStopOvers(latLngs);

        Log.e("allPossibleRoutes" + allPossibleRoutes.size(), "mainPossibleRoutes" + mainPossibleRoutes.size());

        publishRideFragment = new PublishRideFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("RIDE INFO", rideInfoDto);
        publishRideFragment.setArguments(bundle);
        currentScreen = 1;
        getSupportFragmentManager().beginTransaction().add(R.id.offerRideContent, publishRideFragment, null).commit();
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

    public SerializedPlace preparePlace(Place place) {
        SerializedPlace serializedPlace = new SerializedPlace();
        serializedPlace.latitude = place.getLatLng().latitude;
        serializedPlace.longitude = place.getLatLng().longitude;
        serializedPlace.address = (String) place.getAddress();
        return serializedPlace;
    }

    public void setEditValues(Place selectedPlace) {
        switch (selectedEditText) {
            case R.id.et_ofr_where_from:
                if (selectedPlace != null) {
                    departurePlace = preparePlace(selectedPlace);
                    offerWhereFromEdit.setText(selectedPlace.getAddress());
                } else {
                    departurePlace = null;
                    offerWhereFromEdit.setText("");
                }
                updatePrice();
                break;
            case R.id.et_ofr_where_to:
                if (selectedPlace != null) {
                    arrivalPlace = preparePlace(selectedPlace);
                    offerWhereToEdit.setText(selectedPlace.getAddress());
                } else {
                    arrivalPlace = null;
                    offerWhereToEdit.setText("");
                }
                updatePrice();
                break;
            case R.id.et_stopover_point:
                if (selectedPlace != null) {
                    if (!stopOverPlacesHashMap.containsKey(stopOverEdit.getId())) {
                        stopOverPlacesHashMap.put(stopOverEdit.getId(), selectedPlace);
                    }
                    stopOverEdit.setText(selectedPlace.getAddress());
                } else {
                    if (stopOverPlacesHashMap.containsKey(stopOverEdit.getId())) {
                        stopOverPlacesHashMap.remove(stopOverEdit.getId());
                    }
                    stopOverEdit.setText("");
                }
                updatePrice();
                break;
            default:
                try {
                    EditText editText = (EditText) stopOversLayout.findViewById(selectedEditText);
                    if (selectedPlace != null) {
                        if (!stopOverPlacesHashMap.containsKey(selectedEditText)) {
                            stopOverPlacesHashMap.put(selectedEditText, selectedPlace);
                        }
                        editText.setText(selectedPlace.getAddress());
                    } else {
                        if (stopOverPlacesHashMap.containsKey(selectedEditText)) {
                            stopOverPlacesHashMap.remove(selectedEditText);
                        }
                        editText.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updatePrice();
                break;
        }
    }

    HashMap<Integer, Place> stopOverPlacesHashMap = new HashMap<>();

    public int checkIds(int id) {
        ArrayList<Integer> ints = new ArrayList<>();

        for (int i = 0; i < stopOversLayout.getChildCount(); i++) {
            ints.add(stopOversLayout.getChildAt(i).getId());
        }

        if (ints.contains(id)) {
            return checkIds(id + 1);
        }

        return id;
    }

    @OnClick(R.id.tv_addStopOverPoints)
    public void addStopOverEditText() {
        final EditText editText = new EditText(this);
        final int editId = checkIds(stopOversLayout.getChildCount() + 1);
        editText.setId(editId);
        editText.setHint(getString(R.string.ride_stopovers));
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ride_middle, 0, R.drawable.close, 0);
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
                    try {
                        if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                            if (stopOverPlacesHashMap.containsKey(editText.getId())) {
                                stopOverPlacesHashMap.remove(editText.getId());
                            }
                            stopOversLayout.removeView(editText);
                            updatePrice();
                            return true;
                        } else {
                            openAutocompleteActivity(editId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    int currentScreen = 0;

    @Override
    public void onBackPressed() {
        if (currentScreen == 1) {
            currentScreen = 0;
            if (publishRideFragment != null)
                getSupportFragmentManager().beginTransaction().remove(publishRideFragment).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopOverEdit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ride_middle, 0, R.drawable.close, 0);
        stopOverEdit.setCompoundDrawablePadding(4);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                switch (clickedButton) {
                    case R.id.bt_departuredate:
                        departureDateBtn.setText("" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + +year);
                        break;
                    case R.id.bt_arrivaldate:
                        arrivalDateBtn.setText("" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
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
        }
    }

    public static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (currentScreen == 1) {
                currentScreen = 0;
                if (publishRideFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(publishRideFragment).commit();
            } else {
                finish();
            }
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
                    try {
                        if (event.getRawX() >= (stopOverEdit.getRight() - stopOverEdit.getCompoundDrawables()[2].getBounds().width())) {
                            if (stopOverPlacesHashMap.containsKey(stopOverEdit.getId())) {
                                stopOverPlacesHashMap.remove(stopOverEdit.getId());
                            }
                            stopOverEdit.setText("");
                            updatePrice();
                            return true;
                        } else {
                            openAutocompleteActivity(R.id.et_stopover_point);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                if (v.getId() == R.id.bt_departuredate) {
                    mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                } else {
                    String departureDate = departureDateBtn.getText().toString();
                    if (!TextUtils.isEmpty(departureDate)) {
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                        try {
                            date = format.parse(departureDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mDatePickerDialog.getDatePicker().setMinDate(date.getTime());
                    }
                }
                mDatePickerDialog.setTitle("");
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
