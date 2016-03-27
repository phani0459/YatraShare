package com.yatrashare.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.dtos.GetUserBookings;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UserBookingsAdapter extends RecyclerView.Adapter<UserBookingsAdapter.ViewHolder> {

    private List<GetUserBookings.UserBookingData> mValues;
    public int mTitle;
    SetOnItemClickListener setOnItemClickListener;
    private Context mContext;

    public UserBookingsAdapter(Context mContext, ArrayList<GetUserBookings.UserBookingData> data, int mTitle, SetOnItemClickListener setOnItemClickListener) {
        mValues = data;
        this.mTitle = mTitle;
        this.setOnItemClickListener = setOnItemClickListener;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userbooking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GetUserBookings.UserBookingData userBooking = mValues.get(position);
        holder.bukdUserName.setText(userBooking.BookedUserName);
        holder.bukdUserEmail.setText(userBooking.BookedUserEmail);
        holder.bukdUserMobile.setText(userBooking.BookedUserMobileNo);
        holder.bukedSeats.setText("Booked Seats: " + userBooking.BookedSeats + "");
        if (userBooking.BookingStatus == 3) holder.bookedStatus.setText("Booked Status: " + "Rejected");
        else if (userBooking.BookingStatus == 2) holder.bookedStatus.setText("Booked Status: " + "Approved");
        else holder.bookedStatus.setVisibility(View.GONE);
        holder.bookedUserFB.setVisibility(View.GONE);

        String profilePic = userBooking.BookedUserProfilePic;

        if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
            Uri uri = Uri.parse(profilePic);
            holder.userImageDrawee.setImageURI(uri);
        } else {
            holder.userImageDrawee.setImageURI(Constants.getDefaultPicURI());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(-1, position);
            }
        });

        holder.menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu(v, position, userBooking.BookingStatus);
            }
        });
    }

    public void showPopUpMenu(View image, final int position, int status) {
        PopupMenu popup = new PopupMenu(mContext, image);
        popup.getMenuInflater().inflate(R.menu.booking_menu, popup.getMenu());
        popup.getMenu().getItem(0).setVisible(false);
        popup.getMenu().getItem(1).setVisible(false);
        if (mTitle == TabsFragment.PAST_OFFERED_RIDES) {
            popup.getMenu().getItem(2).setVisible(true);
        } else {
            if (status != 2 && status != 3) {
                popup.getMenu().getItem(0).setVisible(true);
                popup.getMenu().getItem(1).setVisible(true);
            }
            popup.getMenu().getItem(2).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                setOnItemClickListener.onItemClick(item.getItemId(), position);
                return true;
            }
        });

        popup.show();
    }

    public GetUserBookings.UserBookingData getItem(int pos) {
        if (mValues != null) {
            return mValues.get(pos);
        }
        return null;
    }

    public void remove(int position) {
        if (mValues != null) {
            mValues.remove(position);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView bukdUserName, bukdUserEmail, bukdUserMobile;
        private TextView bukedSeats, bookedStatus, bookedUserFB;
        private SimpleDraweeView userImageDrawee;
        private ImageView menuImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            bukdUserName = (TextView) view.findViewById(R.id.tv_userName);
            bukdUserEmail = (TextView) view.findViewById(R.id.tv_userEmail);
            bukdUserMobile = (TextView) view.findViewById(R.id.tv_userMobile);

            bookedStatus = (TextView) view.findViewById(R.id.tv_bukdStatus);
            bukedSeats = (TextView) view.findViewById(R.id.tv_bookedSeats);
            bookedUserFB = (TextView) view.findViewById(R.id.tv_userfbProfile);
            menuImage = (ImageView) view.findViewById(R.id.im_overflow_icon);
            userImageDrawee = (SimpleDraweeView) view.findViewById(R.id.image_drawee_user);
        }

    }

    public interface SetOnItemClickListener {
        void onItemClick(int clickedItem, int position);
    }
}
