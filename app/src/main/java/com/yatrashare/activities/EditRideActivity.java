package com.yatrashare.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.RideDetails;
import com.yatrashare.dtos.Seats;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.dtos.Vehicle;
import com.yatrashare.pojos.UpdateRide;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.sql.Time;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EditRideActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = EditRideActivity.class.getSimpleName();
    private RideDetails rideDetails;

    @Bind(R.id.rbtn_ride_vehicle_car)
    public RadioButton radioButtonCar;
    @Bind(R.id.rbtn_ride_vehicle_bike)
    public RadioButton radioButtonBike;
    @Bind(R.id.ride_selectModel)
    public Spinner selectModelSpinner;
    @Bind(R.id.ride_selectSeats)
    public Spinner selectSeatsSpinner;
    @Bind(R.id.et_edit_provideRideDetails)
    public EditText provideRideDetEditText;
    @Bind(R.id.edit_ride_luggageSpinner)
    public Spinner luggageSpinner;
    @Bind(R.id.edit_ride_timeFlexiSpinner)
    public Spinner timeFlexiSpinner;
    @Bind(R.id.edit_ride_detourSpinner)
    public Spinner detourSpinner;
    @Bind(R.id.cb_ride_ladiesOnly)
    public CheckBox ladiesOnlyCheckBox;
    @Bind(R.id.et_ride_PriceSymbol)
    public EditText priceSymbolEditText;
    @Bind(R.id.et_edit_RidePrice)
    public EditText ridePriceEditText;
    @Bind(R.id.bt_ride_departuredate)
    public Button departureDateBtn;
    @Bind(R.id.bt_ride_departuretime)
    public Button departureTimeBtn;
    public String selectedVehicle;
    @Bind(R.id.editRideBGView)
    public View mProgressBGView;
    @Bind(R.id.editRide_progress)
    public ProgressBar mProgressBar;
    private String userGuid;
    private Context mContext;
    private ArrayList<Vehicle.VehicleData> vehicleDatas;
    private String selectedVehicleId;
    private String selectedDetour;
    private String selectedModel;
    private String seatsSelected;
    private String selectedTimeFlexi;
    private String selectedLuggageSize;
    private int clickedButton;
    @Bind(R.id.edit_rideFrom)
    public TextView rideFrom;
    @Bind(R.id.edit_rideTo)
    public TextView rideTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        ButterKnife.bind(this);
        mContext = this;

        userGuid = getIntent().getExtras().getString("USERGUIDE", "");

        if (getIntent().hasExtra("RIDE DETAILS")) {
            rideDetails = (RideDetails) getIntent().getExtras().getSerializable("RIDE DETAILS");
        }

        if (rideDetails != null && !TextUtils.isEmpty(rideDetails.Data.VehicleType)) {
            if (rideDetails.Data.VehicleType.equalsIgnoreCase("Car")) {
                radioButtonCar.setChecked(true);
                radioButtonBike.setChecked(false);
                selectedVehicle = "1";
            } else {
                radioButtonCar.setChecked(false);
                radioButtonBike.setChecked(true);
                selectedVehicle = "2";
            }
        }

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        ridePriceEditText.setFilters(Utils.getInputFilter(4));

        getUserVehicleModels(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Ride");

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        CountryData countryData = Utils.getCountryInfo(EditRideActivity.this, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null)
            priceSymbolEditText.setText(Html.fromHtml(countryData.CurrencySymbol));

        ridePriceEditText.setText(rideDetails.Data.RoutePrice + "");

        if (rideDetails != null) {
            try {
                provideRideDetEditText.setText(!TextUtils.isEmpty(rideDetails.Data.OtherDetails) ? rideDetails.Data.OtherDetails : "");
                departureDateBtn.setText(rideDetails.Data.DepartureDate + "");
                departureTimeBtn.setText(rideDetails.Data.DepartureTime + "");
                luggageSpinner.setSelection(getLuggage("" + rideDetails.Data.MaxLuggageSize));
                timeFlexiSpinner.setSelection(getTimeFlexi("" + rideDetails.Data.DepartureTimeFlexi));
                detourSpinner.setSelection(getDetour("" + rideDetails.Data.MakeDetour));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        rideFrom.setText(!TextUtils.isEmpty(rideDetails.Data.Departure) ? rideDetails.Data.Departure : "");
        rideTo.setText(!TextUtils.isEmpty(rideDetails.Data.Arrival) ? rideDetails.Data.Arrival : "");

        luggageSpinner.setOnItemSelectedListener(this);
        selectModelSpinner.setOnItemSelectedListener(this);
        selectSeatsSpinner.setOnItemSelectedListener(this);
        timeFlexiSpinner.setOnItemSelectedListener(this);
        detourSpinner.setOnItemSelectedListener(this);
        departureDateBtn.setOnClickListener(this);
        departureTimeBtn.setOnClickListener(this);

        seatsSelected = rideDetails.Data.RemainingSeats;
        selectedModel = rideDetails.Data.VehicleModel;

        if (!TextUtils.isEmpty(rideDetails.Data.LadiesOnly) && rideDetails.Data.LadiesOnly.equalsIgnoreCase("true")) {
            ladiesOnlyCheckBox.setChecked(true);
        } else {
            ladiesOnlyCheckBox.setChecked(false);
        }

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                switch (clickedButton) {
                    case R.id.bt_ride_departuredate:
                        departureDateBtn.setText("" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + +year);
                        break;
                }
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                switch (clickedButton) {
                    case R.id.bt_ride_departuretime:
                        departureTimeBtn.setText(getTime(hourOfDay, minute));
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        radioButtonCar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectSeatsSpinner.setEnabled(true);
                    luggageSpinner.setVisibility(View.VISIBLE);
                    selectedVehicle = "1";
                    ArrayList<String> vehicleSeats = new ArrayList<String>();
                    vehicleSeats.add("Select Seats");
                    selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    getUserVehicleModels(false);
                }
            }
        });
        radioButtonBike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectSeatsSpinner.setEnabled(true);
                    luggageSpinner.setVisibility(View.GONE);
                    selectedVehicle = "2";
                    ArrayList<String> vehicleSeats = new ArrayList<String>();
                    vehicleSeats.add("Select Seats");
                    selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    getUserVehicleModels(false);
                }
            }
        });
    }

    private String getTime(int hr, int min) {
        try {
            Time tme = new Time(hr, min, 0);//seconds by default set to zero
            Format formatter = new SimpleDateFormat("hh:mm a");
            return formatter.format(tme);
        } catch (Exception e) {
            return "" + hr + ":" + min;
        }
    }

    private String getVehicleId(String selectedModel) {
        if (selectedModel != null) {
            if (vehicleDatas != null && vehicleDatas.size() > 0) {
                for (int i = 0; i < vehicleDatas.size(); i++) {
                    if (vehicleDatas.get(i).ModelName.equalsIgnoreCase(selectedModel)) {
                        return vehicleDatas.get(i).UserVehicleId;
                    }
                }
            }
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void getUserVehicleModels(final boolean isLoad) {
        ArrayList<String> vehicleModels = new ArrayList<String>();
        vehicleModels.add("Select Model");
        selectModelSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleModels));
        if (Utils.isInternetAvailable(mContext)) {
            Utils.showProgress(true, mProgressBar, mProgressBGView);
            Call<Vehicle> call = Utils.getYatraShareAPI(this).getUserVehicleModels(userGuid, selectedVehicle);
            call.enqueue(new Callback<Vehicle>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<Vehicle> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    ArrayList<String> vehicleModels = new ArrayList<String>();
                    vehicleModels.add("Select Model");
                    if (response.body() != null && response.isSuccess()) {
                        android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                        vehicleDatas = response.body().Data;
                        if (vehicleDatas != null && vehicleDatas.size() > 0) {
                            for (int i = 0; i < vehicleDatas.size(); i++) {
                                if (!vehicleDatas.get(i).ModelName.isEmpty()) {
                                    vehicleModels.add(vehicleDatas.get(i).ModelName);
                                }
                            }
                        }
                    }
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    selectModelSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleModels));
                    if (isLoad) {
                        loadSelectedVehicle();
                    }
                    selectedVehicleId = getVehicleId(rideDetails.Data.VehicleModel);
                    if (!TextUtils.isEmpty(selectedVehicleId)) {
                        getVehicleSeats(selectedVehicleId, isLoad);
                    }
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable Error
                 */
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    private void getVehicleSeats(String vehicleId, final boolean isLoad) {
        if (Utils.isInternetAvailable(mContext)) {
            selectSeatsSpinner.setEnabled(true);
            Utils.showProgress(true, mProgressBar, mProgressBGView);
            Call<Seats> call = Utils.getYatraShareAPI(this).getUserVehicleSeats(userGuid, vehicleId);
            call.enqueue(new Callback<Seats>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<Seats> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    ArrayList<String> vehicleSeats = new ArrayList<String>();
                    if (response.body() != null && response.isSuccess()) {
                        android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                        ArrayList<String> vehicleDatas = response.body().Data;
                        if (vehicleDatas != null && vehicleDatas.size() > 0) {
                            if (vehicleDatas.size() == 1) {
                                vehicleSeats.add("1");
                                selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                                selectSeatsSpinner.setEnabled(false);
                            } else {
                                vehicleSeats.add("Select Seats");
                                for (int i = 0; i < vehicleDatas.size(); i++) {
                                    if (!TextUtils.isEmpty(vehicleDatas.get(i))) {
                                        vehicleSeats.add(vehicleDatas.get(i));
                                    }
                                }
                            }
                        } else {
                            vehicleSeats.add("Select Seats");
                        }
                    } else {
                        vehicleSeats.add("Select Seats");
                    }
                    selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    if (isLoad) {
                        loadSelectedSeats();
                    }
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable Error
                 */
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Utils.showProgress(false, mProgressBar, mProgressBGView);
                    showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    private int year, month, day, hour, minute;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    @Override
    public void onClick(final View v) {
        clickedButton = v.getId();
        switch (v.getId()) {
            case R.id.bt_ride_departuredate:
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, mDateSetListener, year, month, day);
                if (v.getId() == R.id.bt_ride_departuredate) {
                    mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                } else {
                    String departureDate = departureDateBtn.getText().toString();
                    if (!TextUtils.isEmpty(departureDate) && !departureDate.equalsIgnoreCase(getString(R.string.departuredate))) {
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        try {
                            date = format.parse(departureDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mDatePickerDialog.getDatePicker().setMinDate(date.getTime() - 1000);
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
                            case R.id.bt_ride_departuredate:
                                departureDateBtn.setText("" + rideDetails.Data.DepartureDate);
                                break;
                        }
                    }
                });
                break;
            case R.id.bt_ride_departuretime:
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
                            case R.id.bt_ride_departuretime:
                                departureTimeBtn.setText("" + rideDetails.Data.DepartureTime);
                                break;
                        }
                    }
                });
                break;
        }
    }

    @OnClick(R.id.btn_UpdateRide)
    public void updateRide() {
        String departureDate = departureDateBtn.getText().toString();
        String departureTime = departureTimeBtn.getText().toString();
        String otherDetails = provideRideDetEditText.getText().toString();
        String ridePrice = ridePriceEditText.getText().toString();

        if (TextUtils.isEmpty(ridePrice)) {
            Utils.showToast(mContext, "Price cannot be empty");
            return;
        }

        try {
            if (Integer.parseInt(ridePrice) == 0) {
                Utils.showToast(mContext, "Price cannot be zero");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(departureDate)) {
            Utils.showToast(this, "Enter Departure Date");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());

        if (TextUtils.isEmpty(departureTime)) {
            Utils.showToast(this, "Enter Departure Time");
            return;
        }

        if (TextUtils.isEmpty(selectedModel) || selectedModel.equalsIgnoreCase("Select Model")) {
            Utils.showToast(mContext, "Select Vehicle Model");
            return;
        }
        if (TextUtils.isEmpty(seatsSelected) || seatsSelected.equalsIgnoreCase("Select Seats")) {
            Utils.showToast(mContext, "Select Seats");
            return;
        }

        Date todaysDate = new Date();
        String timeString = format.format(todaysDate.getTime() + Utils.TIME_CHECKER);
        try {
            Date rideTime = format.parse(departureDate + " " + departureTime);
            Date currentTime = format.parse(timeString);
            if (currentTime.after(rideTime)) {
                Utils.showToast(this, "add 3 hours to your selected time ");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UpdateRide updateRide = new UpdateRide(rideDetails.Data.PossibleRideGuid, seatsSelected, departureDate, departureTime, selectedTimeFlexi, ladiesOnlyCheckBox.isChecked() + "",
                selectedDetour, selectedLuggageSize, otherDetails, ridePrice, getVehicleId(selectedModel));

        Gson gson = new Gson();
        Log.e(TAG, "updateRide: " + gson.toJson(updateRide));
        Utils.showProgress(true, mProgressBar, mProgressBGView);

        Call<UserDataDTO> call = Utils.getYatraShareAPI(this).updateRide(userGuid, updateRide);
        call.enqueue(new Callback<UserDataDTO>() {
            @Override
            public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        showSnackBar("Ride updated Successfully");
                        finish();
                    } else {
                        showSnackBar(response.body().Data);
                    }
                }
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Utils.showProgress(false, mProgressBar, mProgressBGView);
                showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    private void loadSelectedSeats() {
        try {
            int seats = Integer.parseInt(!TextUtils.isEmpty(rideDetails.Data.RemainingSeats) ? rideDetails.Data.RemainingSeats : "0");
            if (selectSeatsSpinner.getCount() >= seats) {
                selectSeatsSpinner.setSelection(seats);
            } else if (selectSeatsSpinner.getCount() >= (seats - 1)) {
                selectSeatsSpinner.setSelection(seats - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedVehicle() {
        try {
            if (!TextUtils.isEmpty(rideDetails.Data.VehicleModel)) {
                if (vehicleDatas != null) {
                    for (int i = 0; i < vehicleDatas.size(); i++) {
                        if (vehicleDatas.get(i).ModelName.equalsIgnoreCase(rideDetails.Data.VehicleModel)) {
                            if (selectModelSpinner.getCount() >= (i + 1)) {
                                selectModelSpinner.setSelection(i + 1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSnackBar(String msg) {
        Snackbar.make(selectModelSpinner, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public int getTimeFlexi(String flexi) {
        selectedTimeFlexi = flexi;
        if (flexi.equalsIgnoreCase("15Mins")) {
            return 1;
        } else if (flexi.equalsIgnoreCase("30Mins")) {
            return 2;
        } else if (flexi.equalsIgnoreCase("1Hour")) {
            return 3;
        } else if (flexi.equalsIgnoreCase("2Hours")) {
            return 4;
        }
        return 0;
    }

    public int getDetour(String detour) {
        selectedDetour = detour;
        if (detour.equalsIgnoreCase("NoDetour")) {
            return 1;
        } else if (detour.equalsIgnoreCase("15MinsDetour")) {
            return 2;
        } else if (detour.equalsIgnoreCase("30MinsDetour")) {
            return 3;
        } else if (detour.equalsIgnoreCase("AnyDetour")) {
            return 4;
        }
        return 0;
    }

    public int getLuggage(String lugggace) {
        selectedLuggageSize = lugggace;
        if (lugggace.equalsIgnoreCase("Small")) {
            return 1;
        } else if (lugggace.equalsIgnoreCase("Medium")) {
            return 2;
        } else if (lugggace.equalsIgnoreCase("High")) {
            return 3;
        }
        return 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ride_selectModel:
                selectedModel = (String) parent.getAdapter().getItem(position);
                selectedVehicleId = getVehicleId(selectedModel);
                if (selectedVehicleId != null) {
                    getVehicleSeats(selectedVehicleId, false);
                }
                break;
            case R.id.ride_selectSeats:
                seatsSelected = (String) parent.getAdapter().getItem(position);
                break;
            case R.id.edit_ride_timeFlexiSpinner:
                switch (position) {
                    case 0:
                    case 1:
                        selectedTimeFlexi = "15Mins";
                        break;
                    case 2:
                        selectedTimeFlexi = "30Mins";
                        break;
                    case 3:
                        selectedTimeFlexi = "1Hour";
                        break;
                    case 4:
                        selectedTimeFlexi = "2Hours";
                        break;
                }
                break;
            case R.id.edit_ride_detourSpinner:
                switch (position) {
                    case 0:
                    case 1:
                        selectedDetour = "NoDetour";
                        break;
                    case 2:
                        selectedDetour = "15MinsDetour";
                        break;
                    case 3:
                        selectedDetour = "30MinsDetour";
                        break;
                    case 4:
                        selectedDetour = "AnyDetour";
                        break;
                }
                break;
            case R.id.edit_ride_luggageSpinner:
                selectedLuggageSize = (String) parent.getAdapter().getItem(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
