package com.yatrashare.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.yatrashare.R;
import com.yatrashare.fragments.PublishRideFragment;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfferRideActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);
        ButterKnife.bind(this);

        String departure = getIntent().getExtras().getString("DEPARTURE", "");
        String arrival = getIntent().getExtras().getString("ARRIVAL", "");

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
    }

    @OnClick(R.id.btn_nxtStep)
    public void nextStep() {
        getSupportFragmentManager().beginTransaction().add(R.id.offerRideContent, new PublishRideFragment(), null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
