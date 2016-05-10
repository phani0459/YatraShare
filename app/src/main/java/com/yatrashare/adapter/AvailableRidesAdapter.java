package com.yatrashare.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;

/**
 * Created by PHANI KUMAR on 10-01-2016.
 */
public class AvailableRidesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<SearchRides.SearchData> dataSearchArray;
    private OnItemClickListener mListener;

    public AvailableRidesAdapter(Context mContext, ArrayList<SearchRides.SearchData> arrayList, OnItemClickListener mListener) {
        this.mContext = mContext;
        this.dataSearchArray = arrayList;
        this.mListener = mListener;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return dataSearchArray.size();
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
    }

    private boolean mIsLoadingFooterAdded = false;

    private boolean isPositionLoading(int position) {
        return (position == getItemCount() - 1 && mIsLoadingFooterAdded);
    }

    public void addItem(SearchRides.SearchData searchedData) {
        dataSearchArray.add(searchedData);
        notifyItemInserted(dataSearchArray.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionLoading(position)) return Constants.TYPE_LOADING;

        return Constants.TYPE_ITEM;
    }

    class MoreViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;

        public MoreViewHolder(View view) {
            super(view);
            mProgressBar = (ProgressBar) view.findViewById(R.id.recycle_footer_progress);
        }
    }

    public SearchRides.SearchData getItem(int position) {
        return dataSearchArray.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.TYPE_ITEM) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View convertView = inflater.inflate(R.layout.search_ride_item, null);
            MyViewHolder holder = new MyViewHolder(convertView);
            return holder;
        } else {
            View footerLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, null);
            footerLayoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new MoreViewHolder(footerLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            String string = dataSearchArray.get(position).RideDate;

            holder.rideDayText.setText(dataSearchArray.get(position).RideDate);
            holder.rideFareText.setText("" + Utils.getCurrency(mContext) + " " + dataSearchArray.get(position).RoutePrice + " /Seat");
            holder.rideFromText.setText(dataSearchArray.get(position).DeparturePoint);
            holder.rideToText.setText(dataSearchArray.get(position).ArrivalPoint);
            holder.rideVehicleText.setText(dataSearchArray.get(position).VehicleModel);
            holder.availableSeatText.setText(dataSearchArray.get(position).RemainingSeats + " Seat(s)");
            holder.userNameText.setText(dataSearchArray.get(position).UserName);

            String comfortRating = dataSearchArray.get(position).ComfortRating;
            String profilePic = dataSearchArray.get(position).ProfilePicture;

            float comfortRatingFloat = 0.0f;
            if (comfortRating != null && !comfortRating.isEmpty()) {
                try {
                    comfortRatingFloat = Float.parseFloat(comfortRating);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            holder.ratingBar.setRating(comfortRatingFloat);

            if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
                Uri uri = Uri.parse(profilePic);
                holder.simpleDraweeView.setImageURI(uri);
            } else {
                holder.simpleDraweeView.setImageURI(Constants.getDefaultPicURI());
            }


            holder.rideDayText.setSelected(true);
            holder.rideFromText.setSelected(true);
            holder.rideToText.setSelected(true);
            holder.rideVehicleText.setSelected(true);
            holder.userNameText.setSelected(true);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView userNameText, rideFareText, availableSeatText, rideFromText, rideToText, rideDayText, rideTimeText, rideVehicleText;
        private RatingBar ratingBar;
        SimpleDraweeView simpleDraweeView;

        public MyViewHolder(View itemView) {
            super(itemView);

            userNameText = (TextView) itemView.findViewById(R.id.user_name_text);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.userImage_drawee);
            userNameText = (TextView) itemView.findViewById(R.id.user_name_text);
            rideFareText = (TextView) itemView.findViewById(R.id.ride_fare_text);
            rideFromText = (TextView) itemView.findViewById(R.id.ride_from_text);
            rideToText = (TextView) itemView.findViewById(R.id.ride_to_text);
            rideDayText = (TextView) itemView.findViewById(R.id.ride_time_text);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            rideVehicleText = (TextView) itemView.findViewById(R.id.ride_car_text);
            availableSeatText = (TextView) itemView.findViewById(R.id.ride_available_text);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
}
