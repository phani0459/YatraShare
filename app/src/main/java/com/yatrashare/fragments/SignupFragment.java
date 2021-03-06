package com.yatrashare.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.activities.LoginActivity;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.UserSignUp;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    private final String TAG = SignupFragment.class.getSimpleName();

    private Context mContext;
    @Bind(R.id.signUpUserLastName)
    public EditText mSignUpUserLastName;
    @Bind(R.id.signUpUserFirstName)
    public EditText mSignUpUserFirstName;
    @Bind(R.id.signUpEmail)
    public AutoCompleteTextView mSignUpEmail;
    @Bind(R.id.signUpPassword)
    public EditText mSignUpPassword;
    @Bind(R.id.signUpPhone)
    public EditText mSignUpPhone;
    @Bind(R.id.signUpProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.profileImage_drawee)
    public SimpleDraweeView mProfileImageDrawee;
    @Bind(R.id.profileImage)
    public ImageView mProfileImage;
    @Bind(R.id.signupEmailLayout)
    public TextInputLayout mSignUpEmailLayout;
    @Bind(R.id.signupPasswordLayout)
    public TextInputLayout mSignUpPasswordLayout;
    @Bind(R.id.signupFirstNameLayout)
    public TextInputLayout mSignUpFirstNameLayout;
    @Bind(R.id.signupLastNameLayout)
    public TextInputLayout mSignUpLastNameLayout;
    @Bind(R.id.signupProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.signupPhoneLayout)
    public TextInputLayout mSignupPhoneLayout;
    private SharedPreferences.Editor mSharedPrefEditor;
    private static int RESULT_LOAD_IMAGE = 1;
    private SharedPreferences mSharedPreferences;
    @Bind(R.id.rbtn_male)
    public RadioButton maleRadioButton;
    @Bind(R.id.rbtn_female)
    public RadioButton femaleRadioButton;
    private Uri selectedImageUri;
    private boolean isHome;

    public SignupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View inflatedLayout = inflater.inflate(R.layout.fragment_signup, null, false);
        ButterKnife.bind(this, inflatedLayout);

        isHome = !mContext.toString().contains("LoginActivity");

        RelativeLayout mProfileImageLayout = (RelativeLayout) inflatedLayout.findViewById(R.id.profileImageLayout);

        mProfileImage.setVisibility(View.VISIBLE);
        mProfileImageDrawee.setVisibility(View.GONE);
        mSignUpPassword.setFilters(Utils.getInputFilter(Utils.PWD_MAX_CHARS));
        mSignUpEmail.setFilters(Utils.getInputFilter(Utils.EMAIL_MAX_CHARS));
        mSignUpPhone.setFilters(Utils.getInputFilter(Utils.getMobileMaxChars(mContext)));

        mSignUpPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignup();
                    return true;
                }
                return false;
            }
        });

        Button mSignUpButton = (Button) inflatedLayout.findViewById(R.id.signUpButton);

        if (isHome) {
            ((HomeActivity) mContext).setTitle("Sign up");
        } else {
            ((LoginActivity) mContext).setTitle("Sign up");
        }

        mSharedPreferences = Utils.getSharedPrefs(mContext);
        mSharedPrefEditor = mSharedPreferences.edit();

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });

        mProfileImageLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startActivityForResult(getPickImageChooserIntent(), RESULT_LOAD_IMAGE);
                }
                return true;
            }
        });

        return inflatedLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isHome) {
            ((HomeActivity) mContext).setCurrentScreen(HomeActivity.SIGNUP_SCREEN);
        } else {
            ((LoginActivity) mContext).setTitle("Sign up");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = getPickImageResultUri(data);
            mProfileImage.setVisibility(View.INVISIBLE);
            mProfileImageDrawee.setVisibility(View.VISIBLE);
            mProfileImageDrawee.setImageURI(selectedImageUri);

            updateProfilePic();

        }
    }

    private static final int REQUEST_READ_STORAGE = 14;

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateProfilePic();
            }
        }
    }

    private boolean mayRequestStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mSignUpEmail, R.string.storage_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
        }
        return false;
    }

    public void updateProfilePic() {
        if (Utils.isInternetAvailable(mContext)) {
            if (selectedImageUri != null) {
                if (!mayRequestStorage()) {
                    return;
                }

                File file = null;
                try {
                    URI uri = new URI(selectedImageUri.toString());
                    file = new File(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    file = null;
                }
                try {
                    if (file == null) {
                        file = new File(Utils.getPath(selectedImageUri, mContext));
                    }

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    RequestBody body = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("ProfilePic", file.getName(), requestFile).build();

                    String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

                    Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).uploadProfilePic(userGuid, body);
                    call.enqueue(new Callback<UserDataDTO>() {
                        @Override
                        public void onResponse(Response<UserDataDTO> response, Retrofit retrofit) {
                            android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                            Utils.showProgress(false, mProgressView, mProgressBGView);

                            if (response.body() != null && response.isSuccess()) {
                                Log.e(TAG, "Update Pic: " + response.body().Data);
                                selectedImageUri = null;
                                mSharedPrefEditor.putString(Constants.PREF_USER_PROFILE_PIC, response.body().Data);
                                mSharedPrefEditor.commit();
                                if (isHome) {
                                    ((HomeActivity) mContext).showSnackBar(getString(R.string.success_signup));
                                    ((HomeActivity) mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY, null));
                                } else {
                                    ((LoginActivity) mContext).showSnackBar(getString(R.string.success_signup));
                                    ((LoginActivity) mContext).startHomePage();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                            android.util.Log.e(TAG, "FAILURE RESPONSE");
                            Utils.showProgress(false, mProgressView, mProgressBGView);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Utils.showToast(mContext, "Select Image");
            }
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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignup() {
        Utils.hideSoftKeyboard(mSignUpUserLastName);
        // Store values at the time of the login attempt.
        String userFirstName = mSignUpUserFirstName.getText().toString();
        String userLastName = mSignUpUserLastName.getText().toString();
        String email = mSignUpEmail.getText().toString();
        String password = mSignUpPassword.getText().toString();
        String phoneNumber = mSignUpPhone.getText().toString();
        String gender = "";

        // Check for a validity of fields.
        if (TextUtils.isEmpty(userFirstName)) {
            mSignUpFirstNameLayout.setError(getString(R.string.error_invalid_username));
            return;
        }
        mSignUpFirstNameLayout.setErrorEnabled(false);
        if (TextUtils.isEmpty(userLastName)) {
            mSignUpLastNameLayout.setError(getString(R.string.error_invalid_lastname));
            return;
        }
        mSignUpLastNameLayout.setErrorEnabled(false);
        if (TextUtils.isEmpty(email)) {
            mSignUpEmailLayout.setError(getString(R.string.error_field_required));
            return;
        }
        if (!Utils.isEmailValid(email)) {
            mSignUpEmailLayout.setError(getString(R.string.error_invalid_email));
            return;
        }
        mSignUpEmailLayout.setErrorEnabled(false);

        if (TextUtils.isEmpty(phoneNumber)) {
            mSignupPhoneLayout.setError(getString(R.string.error_required_phone));
            return;
        }

        if (!Utils.isPhoneValid(mContext, phoneNumber)) {
            mSignupPhoneLayout.setError(getString(R.string.error_invalid_phone));
            return;
        }

        mSignupPhoneLayout.setErrorEnabled(false);

        if (!isPasswordValid(password)) {
            mSignUpPasswordLayout.setError(getString(R.string.error_invalid_password));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mSignUpPasswordLayout.setError(getString(R.string.error_required_password));
            return;
        }

        mSignUpPasswordLayout.setErrorEnabled(false);

        if (maleRadioButton.isChecked()) {
            gender = "Male";
        } else if (femaleRadioButton.isChecked()) {
            gender = "Female";
        } else {
            Utils.showToast(mContext, "Select Gender");
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        if (Utils.isInternetAvailable(mContext)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            userSignupTask(email, password, phoneNumber, userFirstName, gender, userLastName);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void userSignupTask(final String mEmail, final String mPassword, final String mPhone, final String mUserFirstName, final String gender, final String userLastName) {
        CountryData countryData = Utils.getCountryInfo(mContext, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));
        String countryCode = countryData != null ? countryData.CountryCode : "";
        UserSignUp userSignUp = new UserSignUp(mEmail, mUserFirstName, mPassword, mPhone, countryCode, gender, userLastName);

        Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).userRegistration(userSignUp);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {
            /*
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
                    if (response.body().Data.contains("-")) {
                        mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, mEmail);
                        mSharedPrefEditor.putString(Constants.PREF_USER_FIRST_NAME, mUserFirstName);
                        mSharedPrefEditor.putString(Constants.PREF_USER_LAST_NAME, userLastName);
                        mSharedPrefEditor.putString(Constants.PREF_USER_PHONE, mPhone);
                        mSharedPrefEditor.putString(Constants.PREF_USER_GENDER, gender);
                        mSharedPrefEditor.putString(Constants.PREF_USER_GUID, response.body().Data);
                        mSharedPrefEditor.putBoolean(Constants.PREF_LOGGEDIN, true);
                        mSharedPrefEditor.commit();

                        if (selectedImageUri != null) {
                            updateProfilePic();
                        } else {
                            Utils.showProgress(false, mProgressView, mProgressBGView);
                            if (isHome) {
                                ((HomeActivity) mContext).showSnackBar(getString(R.string.success_signup));
                                ((HomeActivity) mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY, null));
                            } else {
                                ((LoginActivity) mContext).showSnackBar(getString(R.string.success_signup));
                                ((LoginActivity) mContext).startHomePage();
                            }
                        }

                    } else {
                        Utils.showProgress(false, mProgressView, mProgressBGView);
                        if (isHome) {
                            ((HomeActivity) mContext).showSnackBar(response.body().Data);
                        } else {
                            ((LoginActivity) mContext).showSnackBar(response.body().Data);
                        }
                    }
                }
            }

            /*
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e(TAG, "FAILURE RESPONSE");
                Utils.showProgress(false, mProgressView, mProgressBGView);
                if (isHome) {
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                } else {
                    ((LoginActivity) mContext).showSnackBar(getString(R.string.tryagain));
                }
            }
        });

    }

}
