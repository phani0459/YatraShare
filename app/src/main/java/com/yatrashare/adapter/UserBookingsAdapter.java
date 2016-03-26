package com.yatrashare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.GetUserBookings;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offeredride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mValues != null) {
            GetUserBookings.UserBookingData userBooking = mValues.get(position);
            holder.rideDeparturePoint.setText(userBooking.BookedUserEmail);
            holder.rideAraivalPoint.setText(userBooking.BookedUserMobileNo);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(1, position);
            }
        });
    }

    public Object getItem(int pos) {
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
        private TextView rideDeparturePoint, rideDateText, rideAraivalPoint;
        private TextView remainingSeats, bookedSeats, seatPrice;
        private View remainingSeatsView, bookedSeatsView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            rideDeparturePoint = (TextView) view.findViewById(R.id.tv_rideDeparturePoint);
            rideAraivalPoint = (TextView) view.findViewById(R.id.tv_rideArrivalPoint);
            rideDateText = (TextView) view.findViewById(R.id.tv_rideDate);

            remainingSeats = (TextView) view.findViewById(R.id.tv_remainingSeats);
            bookedSeats = (TextView) view.findViewById(R.id.tv_bookdSeats);
            seatPrice = (TextView) view.findViewById(R.id.tv_seatPrice);

            remainingSeatsView = view.findViewById(R.id.remainSeats_divider);
            bookedSeatsView = view.findViewById(R.id.bukdSeats_divider);
        }

    }

    public interface SetOnItemClickListener {
        void onItemClick(int clickedItem, int position);
    }
}
