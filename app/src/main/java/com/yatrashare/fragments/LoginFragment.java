package com.yatrashare.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.pojos.UserFBLogin;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
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

        mFBLoginButton.setReadPermissions("email", "public_profile", "user_birthday");
        // If using in a fragment
        mFBLoginButton.setFragment(this);
        mCallbackManager = CallbackManager.Factory.create();

        ((HomeActivity)mContext).setTitle("Login");

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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
                ((HomeActivity) mContext).loadScreen(HomeActivity.LOGIN_WITH_EMAIL_SCREEN, false, null);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.SIGNUP_SCREEN, false, null);
            }
        });


        return inflatedLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFBLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               /* */
                GraphRequest graphRequest = new GraphRequest(loginResult.getAccessToken(), "/" + loginResult.getAccessToken().getUserId(), null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONObject object = response.getJSONObject();
                                    /*Log.e("" + object.get("name"), "FB NAME");
                                    Log.e("" + object.get("gender"), "GENDER");
                                    Log.e("" + object.get("email"), "EMAIL");
                                    Log.e("" + object.get("birthday"), "BDAY");
                                    Log.e("" + object.get("id"), "ID");*/
                                    registerUserinServer(object.optString("id"), object.optString("name"), object.optString("email"),
                                            "https://graph.facebook.com/" + object.optString("id") + "/picture?type=large");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,picture");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

               /* GraphRequest pictureGraphRequest = new GraphRequest(loginResult.getAccessToken(), "/" + loginResult.getAccessToken().getUserId() + "/picture", null, HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    Log.e("" + response.toString(), "PCITURE");
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

    private void registerUserinServer(final String id, final String name, final String email, String profilePicUrl) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YatraShareAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
        UserFBLogin userFBLogin = new UserFBLogin(email, profilePicUrl, name, id);

        Call<String> call = yatraShareAPI.userFBLogin(userFBLogin);
        //asynchronous call
        call.enqueue(new Callback<String>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response != null && response.body() != null && response.body() != null) {
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, email);
                    mSharedPrefEditor.putString(Constants.PREF_USER_NAME, name);
                    mSharedPrefEditor.putString(Constants.PREF_USER_FB_ID, id);
                    mSharedPrefEditor.putString(Constants.PREF_USER_GUID, response.body().toString());
                    mSharedPrefEditor.putBoolean(Constants.PREF_LOGGEDIN, true);
                    mSharedPrefEditor.commit();
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.success_login));
                    ((HomeActivity)mContext).loadHomePage(false);
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity)mContext).showSnackBar(getString(R.string.tryagain));
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
