package com.yatrashare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.DummyContent.DummyItem;
import com.yatrashare.dtos.Rating;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class RatingsRecyclerViewAdapter extends RecyclerView.Adapter<RatingsRecyclerViewAdapter.ViewHolder> {

    private final List<Rating.RatingData> mValues;

    public RatingsRecyclerViewAdapter(List<Rating.RatingData> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mFeedBack.setText(mValues.get(position).Feedback);
        holder.mRatingValue.setText(mValues.get(position).Stars);
        holder.mRatingUserName.setText(mValues.get(position).ProviderName);
        holder.mRatingDate.setText(mValues.get(position).RatingGivenDate);

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
        public final TextView mFeedBack;
        public final TextView mRatingValue;
        public final TextView mRatingUserName;
        public final TextView mRatingDate;
        public Rating.RatingData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFeedBack = (TextView) view.findViewById(R.id.feedBack);
            mRatingValue = (TextView) view.findViewById(R.id.ratingValue);
            mRatingUserName = (TextView) view.findViewById(R.id.ratingUserName);
            mRatingDate = (TextView) view.findViewById(R.id.ratingTime);
        }
    }
}
