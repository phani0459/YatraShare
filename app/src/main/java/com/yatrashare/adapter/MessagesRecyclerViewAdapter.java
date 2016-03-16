package com.yatrashare.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.List;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {

    private final List<MessagesList.MessagesListData> mMessages;
    private Context mContext;
    private SetOnItemClickListener onItemClickListener;

    public MessagesRecyclerViewAdapter(Context mContext, List<MessagesList.MessagesListData> items, SetOnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        mMessages = items;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_message_item, parent, false);
        return new ViewHolder(view);
    }

    public MessagesList.MessagesListData getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.msgSentTime.setText(mMessages.get(position).MessageSentTime);
        holder.senderName.setText(mMessages.get(position).SenderName);
        holder.msgDetails.setText(mMessages.get(position).Message);
        String senderProfilePic = mMessages.get(position).SenderProfilePic;

        if (senderProfilePic != null && !senderProfilePic.isEmpty() && !senderProfilePic.startsWith("/")) {
            Uri uri = Uri.parse(senderProfilePic);
            holder.senderDrawee.setImageURI(uri);
        } else {
            holder.senderDrawee.setImageURI(Constants.getDefaultPicURI());
        }

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(false, position);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(true, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public SimpleDraweeView senderDrawee;
        public TextView senderName;
        public TextView msgSentTime;
        public TextView msgDetails;
        public ImageView deleteImage;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            senderDrawee = (SimpleDraweeView) view.findViewById(R.id.image_drawee_sender);
            msgSentTime = (TextView) view.findViewById(R.id.tv_msg_time);
            senderName = (TextView) view.findViewById(R.id.tv_senderName);
            msgDetails = (TextView) view.findViewById(R.id.tv_msgDetails);
            deleteImage = (ImageView) view.findViewById(R.id.im_delete_msg);
        }
    }

    public interface SetOnItemClickListener {
        public void onItemClick(boolean isView, int position);
    }
}
