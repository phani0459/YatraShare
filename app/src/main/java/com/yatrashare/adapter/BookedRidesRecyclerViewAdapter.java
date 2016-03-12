package com.yatrashare.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.dtos.BookedRides;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class BookedRidesRecyclerViewAdapter extends RecyclerView.Adapter<BookedRidesRecyclerViewAdapter.ViewHolder> {

    private final List<BookedRides.BookedData> mValues;

    public BookedRidesRecyclerViewAdapter(ArrayList<BookedRides.BookedData> data) {
        mValues = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookedride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BookedRides.BookedData bookedRide = mValues.get(position);

        holder.ownerNameText.setText(bookedRide.OwnerName);
        holder.rideTimeText.setText(bookedRide.RideTime);
        holder.rideDetailsText.setText(bookedRide.Ride);
        String rideStatus = bookedRide.RideStatus != null ? bookedRide.RideStatus : "";
        String bookingStatus = bookedRide.BookingStatus != null ? bookedRide.BookingStatus : "";
        holder.rideStatusText.setText(Html.fromHtml("<font color=\"#303F9F\">Ride Status: </font>" + rideStatus) );
        holder.bookingStatusText.setText(Html.fromHtml("<font color=\"#303F9F\">Booking Status: </font>" + bookingStatus));

        String profilePic = bookedRide.OwnerPicture;
        if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
            holder.simpleDraweeView.setVisibility(View.VISIBLE);
            holder.userImageView.setVisibility(View.GONE);
            Uri uri = Uri.parse(profilePic);
            holder.simpleDraweeView.setImageURI(uri);
        } else {
            holder.simpleDraweeView.setVisibility(View.GONE);
            holder.userImageView.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView ownerNameText, rideTimeText, bookingStatusText, rideStatusText, rideDetailsText;
        SimpleDraweeView simpleDraweeView;
        ImageView userImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ownerNameText = (TextView) view.findViewById(R.id.tv_ownerName);
            rideTimeText = (TextView) view.findViewById(R.id.tv_RideTime);
            rideDetailsText = (TextView) view.findViewById(R.id.tv_rideDetails);
            bookingStatusText = (TextView) view.findViewById(R.id.tv_bookingStatus);
            rideStatusText = (TextView) view.findViewById(R.id.tv_ridestatus);
            simpleDraweeView  = (SimpleDraweeView) view.findViewById(R.id.im_drawee_owner);
            userImageView  = (ImageView) view.findViewById(R.id.im_owner);

        }

    }
}
