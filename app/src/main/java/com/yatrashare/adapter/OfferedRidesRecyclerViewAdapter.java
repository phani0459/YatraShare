package com.yatrashare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.OfferedRides;
import com.yatrashare.dtos.OfferedSubRides;
import com.yatrashare.fragments.TabsFragment;

import java.util.ArrayList;
import java.util.List;

public class OfferedRidesRecyclerViewAdapter extends RecyclerView.Adapter<OfferedRidesRecyclerViewAdapter.ViewHolder> {

    private List<OfferedRides.OfferedRideData> mValues;
    ArrayList<OfferedSubRides.SubRideData> subRides;
    public int mTitle;
    SetOnItemClickListener setOnItemClickListener;
    private Context mContext;

    public OfferedRidesRecyclerViewAdapter(Context mContext, ArrayList<OfferedRides.OfferedRideData> data, int mTitle, SetOnItemClickListener setOnItemClickListener) {
        mValues = data;
        this.mTitle = mTitle;
        this.setOnItemClickListener = setOnItemClickListener;
        this.mContext = mContext;
    }

    public OfferedRidesRecyclerViewAdapter(Context mContext, ArrayList<OfferedSubRides.SubRideData> subRides, int mTitle, SetOnItemClickListener setOnItemClickListener, String todo) {
        this.subRides = subRides;
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
            OfferedRides.OfferedRideData offeredRide = mValues.get(position);
            holder.rideDeparturePoint.setText(offeredRide.DeparturePoint);
            holder.rideAraivalPoint.setText(offeredRide.ArrivalPoint);
            holder.rideDateText.setText(offeredRide.DepartureDate);
            if (mTitle == TabsFragment.UPCOMING_OFFERED_RIDES) {
                holder.deleteRide.setVisibility(View.VISIBLE);
            }
        } else {
            OfferedSubRides.SubRideData subRideData = subRides.get(position);
            holder.rideDeparturePoint.setText(subRideData.DeparturePoint);
            holder.rideAraivalPoint.setText(subRideData.ArrivalPoint);
            holder.rideDateText.setText(subRideData.DepartureTime);

            holder.remainingSeats.setVisibility(View.VISIBLE);
            holder.bookedSeats.setVisibility(View.VISIBLE);
            holder.seatPrice.setVisibility(View.VISIBLE);

            holder.bookedSeatsView.setVisibility(View.VISIBLE);
            holder.remainingSeatsView.setVisibility(View.VISIBLE);

            holder.remainingSeats.setText(subRideData.AvailableSeats + " Seat(s) left");
            holder.bookedSeats.setText("Booked Seats: " + subRideData.BookedSeats);
            holder.seatPrice.setText("" + mContext.getResources().getString(R.string.Rs) + " " + subRideData.RoutePrice + " /Seat");
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(1, position);
            }
        });

        holder.deleteRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(2, position);
            }
        });
    }

    public Object getItem(int pos) {
        if (mValues != null) {
            return mValues.get(pos);
        } else if (subRides != null) {
            return subRides.get(pos);
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
        } else if (subRides != null) {
            return subRides.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView rideDeparturePoint, rideDateText, rideAraivalPoint;
        private TextView remainingSeats, bookedSeats, seatPrice;
        private View remainingSeatsView, bookedSeatsView;
        private ImageView deleteRide;

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
            deleteRide = (ImageView) view.findViewById(R.id.im_delete_ofrdRide);
            bookedSeatsView = view.findViewById(R.id.bukdSeats_divider);
        }

    }

    public interface SetOnItemClickListener {
        void onItemClick(int clickedItem, int position);
    }
}
