package com.yatrashare;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yatrashare.utils.Constants;
import com.yatrashare.utils.GPSTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


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
