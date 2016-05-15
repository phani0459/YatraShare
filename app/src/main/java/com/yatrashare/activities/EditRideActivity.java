package com.yatrashare.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.yatrashare.R;
import com.yatrashare.dtos.SearchRides;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditRideActivity extends AppCompatActivity {

    private SearchRides.SearchData rideData;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        ButterKnife.bind(this);

        rideData = (SearchRides.SearchData) getIntent().getExtras().getSerializable("RIDE");


    }
}
