package com.yatrashare.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.appyvet.rangebar.RangeBar;
import com.yatrashare.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RideFilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RideFilterActivity.class.getSimpleName();
    @Bind(R.id.rg_rideType)
    public RadioGroup rideTypeRadioGroup;
    @Bind(R.id.rg_genderType)
    public RadioGroup genderRadioGroup;
    @Bind(R.id.sp_vehicleType)
    public Spinner vehicleTypeSpinner;
    @Bind(R.id.sp_vehicleComforType)
    public Spinner vehicleComfortTypeSpinner;
    @Bind(R.id.et_dateFilter)
    public EditText dateEditText;
    @Bind(R.id.rbtn_All)
    public RadioButton genderAllRadioBtn;
    @Bind(R.id.rbtn_dailyRide)
    public RadioButton dailyRideRadioButton;
    @Bind(R.id.rbtn_allRides)
    public RadioButton allRideRadioButton;
    @Bind(R.id.rbtn_ladiesOnly)
    public RadioButton ladiesOnlyRadioBtn;
    @Bind(R.id.rg_vehicleRegdType)
    public RadioGroup vehicleRegdTypeRadioGroup;
    @Bind(R.id.rbtn_longRide)
    public RadioButton longRideRadioBtn;
    @Bind(R.id.rbtn_vehicleRegdAll)
    public RadioButton vehicleRegdAllRadioBtn;
    @Bind(R.id.rbtn_vehicleRegdEven)
    public RadioButton vehicleRegdEvenRadioBtn;
    @Bind(R.id.rbtn_vehicleRegdOdd)
    public RadioButton vehicleRegdOddRadioBtn;
    @Bind(R.id.timeRangeBar)
    public RangeBar rangeBar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_filter);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        rideType = data.getString("RIDE TYPE", "2");
        vehicleType = data.getString("VEHICLE TYPE", "2");
        comfortLevel = data.getString("COMFORT TYPE", "ALLTYPES");
        gender = data.getString("GENDER", "All");
        date = data.getString("DATE", "");
        startTime = data.getString("START TIME", "1");
        endTime = data.getString("END TIME", "24");

        /**
         * Odd - 1
         * Even - 2
         * All - 0
         */
        vehicleRegdNo = data.getString("VEHICLE REGD", "0");

        mContext = this;

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Refine Rides");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (vehicleType.equalsIgnoreCase("1")) {
            vehicleTypeSpinner.setSelection(1);
        }

        if (vehicleRegdNo.equalsIgnoreCase("0")) {
            vehicleRegdAllRadioBtn.setChecked(true);
        } else if (vehicleRegdNo.equalsIgnoreCase("1")) {
            vehicleRegdOddRadioBtn.setChecked(true);
        } else {
            vehicleRegdEvenRadioBtn.setChecked(true);
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

        rangeBar.setRangePinsByValue(Float.parseFloat(startTime), Float.parseFloat(endTime));

        if (gender.equalsIgnoreCase("LadiesOnly")) {
            ladiesOnlyRadioBtn.setChecked(true);
        } else {
            genderAllRadioBtn.setChecked(true);
        }

        rideTypeRadioGroup.clearCheck();
        rideTypeRadioGroup.clearFocus();

        if (rideType.equalsIgnoreCase("2")) {
            dailyRideRadioButton.setChecked(true);
        } else if (rideType.equalsIgnoreCase("1")) {
            longRideRadioBtn.setChecked(true);
        } else {
            allRideRadioButton.setChecked(true);
        }

    }

    public String rideType;
    public String vehicleType;
    public String comfortLevel;
    public String gender;
    public String date;
    public String startTime;
    public String endTime, vehicleRegdNo;

    @Override
    protected void onResume() {
        super.onResume();

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                startTime = leftPinValue + "";
                endTime = rightPinValue + "";
            }
        });

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

        dateEditText.setText(date);
        dateEditText.setInputType(InputType.TYPE_NULL);

        Calendar newCalendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = "" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                dateEditText.setText(date);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        if (!TextUtils.isEmpty(date)) {
            try {
                String[] strings = date.split("/");
                for (int i = 0; i < strings.length; i++) {
                    /**
                     * 0 - month
                     * 1 - day of month
                     * 2 - year
                     */
                    datePickerDialog.getDatePicker().updateDate(Integer.parseInt(strings[2]), Integer.parseInt(strings[0]) - 1, Integer.parseInt(strings[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        datePickerDialog.setTitle("");

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateEditText.setText(date != null ? date : "");
                dialog.dismiss();
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        dailyRideRadioButton.setOnClickListener(this);
        longRideRadioBtn.setOnClickListener(this);
        genderAllRadioBtn.setOnClickListener(this);
        ladiesOnlyRadioBtn.setOnClickListener(this);
    }

    public void prepareIntent() {
        getDetails();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("RIDE TYPE", rideType);
        returnIntent.putExtra("VEHICLE TYPE", vehicleType);
        returnIntent.putExtra("COMFORT TYPE", comfortLevel);
        returnIntent.putExtra("GENDER", gender);
        returnIntent.putExtra("DATE", date);
        returnIntent.putExtra("START TIME", startTime);
        returnIntent.putExtra("END TIME", endTime);
        returnIntent.putExtra("VEHICLE REGD", vehicleRegdNo);
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
            case R.id.rbtn_allRides:
                rideType = "0";
                break;
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
                gender = "LadiesOnly";
                break;
            default:
                gender = "All";
                break;
        }
        int regdTypeBtn = vehicleRegdTypeRadioGroup.getCheckedRadioButtonId();
        switch (regdTypeBtn) {
            case R.id.rbtn_vehicleRegdAll:
                vehicleRegdNo = "0";
                break;
            case R.id.rbtn_vehicleRegdOdd:
                vehicleRegdNo = "1";
                break;
            case R.id.rbtn_vehicleRegdEven:
                vehicleRegdNo = "2";
                break;
            default:
                vehicleRegdNo = "0";
                break;
        }
    }

    @Override
    public void onClick(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        if (checked) {
            switch (v.getId()) {
                case R.id.rbtn_longRide:
                    longRideRadioBtn.setChecked(true);
                    dailyRideRadioButton.setChecked(false);
                    dailyRideRadioButton.clearFocus();
                    break;
                case R.id.rbtn_dailyRide:
                    dailyRideRadioButton.setChecked(true);
                    longRideRadioBtn.setChecked(false);
                    longRideRadioBtn.clearFocus();
                    break;
                case R.id.rbtn_All:
                    genderAllRadioBtn.setChecked(true);
                    ladiesOnlyRadioBtn.setChecked(false);
                    ladiesOnlyRadioBtn.clearFocus();
                    break;
                case R.id.rbtn_ladiesOnly:
                    genderAllRadioBtn.setChecked(false);
                    ladiesOnlyRadioBtn.setChecked(true);
                    genderAllRadioBtn.clearFocus();
                    break;
            }
        }
    }
}
