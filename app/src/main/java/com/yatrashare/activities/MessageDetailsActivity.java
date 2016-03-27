package com.yatrashare.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.adapter.ChatDetailsAdapter;
import com.yatrashare.dtos.GetUserBookings;
import com.yatrashare.dtos.MessageDetails;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.dtos.RideDetails;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * Created by pkandagatla on 6/3/16.
 */
public class MessageDetailsActivity extends AppCompatActivity {
    private static final String TAG = MessageDetailsActivity.class.getSimpleName();
    @Bind(R.id.list_view_messages)
    public ListView messagesDetailsListView;
    private Context mContext;
    private String userGuid;
    @Bind(R.id.msgsDetailsProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.msgsDetailsProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.inputMsg)
    public EditText inputMsgEdit;
    private MessagesList.MessagesListData messagesListData;
    private MessageDetails messagesDetailsList;
    private ChatDetailsAdapter adapter;
    private RideDetails.RideDetailData rideDetailData;
    private String screenName;
    GetUserBookings.UserBookingData userBookingData;
    private String PossibleRideGuid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        ButterKnife.bind(this);
        mContext = this;

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        screenName = getIntent().getExtras().getString(Constants.ORIGIN_SCREEN_KEY, "");

        if (screenName.equalsIgnoreCase(Constants.BOOK_a_RIDE_SCREEN_NAME) || screenName.equalsIgnoreCase(Constants.RIDE_CONFIRM_SCREEN_NAME)) {
            rideDetailData = (RideDetails.RideDetailData) getIntent().getExtras().getSerializable("Message");
        } else if (screenName.equalsIgnoreCase("User Bookings")) {
            userBookingData = (GetUserBookings.UserBookingData) getIntent().getExtras().getSerializable("Message");
            PossibleRideGuid = getIntent().getExtras().getString("PossibleRideGuid");
        } else {
            messagesListData = (MessagesList.MessagesListData) getIntent().getExtras().getSerializable("Message");
        }

        adapter = new ChatDetailsAdapter(mContext, new ArrayList<MessageDetails.MessageDetailData>());
        messagesDetailsListView.setAdapter(adapter);

        getConversationList();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Message Details");
        if (messagesListData != null) {
            getSupportActionBar().setTitle(messagesListData.Route);
        } else if (rideDetailData != null) {
            getSupportActionBar().setTitle("" + rideDetailData.UserName);
        } else if (userBookingData != null) {
            getSupportActionBar().setTitle("" + userBookingData.BookedUserName);
        }
    }

    @OnClick(R.id.btnSend)
    public void sendMessage() {
        final String msg = inputMsgEdit.getText().toString();
        Utils.hideSoftKeyboard(inputMsgEdit);
        if (!msg.isEmpty()) {
            Call<UserDataDTO> call = null;
            if (messagesDetailsList != null && messagesDetailsList.Data != null && messagesDetailsList.Data.size() > 0) {
                call = Utils.getYatraShareAPI().SendReplyMessage(userGuid, messagesDetailsList.Data.get(0).MessageGuid, msg);
            } else if (rideDetailData != null && rideDetailData.PossibleRideGuid != null && rideDetailData.UserGuid != null) {
                call = Utils.getYatraShareAPI().sendMessage(userGuid, rideDetailData.UserGuid, rideDetailData.PossibleRideGuid, msg);
            } else if (userBookingData != null) {
                call = Utils.getYatraShareAPI().sendMessage(userGuid, userBookingData.BookedUserGuid, PossibleRideGuid, msg);
            }
            if (call != null) {
                Utils.showProgress(true, mProgressView, mProgressBGView);
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
                            if (response.body().Data.equalsIgnoreCase("") || response.body().Data.equalsIgnoreCase("Success")) {
                                inputMsgEdit.setText("");
                                MessageDetails.MessageDetailData messageDetailData = new MessageDetails().new MessageDetailData();
                                messageDetailData.Message = msg;
                                messageDetailData.TypeOfMessage = "Sent";
                                messageDetailData.Name = "Me";
                                adapter.addMessage(messageDetailData);
                            } else {
                                Utils.showToast(mContext, getResources().getString(R.string.tryagain));
                            }
                        } else {
                            Utils.showToast(mContext, getResources().getString(R.string.tryagain));
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
                        showSnackBar(getString(R.string.tryagain));
                    }
                });
            }
        } else {
            Utils.showToast(mContext, "Enter Message");
        }
    }

    public void showSnackBar(String msg) {
        Snackbar.make(messagesDetailsListView, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    public void getConversationList() {
        if (messagesListData != null) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            Call<MessageDetails> call = Utils.getYatraShareAPI().getMessageConversation(userGuid, messagesListData.MessageGuid);
            //asynchronous call
            call.enqueue(new Callback<MessageDetails>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param response
                 * @param retrofit
                 */
                @Override
                public void onResponse(retrofit.Response<MessageDetails> response, Retrofit retrofit) {
                    android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                    if (response.body() != null) {
                        messagesDetailsList = response.body();
                        loadMessages(messagesDetailsList);
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
                    showSnackBar(getString(R.string.tryagain));
                }
            });
        }
    }

    private void loadMessages(MessageDetails messagesList) {
        if (messagesList != null && messagesList.Data != null && messagesList.Data.size() > 0) {
            adapter = new ChatDetailsAdapter(mContext, messagesList.Data);
            messagesDetailsListView.setAdapter(adapter);
        }
    }

    public void refreshMessageDetails() {
        getConversationList();
    }
}
