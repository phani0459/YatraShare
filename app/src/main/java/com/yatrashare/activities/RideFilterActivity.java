package com.yatrashare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.yatrashare.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RideFilterActivity extends AppCompatActivity {

    @Bind(R.id.rg_rideType)
    public RadioGroup rideTypeRadioGroup;
    @Bind(R.id.rg_genderType)
    public RadioGroup genderRadioGroup;
    @Bind(R.id.sp_vehicleType)
    public Spinner vehicleTypeSpinner;
    @Bind(R.id.sp_vehicleComforType)
    public Spinner vehicleComfortTypeSpinner;

    @Bind(R.id.rbtn_All)
    public RadioButton genderAllRadioBtn;
    @Bind(R.id.rbtn_dailyRide)
    public RadioButton dailyRideRadioButton;
    @Bind(R.id.rbtn_ladiesOnly)
    public RadioButton ladiesOnlyRadioBtn;
    @Bind(R.id.rbtn_longRide)
    public RadioButton longRideRadioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ride_filter);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        rideType = data.getString("RIDE TYPE", "2");
        vehicleType = data.getString("VEHICLE TYPE", "2");
        comfortLevel = data.getString("COMFORT TYPE", "ALLTYPES");
        gender = data.getString("GENDER", "All");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Refine Rides");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (vehicleType.equalsIgnoreCase("1")) {
            vehicleTypeSpinner.setSelection(1);
        }

        if (comfortLevel.equalsIgnoreCase("LUXURY")) {
            vehicleComfortTypeSpinner.setSelection(1);
        } else if (comfortLevel.equalsIgnoreCase("COMFORT")) {
            vehicleComfortTypeSpinner.setSelection(2);
        } else if (comfortLevel.equalsIgnoreCase("NORMAL")) {
            vehicleComfortTypeSpinner.setSelection(3);
        } else if (comfortLevel.equalsIgnoreCase("BASIC")) {
            vehicleComfortTypeSpinner.setSelection(4);
        }

        if (gender.equalsIgnoreCase("LadiesOnly")) {
            ladiesOnlyRadioBtn.setChecked(true);
        } else {
            genderAllRadioBtn.setChecked(true);
        }

        if (rideType.equalsIgnoreCase("2")) {
            dailyRideRadioButton.setChecked(true);
        } else {
            longRideRadioBtn.setChecked(true);
        }

    }

    public String rideType;
    public String vehicleType;
    public String comfortLevel;
    public String gender;

    @Override
    protected void onResume() {
        super.onResume();
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    vehicleType = "2";
                } else {
                    vehicleType = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vehicleComfortTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        comfortLevel = "ALLTYPES";
                        break;
                    case 1:
                        comfortLevel = "LUXURY";
                        break;
                    case 2:
                        comfortLevel = "COMFORT";
                        break;
                    case 3:
                        comfortLevel = "NORMAL";
                        break;
                    case 4:
                        comfortLevel = "BASIC";
                        break;
                    default:
                        comfortLevel = "ALLTYPES";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void prepareIntent() {
        getDetails();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("RIDE TYPE", rideType);
        returnIntent.putExtra("VEHICLE TYPE", vehicleType);
        returnIntent.putExtra("COMFORT TYPE", comfortLevel);
        returnIntent.putExtra("GENDER", gender);
        setResult(RESULT_OK, returnIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            prepareIntent();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        prepareIntent();
        super.onBackPressed();
    }

    private void getDetails() {
        int rideTypeBtn = rideTypeRadioGroup.getCheckedRadioButtonId();
        switch (rideTypeBtn) {
            case R.id.rbtn_longRide:
                rideType = "1";
                break;
            default:
                rideType = "2";
                break;
        }
        int genderBtn = genderRadioGroup.getCheckedRadioButtonId();
        switch (genderBtn) {
            case R.id.rbtn_ladiesOnly:
                Log.e("asgtewtewtewtewt", "rytruetutruetuetuet");
                gender = "LadiesOnly";
                break;
            default:
                gender = "All";
                break;
        }
    }
}
