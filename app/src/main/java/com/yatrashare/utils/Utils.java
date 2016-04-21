package com.yatrashare.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
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

import com.squareup.okhttp.OkHttpClient;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.Profile;
import com.yatrashare.interfaces.YatraShareAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public static void deleteFile(Context mContext, String fileName) {
        File file = new File(mContext.getFilesDir(), fileName + ".ser");
        if (file.exists()) {
            file.delete();
            Log.e("Profile", " Deleted");
        }
    }

    public static Profile checkforProfile(Context mContext, String fileName) {
        Profile profile = null;
        try {
            FileInputStream fis = mContext.openFileInput(fileName + ".ser");
            ObjectInputStream is = new ObjectInputStream(fis);
            profile = (Profile) is.readObject();
            is.close();
            fis.close();
            Log.e("Profile", " Retrieved");
        } catch (Exception e) {
            e.printStackTrace();
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

    public static CountryData getCountryInfo(Context mContext, String fileName) {
        CountryData countryData = null;
        try {
            FileInputStream fis = mContext.openFileInput(fileName + ".ser");
            ObjectInputStream is = new ObjectInputStream(fis);
            countryData = (CountryData) is.readObject();
            is.close();
            fis.close();
            Log.e("Country Data", " Retrieved");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countryData;
    }

    public static Retrofit retrofit;
    public static YatraShareAPI yatraShareAPI;

    public static YatraShareAPI getYatraShareAPI() {
        if (yatraShareAPI == null) {
            yatraShareAPI = getRetrofit().create(YatraShareAPI.class);
        }
        return yatraShareAPI;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(YatraShareAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getOkHttpClient())
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

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean isPhoneValid(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return phoneNumber.length() == 10;
        } else {
            return false;
        }
    }

    public static OkHttpClient getOkHttpClient() {
        final OkHttpClient okHttpClient = new OkHttpClient();
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
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = mProgressBar.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                    mProgressBGView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBGView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public static boolean isLoggedIn(Context mContext) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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

    public static void showMobileVerifyDialog(final Context mContext, final String msg, final String originScree) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.appCompatDialog);
        builder.setTitle("Mobile Number not verified!");
        builder.setMessage("Verify Mobile to book a seat.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.UPDATE_MOBILE_SCREEN, false, null, originScree);
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
