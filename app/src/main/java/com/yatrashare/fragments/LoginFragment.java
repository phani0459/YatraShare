package com.yatrashare.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.Profile;
import com.yatrashare.pojos.UserFBLogin;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getSimpleName();
    @Bind(R.id.fb_login_button)
    public LoginButton mFBLoginButton;
    @Bind(R.id.joinUSButton)
    public Button mSignUpButton;
    @Bind(R.id.email_sign_in_button)
    public Button mLoginWithEmailButton;
    @Bind(R.id.fbLoginProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.fbloginProgressBGView)
    public View mProgressBGView;
    public CallbackManager mCallbackManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPrefEditor;
    private Context mContext;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Deprecated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedLayout = inflater.inflate(R.layout.fragment_login, null, false);
        mContext = getActivity();
        ButterKnife.bind(this, inflatedLayout);

        mFBLoginButton.setReadPermissions("email", "public_profile", "user_birthday", "user_friends");
        // If using in a fragment
        mFBLoginButton.setFragment(this);
        mCallbackManager = CallbackManager.Factory.create();

        ((HomeActivity) mContext).setTitle("Login");

        mSharedPreferences = Utils.getSharedPrefs(mContext);
        mSharedPrefEditor = mSharedPreferences.edit();

        /**
         * Facebook login button size
         */

        float fbIconScale = 1.45F;
        Drawable drawable = mContext.getResources().getDrawable(com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale), (int) (drawable.getIntrinsicHeight() * fbIconScale));
        mFBLoginButton.setCompoundDrawables(drawable, null, null, null);
        mFBLoginButton.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        mFBLoginButton.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr),
                mContext.getResources().getDimensionPixelSize(R.dimen.fb_margin_override_top), 0,
                mContext.getResources().getDimensionPixelSize(R.dimen.fb_margin_override_bottom));

        mLoginWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.LOGIN_WITH_EMAIL_SCREEN, false, null, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.SIGNUP_SCREEN, false, null, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
            }
        });


        return inflatedLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.LOGIN_SCREEN);
        mFBLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               /* */
                GraphRequest graphRequest = new GraphRequest(loginResult.getAccessToken(), "/" + loginResult.getAccessToken().getUserId(), null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    Log.e(TAG, "onCompleted: " + response );
                                    JSONObject object = response.getJSONObject();
                                    /*UtilsLog.e("" + object.get("name"), "FB NAME");
                                    UtilsLog.e("" + object.get("gender"), "GENDER");
                                    UtilsLog.e("" + object.get("email"), "EMAIL");
                                    UtilsLog.e("" + object.get("birthday"), "BDAY");
                                    UtilsLog.e("" + object.get("id"), "ID");*/

                                    String friendsCount = "";
                                    try {
                                        JSONObject innerObject = object.getJSONObject("friends");
                                        JSONObject summaryObject = innerObject.getJSONObject("summary");

                                        /*UtilsLog.e("" + summaryObject.get("total_count"), "ID");*/

                                        friendsCount = summaryObject.optString("total_count");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (Utils.isInternetAvailable(mContext)) {
                                        registerUserinServer(object.optString("id"), object.optString("first_name"), object.optString("email"),
                                                "https://graph.facebook.com/" + object.optString("id") + "/picture?type=large", friendsCount, object.optString("gender"));
                                    } else {
                                        Utils.showProgress(false, mProgressView, mProgressBGView);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,email,gender,birthday,picture,friends");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

               /* GraphRequest pictureGraphRequest = new GraphRequest(loginResult.getAccessToken(), "/" + loginResult.getAccessToken().getUserId() + "/picture", null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    UtilsLog.e("" + response.toString(), "PCITURE");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                pictureGraphRequest.executeAsync();*/
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("error", exception.toString());
            }
        });
    }

    private void getBasicProfileInfo(String userGuid) {
        Call<Profile> call = Utils.getYatraShareAPI(mContext).userBasicProfile(userGuid);
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
                    Log.e(TAG, "response.body().Data.Licence1: " + response.body().Data.Licence1);
                    Log.e(TAG, "response.body().Data.Licence2: " + response.body().Data.Licence2);
                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_1, response.body().Data.Licence1);
                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_2, response.body().Data.Licence2);

                    String mobileStatus = response.body().Data.VerificationStatus.MobileNumberStatus != null ? response.body().Data.VerificationStatus.MobileNumberStatus : "0";

                    mSharedPrefEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, mobileStatus.equals("2"));
                    mSharedPrefEditor.putBoolean(Constants.PREF_LOGGEDIN, true);
                    mSharedPrefEditor.commit();

                    ((HomeActivity) mContext).showSnackBar(getString(R.string.success_login));
                    ((HomeActivity) mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
                }
                Utils.showProgress(false, mProgressView, mProgressBGView);
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

    private void registerUserinServer(final String id, final String name, final String email, String profilePicUrl, String friendsCount, String gender) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        CountryData countryData = Utils.getCountryInfo(mContext, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));
        String countryCode = countryData != null ? countryData.CountryCode : "";
        UserFBLogin userFBLogin = new UserFBLogin(email, profilePicUrl, name, id, countryCode, friendsCount, gender);

        Call<String> call = Utils.getYatraShareAPI(mContext).userFBLogin(userFBLogin);
        //asynchronous call
        call.enqueue(new Callback<String>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
             */
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                if (response.body() != null) {
                    Log.e(TAG, "onResponse: " + response.body());
                    mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, email);
                    mSharedPrefEditor.putString(Constants.PREF_USER_FIRST_NAME, name);
                    mSharedPrefEditor.putString(Constants.PREF_USER_FB_ID, id);
                    mSharedPrefEditor.putString(Constants.PREF_USER_GUID, response.body());
                    mSharedPrefEditor.commit();

                    getBasicProfileInfo(response.body());
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                LoginManager.getInstance().logOut();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
