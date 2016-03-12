package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
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
    @Bind(R.id.edit_number_bt)
    public Button editNoBt ;
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

    private boolean isPhoneSaved;
    private Context mContext;
    private String userGuid;
    private boolean isValidPhoneNUmber;
    private SharedPreferences.Editor mEditor;


    public UpdateMobileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_mobile, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        editNoBt.getBackground().setLevel(3);
        /*verifyBt.getBackground().setLevel(1);*/
        saveBt.getBackground().setLevel(1);
        cancelBt.getBackground().setLevel(0);
        resendCodeBt.getBackground().setLevel(2);
        verifyBt.setEnabled(false);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
        String mobile = mSharedPreferences.getString(Constants.PREF_USER_PHONE, "");
        phoneEdit.setText(mobile);
        phoneEdit.setEnabled(false);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        return view;
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

    @OnClick(R.id.save_bt)
    public void saveMobileNumber() {
        if (isPhoneValid(phoneEdit.getText().toString())) {
            isPhoneSaved = true;
            phoneEdit.setEnabled(false);
            verifyBtnLayout.setVisibility(View.VISIBLE);
            editNumberBtnsLayout.setVisibility(View.GONE);
            phoneNumberLayout.setError(null);
            phoneNumberLayout.setErrorEnabled(false);
        } else {
            isPhoneSaved = false;
            phoneNumberLayout.setError("Enter valid phone number");
        }
    }

    @OnClick(R.id.cancel_bt)
    public void cancelMobileNumber() {
        phoneEdit.setEnabled(false);
        verifyBtnLayout.setVisibility(View.VISIBLE);
        editNumberBtnsLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.verify_code_bt)
    public void verifyCode() {
        if (!verificationCodeEdit.getText().toString().isEmpty()) {
            verifyCodeLayout.setError(null);
            verifyCodeLayout.setErrorEnabled(false);
            Call<UserDataDTO> call = Utils.getYatraShareAPI().verifyMobileNumber(userGuid, phoneEdit.getText().toString(), verificationCodeEdit.getText().toString());
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
                        if (response.body().Data.equalsIgnoreCase("Success")) {
                            mEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, true);
                            mEditor.commit();
                            ((HomeActivity) mContext).showSnackBar(getString(R.string.mobileUpdated));
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
                 * @param t
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

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.UPDATE_MOBILE_SCREEN);
        ((HomeActivity)mContext).setTitle("Verify Mobile Number");
    }

    @OnClick(R.id.resend_code_bt)
    public void sendVerifyCode() {
        if (isPhoneValid(phoneEdit.getText().toString())) {
            resendCodeBt.setText("Resend Code");
            Utils.showProgress(true, mProgressView, mProgressBGView);

            Call<UserDataDTO> call = Utils.getYatraShareAPI().sendVerificationCode(userGuid);
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
                        if (response.body().Data.equalsIgnoreCase("Success")) {
                            Utils.showToast(mContext, "Verification code sent");
                            verifyBt.setBackground(getResources().getDrawable(R.drawable.mobile_validation_stroke));
                            verifyBt.getBackground().setLevel(1);
                            verifyBt.setEnabled(true);
                        }
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

    @OnClick(R.id.edit_number_bt)
    public void editMobileNumber() {
        if(editNumberBtnsLayout.getVisibility() == View.GONE) {
            phoneEdit.setEnabled(true);
            editNumberBtnsLayout.setVisibility(View.VISIBLE);
            verifyBtnLayout.setVisibility(View.GONE);
        } else {
            phoneEdit.setEnabled(false);
            editNumberBtnsLayout.setVisibility(View.GONE);
        }
    }

}
