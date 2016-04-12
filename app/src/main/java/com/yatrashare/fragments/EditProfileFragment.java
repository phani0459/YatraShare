package com.yatrashare.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.pojos.UserProfile;
import com.yatrashare.pojos.UserSignUp;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {


    private static final String TAG = EditProfileFragment.class.getSimpleName();
    @Bind(R.id.updateProfileProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.updateProfileProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.editProfileDateEdit)
    public EditText dobEdit;
    @Bind(R.id.editProfileAboutMeEdit)
    public EditText aboutMeEdit;
    @Bind(R.id.updateUserFirstName)
    public EditText firstNameEdit;
    @Bind(R.id.updateUserLastName)
    public EditText lastNameEdit;
    @Bind(R.id.editProfilePhoneEdit)
    public EditText phoneNoEdit;
    @Bind(R.id.editProfileEmailEdit)
    public EditText emailEdit;
    @Bind(R.id.editProfilePhoneLayout)
    public TextInputLayout mUpdatePhoneLayout;
    @Bind(R.id.updateFirstNameLayout)
    public TextInputLayout mUpdateUserNameLayout;
    private Profile profile;
    private String userGuid;
    private SimpleDateFormat dateFormatter;
    @Bind(R.id.editProfileImage_drawee)
    public SimpleDraweeView userDraweeImageView;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (profile != null && getArguments() != null) {
            getArguments().putSerializable("PROFILE", profile);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();

        if (getArguments() != null) {
            profile = (Profile) getArguments().getSerializable("PROFILE");
        }

        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.EDIT_PROFILE_SCREEN);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");
        String email = mSharedPreferences.getString(Constants.PREF_USER_EMAIL, null);
        String phone = mSharedPreferences.getString(Constants.PREF_USER_PHONE, null);
        String userProfilePic = mSharedPreferences.getString(Constants.PREF_USER_PROFILE_PIC, "");
        String userFBId = mSharedPreferences.getString(Constants.PREF_USER_FB_ID, "");

        if (profile != null && profile.Data != null) {
            String aboutMe = profile.Data.AboutMe;
            String userName = profile.Data.UserName;

            aboutMeEdit.setText(aboutMe);
            firstNameEdit.setText(userName);

            if (!TextUtils.isEmpty(profile.Data.FirstName)) firstNameEdit.setText(profile.Data.FirstName);
            if (!TextUtils.isEmpty(profile.Data.LastName)) lastNameEdit.setText(profile.Data.LastName);
        }

        if (!TextUtils.isEmpty(email)) {
            emailEdit.setText(email);
            emailEdit.setEnabled(false);
        }

        if (!TextUtils.isEmpty(phone)) {
            phoneNoEdit.setText(phone);
        }

        if (userFBId.isEmpty() && (userProfilePic.isEmpty() || userProfilePic.startsWith("/"))) {
            userDraweeImageView.setImageURI(Constants.getDefaultPicURI());
        } else if (!userFBId.isEmpty()) {
            Uri uri = Uri.parse("https://graph.facebook.com/" + userFBId + "/picture?type=large");
            userDraweeImageView.setImageURI(uri);
        } else if (!userProfilePic.isEmpty()) {
            Uri uri = Uri.parse(userProfilePic);
            userDraweeImageView.setImageURI(uri);
        }

        dobEdit.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Calendar newCalendar = Calendar.getInstance();
        Date maxDate = null;
        try {
            maxDate = dateFormatter.parse("12/31/2005");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final DatePickerDialog dobDatePickerDialog = new DatePickerDialog(mContext,new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dobEdit.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dobDatePickerDialog.getDatePicker().setMaxDate(maxDate != null ? maxDate.getTime() : 0);
        dobDatePickerDialog.setTitle("");

        dobDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dobEdit.setText("");
                dialog.dismiss();
            }
        });

        dobEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) dobDatePickerDialog.show();
                return false;
            }
        });

    }

    @OnClick(R.id.updateProfileButton)
    public void updateProfile(){
        Utils.hideSoftKeyboard(emailEdit);
        // Store values at the time of the login attempt.
        String userFirstName = firstNameEdit.getText().toString();
        String userLastName = lastNameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String dob = dobEdit.getText().toString();
        String phoneNumber = phoneNoEdit.getText().toString();
        String aboutMe = aboutMeEdit.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(userFirstName) || !isUserNameValid(userFirstName)) {
            mUpdateUserNameLayout.setError(getString(R.string.error_invalid_username));
            cancel = true;
        } else if (TextUtils.isEmpty(phoneNumber) || !Utils.isPhoneValid(phoneNumber)) {
            mUpdateUserNameLayout.setErrorEnabled(false);
            mUpdatePhoneLayout.setError(getString(R.string.error_invalid_phone));
            cancel = true;
        }

        if (!cancel) {
            mUpdatePhoneLayout.setErrorEnabled(false);
            mUpdateUserNameLayout.setErrorEnabled(false);
            updateProfile(userGuid, userFirstName, userLastName, email, TextUtils.isEmpty(dob) ? "MM/DD/YYYY" : dob, phoneNumber, aboutMe);
        }

    }

    private boolean isUserNameValid(String userName) {
        return userName.length() > 4;
    }

    private void updateProfile(String userGuid, String userFirstName, String userLastName, String email, String dob, String phoneNumber, String aboutMe) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        UserProfile userProfile = new UserProfile(email, userFirstName, userLastName, phoneNumber, dob, "", aboutMe);
        Gson gson = new Gson();
        String s = gson.toJson(userProfile);
        Log.e("asgagds", "wetetrew" + s);

        Call<UserDataDTO> call = Utils.getYatraShareAPI().updateProfile(userGuid, userProfile);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response != null && response.body() != null && response.body().Data != null) {
                    android.util.Log.e("SUCCEESS RESPONSE DATA", response.body().Data + "");
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        ((HomeActivity)mContext).showSnackBar(getString(R.string.profile_updated_rationale));
                        ((HomeActivity)mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
                    } else {
                        ((HomeActivity)mContext).showSnackBar(response.body().Data);
                    }
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
                ((HomeActivity)mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

}
