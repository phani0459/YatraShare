package com.yatrashare.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.ChatDetailsAdapter;
import com.yatrashare.dtos.MessageDetails;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.dtos.RideDetails;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by pkandagatla on 6/3/16.
 */
public class MessageDetailsFragment extends Fragment {
    private static final String TAG = MessageDetailsFragment.class.getSimpleName();
    @Bind(R.id.list_view_messages)
    public ListView messagesDetailsListView;
    private Context mContext;
    private String userGuid;
    @Bind(R.id.msgsDetailsProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.msgsDetailsProgressBGView)
    public View mProgressBGView;
    @Bind(R.id.btnSend)
    public Button btnSend;
    @Bind(R.id.inputMsg)
    public EditText inputMsgEdit;
    private MessagesList.MessagesListData messagesListData;
    private MessageDetails messagesDetailsList;
    private ChatDetailsAdapter adapter;
    private SharedPreferences mSharedPreferences;
    private RideDetails.RideDetailData rideDetailData;
    private String screenName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_details, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        screenName = getArguments().getString(Constants.ORIGIN_SCREEN_KEY, "");

        if (screenName.equalsIgnoreCase(Constants.BOOK_a_RIDE_SCREEN_NAME)) {
            rideDetailData = (RideDetails.RideDetailData) getArguments().getSerializable("Message");
        } else {
            messagesListData = (MessagesList.MessagesListData) getArguments().getSerializable("Message");
        }

        getConversationList();

        return view;
    }

    @OnClick(R.id.btnSend)
    public void sendMessage() {
        final String msg = inputMsgEdit.getText().toString();
        Utils.hideSoftKeyboard(inputMsgEdit);
        if (msg != null && !msg.isEmpty()) {
            Call<UserDataDTO> call = null;
            if (messagesDetailsList != null && messagesDetailsList.Data != null && messagesDetailsList.Data.size() > 0) {
                call = Utils.getYatraShareAPI().SendReplyMessage(userGuid, messagesDetailsList.Data.get(0).MessageGuid, msg);
            } else if (rideDetailData != null && rideDetailData.PossibleRideGuid != null && rideDetailData.UserGuid != null) {
                call = Utils.getYatraShareAPI().sendMessage(userGuid, rideDetailData.UserGuid, rideDetailData.PossibleRideGuid, msg);
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
                        ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
                    }
                });
            }
        } else {
            Utils.showToast(mContext, "Enter Message");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)mContext).setTitle("Message Details");
        if (messagesDetailsList != null) {
        }
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.MESSAGE_DETAILS_SCREEN);
        ((HomeActivity)mContext).prepareMenu();
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
                    ((HomeActivity) mContext).showSnackBar(getString(R.string.tryagain));
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
