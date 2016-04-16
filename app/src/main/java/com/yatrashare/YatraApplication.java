package com.yatrashare;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.yatrashare.utils.Constants;
import com.yatrashare.utils.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by KANDAGATLAS on 22-12-2015.
 */
public class YatraApplication extends Application {

    public static final String TAG = YatraApplication.class.getSimpleName();
    private static YatraApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        getCurrentCountry();
    }

    public void getCurrentCountry() {
        GPSTracker gps = new GPSTracker(this);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();

        if (TextUtils.isEmpty(mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""))) {
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Log.e("Current latitude", "" + latitude);
                Log.e("Current longitude", "" + longitude);

                if (latitude != 0.0 && longitude != 0.0) {
                    Address address = getAddress(latitude, longitude);
                    mEditor.putString(Constants.PREF_USER_COUNTRY, address.getCountryName());
                    mEditor.apply();
                }

            }
        }
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized YatraApplication getInstance() {
        return mInstance;
    }

}
