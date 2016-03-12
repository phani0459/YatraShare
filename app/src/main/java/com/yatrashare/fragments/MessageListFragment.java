package com.yatrashare.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.MessagesRecyclerViewAdapter;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class MessageListFragment extends Fragment implements MessagesRecyclerViewAdapter.SetOnItemClickListener{

    private static final String TAG = MessageListFragment.class.getSimpleName();
    private int mColumnCount = 1;
    @Bind(R.id.messagesRecyclerView)
    public RecyclerView messagesRecycleView;
    private Context mContext;
    private String userGuid;
    @Bind(R.id.msgsProgress)
    public ProgressBar mProgressView;
    @Bind(R.id.messagesProgressBGView)
    public View mProgressBGView;
    private MessagesRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        // Set the adapter
        messagesRecycleView.setLayoutManager(new LinearLayoutManager(mContext));

        getMessages();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)mContext).setTitle("Messages");
        ((HomeActivity)mContext).setCurrentScreen(HomeActivity.MESSAGES_SCREEN);
        ((HomeActivity)mContext).prepareMenu();
    }

    public void getMessages() {
        Utils.showProgress(true, mProgressView, mProgressBGView);
        Call<MessagesList> call = Utils.getYatraShareAPI().userInboxMessages(userGuid, "1", "5");
        //asynchronous call
        call.enqueue(new Callback<MessagesList>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<MessagesList> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null) {
                    MessagesList messagesList = response.body();
                    loadMessages(messagesList);
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

    private void loadMessages(MessagesList messagesList) {
        if (messagesList != null && messagesList.Data != null && messagesList.Data.size()  > 0) {
            adapter = new MessagesRecyclerViewAdapter(mContext, messagesList.Data, this);
            messagesRecycleView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(boolean isView, int position) {
        if (adapter != null) {
            MessagesList.MessagesListData messageData = adapter.getItem(position);
            if (isView) {
                ((HomeActivity)mContext).loadScreen(HomeActivity.MESSAGE_DETAILS_SCREEN, false, messageData, Constants.MESSAGE_SCREEN_NAME);
            }
        }
    }

    public void refreshMessagesList() {
        getMessages();
    }
}
