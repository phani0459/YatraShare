package com.yatrashare.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.OfferedRides;

import java.util.ArrayList;
import java.util.List;

public class OfferedRidesRecyclerViewAdapter extends RecyclerView.Adapter<OfferedRidesRecyclerViewAdapter.ViewHolder> {

    private final List<OfferedRides.OfferedRideData> mValues;
    public final int mTitle;
    SetOnItemClickListener setOnItemClickListener;

    public OfferedRidesRecyclerViewAdapter(ArrayList<OfferedRides.OfferedRideData> data, int mTitle, SetOnItemClickListener setOnItemClickListener) {
        mValues = data;
        this.mTitle = mTitle;
        this.setOnItemClickListener = setOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offeredride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OfferedRides.OfferedRideData offeredRide = mValues.get(position);
        holder.rideDeparturePoint.setText(offeredRide.DeparturePoint);
        holder.rideAraivalPoint.setText(offeredRide.ArrivalPoint);
        holder.rideDateText.setText(offeredRide.DepartureDate);
    }

    public OfferedRides.OfferedRideData getItem(int pos) {
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
        public final View mView;
        private TextView rideDeparturePoint, rideDateText, rideAraivalPoint;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            rideDeparturePoint = (TextView) view.findViewById(R.id.tv_rideDeparturePoint);
            rideAraivalPoint = (TextView) view.findViewById(R.id.tv_rideArrivalPoint);
            rideDateText = (TextView) view.findViewById(R.id.tv_rideDate);
        }

    }

    public interface SetOnItemClickListener {
        void onItemClick(int clickedItem, int position);
    }
}
