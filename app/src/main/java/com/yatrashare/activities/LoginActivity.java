package com.yatrashare.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yatrashare.R;
import com.yatrashare.fragments.LoginFragment;
import com.yatrashare.fragments.LoginWithEmailFragment;
import com.yatrashare.fragments.SignupFragment;
import com.yatrashare.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private boolean init;
    @Bind(R.id.content_layout)
    public FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Initialising Libraries
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fresco.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);

        mContext = this;
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Login");

        init = true;

        LoginFragment loginFragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ORIGIN_SCREEN_KEY, "");
        loginFragment.setArguments(bundle);
        if (init) {
            init = false;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .add(R.id.content_layout, loginFragment).addToBackStack(Constants.LOGIN_SCREEN_NAME).commit();
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .replace(R.id.content_layout, loginFragment).addToBackStack(Constants.LOGIN_SCREEN_NAME).commit();
        }
    }

    public void startHomePage() {
        Intent mainIntent = new Intent(mContext, HomeActivity.class);
        overridePendingTransition(R.anim.jump_to_down, R.anim.jump_from_down);
        startActivity(mainIntent);
        finish();
    }

    public void loadScreen(int screen) {
        if (screen == 1) {
            LoginWithEmailFragment loginWithEmailFragment = new LoginWithEmailFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ORIGIN_SCREEN_KEY, "");
            loginWithEmailFragment.setArguments(bundle);
            if (init) {
                init = false;
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .add(R.id.content_layout, loginWithEmailFragment).addToBackStack(Constants.LOGIN_WITH_EMAIL_SCREEN_NAME).commit();
            } else {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                        replace(R.id.content_layout, loginWithEmailFragment).addToBackStack(Constants.LOGIN_WITH_EMAIL_SCREEN_NAME).commit();
            }
        } else if (screen == 2) {
            SignupFragment signupFragment = new SignupFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ORIGIN_SCREEN_KEY, "");
            signupFragment.setArguments(bundle);
            if (init) {
                init = false;
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.content_layout, signupFragment).addToBackStack(Constants.SIGNUP_SCREEN_NAME).commit();
            } else {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                        replace(R.id.content_layout, signupFragment).addToBackStack(Constants.SIGNUP_SCREEN_NAME).commit();
            }
        }
    }

    public void showSnackBar(String msg) {
        try {
            Snackbar snack = Snackbar.make(frameLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(frameLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}
