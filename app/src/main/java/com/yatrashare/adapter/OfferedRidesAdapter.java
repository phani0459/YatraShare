package com.yatrashare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.OfferedRides;
import com.yatrashare.dtos.OfferedSubRides;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OfferedRidesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OfferedRides.OfferedRideData> mValues;
    ArrayList<OfferedSubRides.SubRideData> subRides;
    public int mTitle;
    SetOnItemClickListener setOnItemClickListener;
    private Context mContext;

    public OfferedRidesAdapter(Context mContext, ArrayList<OfferedRides.OfferedRideData> data, int mTitle, SetOnItemClickListener setOnItemClickListener) {
        mValues = data;
        this.mTitle = mTitle;
        this.setOnItemClickListener = setOnItemClickListener;
        this.mContext = mContext;
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

    public void addItem(OfferedRides.OfferedRideData offeredRideData) {
        mValues.add(offeredRideData);
        notifyItemInserted(mValues.size());
    }

    public OfferedRidesAdapter(Context mContext, ArrayList<OfferedSubRides.SubRideData> subRides, int mTitle, SetOnItemClickListener setOnItemClickListener, String todo) {
        this.subRides = subRides;
        this.mTitle = mTitle;
        this.setOnItemClickListener = setOnItemClickListener;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offeredride_item, parent, false);
            return new ViewHolder(view);
        } else {
            View footerLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, null);
            footerLayoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new MoreViewHolder(footerLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            if (mValues != null) {
                OfferedRides.OfferedRideData offeredRide = mValues.get(position);
                holder.rideDeparturePoint.setText(offeredRide.DeparturePoint);
                holder.rideAraivalPoint.setText(offeredRide.ArrivalPoint);
                holder.rideDateText.setText(offeredRide.DepartureDate);

                if (mTitle == TabsFragment.UPCOMING_OFFERED_RIDES) {
                    holder.deleteRide.setVisibility(View.VISIBLE);
                    if (offeredRide.PendingBooking) {
                        holder.editRide.setVisibility(View.INVISIBLE);
                        holder.pendingText.setVisibility(View.VISIBLE);
                    } else {
                        holder.editRide.setVisibility(View.VISIBLE);
                        holder.pendingText.setVisibility(View.GONE);
                    }
                } else {
                    holder.pendingText.setVisibility(View.GONE);
                    holder.editRide.setVisibility(View.INVISIBLE);
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
                holder.seatPrice.setText("" + Utils.getCurrency(mContext) + " " + subRideData.RoutePrice + " /Seat");
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

            holder.editRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnItemClickListener.onItemClick(3, position);
                }
            });
        }
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
        private TextView remainingSeats, bookedSeats, seatPrice, pendingText;
        private View remainingSeatsView, bookedSeatsView;
        private ImageView deleteRide, editRide;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            rideDeparturePoint = (TextView) view.findViewById(R.id.tv_rideDeparturePoint);
            rideAraivalPoint = (TextView) view.findViewById(R.id.tv_rideArrivalPoint);
            rideDateText = (TextView) view.findViewById(R.id.tv_rideDate);
            pendingText = (TextView) view.findViewById(R.id.tv_pendingStatus);

            remainingSeats = (TextView) view.findViewById(R.id.tv_remainingSeats);
            bookedSeats = (TextView) view.findViewById(R.id.tv_bookdSeats);
            seatPrice = (TextView) view.findViewById(R.id.tv_seatPrice);

            remainingSeatsView = view.findViewById(R.id.remainSeats_divider);
            deleteRide = (ImageView) view.findViewById(R.id.im_delete_ofrdRide);
            editRide = (ImageView) view.findViewById(R.id.im_edit_ofrdRide);
            bookedSeatsView = view.findViewById(R.id.bukdSeats_divider);
        }

    }

    public interface SetOnItemClickListener {
        void onItemClick(int clickedItem, int position);
    }
}
