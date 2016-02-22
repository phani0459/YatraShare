package com.yatrashare.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

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

    public EditProfileFragment() {
        // Required empty public constructor
    }

    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mContext = getActivity();

        Profile profile = (Profile) getArguments().getSerializable("PROFILE");
        ButterKnife.bind(this, view);

        Button updateProfileButton = (Button)view.findViewById(R.id.updateProfileButton);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");
        String email = mSharedPreferences.getString(Constants.PREF_USER_EMAIL, null);

        if (profile.Data != null) {
            String aboutMe = profile.Data.AboutMe;
            String userName = profile.Data.UserName;

            aboutMeEdit.setText(aboutMe);
            firstNameEdit.setText(userName);

            if (email != null && !email.isEmpty()) {
                emailEdit.setText(email);
                emailEdit.setEnabled(false);
            }
        }

        dobEdit.setInputType(InputType.TYPE_NULL);

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Calendar newCalendar = Calendar.getInstance();

        final DatePickerDialog dobDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dobEdit.setText(dateFormatter.format(newDate.getTime()));
                try {
                    Date minDate = dateFormatter.parse("01/01/2012");
                    view.setMinDate(minDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dobEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dobDatePickerDialog.show();
            }
        });


        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                } else if (TextUtils.isEmpty(phoneNumber) || !isPhoneValid(phoneNumber)) {
                    mUpdatePhoneLayout.setError(getString(R.string.error_invalid_phone));
                    cancel = true;
                }

                if (!cancel) {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    Utils.showProgress(true, mProgressView, mProgressBGView);
                    updateProfile(userGuid, userFirstName, userLastName, email, dob, phoneNumber, aboutMe);
                }
            }
        });
        return view;
    }



    private boolean isUserNameValid(String userName) {
        return userName.length() > 4;
    }

    private boolean isPhoneValid(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            if (phoneNumber.length() == 10)
                return true;
            else
                return false;
        } else {
            return false;
        }
    }

    private void updateProfile(String userGuid, String userFirstName, String userLastName, String email, String dob, String phoneNumber, String aboutMe) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YatraShareAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);
        UserProfile userProfile = new UserProfile(email, userFirstName, userLastName, phoneNumber, dob, "", aboutMe);

        Call<UserDataDTO> call = yatraShareAPI.updateProfile(userGuid, userProfile);
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
                if (response != null && response.body() != null && response.body().Data != null) {
                    android.util.Log.e("SUCCEESS RESPONSE DATA", response.body().Data + "");
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        ((HomeActivity)mContext).showSnackBar(getString(R.string.profile_updated_rationale));
                        ((HomeActivity)mContext).loadHomePage(false);
                    } else {
                        ((HomeActivity)mContext).showSnackBar(response.body().Data);
                    }
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
            }
        });
    }

}
