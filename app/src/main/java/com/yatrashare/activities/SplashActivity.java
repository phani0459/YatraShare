package com.yatrashare.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.yatrashare.R;
import com.yatrashare.dtos.Countries;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.CountryInfo;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.GPSTracker;
import com.yatrashare.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2000;
    private Runnable runnable;
    private Handler handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        requestLocation();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getCurrentCountry();
            return;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentCountry();
            return;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(splashLayout, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOAD_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOAD_LOCATION);
        }
    }

    public void getCurrentCountry() {
        GPSTracker gps = new GPSTracker(this);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Log.e("Current latitude: ", "" + latitude);
            Log.e("Current longitude: ", "" + longitude);

            if (latitude != 0.0 && longitude != 0.0) {
                Address address = getAddress(latitude, longitude);
                Log.e("Country: ", "" + address.getCountryName());
                mEditor.putString(Constants.PREF_USER_COUNTRY, address.getCountryName());
                mEditor.apply();

                CountryData countryData = Utils.getCountryInfo(SplashActivity.this, address.getCountryName());

                if (countryData != null) {
                    startHomePage();
                } else {
                    getCountries(address.getCountryName());
                }
            }
        }
    }

    private void getCountries(final String countryName) {
        Utils.showProgress(true, splashProgress, progressBGView);
        Call<Countries> call = Utils.getYatraShareAPI().GetCountries();
        call.enqueue(new Callback<Countries>() {
            @Override
            public void onResponse(Response<Countries> response, Retrofit retrofit) {
                if (response.body() != null && response.body().Data != null && response.body().Data.size() > 0) {
                    for (int i = 0; i < response.body().Data.size(); i++) {
                        if (response.body().Data.get(i).CountryName.equalsIgnoreCase(countryName)) {
                            getCountryInfo(response.body().Data.get(i).CountryCode, countryName);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.showProgress(false, splashProgress, progressBGView);
                startHomePage();
            }
        });
    }

    public void startHomePage() {
        Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
        overridePendingTransition(R.anim.jump_to_down, R.anim.jump_from_down);
        startActivity(mainIntent);
        finish();
    }

    private void getCountryInfo(final String countryCode, final String countryName) {
        Call<CountryInfo> call = Utils.getYatraShareAPI().GetCountryInfo(countryCode);
        call.enqueue(new Callback<CountryInfo>() {
            @Override
            public void onResponse(Response<CountryInfo> response, Retrofit retrofit) {
                if (response.body() != null && response.body().Data != null) {
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

    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOAD_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentCountry();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
