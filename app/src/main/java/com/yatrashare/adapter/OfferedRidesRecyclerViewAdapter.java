package com.yatrashare.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.dtos.BookedRides;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class OfferedRidesRecyclerViewAdapter extends RecyclerView.Adapter<OfferedRidesRecyclerViewAdapter.ViewHolder> {

    private final List<BookedRides.BookedData> mValues;
    private final int mTitle;
    SetOnItemClickListener setOnItemClickListener;
    public static final int cancelRide = 0;
    public static final int getOwnerDetailsbySMS = 1;
    public static final int deleteRide = 2;
    public static final int viewRide = 3;

    public OfferedRidesRecyclerViewAdapter(ArrayList<BookedRides.BookedData> data, int mTitle, SetOnItemClickListener setOnItemClickListener) {
        mValues = data;
        this.mTitle = mTitle;
        this.setOnItemClickListener = setOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookedride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        BookedRides.BookedData bookedRide = mValues.get(position);

        holder.ownerNameText.setText(bookedRide.OwnerName);
        holder.rideTimeText.setText(bookedRide.RideTime);
        holder.rideDetailsText.setText(bookedRide.Ride);
        String rideStatus = bookedRide.RideStatus != null ? bookedRide.RideStatus : "";
        String bookingStatus = bookedRide.BookingStatus != null ? bookedRide.BookingStatus : "";
        holder.rideStatusText.setText(Html.fromHtml("<font color=\"#303F9F\">Ride Status: </font>" + rideStatus));
        holder.bookingStatusText.setText(Html.fromHtml("<font color=\"#303F9F\">Booking Status: </font>" + bookingStatus));

        String profilePic = bookedRide.OwnerPicture;
        if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
            Uri uri = Uri.parse(profilePic);
            holder.simpleDraweeView.setImageURI(uri);
        } else {
            holder.simpleDraweeView.setImageURI(Constants.getDefaultPicURI());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitle == TabsFragment.UPCOMING_BOOKED_RIDES) {
                    setOnItemClickListener.onItemClick(viewRide, position);
                }
            }
        });

        if (mTitle == TabsFragment.PAST_BOOKED_RIDES) {
            holder.cancelSeatBtn.setVisibility(View.GONE);
            holder.getDetailsSMSBtn.setText("Delete");
            holder.getDetailsSMSBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delete, 0, 0, 0);
        }

        if (bookingStatus.equalsIgnoreCase("Cancelled")) {
            holder.cancelSeatBtn.setVisibility(View.GONE);
            holder.getDetailsSMSBtn.setVisibility(View.GONE);
            holder.btnsAboveView.setVisibility(View.GONE);
        }

        holder.cancelSeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(cancelRide, position);
            }
        });

        holder.getDetailsSMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitle == TabsFragment.PAST_BOOKED_RIDES) {
                    setOnItemClickListener.onItemClick(deleteRide, position);
                } else {
                    setOnItemClickListener.onItemClick(getOwnerDetailsbySMS, position);
                }
            }
        });
    }

    public BookedRides.BookedData getItem(int pos) {
        return mValues.get(pos);
    }

    public void remove(int position) {
        mValues.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView, btnsAboveView;
        private TextView ownerNameText, rideTimeText, bookingStatusText, rideStatusText, rideDetailsText;
        SimpleDraweeView simpleDraweeView;
        Button cancelSeatBtn, getDetailsSMSBtn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btnsAboveView = (View) view.findViewById(R.id.btnsAboveView);
            ownerNameText = (TextView) view.findViewById(R.id.tv_ownerName);
            rideTimeText = (TextView) view.findViewById(R.id.tv_RideTime);
            rideDetailsText = (TextView) view.findViewById(R.id.tv_rideDetails);
            bookingStatusText = (TextView) view.findViewById(R.id.tv_bookingStatus);
            rideStatusText = (TextView) view.findViewById(R.id.tv_ridestatus);
            cancelSeatBtn = (Button) view.findViewById(R.id.btnCancelSeat);
            getDetailsSMSBtn = (Button) view.findViewById(R.id.btnSendSMS);
            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.im_drawee_owner);
        }

    }

    public interface SetOnItemClickListener {
        void onItemClick(int clickedItem, int position);
    }
}
