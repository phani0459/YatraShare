package com.yatrashare.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.UserFgtPassword;
import com.yatrashare.pojos.UserLogin;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginWithEmailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private final String TAG = LoginWithEmailFragment.class.getSimpleName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    @Bind(R.id.email)
    public AutoCompleteTextView mEmailView;
    @Bind(R.id.password)
    public EditText mPasswordView;
    @Bind(R.id.login_progress)
    public ProgressBar mProgressView;
    private SharedPreferences.Editor mSharedPrefEditor;
    private Context mContext;
    @Bind(R.id.emailLayout)
    public TextInputLayout mEmailLayout;
    @Bind(R.id.passwordLayout)
    public TextInputLayout mPasswordLayout;
    private Button cancelButton;
    private Button resetPwdButton;
    public ProgressBar mFgtPwdProgressBar;
    @Bind(R.id.loginProgressBGView)
    public View mProgressBGView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View inflatedLayout = inflater.inflate(R.layout.fragment_email_login, null);
        mContext = getActivity();

        ((HomeActivity)mContext).setTitle("Login with email");
        ButterKnife.bind(this, inflatedLayout);

        TextView mForgotPwdTextView = (TextView) inflatedLayout.findViewById(R.id.fgtPwdText);
        populateAutoComplete();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) inflatedLayout.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mForgotPwdTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPrefEditor = mSharedPreferences.edit();
        return inflatedLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.LOGIN_WITH_EMAIL_SCREEN);
    }

    private void showForgotPwdDialog() {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_forgotpwd);
        dialog.setTitle("Reset password");
        final EditText fgtEmailIdEdit = (EditText) dialog.findViewById(R.id.fgt_emailId);
        final EditText fgtPhoneEdit = (EditText) dialog.findViewById(R.id.fgt_phone);
        final TextInputLayout fgtEmailLayout = (TextInputLayout) dialog.findViewById(R.id.fgt_emailIdLayout);
        final TextInputLayout fgtPhoneLayout = (TextInputLayout) dialog.findViewById(R.id.fgt_phoneLayout);
        resetPwdButton = (Button) dialog.findViewById(R.id.btnReset);
        cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
        mFgtPwdProgressBar = (ProgressBar) dialog.findViewById(R.id.fgtPwdProgress);

        fgtPhoneEdit.setHint(getString(R.string.prompt_mobile));

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        resetPwdButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = fgtEmailIdEdit.getText().toString();
                String phoneNumber = fgtPhoneEdit.getText().toString();

                boolean cancel = false;

                // Check for a valid email address and Phone Number.
                if (TextUtils.isEmpty(email)) {
                    fgtEmailLayout.setError(getString(R.string.error_field_required));
                    cancel = true;
                } else if (!Utils.isEmailValid(email)) {
                    fgtEmailLayout.setError(getString(R.string.error_invalid_email));
                    cancel = true;
                } else if (TextUtils.isEmpty(phoneNumber) || !Utils.isPhoneValid(phoneNumber)) {
                    fgtPhoneLayout.setError(getString(R.string.error_invalid_phone));
                    cancel = true;
                }

                if (!cancel) {
                    // Show a progress bar, and kick off a background task to
                    // perform the user forgot password attempt.
                    Utils.hideSoftKeyboard(fgtPhoneEdit);
                    fgtEmailLayout.setErrorEnabled(false);
                    fgtPhoneLayout.setErrorEnabled(false);
                    showFgtPwdProgress(true);
                    userFgtPwdTask(email, phoneNumber, dialog);
                    dialog.setCancelable(false);
                }
            }
        });

        dialog.show();
    }

    private void showFgtPwdProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mFgtPwdProgressBar.setIndeterminate(show);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFgtPwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mFgtPwdProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFgtPwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mFgtPwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        resetPwdButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
    }

    private void userFgtPwdTask(final String mEmail, final String mPhoneNumber, final Dialog dialog) {
        final TextInputLayout fgtPhoneLayout = (TextInputLayout) dialog.findViewById(R.id.fgt_phoneLayout);
        UserFgtPassword userFgtPassword = new UserFgtPassword(mEmail, mPhoneNumber);

        Call<UserDataDTO> call = Utils.getYatraShareAPI().userFgtPwd(userFgtPassword);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        dialog.dismiss();
                        ((HomeActivity)mContext).showSnackBar(getString(R.string.resetpwd_ratioanle));
                    } else {
                        fgtPhoneLayout.setError(response.body().Data);
                    }
                    dialog.setCancelable(true);
                    showFgtPwdProgress(false);
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
                dialog.setCancelable(true);
                showFgtPwdProgress(false);
                fgtPhoneLayout.setError("Something went wrong, try again later!");
            }
        });
    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mContext.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String userId = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        Utils.hideSoftKeyboard(mPasswordView);

        boolean cancel = false;

        // Check for a valid email address and password.
        if (TextUtils.isEmpty(userId)) {
            mEmailLayout.setError(getString(R.string.userid_field_required));
            cancel = true;
        } else if (!Utils.isEmailValid(userId) && !Utils.isPhoneValid(userId)) {
            mEmailLayout.setError(getString(R.string.error_invalid_user));
            cancel = true;
        } else if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mEmailLayout.setErrorEnabled(false);
            mPasswordLayout.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mEmailLayout.setErrorEnabled(false);
            mPasswordLayout.setErrorEnabled(false);
            Utils.showProgress(true, mProgressView, mProgressBGView);
            userLoginTask(userId, password);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(mContext,
                // Retrieve data rows for the device user's 'profile' contact.
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void userLoginTask(final String mEmail, final String mPassword) {
        UserLogin userLogin = new UserLogin(mEmail, mPassword);

        Call<String> call = Utils.getYatraShareAPI().userLogin(userLogin);
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
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null && response.isSuccess()) {
                    android.util.Log.e("SUCCEESS RESPONSE BODY", response.body() + "");
                    mSharedPrefEditor.putString(Constants.PREF_USER_GUID, response.body());
                    mSharedPrefEditor.commit();
                    getBasicProfileInfo(response.body());
                } else {
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.error_invalid_login));
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
                ((HomeActivity) mContext).showSnackBar(getString(R.string.error_invalid_login));
            }
        });

        /*RequestParams params = new RequestParams();
        params.put("Email", mEmail);
        params.put("Password", mPassword);
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(Constants.LOGIN_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    UtilsLog.w("myapp","success status code..." + statusCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                UtilsLog.w("myapp", "failure status code..." + statusCode);
                try {
                    UtilsLog.w("myapp", "error ..." + errorResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });*/

    }

    private void getBasicProfileInfo(String userGuid) {
        Call<Profile> call = Utils.getYatraShareAPI().userBasicProfile(userGuid);
        //asynchronous call
        call.enqueue(new Callback<Profile>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<Profile> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    mSharedPrefEditor.putString(Constants.PREF_USER_NAME, response.body().Data.FirstName);
                    mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, response.body().Data.Email);
                    mSharedPrefEditor.putString(Constants.PREF_USER_PHONE, response.body().Data.PhoneNo);
                    mSharedPrefEditor.putString(Constants.PREF_USER_GENDER, response.body().Data.Gender);
                    mSharedPrefEditor.putString(Constants.PREF_USER_PROFILE_PIC, response.body().Data.ProfilePhoto);

                    String mobileStatus = response.body().Data.VerificationStatus.MobileNumberStatus != null ? response.body().Data.VerificationStatus.MobileNumberStatus : "0";

                    mSharedPrefEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, mobileStatus.equals("2") ? true : false);
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
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

}

