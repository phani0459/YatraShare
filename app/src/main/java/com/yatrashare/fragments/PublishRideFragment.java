package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.yatrashare.R;
import com.yatrashare.dtos.Seats;
import com.yatrashare.dtos.Vehicle;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishRideFragment extends Fragment {

    private static final String TAG = PublishRideFragment.class.getSimpleName();
    @Bind(R.id.rbtn_vehicle_car)
    public RadioButton radioButtonCar;
    @Bind(R.id.rbtn_vehicle_bike)
    public RadioButton radioButtonBike;
    @Bind(R.id.selectModel)
    public Spinner selectModelSpinner;
    @Bind(R.id.selectSeats)
    public Spinner selectSeatsSpinner;
    @Bind(R.id.et_provideRideDetails)
    public EditText provideRideDetEditText;
    @Bind(R.id.luggageSpinner)
    public Spinner luggageSpinner;
    @Bind(R.id.timeFlexiSpinner)
    public Spinner timeFlexiSpinner;
    @Bind(R.id.detourSpinner)
    public Spinner detourSpinner;
    @Bind(R.id.cb_ladiesOnly)
    public CheckBox ladiesOnlyCheckBox;
    @Bind(R.id.cb_agreeTerms)
    public CheckBox agreeTermsCheckBox;
    @Bind(R.id.btn_PublishRide)
    public Button publishRideButton;
    @Bind(R.id.publishRideBGView)
    public View mProgressBGView;
    @Bind(R.id.publishRide_progress)
    public ProgressBar mProgressBar;
    @Bind(R.id.vehicleGroup)
    public RadioGroup mVehicleRadioGroup;
    public String selectedVehicle;

    private Context mContext;
    private String userGuid;
    private ArrayList<Vehicle.VehicleData> vehicleDatas;

    public PublishRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View inflatedLayout = inflater.inflate(R.layout.fragment_offer_nxt_step, null);
        mContext = getActivity();

        ButterKnife.bind(this, inflatedLayout);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        radioButtonCar.setChecked(true);
        selectedVehicle = "1";
        getUserVehicleModels();

        return inflatedLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        radioButtonCar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedVehicle = "1";
                    getUserVehicleModels();
                }
            }
        });
        radioButtonBike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedVehicle = "2";
                    getUserVehicleModels();
                }
            }
        });


        selectModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedModel = (String) parent.getAdapter().getItem(position);
                String vehicleId = getVehicleId(selectedModel);
                if (vehicleId != null) {
                    getVehicleSeats(vehicleId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String getVehicleId(String selectedModel) {
        if (vehicleDatas != null && vehicleDatas.size() > 0) {
            for (int i = 0; i < vehicleDatas.size(); i++) {
                if (vehicleDatas.get(i).ModelName.equalsIgnoreCase(selectedModel)) {
                    return vehicleDatas.get(i).UserVehicleId;
                }
            }
        }
        return null;
    }

    private void getVehicleSeats(String vehicleId) {
        Utils.showProgress(true, mProgressBar, mProgressBGView);
        Call<Seats> call = Utils.getYatraShareAPI().getUserVehicleSeats(userGuid, vehicleId);
        call.enqueue(new Callback<Seats>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<Seats> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                ArrayList<String> vehicleSeats = new ArrayList<String>();
                vehicleSeats.add("Select Seats");
                if (response.body() != null && response.isSuccess()) {
                    android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                    ArrayList<String> vehicleDatas = response.body().Data;
                    if (vehicleDatas != null && vehicleDatas.size() > 0) {
                        for (int i = 0; i< vehicleDatas.size(); i++) {
                            if (!vehicleDatas.get(i).isEmpty()) {
                                vehicleSeats.add(vehicleDatas.get(i));
                            }
                        }
                        selectSeatsSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleSeats));
                    }
                }
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressBar, mProgressBGView);
                showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    private void getUserVehicleModels() {
        Utils.showProgress(true, mProgressBar, mProgressBGView);
        Call<Vehicle> call = Utils.getYatraShareAPI().getUserVehicleModels(userGuid, selectedVehicle);
        call.enqueue(new Callback<Vehicle>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
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
                        for (int i = 0; i< vehicleDatas.size(); i++) {
                            if (!vehicleDatas.get(i).ModelName.isEmpty()) {
                                vehicleModels.add(vehicleDatas.get(i).ModelName);
                            }
                        }
                        selectModelSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleModels));
                    }
                }
                Utils.showProgress(false, mProgressBar, mProgressBGView);
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressBar, mProgressBGView);
                showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    public void showSnackBar(String msg) {
        Snackbar.make(selectModelSpinner, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
