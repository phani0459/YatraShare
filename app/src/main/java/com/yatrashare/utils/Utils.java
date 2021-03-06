package com.yatrashare.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.Profile;
import com.yatrashare.interfaces.YatraShareAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by KANDAGATLAS on 03-01-2016.
 */
public class Utils {

    public static void hideSoftKeyboard(EditText editText) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText()) {
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPath(Uri uri, Context mContext) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return filePath;
    }

    public static void deleteFile(Context mContext, String fileName) {
        File file = new File(mContext.getFilesDir(), fileName + ".ser");
        if (file.exists()) {
            file.delete();
            profile = null;
            Log.e("Profile", " Deleted");
        }
    }

    private static Profile profile;

    public static Profile getProfile(Context mContext, String fileName) {
        if (profile == null) {
            try {
                FileInputStream fis = mContext.openFileInput(fileName + ".ser");
                ObjectInputStream is = new ObjectInputStream(fis);
                profile = (Profile) is.readObject();
                is.close();
                fis.close();
                Log.e("Profile", " Retrieved");
            } catch (Exception e) {
                profile = null;
                e.printStackTrace();
            }
        }
        return profile;
    }

    public static void saveProfile(Context mContext, Profile profile, String fileName) {
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName + ".ser", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(profile);
            os.close();
            fos.close();
            Log.e("Profile", " Saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCountryInfo(Context mContext, CountryData countryData, String fileName) {
        try {
            FileOutputStream fos = mContext.openFileOutput(countryData.CountryName + ".ser", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(countryData);
            os.close();
            fos.close();
            Log.e("Country Data", " Saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CountryData countryData;

    public static CountryData getCountryInfo(Context mContext, String fileName) {
        if (countryData == null) {
            try {
                FileInputStream fis = mContext.openFileInput(fileName + ".ser");
                ObjectInputStream is = new ObjectInputStream(fis);
                countryData = (CountryData) is.readObject();
                is.close();
                fis.close();
                Log.e("Country Data", " Retrieved");
            } catch (Exception e) {
                countryData = null;
                e.printStackTrace();
            }
        }
        return countryData;
    }

    public static Retrofit retrofit;
    public static YatraShareAPI yatraShareAPI;

    public static YatraShareAPI getYatraShareAPI(Context mContext) {
        if (yatraShareAPI == null) {
            yatraShareAPI = getRetrofit(mContext).create(YatraShareAPI.class);
        }
        return yatraShareAPI;
    }

    public static Retrofit getRetrofit(Context mContext) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(YatraShareAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient(mContext))
                    .build();
        }
        return retrofit;
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(800);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(800);
        v.startAnimation(a);
    }

    public static int PWD_MAX_CHARS = 20;
    public static int EMAIL_MAX_CHARS = 50;

    public static long TIME_CHECKER = 3 * 60 * 60 * 1000;

    public static InputFilter[] getInputFilter(int maxChars) {
        InputFilter[] filter = new InputFilter[]{new InputFilter.LengthFilter(maxChars)};
        return filter;
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static SharedPreferences mSharedPreferences;

    public static SharedPreferences getSharedPrefs(Context mContext) {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(Constants.peferencesName, Context.MODE_PRIVATE);
            ;
        }
        return mSharedPreferences;
    }

    public static int getMobileMaxChars(Context mContext) {
        int maxChars = 10;
        CountryData countryData = getCountryInfo(mContext, getSharedPrefs(mContext).getString(Constants.PREF_USER_COUNTRY, ""));
        if (countryData != null) {
            if (!countryData.CountryName.equalsIgnoreCase("INDIA")) {
                maxChars = 15;
            }
        }
        return maxChars;
    }

    public static boolean isPhoneValid(Context mContext, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return phoneNumber.length() >= 10;
        }
        return false;
    }

    public static OkHttpClient getOkHttpClient(final Context mContext) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Token", getToken(mContext))
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        return okHttpClient;
    }

    public static void showToast(Context mContext, String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final ProgressBar mProgressBar, final View mProgressBGView) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBGView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static boolean isLoggedIn(Context mContext) {
        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(mContext);
        if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
            return true;
        }
        return false;
    }

    public static void showLoginDialog(final Context mContext, final String originScreen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.appCompatDialog);
        builder.setTitle("Not Logged in!");
        builder.setMessage("Login/Register to know more");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.LOGIN_SCREEN, false, null, originScreen);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static boolean isInternetAvailable(Context mContext) {
        boolean isConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        if (!isConnected) {
            showToast(mContext, "We are unable connect to our servers, please check your internet connection");
        }
        return isConnected;
    }

    public static Spanned getCurrency(Context mContext) {
        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(mContext);
        CountryData countryData = getCountryInfo(mContext, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));
        if (countryData != null) return Html.fromHtml(countryData.CurrencySymbol);
        return Html.fromHtml("&#8377;");
    }

    public static LatLng getPresentLatLng(Context mContext) {
        GPSTracker gps = new GPSTracker(mContext);
        LatLng latLng;

        double longitude = 0;
        double latitude = 0;
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        latLng = new LatLng(latitude, longitude);
        return latLng;
    }


    public static String token;

    public static String getToken(Context mContext) {
        if (TextUtils.isEmpty(token)) {
            token = Utils.getSharedPrefs(mContext).getString(Constants.PREF_USER_TOKEN, "");
        }
        return token;
    }

    public static Address getLatlngsFromLocation(String name, Context mContext) {
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(name, 1);
            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static LatLngBounds createBoundsWithMinDiagonal(Context mContext) {
        /*LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(getPresentLatLng(mContext));
        builder.include(getPresentLatLng(mContext));

        LatLngBounds tmpBounds = builder.build();
        *//** Add 2 points 1000m northEast and southWest of the center.
         * They increase the bounds only, if they are not already larger
         * than this.
         * 1000m on the diagonal translates into about 709m to each direction. *//*
        LatLng center = tmpBounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);
        builder.include(southWest);
        builder.include(northEast);
        return builder.build();*/
        LatLngBounds latLngBounds = new LatLngBounds(getPresentLatLng(mContext), getPresentLatLng(mContext));

        SharedPreferences sharedPreferences = getSharedPrefs(mContext);
        String country = sharedPreferences.getString(Constants.PREF_USER_COUNTRY, "");

        if (!TextUtils.isEmpty(country)) {
            try {
                Address address = getLatlngsFromLocation(country, mContext);
                if (address != null) {
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    LatLng latLng1 = new LatLng(32.6393, -117.004304);
                    LatLng latLng2 = new LatLng(44.901184, -67.32254);
                    latLngBounds = new LatLngBounds(latLng1, latLng2);
                    return latLngBounds;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return latLngBounds;
    }

    private static final double EARTHRADIUS = 6366198;

    /**
     * Create a new LatLng which lies toNorth meters north and toEast meters
     * east of startLL
     */
    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    public static void showMobileVerifyDialog(final Context mContext, final String msg, final String originScree, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.appCompatDialog);
        builder.setTitle("Mobile Number not verified!");
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.UPDATE_MOBILE_SCREEN, false, false, originScree);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
