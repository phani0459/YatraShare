package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateMobileFragment extends Fragment {

    private static final String TAG = UpdateMobileFragment.class.getSimpleName();
    @Bind(R.id.et_country)
    public EditText countryEditText;
    @Bind(R.id.edit_number_bt)
    public Button editNoBt;
    @Bind(R.id.verify_code_bt)
    public Button verifyBt;
    @Bind(R.id.resend_code_bt)
    public Button resendCodeBt;
    @Bind(R.id.save_bt)
    public Button saveBt;
    @Bind(R.id.cancel_bt)
    public Button cancelBt;
    @Bind(R.id.phone_edit)
    public EditText phoneEdit;
    @Bind(R.id.code_Edit)
    public EditText verificationCodeEdit;
    @Bind(R.id.editNumberBtns)
    public LinearLayout editNumberBtnsLayout;
    @Bind(R.id.verifyBtnLayout)
    public LinearLayout verifyBtnLayout;
    @Bind(R.id.phone_numberLayout)
    public TextInputLayout phoneNumberLayout;
    @Bind(R.id.confirmation_codeLayout)
    public TextInputLayout verifyCodeLayout;
    @Bind(R.id.verifyProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.verifyProgressBGView)
    public View mProgressBGView;

    private Context mContext;
    private String userGuid;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;


    public UpdateMobileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_mobile, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        editNoBt.getBackground().setLevel(3);
        verifyBt.getBackground().setLevel(1);
        saveBt.getBackground().setLevel(1);
        cancelBt.getBackground().setLevel(0);
        resendCodeBt.getBackground().setLevel(2);

        mSharedPreferences = Utils.getSharedPrefs(mContext);
        mEditor = mSharedPreferences.edit();
        String mobile = mSharedPreferences.getString(Constants.PREF_USER_PHONE, "");
        phoneEdit.setText(mobile);

        if (!TextUtils.isEmpty(mobile)) phoneEdit.setEnabled(false);
        else editMobileNumber();

        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        phoneEdit.setFilters(Utils.getInputFilter(Utils.getMobileMaxChars(mContext)));

        CountryData countryData = Utils.getCountryInfo(mContext, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null) {
            countryEditText.setText(countryData.MobileCode + "   " + countryData.CountryName);
        }

        if (!getArguments().getBoolean("IS VERIFIED", false) && !TextUtils.isEmpty(mobile)) {
            sendVerifyCode();
        }

        return view;
    }

    @OnClick(R.id.save_bt)
    public void saveMobileNumber() {
        verificationCodeEdit.setEnabled(true);
        if (Utils.isPhoneValid(mContext, phoneEdit.getText().toString())) {
            phoneNumberLayout.setError(null);
            phoneNumberLayout.setErrorEnabled(false);
            updateMobile(phoneEdit.getText().toString());
        } else {
            phoneNumberLayout.setError("Enter valid phone number");
        }
    }

    private void updateMobile(final String mobNum) {
        if (Utils.isInternetAvailable(mContext)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).saveMobileNumber(userGuid, mobNum);

            call.enqueue(new Callback<UserDataDTO>() {
                @Override
                public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                    if (response.body() != null && response.body().Data != null) {
                        if (response.body().Data.equalsIgnoreCase("Success")) {

                            phoneEdit.setEnabled(false);
                            verificationCodeEdit.setVisibility(View.VISIBLE);
                            verifyBtnLayout.setVisibility(View.VISIBLE);
                            editNumberBtnsLayout.setVisibility(View.GONE);
                            mEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, false);
                            mEditor.putString(Constants.PREF_USER_PHONE, mobNum);
                            mEditor.commit();

                            ((HomeActivity) mContext).showSnackBar(getString(R.string.mobileSaved));
                            Utils.deleteFile(mContext, userGuid);
                        } else {
                            ((HomeActivity) mContext).showSnackBar(response.body().Data);
                        }
                    }
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                }

                @Override
                public void onFailure(Throwable t) {
                    android.util.Log.e(TAG, "FAILURE RESPONSE");
                    Utils.showProgress(false, mProgressView, mProgressBGView);
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    @OnClick(R.id.cancel_bt)
    public void cancelMobileNumber() {

        phoneEdit.setText(mSharedPreferences.getString(Constants.PREF_USER_PHONE, ""));

        phoneEdit.setEnabled(false);
        verificationCodeEdit.setVisibility(View.VISIBLE);
        verificationCodeEdit.setEnabled(true);
        verifyBtnLayout.setVisibility(View.VISIBLE);
        editNumberBtnsLayout.setVisibility(View.GONE);

        verifyCodeLayout.setErrorEnabled(false);
        phoneNumberLayout.setErrorEnabled(false);
    }

    @OnClick(R.id.verify_code_bt)
    public void verifyCode() {
        if (Utils.isInternetAvailable(mContext)) {
            if (!verificationCodeEdit.getText().toString().isEmpty()) {
                verifyCodeLayout.setError(null);
                verifyCodeLayout.setErrorEnabled(false);
                Utils.showProgress(true, mProgressView, mProgressBGView);
                Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).verifyMobileNumber(userGuid, phoneEdit.getText().toString(), verificationCodeEdit.getText().toString());
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
                            if (response.body().Data.equalsIgnoreCase("Success")) {
                                mEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, true);
                                mEditor.commit();
                                ((HomeActivity) mContext).showSnackBar(getString(R.string.mobileUpdated));
                                Utils.deleteFile(mContext, userGuid);
                                ((HomeActivity) mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
                            } else {
                                ((HomeActivity) mContext).showSnackBar(response.body().Data);
                            }
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
            } else {
                verifyCodeLayout.setError("Enter code");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.UPDATE_MOBILE_SCREEN);
        ((HomeActivity) mContext).setTitle("Verify Mobile Number");
    }

    @OnClick(R.id.resend_code_bt)
    public void sendVerifyCode() {
        if (Utils.isInternetAvailable(mContext)) {
            if (Utils.isPhoneValid(mContext, phoneEdit.getText().toString())) {
                resendCodeBt.setText("Resend Code");
                Utils.showProgress(true, mProgressView, mProgressBGView);

                Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).sendVerificationCode(userGuid);
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
                            if (response.body().Data.equalsIgnoreCase("Success")) {
                                Utils.showToast(mContext, "Verification code sent");
                            }
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
        }
    }

    @OnClick(R.id.edit_number_bt)
    public void editMobileNumber() {
        resendCodeBt.setText("Send Code");
        verificationCodeEdit.setEnabled(false);
        if (editNumberBtnsLayout.getVisibility() == View.GONE) {
            phoneEdit.setEnabled(true);
            verifyCodeLayout.setErrorEnabled(false);
            editNumberBtnsLayout.setVisibility(View.VISIBLE);
            verifyBtnLayout.setVisibility(View.GONE);
            verificationCodeEdit.setVisibility(View.GONE);
        } else {
            phoneEdit.setEnabled(false);
            editNumberBtnsLayout.setVisibility(View.GONE);
        }
    }

}
