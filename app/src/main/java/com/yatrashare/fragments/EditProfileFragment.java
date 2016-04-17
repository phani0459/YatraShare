package com.yatrashare.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.UserProfile;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
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
    @Bind(R.id.editProfileDobLayout)
    public TextInputLayout dobTextInputLayout;
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
    private static int RESULT_LOAD_IMAGE = 1;
    private SharedPreferences.Editor mEditor;
    private String userGender;
    private boolean isImageLoaded;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);

        userDraweeImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startActivityForResult(getPickImageChooserIntent(), RESULT_LOAD_IMAGE);
                }
                return true;
            }
        });

        return view;
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<Intent>();
        PackageManager packageManager = mContext.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = mContext.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            isImageLoaded = true;
            userDraweeImageView.setImageURI(imageUri);
        } else {
            isImageLoaded = false;
        }
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
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

        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.EDIT_PROFILE_SCREEN);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");
        String email = mSharedPreferences.getString(Constants.PREF_USER_EMAIL, null);
        String phone = mSharedPreferences.getString(Constants.PREF_USER_PHONE, null);
        String userProfilePic = mSharedPreferences.getString(Constants.PREF_USER_PROFILE_PIC, "");
        String userFBId = mSharedPreferences.getString(Constants.PREF_USER_FB_ID, "");
        String userDob = mSharedPreferences.getString(Constants.PREF_USER_DOB, "");
        userGender = mSharedPreferences.getString(Constants.PREF_USER_GENDER, "");

        if (profile != null && profile.Data != null) {
            String aboutMe = profile.Data.AboutMe;
            String userName = profile.Data.UserName;

            aboutMeEdit.setText(aboutMe);
            firstNameEdit.setText(userName);

            if (!TextUtils.isEmpty(profile.Data.FirstName))
                firstNameEdit.setText(profile.Data.FirstName);
            if (!TextUtils.isEmpty(profile.Data.LastName))
                lastNameEdit.setText(profile.Data.LastName);
        }

        if (!TextUtils.isEmpty(email)) {
            emailEdit.setText(email);
            emailEdit.setEnabled(false);
        }

        if (!TextUtils.isEmpty(phone)) {
            phoneNoEdit.setText(phone);
        }

        if (!TextUtils.isEmpty(userDob)) {
            dobEdit.setText(userDob);
        }

        if (!isImageLoaded) {
            if (userFBId.isEmpty() && (userProfilePic.isEmpty() || userProfilePic.startsWith("/"))) {
                if (userGender.equalsIgnoreCase("Female")) {
                    userDraweeImageView.setImageURI(Constants.getDefaultFemaleURI());
                } else {
                    userDraweeImageView.setImageURI(Constants.getDefaultPicURI());
                }
            } else if (!userFBId.isEmpty()) {
                Uri uri = Uri.parse("https://graph.facebook.com/" + userFBId + "/picture?type=large");
                userDraweeImageView.setImageURI(uri);
            } else if (!userProfilePic.isEmpty()) {
                Uri uri = Uri.parse(userProfilePic);
                userDraweeImageView.setImageURI(uri);
            }
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

        final DatePickerDialog dobDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

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
    public void updateProfile() {
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
        } else if (TextUtils.isEmpty(dob)) {
            mUpdateUserNameLayout.setErrorEnabled(false);
            mUpdatePhoneLayout.setErrorEnabled(false);
            dobTextInputLayout.setError("Enter Date of Birth");
            cancel = true;
        }

        if (!cancel) {
            mUpdatePhoneLayout.setErrorEnabled(false);
            mUpdateUserNameLayout.setErrorEnabled(false);
            dobTextInputLayout.setErrorEnabled(false);
            updateProfile(userGuid, userFirstName, userLastName, email, dob, phoneNumber, aboutMe);
        }

    }

    private boolean isUserNameValid(String userName) {
        return userName.length() > 4;
    }

    private void updateProfile(final String userGuid, String userFirstName, String userLastName, String email, final String dob, final String phoneNumber, String aboutMe) {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        UserProfile userProfile = new UserProfile(email, userFirstName, userLastName, phoneNumber, dob, userGender, aboutMe);

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
                        Utils.deleteProfile(userGuid);
                        ((HomeActivity) mContext).showSnackBar(getString(R.string.profile_updated_rationale));
                        ((HomeActivity) mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
                        mEditor.putString(Constants.PREF_USER_DOB, dob);
                        mEditor.putString(Constants.PREF_USER_PHONE, phoneNumber);
                        mEditor.apply();
                    } else {
                        ((HomeActivity) mContext).showSnackBar(response.body().Data);
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
                ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
            }
        });
    }

}
