package com.yatrashare.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.RatingReceiverInfo;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.UserRating;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import org.w3c.dom.Text;

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
    @Bind(R.id.btnSubmitRating)
    public Button ratingButton;
    @Bind(R.id.sp_travellerType)
    public Spinner travellerTypeSpinner;
    @Bind(R.id.giveRatingProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.ratingBGView)
    public View mProgressBGView;
    @Bind(R.id.privideRatingLayout)
    public LinearLayout giveFeedBackLayout;
    @Bind(R.id.findmemberLayout)
    public LinearLayout findMemberLayout;
    @Bind(R.id.btnshowFindMember)
    public Button showFindMemberBtn;
    @Bind(R.id.et_mobile_find_mem)
    public EditText findMemMobileEdit;
    @Bind(R.id.receiverInfoLayout)
    public View receiverInfoLayout;
    @Bind(R.id.tv_receiverEmail)
    public TextView ratingReceiverEmail;
    @Bind(R.id.tv_receiverMobile)
    public TextView ratingReceiverMobile;
    @Bind(R.id.im_drawee_receiver)
    public SimpleDraweeView ratingReciverDrawee;

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

        ratingValue.setText("0.0");

        provideRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue.setText(rating + "");
            }
        });

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        receiverGuid = getArguments().getString("Receiver GUID");

        if (receiverGuid != null) {
            ((HomeActivity) mContext).setTitle("Submit Rating");
            giveFeedBackLayout.setVisibility(View.VISIBLE);
            findMemberLayout.setVisibility(View.GONE);
        } else {
            ((HomeActivity) mContext).setTitle("Find Member");
            giveFeedBackLayout.setVisibility(View.GONE);
            findMemberLayout.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.btnshowFindMember)
    public void showFindMemberLayout() {
        showFindMemberBtn.setVisibility(View.GONE);
        receiverInfoLayout.setVisibility(View.GONE);
        giveFeedBackLayout.setVisibility(View.GONE);
        findMemberLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnfindMember)
    public void findMember() {
        Utils.hideSoftKeyboard(findMemMobileEdit);
        String mobile = findMemMobileEdit.getText().toString();
        if (mobile.isEmpty()) {
            Utils.showToast(mContext, "Enter Mobile Number");
        } else {
            if (isPhoneValid(mobile)) {
                Utils.showProgress(true, mProgressView, mProgressBGView);

                Call<RatingReceiverInfo> call = Utils.getYatraShareAPI().getRatingReceiverUserinfoId(userGuid, mobile);
                //asynchronous call
                call.enqueue(new Callback<RatingReceiverInfo>() {
                    /**
                     * Successful HTTP response.
                     *
                     * @param response
                     * @param retrofit
                     */
                    @Override
                    public void onResponse(retrofit.Response<RatingReceiverInfo> response, Retrofit retrofit) {
                        android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                        if (response.body() != null && response.body().Data != null) {
                            showFindMemberBtn.setVisibility(View.VISIBLE);
                            receiverInfoLayout.setVisibility(View.VISIBLE);
                            giveFeedBackLayout.setVisibility(View.VISIBLE);
                            findMemberLayout.setVisibility(View.GONE);

                            ratingReceiverEmail.setText(response.body().Data.Email);
                            ratingReceiverMobile.setText(response.body().Data.MobileNumber);

                            String profilePic = response.body().Data.ReceiverProfilePic;
                            if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
                                Uri uri = Uri.parse(profilePic);
                                ratingReciverDrawee.setImageURI(uri);
                            } else {
                                ratingReciverDrawee.setImageURI(Constants.getDefaultPicURI());
                            }

                            receiverGuid = response.body().Data.ReceiverGuid;

                        } else {

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
                Utils.showToast(mContext, "Enter Valid Mobile Number");
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
        Utils.showProgress(true, mProgressView, mProgressBGView);
        String feedBack = "", stars = "", travellerType = "";
        feedBack = feedBackEditText.getText().toString();
        stars = ratingValue.getText().toString();
        travellerType = travellerTypeSpinner.getSelectedItem().toString();

        UserRating userRating = new UserRating(receiverGuid, stars, feedBack, travellerType);

        Call<UserDataDTO> call = Utils.getYatraShareAPI().giveRatingtoUser(userGuid, userRating);
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
