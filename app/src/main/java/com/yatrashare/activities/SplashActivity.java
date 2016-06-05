package com.yatrashare.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.yatrashare.AsyncTask.FetchMyProfile;
import com.yatrashare.R;
import com.yatrashare.dtos.Countries;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.CountryInfo;
import com.yatrashare.dtos.GoogleAddressDto;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.GPSTracker;
import com.yatrashare.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_CHECK_SETTINGS = 123;
    @Bind(R.id.relativeSplash)
    RelativeLayout splashLayout;
    @Bind(R.id.splash_progress)
    ProgressBar splashProgress;
    @Bind(R.id.splashProgressBGView)
    View progressBGView;


    /**
     * Id to identity ACCESS_LOCATION permission request.
     */
    private static final int REQUEST_LOAD_LOCATION = 0;
    private SharedPreferences.Editor mEditor;
    private CountryData countryData;
    private GoogleApiClient mGoogleClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(this);
        mEditor = mSharedPreferences.edit();

        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        countryData = Utils.getCountryInfo(SplashActivity.this, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null) {
            startHandler();
        } else {
            loadGetLocationDialog();
        }

        if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
            if (Utils.isInternetAvailable(this)) {
                Intent intent = new Intent(this, FetchMyProfile.class);
                startService(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getCurrentCountry();
                        break;
                    case Activity.RESULT_CANCELED:
                        Utils.showProgress(false, splashProgress, progressBGView);
                        Utils.showToast(this, "Turn on Location to use your current location");
                        getCountries(null, true);
                        break;
                }
                break;
        }
    }

    protected void createLocationRequest() {

        if (mGoogleClient != null && !mGoogleClient.isConnected()) {
            mGoogleClient.connect();
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        getCurrentCountry();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    private static final long SPLASH_DISPLAY_LENGTH = 2000;
    private Runnable runnable;
    private Handler handler;

    public void startHandler() {
        try {
            runnable = new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Home Activity. */
                    startHomePage();
                }
            };
            handler = new Handler();
            handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestLocation() {
        Utils.showProgress(true, splashProgress, progressBGView);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            createLocationRequest();
            return;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            createLocationRequest();
            return;
        }
        requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOAD_LOCATION);
    }

    private void loadGetLocationDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_get_location);
        Button allowButton = (Button) dialog.findViewById(R.id.btn_allow);
        Button skipButton = (Button) dialog.findViewById(R.id.btn_skip);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getCountries(null, true);
            }
        });

        allowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestLocation();
            }
        });
        dialog.show();
    }

    public void getCurrentCountry() {
        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Log.e("Current latitude: ", "" + latitude);
            Log.e("Current longitude: ", "" + longitude);

            if (latitude != 0.0 && longitude != 0.0) {
                Address address = getAddress(latitude, longitude);
                if (address != null) {
                    Log.e("Country: ", "" + address.getCountryName());
                    mEditor.putString(Constants.PREF_USER_COUNTRY, address.getCountryName());
                    mEditor.apply();
                    getCountries(address.getCountryName(), false);
                } else {
                    getCountryFromApi(latitude, longitude);
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.e("From Location lat: ", "" + latitude);
        Log.e("From location long: ", "" + longitude);

        if (latitude != 0.0 && longitude != 0.0) {

            if (mGoogleClient != null && mGoogleClient.isConnected()) mGoogleClient.disconnect();

            Address address = getAddress(latitude, longitude);
            if (address != null) {
                Log.e("Country: ", "" + address.getCountryName());
                mEditor.putString(Constants.PREF_USER_COUNTRY, address.getCountryName());
                mEditor.apply();
                getCountries(address.getCountryName(), false);
            } else {
                if (Utils.isInternetAvailable(this)) {
                    getCountryFromApi(latitude, longitude);
                } else {
                    Utils.showProgress(false, splashProgress, progressBGView);
                }
            }
        } else {
            Utils.showToast(this, "Cannot fetch Current Location");
        }
    }

    private void getCountryFromApi(double lat, double lng) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://maps.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(Utils.getOkHttpClient(this))
                    .build();
            YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
            Call<GoogleAddressDto> call = yatraShareAPI.getGoogleAddressAPI(lat + "," + lng);
            call.enqueue(new Callback<GoogleAddressDto>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response response from server
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<GoogleAddressDto> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    if (response.body() != null && response.isSuccess()) {
                        ArrayList<GoogleAddressDto.AddressResults> results = response.body().results;
                        if (results != null && results.size() > 0 && results.get(0).address_components != null && results.get(0).address_components.size() > 0) {
                            /**
                             * administrative_area_level_1 = State
                             * locality = City
                             * country = Country
                             */

                            String countryName = "";
                            for (int i = 0; i < results.get(0).address_components.size(); i++) {
                                GoogleAddressDto.AddressComponents component = results.get(0).address_components.get(i);
                                if (component.types != null && component.types.size() > 0 && component.types.contains("country")) {
                                    countryName = component.long_name;
                                }
                            }

                            Log.e("Country: ", "" + countryName);
                            mEditor.putString(Constants.PREF_USER_COUNTRY, countryName);
                            mEditor.apply();

                            getCountries(countryName, false);
                        }
                    }
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t throwable error
                 */
                @Override
                public void onFailure(Throwable t) {
                    android.util.Log.e("", "FAILURE RESPONSE");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCountries(final String countryName, final boolean isSkip) {
        if (Utils.isInternetAvailable(this)) {
            Utils.showProgress(true, splashProgress, progressBGView);
            Call<Countries> call = Utils.getYatraShareAPI(this).GetCountries();
            call.enqueue(new Callback<Countries>() {
                @Override
                public void onResponse(Response<Countries> response, Retrofit retrofit) {
                    if (response.body() != null && response.body().Data != null && response.body().Data.size() > 0) {
                        if (!isSkip) {
                            for (int i = 0; i < response.body().Data.size(); i++) {
                                if (response.body().Data.get(i).CountryName.equalsIgnoreCase(countryName)) {
                                    getCountryInfo(response.body().Data.get(i).CountryCode, countryName);
                                }
                            }
                        } else {
                            Utils.showProgress(false, splashProgress, progressBGView);
                            Intent intent = new Intent(SplashActivity.this, SelectCountryActivity.class);
                            intent.putExtra("Countries", response.body().Data);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.showProgress(false, splashProgress, progressBGView);
                    startHomePage();
                }
            });
        } else {
            Utils.showProgress(false, splashProgress, progressBGView);
        }
    }

    public void startHomePage() {
        Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
        overridePendingTransition(R.anim.jump_to_down, R.anim.jump_from_down);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mGoogleClient.isConnected()) {
                mGoogleClient.disconnect();
            }

            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCountryInfo(final String countryCode, final String countryName) {
        if (Utils.isInternetAvailable(this)) {
            Call<CountryInfo> call = Utils.getYatraShareAPI(this).GetCountryInfo(countryCode);
            call.enqueue(new Callback<CountryInfo>() {
                @Override
                public void onResponse(Response<CountryInfo> response, Retrofit retrofit) {
                    if (response.body() != null && response.body().Data != null) {
                        mEditor.putString(Constants.PREF_USER_TOKEN, response.body().Data.Token);
                        mEditor.apply();
                        Utils.saveCountryInfo(SplashActivity.this, response.body().Data, countryName);
                        Utils.showProgress(false, splashProgress, progressBGView);
                        startHomePage();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.showProgress(false, splashProgress, progressBGView);
                    startHomePage();
                }
            });
        }
    }

    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (addresses != null && addresses.size() > 0) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOAD_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createLocationRequest();
            } else {
                Utils.showProgress(false, splashProgress, progressBGView);
                getCountries(null, true);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
