package com.yatrashare.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.pojos.UserPreferences;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {


    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPrefEditor;
    private Context mContext;
    private final String TAG = ProfileFragment.class.getSimpleName();
    @Bind(R.id.profileProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.profileImage_drawee)
    public SimpleDraweeView mProfileImageDrawee;
    /* @Bind(R.id.licenceStatus)
     public ImageView mLicenceStatus;*/
    @Bind(R.id.mobileStatus)
    public ImageView mMobileStatus;
    @Bind(R.id.emailStatus)
    public ImageView mEmailStatus;
    @Bind(R.id.lastLoginTime)
    public TextView lastLoginTimeText;
    @Bind(R.id.userName)
    public TextView userTitle;
    @Bind(R.id.aboutMeText)
    public TextView aboutMeText;
    @Bind(R.id.profileProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.chatPrefernece)
    public ImageView mChatPreference;
    @Bind(R.id.musicPreference)
    public ImageView mMusicPreference;
    @Bind(R.id.smokePreference)
    public ImageView mSmokePreference;
    @Bind(R.id.petsPreference)
    public ImageView mPetsPreference;
    @Bind(R.id.foodPreference)
    public ImageView mFoodPreference;
    private Profile profile;
    private String userGuide;
    @Bind(R.id.userSince)
    public TextView userSinceText;
    @Bind(R.id.noOfRidesText)
    public TextView noOfRidesText;
    @Bind(R.id.mobileStatusHeading)
    public TextView mobileStatusHeading;
    @Bind(R.id.mobileStatusText)
    public TextView mobileStatusText;
    @Bind(R.id.emailStatusHeading)
    public TextView emailStatusHeading;
    @Bind(R.id.emailStatusText)
    public TextView emailStatusText;
    @Bind(R.id.licenceStatusHeading)
    public TextView licenceStatusHeading;
    @Bind(R.id.liceneceStatusText)
    public TextView liceneceStatusText;
    @Bind(R.id.profileRatingBar)
    public RatingBar ratingBar;
    private boolean isMobileVerified;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedLayout = inflater.inflate(R.layout.fragment_profile_new, null, false);
        mContext = getActivity();
        ButterKnife.bind(this, inflatedLayout);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPrefEditor = mSharedPreferences.edit();

        userGuide = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        if (!TextUtils.isEmpty(userGuide) && userGuide.contains("\"")) {
            userGuide = userGuide.replace("\"", "");
        }

        mChatPreference.setOnClickListener(this);
        mMusicPreference.setOnClickListener(this);
        mSmokePreference.setOnClickListener(this);
        mPetsPreference.setOnClickListener(this);
        mFoodPreference.setOnClickListener(this);

        ((HomeActivity) mContext).setTitle("My Account");

        profile = Utils.checkforProfile(mContext, userGuide);

        if (profile != null) {
            loadProfile();
        } else {
            userProfileTask();
        }

        return inflatedLayout;
    }

    public void editProfile() {
        profile = Utils.checkforProfile(mContext, userGuide);
        if (profile != null) {
            ((HomeActivity) mContext).loadScreen(HomeActivity.EDIT_PROFILE_SCREEN, false, profile, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
        }
    }

    public void updateUserPreferences() {
        if (profile != null && profile.Data != null) {
            try {
                int chatPref = profile.Data.Chat;
                int musicPref = profile.Data.Music;
                int smokePref = profile.Data.Smoking;
                int foodPref = profile.Data.Food;

                mChatPreference.setImageResource(chatPref == 1 ? R.drawable.chat_allow : chatPref == 3 ? R.drawable.chat_not_allow : R.drawable.chat);
                mMusicPreference.setImageResource(musicPref == 1 ? R.drawable.music_allow : musicPref == 3 ? R.drawable.music_not_allow : R.drawable.music_not_much);
                mSmokePreference.setImageResource(smokePref == 1 ? R.drawable.smoke_allow : smokePref == 3 ? R.drawable.smoke_not_allow : R.drawable.smoke_not_much);
                mFoodPreference.setImageResource(foodPref == 1 ? R.drawable.food_allow : foodPref == 3 ? R.drawable.food_not_allow : R.drawable.food_not_much);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.mobileStatusHeading)
    public void changeMobileNumber() {
        ((HomeActivity) mContext).loadScreen(HomeActivity.UPDATE_MOBILE_SCREEN, false, isMobileVerified, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
    }

    @OnTouch(R.id.emailStatusHeading)
    public boolean verifyEmail(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getRawX() >= (v.getRight() - ((TextView) v).getCompoundDrawables()[2].getBounds().width())) {
                Utils.showProgress(true, mProgressView, mProgressBGView);

                Call<UserDataDTO> call = Utils.getYatraShareAPI().sendVerificationEmail(userGuide);
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
                        android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null) {
                            if (!TextUtils.isEmpty(response.body().Data) && response.body().Data.equalsIgnoreCase("Success")) {
                                ((HomeActivity) mContext).showSnackBar("Verification Mail sent to your email");
                                Utils.deleteFile(mContext, userGuide);
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
                    }
                });
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (profile != null) {
            if (getArguments() != null) {
                getArguments().putSerializable("Profile", profile);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.PROFILE_SCREEN);
        ((HomeActivity) mContext).prepareMenu();
        if (getArguments() != null) {
            profile = (Profile) getArguments().getSerializable("Profile");
            loadProfile();
        }

    }

    public void loadProfile() {
        if (profile != null && profile.Data != null) {

            String profilePic = profile.Data.ProfilePicture;
            String userName = profile.Data.UserName;
            String lastLoginTime = profile.Data.LastLogin;
            String aboutMe = profile.Data.AboutMe;
            String offeredRides = profile.Data.OfferedRides;
            String userSince = profile.Data.MemberSince;

            try {
                ratingBar.setRating(Float.parseFloat(profile.Data.UserAvgRating));
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            try {
                /**
                 * 2 - verified
                 * else - not verified
                 */
                String mobileStatus = profile.Data.MobileNumberStatus != null ? profile.Data.MobileNumberStatus : "0";
                String emailStatus = profile.Data.EmailStatus != null ? profile.Data.EmailStatus : "0";
                String licenceStatus = profile.Data.LicenceStatus != null ? profile.Data.LicenceStatus : "0";
                mMobileStatus.setImageResource(mobileStatus.equals("2") ? R.drawable.verified : R.drawable.unverified);

                isMobileVerified = mobileStatus.equals("2");

                mPrefEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, mobileStatus.equals("2"));
                mPrefEditor.commit();

                mEmailStatus.setImageResource(emailStatus.equals("2") ? R.drawable.verified : R.drawable.unverified);
                String mobileHeading = mobileStatus.equals("2") ? "<font color=\"#5CB85C\">Verified</font>" :
                        "<font color=\"#D9534F\">Not Verified</font>";
                String mobileSuggetionText = isMobileVerified ? "Click here if you want to change your number" :
                        "Your number is not verified \n Click here to verify";
                mobileStatusHeading.setText(Html.fromHtml("Mobile Number: " + mobileHeading));
                mobileStatusHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_edit, 0);
                mobileStatusText.setText(mobileSuggetionText);
                String emailHeading = emailStatus.equals("2") ? "<font color=\"#5CB85C\">Verified</font>" :
                        "<font color=\"#D9534F\">Not Verified</font>";
                String emailSuggetionText = emailStatus.equals("2") ? "Click here if you want to change your email" :
                        "Your Email is not verified \n Click here to verify";
                emailStatusHeading.setText(Html.fromHtml("Email: " + emailHeading));
                emailStatusHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_edit, 0);
                emailStatusText.setText(emailSuggetionText);
                String licenceHeading = licenceStatus.equals("2") ? "<font color=\"#5CB85C\">Accepted</font>" :
                        "<font color=\"#D9534F\">Rejected</font>";
                String licenceSuggetionText = licenceStatus.equals("2") ? "Your licence is approved" :
                        "Your Licence is rejected \n Click on Edit icon to provide Valid Licence";
                licenceStatusHeading.setText(Html.fromHtml("Licence Status: " + licenceHeading));
                licenceStatusHeading.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_edit, 0);
                liceneceStatusText.setText(licenceSuggetionText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String userGender = mSharedPreferences.getString(Constants.PREF_USER_GENDER, "");

            updateUserPreferences();

            if (TextUtils.isEmpty(profilePic) || profilePic.startsWith("/")) {
                profilePic = mSharedPreferences.getString(Constants.PREF_USER_PROFILE_PIC, "");
            }

            if (!TextUtils.isEmpty(profilePic) && !profilePic.startsWith("/")) {
                Uri uri = Uri.parse(profilePic);
                mProfileImageDrawee.setImageURI(uri);
            } else {
                if (userGender.equalsIgnoreCase("Female")) {
                    mProfileImageDrawee.setImageURI(Constants.getDefaultFemaleURI());
                } else {
                    mProfileImageDrawee.setImageURI(Constants.getDefaultPicURI());
                }
            }

            if (lastLoginTime != null && !lastLoginTime.isEmpty()) {
                lastLoginTimeText.setText("Last active on " + lastLoginTime);
            }

            if (aboutMe != null && !aboutMe.isEmpty()) {
                aboutMeText.setText(aboutMe);
            }

            if (offeredRides != null && !offeredRides.isEmpty()) {
                noOfRidesText.setText(offeredRides + " ride(s) Offered");
            } else {
                noOfRidesText.setVisibility(View.GONE);
            }

            if (userSince != null && !userSince.isEmpty()) {
                userSinceText.setText("User Since " + userSince);
            } else {
                userSinceText.setVisibility(View.GONE);
            }

            userTitle.setText("My Account");
            if (userName != null && !userName.isEmpty()) {
                userTitle.setText(userName + "'s Profile");
            }


        }
    }

    public void userProfileTask() {
        Utils.showProgress(true, mProgressView, mProgressBGView);

        Call<Profile> call = Utils.getYatraShareAPI().userPublicProfile(userGuide);
        //asynchronous call
        call.enqueue(new Callback<Profile>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
             */
            @Override
            public void onResponse(retrofit.Response<Profile> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                if (response.body() != null) {
                    profile = response.body();
                    Utils.saveProfile(mContext, profile, userGuide);
                    loadProfile();
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
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatPrefernece:
            case R.id.musicPreference:
            case R.id.smokePreference:
            case R.id.petsPreference:
            case R.id.foodPreference:
                showUpdatePrefsDialog();
                break;
        }
    }

    private void showUpdatePrefsDialog() {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_update_prefs);
        dialog.setTitle("Change preferences");

        final View progressView = dialog.findViewById(R.id.updateProgressBGView);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.updatePref_progress);

        ImageView chatAllowImage = (ImageView) dialog.findViewById(R.id.chatAllowImage);
        ImageView chatNotAllowImage = (ImageView) dialog.findViewById(R.id.chatNotAllowImage);
        ImageView chatNotMuchImage = (ImageView) dialog.findViewById(R.id.chatImage);

        ImageView musicAllowImage = (ImageView) dialog.findViewById(R.id.musicAllowImage);
        ImageView musicNotAllowImage = (ImageView) dialog.findViewById(R.id.musicNotAllowImage);
        ImageView musicNotMuchImage = (ImageView) dialog.findViewById(R.id.musicImage);

        ImageView foodAllowImage = (ImageView) dialog.findViewById(R.id.foodAllowImage);
        ImageView foodNotAllowImage = (ImageView) dialog.findViewById(R.id.foodNotAllowImage);
        ImageView foodNotMuchImage = (ImageView) dialog.findViewById(R.id.foodImage);

        ImageView smokeAllowImage = (ImageView) dialog.findViewById(R.id.smokeAllowImage);
        ImageView smokeNotAllowImage = (ImageView) dialog.findViewById(R.id.smokeNotAllowImage);
        ImageView smokeNotMuchImage = (ImageView) dialog.findViewById(R.id.smokeImage);

        final View chatAllowRingView = dialog.findViewById(R.id.chatAllowRingView);
        final View chatNotAllowRingView = dialog.findViewById(R.id.chatNotAllowRingView);
        final View chatNotMuchRingView = dialog.findViewById(R.id.chatRingView);

        final View musicAllowRingView = dialog.findViewById(R.id.musicAllowRingView);
        final View musicNotAllowRingView = dialog.findViewById(R.id.musicNotAllowRingView);
        final View musicNotMuchRingView = dialog.findViewById(R.id.musicRingView);

        final View foodAllowRingView = dialog.findViewById(R.id.foodAllowRingView);
        final View foodNotAllowRingView = dialog.findViewById(R.id.foodNotAllowRingView);
        final View foodNotMuchRingView = dialog.findViewById(R.id.foodRingView);

        final View smokeAllowRingView = dialog.findViewById(R.id.smokeAllowRingView);
        final View smokeNotAllowRingView = dialog.findViewById(R.id.smokeNotAllowRingView);
        final View smokeNotMuchRingView = dialog.findViewById(R.id.smokeRingView);

        final TextView chatToolTip = (TextView) dialog.findViewById(R.id.chatToolTip);
        final TextView musicToolTip = (TextView) dialog.findViewById(R.id.musicToolTip);
        final TextView foodToolTip = (TextView) dialog.findViewById(R.id.foodToolTip);
        final TextView smokeToolTip = (TextView) dialog.findViewById(R.id.smokeToolTip);

        if (profile != null && profile.Data != null) {
            changeViews(profile.Data.Chat, chatAllowRingView, chatNotAllowRingView, chatNotMuchRingView);
            changeViews(profile.Data.Music, musicAllowRingView, musicNotAllowRingView, musicNotMuchRingView);
            changeViews(profile.Data.Food, foodAllowRingView, foodNotAllowRingView, foodNotMuchRingView);
            changeViews(profile.Data.Smoking, smokeAllowRingView, smokeNotAllowRingView, smokeNotMuchRingView);
            chatToolTip.setText(profile.Data.ChatTooltip != null ? profile.Data.ChatTooltip : "I talk moderately");
            musicToolTip.setText(profile.Data.MusicTooltip != null ? profile.Data.MusicTooltip : "I listen to music sometimes");
            foodToolTip.setText(profile.Data.FoodTooltip != null ? profile.Data.FoodTooltip : "You can have food neatly");
            smokeToolTip.setText(profile.Data.SmokingTooltip != null ? profile.Data.SmokingTooltip : "I don’t encourage smoking much");
        }

        Button cancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button submit = (Button) dialog.findViewById(R.id.btnSubmit);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        chatAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), chatAllowRingView, chatNotAllowRingView, chatNotMuchRingView);
                chatToolTip.setText("I’m a talking geek");

            }
        });
        chatNotAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), chatAllowRingView, chatNotAllowRingView, chatNotMuchRingView);
                chatToolTip.setText("I stay calm");
            }
        });
        chatNotMuchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), chatAllowRingView, chatNotAllowRingView, chatNotMuchRingView);
                chatToolTip.setText("I talk moderately");
            }
        });

        musicAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), musicAllowRingView, musicNotAllowRingView, musicNotMuchRingView);
                musicToolTip.setText("I’m a music geek");
            }
        });
        musicNotAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), musicAllowRingView, musicNotAllowRingView, musicNotMuchRingView);
                musicToolTip.setText("Not interested in Music");
            }
        });
        musicNotMuchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), musicAllowRingView, musicNotAllowRingView, musicNotMuchRingView);
                musicToolTip.setText("I listen to music sometimes");
            }
        });

        foodAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), foodAllowRingView, foodNotAllowRingView, foodNotMuchRingView);
                foodToolTip.setText("You can have food whenever you like");
            }
        });
        foodNotAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), foodAllowRingView, foodNotAllowRingView, foodNotMuchRingView);
                foodToolTip.setText("I don’t like having food in my vehicle");
            }
        });
        foodNotMuchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), foodAllowRingView, foodNotAllowRingView, foodNotMuchRingView);
                foodToolTip.setText("You can have food neatly");
            }
        });

        smokeAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), smokeAllowRingView, smokeNotAllowRingView, smokeNotMuchRingView);
                smokeToolTip.setText("I tolerate smoking");
            }
        });
        smokeNotAllowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), smokeAllowRingView, smokeNotAllowRingView, smokeNotMuchRingView);
                smokeToolTip.setText("I don’t tolerate smoking");
            }
        });
        smokeNotMuchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViews(v.getId(), smokeAllowRingView, smokeNotAllowRingView, smokeNotMuchRingView);
                smokeToolTip.setText("I don’t encourage smoking much");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String updateChat = chatAllowRingView.getVisibility() == visible ? "1" : chatNotAllowRingView.getVisibility() == visible ? "3" : "2";
                final String updateMusic = musicAllowRingView.getVisibility() == visible ? "1" : musicNotAllowRingView.getVisibility() == visible ? "3" : "2";
                final String updateFood = foodAllowRingView.getVisibility() == visible ? "1" : foodNotAllowRingView.getVisibility() == visible ? "3" : "2";
                final String updateSmoke = smokeAllowRingView.getVisibility() == visible ? "1" : smokeNotAllowRingView.getVisibility() == visible ? "3" : "2";

                UserPreferences userPreferences = new UserPreferences(updateChat, updateMusic, updateSmoke, updateFood, "3");

                Utils.showProgress(true, progressBar, progressView);
                dialog.setCancelable(false);

                Call<UserDataDTO> call = Utils.getYatraShareAPI().updateUserPreferences(userGuide, userPreferences);
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
                        dialog.setCancelable(true);
                        android.util.Log.e("SUCCEESS RESPONSE RAW", response.raw() + "");
                        if (response.body() != null) {
                            if (response.body().Data.equalsIgnoreCase("Success")) {
                                Utils.showProgress(false, progressBar, progressView);
                                Utils.showToast(mContext, "Prefernces updated Successfully");

                                profile.Data.Chat = Integer.parseInt(updateChat);
                                profile.Data.Music = Integer.parseInt(updateMusic);
                                profile.Data.Food = Integer.parseInt(updateFood);
                                profile.Data.Smoking = Integer.parseInt(updateSmoke);
                                updateUserPreferences();

                                dialog.dismiss();
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
                        dialog.setCancelable(true);
                        android.util.Log.e(TAG, "FAILURE RESPONSE");
                        Utils.showToast(mContext, getString(R.string.tryagain));
                        Utils.showProgress(false, progressBar, progressView);
                    }
                });

            }
        });

        dialog.show();
    }


    int visible = View.VISIBLE;
    int invisible = View.INVISIBLE;

    private void changeViews(int id, View allowRingView, View notAllowRingView, View notMuchRingView) {
        switch (id) {
            case R.id.chatAllowImage:
            case R.id.musicAllowImage:
            case R.id.foodAllowImage:
            case R.id.smokeAllowImage:
            case 1:
                allowRingView.setVisibility(visible);
                notMuchRingView.setVisibility(invisible);
                notAllowRingView.setVisibility(invisible);
                break;
            case R.id.chatNotAllowImage:
            case R.id.musicNotAllowImage:
            case R.id.foodNotAllowImage:
            case R.id.smokeNotAllowImage:
            case 3:
                allowRingView.setVisibility(invisible);
                notMuchRingView.setVisibility(invisible);
                notAllowRingView.setVisibility(visible);
                break;
            case R.id.chatImage:
            case R.id.musicImage:
            case R.id.foodImage:
            case R.id.smokeImage:
            case 2:
                allowRingView.setVisibility(invisible);
                notMuchRingView.setVisibility(visible);
                notAllowRingView.setVisibility(invisible);
                break;
        }
    }
}
