package com.yatrashare.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.dtos.Vehicle;
import com.yatrashare.pojos.RegisterVehicle;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegisterVehicleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = RegisterVehicleActivity.class.getSimpleName();
    @Bind(R.id.selectVehicleColor)
    public Spinner vehicleColorSpinner;
    @Bind(R.id.selectVehicleBrand)
    public Spinner vehicleBrandSpinner;
    @Bind(R.id.selectVehicleModel)
    public Spinner vehicleModelSpinner;
    @Bind(R.id.selectVehicleSeats)
    public Spinner vehicleSeatsSpinner;
    @Bind(R.id.selectVehicleComfort)
    public Spinner vehicleComfortSpinner;
    private String vehicleType;
    @Bind(R.id.registerVehicleBGView)
    public View registerBGView;
    @Bind(R.id.registerVehicle_progress)
    public ProgressBar registerProgressBar;
    private String userGuide;
    private Context mContext;
    private ArrayList<Vehicle.VehicleData> vehicleModelDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);
        ButterKnife.bind(this);
        mContext = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vehicleColorSpinner.setOnItemSelectedListener(this);
        vehicleBrandSpinner.setOnItemSelectedListener(this);
        vehicleModelSpinner.setOnItemSelectedListener(this);
        vehicleSeatsSpinner.setOnItemSelectedListener(this);
        vehicleComfortSpinner.setOnItemSelectedListener(this);

        vehicleType = getIntent().getExtras().getString("Selected Vehicle");
        userGuide = getIntent().getExtras().getString("User Guide");

        /**
         * Car = 1
         * Bike = 2
         */
        if (vehicleType.equalsIgnoreCase("1")) {
            vehicleSeatsSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.selectCarSeats_array)));
            vehicleSeatsSpinner.setSelection(3);
            getSupportActionBar().setTitle("Register new Car");
        } else {
            vehicleSeatsSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.selectBikeSeats_array)));
            vehicleSeatsSpinner.setSelection(1);
            getSupportActionBar().setTitle("Register new Bike");
        }

        getVehicleBrands();
    }

    public void getVehicleModels() {
        Utils.showProgress(true, registerProgressBar, registerBGView);
        Call<Vehicle> call = Utils.getYatraShareAPI().getVehicleModels(userGuide, vehicleType, vehicleBrand);
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
                if (response.body() != null && response.isSuccess()) {
                    vehicleModelDatas = response.body().Data;
                    if (vehicleModelDatas != null && vehicleModelDatas.size() > 0) {
                        for (int i = 0; i < vehicleModelDatas.size(); i++) {
                            if (!TextUtils.isEmpty(vehicleModelDatas.get(i).ModelName)) {
                                vehicleModels.add(vehicleModelDatas.get(i).ModelName);
                            }
                        }
                        vehicleModelSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleModels));
                    }
                }
                Utils.showProgress(false, registerProgressBar, registerBGView);
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t throwable Error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, registerProgressBar, registerBGView);
                showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    public void getVehicleBrands() {
        Utils.showProgress(true, registerProgressBar, registerBGView);
        Call<Vehicle> call = Utils.getYatraShareAPI().getVehicleBrands(userGuide, vehicleType);
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
                ArrayList<String> vehicleBrands = new ArrayList<String>();
                if (response.body() != null && response.isSuccess()) {
                    ArrayList<Vehicle.VehicleData> vehicleDatas = response.body().Data;
                    if (vehicleDatas != null && vehicleDatas.size() > 0) {
                        for (int i = 0; i < vehicleDatas.size(); i++) {
                            if (!TextUtils.isEmpty(vehicleDatas.get(i).BrandName)) {
                                vehicleBrands.add(vehicleDatas.get(i).BrandName);
                            }
                        }
                        vehicleBrandSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, vehicleBrands));
                    }
                }
                Utils.showProgress(false, registerProgressBar, registerBGView);
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t throwable Error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, registerProgressBar, registerBGView);
                showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    public void showSnackBar(String msg) {
        Snackbar.make(vehicleBrandSpinner, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private String getVehicleId(String selectedModel) {
        if (vehicleModelDatas != null && vehicleModelDatas.size() > 0) {
            for (int i = 0; i < vehicleModelDatas.size(); i++) {
                if (vehicleModelDatas.get(i).ModelName.equalsIgnoreCase(selectedModel)) {
                    return vehicleModelDatas.get(i).VehicleModelId;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_registerNewVehicle)
    public void registerVehicle() {
        if (TextUtils.isEmpty(vehicleBrand)) {
            Utils.showToast(mContext, "Select Vehicle Brand");
            return;
        }
        if (TextUtils.isEmpty(vehicleModel)) {
            Utils.showToast(mContext, "Select Vehicle Model");
            return;
        }
        Utils.showProgress(true, registerProgressBar, registerBGView);
        RegisterVehicle.VehicleInfo vehicleInfo = new RegisterVehicle().new VehicleInfo(vehicleType, vehicleSeats, getVehicleId(vehicleModel), vehicleColor, vehicleComfort);
        RegisterVehicle registerVehicle = new RegisterVehicle(vehicleInfo);

        Gson gson = new Gson();
        String s = gson.toJson(registerVehicle);
        Log.e("asgasf", "ertrewtrew" + s);

        Call<UserDataDTO> call = Utils.getYatraShareAPI().registerVehicle(userGuide, registerVehicle);
        call.enqueue(new Callback<UserDataDTO>() {
            @Override
            public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        showSnackBar("Vehicle Registered");
                    } else {
                        showSnackBar(response.body().Data);
                    }
                }
                Utils.showProgress(false, registerProgressBar, registerBGView);
            }

            @Override
            public void onFailure(Throwable t) {
                showSnackBar(getString(R.string.tryagain));
                Utils.showProgress(false, registerProgressBar, registerBGView);
            }
        });
    }

    String vehicleColor, vehicleBrand, vehicleModel, vehicleSeats, vehicleComfort;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.selectVehicleColor:
                vehicleColor = parent.getAdapter().getItem(position).toString();
                break;
            case R.id.selectVehicleBrand:
                vehicleBrand = parent.getAdapter().getItem(position).toString();
                getVehicleModels();
                break;
            case R.id.selectVehicleModel:
                vehicleModel = parent.getAdapter().getItem(position).toString();
                break;
            case R.id.selectVehicleSeats:
                vehicleSeats = parent.getAdapter().getItem(position).toString();
                break;
            case R.id.selectVehicleComfort:
                vehicleComfort = parent.getAdapter().getItem(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
