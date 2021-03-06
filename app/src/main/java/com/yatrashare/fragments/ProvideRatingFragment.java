package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.RatingReceiverInfo;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.UserRating;
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
public class ProvideRatingFragment extends Fragment {


    private static final String TAG = ProvideRatingFragment.class.getSimpleName();
    private Context mContext;
    @Bind(R.id.provideRatingBar)
    public RatingBar provideRatingBar;
    @Bind(R.id.tv_ratingValue)
    public TextView ratingValue;
    @Bind(R.id.et_feedback)
    public EditText feedBackEditText;
    @Bind(R.id.sp_travellerType)
    public Spinner travellerTypeSpinner;
    @Bind(R.id.giveRatingProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.ratingBGView)
    public View mProgressBGView;
    @Bind(R.id.et_mobile_find_mem)
    public EditText findMemMobileEdit;
    @Bind(R.id.receiverInfoLayout)
    public View receiverInfoLayout;
    @Bind(R.id.tv_receiverEmail)
    public TextView ratingReceiverEmail;
    @Bind(R.id.tv_receiverName)
    public TextView ratingReceiverName;
    @Bind(R.id.tv_receiverMobile)
    public TextView ratingReceiverMobile;
    @Bind(R.id.im_drawee_receiver)
    public SimpleDraweeView ratingReciverDrawee;
    @Bind(R.id.feedBackTextLayout)
    public TextInputLayout feedBackTextLayout;
    @Bind(R.id.btnfindMember)
    public Button findMemButton;
    @Bind(R.id.et_rating_country)
    public EditText countryEditText;
    @Bind(R.id.scrollView)
    public ScrollView scrollView;
    @Bind(R.id.mobileEt_Layout)
    public TextInputLayout mobileTextInputLayout;
    @Bind(R.id.btnSubmitRating)
    public Button submitRatingButton;


    private String receiverGuid;
    private String userGuid;

    public ProvideRatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provide_rating, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        ratingValue.setText("1.0");

        findMemButton.getBackground().setLevel(0);
        submitRatingButton.getBackground().setLevel(1);

        provideRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 1) {
                    rating = 1;
                }
                provideRatingBar.setRating(rating);
                ratingValue.setText(rating + "");
            }
        });

        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(mContext);

        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        receiverGuid = getArguments().getString("Receiver GUID");

        ((HomeActivity) mContext).setTitle("Submit Rating");

        CountryData countryData = Utils.getCountryInfo(mContext, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        if (countryData != null) {
            countryEditText.setText(countryData.MobileCode + "   " + countryData.CountryName);
        }

        return view;
    }

    private boolean isPhoneValid(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return phoneNumber.length() == 10;
        } else {
            return false;
        }
    }

    @OnClick(R.id.btnfindMember)
    public void findMember() {
        if (Utils.isInternetAvailable(mContext)) {
            Utils.hideSoftKeyboard(findMemMobileEdit);
            String mobile = findMemMobileEdit.getText().toString();
            if (TextUtils.isEmpty(mobile)) {
                mobileTextInputLayout.setError("Enter Mobile Number");
            } else {
                if (isPhoneValid(mobile)) {
                    mobileTextInputLayout.setErrorEnabled(false);
                    Utils.showProgress(true, mProgressView, mProgressBGView);

                    Call<RatingReceiverInfo> call = Utils.getYatraShareAPI(mContext).getRatingReceiverUserinfoId(userGuid, mobile);
                    //asynchronous call
                    call.enqueue(new Callback<RatingReceiverInfo>() {
                        /**
                         * Successful HTTP response.
                         *
                         * @param response server response
                         * @param retrofit adapter
                         */
                        @Override
                        public void onResponse(retrofit.Response<RatingReceiverInfo> response, Retrofit retrofit) {
                            android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                            if (response.body() != null && response.body().Data != null && response.body().Data.Email != null && response.body().Data.MobileNumber != null) {
                                receiverGuid = response.body().Data.ReceiverGuid;

                                if (receiverGuid.equalsIgnoreCase(userGuid)) {
                                    Utils.showToast(mContext, "You cannot provide rating to yourself");
                                    Utils.showProgress(false, mProgressView, mProgressBGView);
                                    return;
                                }
                                receiverInfoLayout.setVisibility(View.VISIBLE);
                                scrollView.fullScroll(View.FOCUS_DOWN);

                                ratingReceiverEmail.setText(response.body().Data.Email);
                                ratingReceiverMobile.setText(response.body().Data.MobileNumber);
                                ratingReceiverName.setText(response.body().Data.ReceiverName);

                                String profilePic = response.body().Data.ReceiverProfilePic;
                                if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
                                    Uri uri = Uri.parse(profilePic);
                                    ratingReciverDrawee.setImageURI(uri);
                                } else {
                                    ratingReciverDrawee.setImageURI(Constants.getDefaultPicURI());
                                }


                            } else {

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
                    mobileTextInputLayout.setError("Enter Valid Mobile Number");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.PROVIDE_RATING_SCREEN);
    }

    @OnClick(R.id.btnSubmitRating)
    public void submitRating() {
        if (Utils.isInternetAvailable(mContext)) {
            Utils.hideSoftKeyboard(feedBackEditText);
            String feedBack = "", stars = "", travellerType = "";
            feedBack = feedBackEditText.getText().toString();
            stars = ratingValue.getText().toString();

            if (TextUtils.isEmpty(stars) || stars.equalsIgnoreCase("0.0")) {
                Utils.showToast(mContext, "Provide rating");
                return;
            }

            if (TextUtils.isEmpty(feedBack)) {
                feedBackTextLayout.setError("Say how was your journey");
                return;
            }

            feedBackTextLayout.setErrorEnabled(false);

            Utils.showProgress(true, mProgressView, mProgressBGView);
            if (stars.contains(".")) {
                stars = stars.substring(0, stars.lastIndexOf("."));
            }

            travellerType = travellerTypeSpinner.getSelectedItem().toString();

            UserRating userRating = new UserRating(receiverGuid, stars, feedBack, travellerType.replace(" ", ""));

            Gson gson = new Gson();
            Log.e(TAG, "submitRating: " + gson.toJson(userRating));

            Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).giveRatingtoUser(userGuid, userRating);
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
                    if (response.body() != null && response.body().Data != null) {
                        if (response.body().Data.equalsIgnoreCase("Success")) {
                            ((HomeActivity) mContext).showSnackBar("Rating submitted Successfully");
                            ((HomeActivity) mContext).loadHomePage(false, getArguments().getString(Constants.ORIGIN_SCREEN_KEY, ""));
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
        }
    }

}
