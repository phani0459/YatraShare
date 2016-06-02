package com.yatrashare.AsyncTask;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.yatrashare.dtos.Profile;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class FetchMyProfile extends IntentService {

    private static final String TAG = FetchMyProfile.class.getSimpleName();
    private SharedPreferences.Editor mSharedPrefEditor;
    private String userGuide;

    public FetchMyProfile() {
        super("FetchMyProfile");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPreferences.edit();
        mSharedPrefEditor.apply();

        userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        if (!TextUtils.isEmpty(userGuide)) getBasicProfileInfo(userGuide);
    }

    public void userProfileTask(final String userGuide) {
        if (Utils.isInternetAvailable(this)) {

            Call<Profile> call = Utils.getYatraShareAPI(this).userPublicProfile(userGuide);
            //asynchronous call
            call.enqueue(new Callback<Profile>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response server response
                 * @param retrofit adapter
                 */
                @Override
                public void onResponse(retrofit.Response<Profile> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                    if (response.body() != null) {
                        Profile profile = response.body();
                        Utils.deleteFile(FetchMyProfile.this, userGuide);
                        Utils.saveProfile(FetchMyProfile.this, profile, userGuide);
                    }
                }

                /**
                 * Invoked when a network or unexpected exception occurred during the HTTP request.
                 *
                 * @param t error
                 */
                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void getBasicProfileInfo(final String userGuid) {
        Call<Profile> call = Utils.getYatraShareAPI(this).userBasicProfile(userGuid);
        //asynchronous call
        call.enqueue(new Callback<Profile>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
             */
            @Override
            public void onResponse(retrofit.Response<Profile> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    mSharedPrefEditor.putString(Constants.PREF_USER_FIRST_NAME, response.body().Data.FirstName);
                    mSharedPrefEditor.putString(Constants.PREF_USER_LAST_NAME, response.body().Data.LastName);
                    mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, response.body().Data.Email);
                    mSharedPrefEditor.putString(Constants.PREF_USER_PHONE, response.body().Data.PhoneNo);
                    mSharedPrefEditor.putString(Constants.PREF_USER_GENDER, response.body().Data.Gender);
                    mSharedPrefEditor.putString(Constants.PREF_USER_PROFILE_PIC, response.body().Data.ProfilePhoto);
                    mSharedPrefEditor.putString(Constants.PREF_USER_DOB, response.body().Data.Dob);
                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_1, response.body().Data.Licence1);
                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_2, response.body().Data.Licence2);

                    String mobileStatus = "0";

                    try {
                        if (response.body().Data.VerificationStatus != null) {
                            mobileStatus = response.body().Data.VerificationStatus.MobileNumberStatus != null ? response.body().Data.VerificationStatus.MobileNumberStatus : "0";
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    mSharedPrefEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, mobileStatus.equals("2"));
                    mSharedPrefEditor.putBoolean(Constants.PREF_LOGGEDIN, true);
                    mSharedPrefEditor.commit();
                    userProfileTask(userGuid);
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t error
             */
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
