package com.yatrashare.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.adapter.MessagesRecyclerViewAdapter;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class MessageListFragment extends Fragment implements MessagesRecyclerViewAdapter.SetOnItemClickListener {

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
    @Bind(R.id.emptyRidesLayout)
    public ScrollView emptyRidesLayout;
    private LinearLayoutManager mLayoutManager;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        TextView emptyRidesHeading = (TextView) view.findViewById(R.id.emptyRidesHeading);
        TextView emptyRidesSubHeading = (TextView) view.findViewById(R.id.emptyRidesSubHeading);
        ImageView emptyRidesImage = (ImageView) view.findViewById(R.id.emptyRideImage);

        emptyRidesHeading.setText("No Messages yet.");
        emptyRidesSubHeading.setText("Once you get messages, you'll see them here.");
        emptyRidesImage.setBackgroundResource(R.drawable.no_messages);

        SharedPreferences mSharedPreferences = Utils.getSharedPrefs(mContext);
        userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        mLayoutManager = new LinearLayoutManager(mContext);
        messagesRecycleView.setLayoutManager(mLayoutManager);

        if (Utils.isInternetAvailable(mContext)) {
            Utils.showProgress(true, mProgressView, mProgressBGView);
            getMessages();
        } else {
            messagesRecycleView.setVisibility(View.GONE);
            emptyRidesLayout.setVisibility(View.VISIBLE);
        }

        messagesRecycleView.addOnScrollListener(mRecyclerViewOnScrollListener);
        return view;
    }

    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;
    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mIsLoading && !mIsLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= Constants.PAGE_SIZE) {
                    loadMoreItems();
                }
            }
        }
    };

    private void loadMoreItems() {
        mIsLoading = true;
        if (adapter != null) adapter.addLoading();
        currentPage = currentPage + 1;
        getMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) mContext).setTitle("Messages");
        ((HomeActivity) mContext).setCurrentScreen(HomeActivity.MESSAGES_SCREEN);
        ((HomeActivity) mContext).prepareMenu();
    }

    int currentPage = 1;

    public void getMessages() {
        Call<MessagesList> call = Utils.getYatraShareAPI(mContext).userInboxMessages(userGuid, currentPage + "", "" + Constants.PAGE_SIZE);
        //asynchronous call
        call.enqueue(new Callback<MessagesList>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
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

    private void loadMessages(MessagesList messagesList) {
        if (messagesList != null && messagesList.Data != null && messagesList.Data.size() > 0) {

            emptyRidesLayout.setVisibility(View.GONE);
            messagesRecycleView.setVisibility(View.VISIBLE);

            try {
                Collections.reverse(messagesList.Data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mIsLoading = false;
            if (messagesList.Data.size() < Constants.PAGE_SIZE) mIsLastPage = true;
            if (adapter != null) {
                for (int i = 0; i < messagesList.Data.size(); i++) {
                    adapter.addItem(messagesList.Data.get(i));
                }
            } else {
                adapter = new MessagesRecyclerViewAdapter(mContext, messagesList.Data, this);
                messagesRecycleView.setAdapter(adapter);
            }
            adapter.removeLoading();
        } else {
            if (adapter != null && adapter.getItemCount() > 0) {
                mIsLastPage = true;
            } else {
                messagesRecycleView.setVisibility(View.GONE);
                emptyRidesLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void deleteMessage(MessagesList.MessagesListData messageData, final int position) {
        Call<UserDataDTO> call = Utils.getYatraShareAPI(mContext).deleteMessage(userGuid, messageData.MessageGuid);
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
                if (response.body() != null && response.body().Data.equalsIgnoreCase("SUCCESS")) {
                    try {
                        adapter.removeItem(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (adapter.getItemCount() == 0) {
                        emptyRidesLayout.setVisibility(View.VISIBLE);
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

    public void areYouSureDialog(final MessagesList.MessagesListData messagesListData, final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (Utils.isInternetAvailable(mContext)) {
                            Utils.showProgress(true, mProgressView, mProgressBGView);
                            deleteMessage(messagesListData, position);
                        }
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onItemClick(boolean isView, final int position) {
        if (adapter != null) {

            MessagesList.MessagesListData messageData = adapter.getItem(position);
            if (isView) {
                ((HomeActivity) mContext).loadScreen(HomeActivity.MESSAGE_DETAILS_SCREEN, false, messageData, Constants.MESSAGE_SCREEN_NAME);
            } else {
                areYouSureDialog(messageData, position);
            }
        }
    }

    public void refreshMessagesList() {
        if (Utils.isInternetAvailable(mContext)) {
            adapter = null;
            currentPage = 1;
            Utils.showProgress(true, mProgressView, mProgressBGView);
            getMessages();
        }
    }
}
