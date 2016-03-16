package com.yatrashare.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yatrashare.R;
import com.yatrashare.dtos.Rating;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.utils.Constants;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class RatingsRecyclerViewAdapter extends RecyclerView.Adapter<RatingsRecyclerViewAdapter.ViewHolder> {

    private final List<Rating.RatingData> mValues;
    private int title;

    public RatingsRecyclerViewAdapter(List<Rating.RatingData> items, int title) {
        mValues = items;
        this.title = title;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Rating.RatingData ratingData = mValues.get(position);
        holder.mFeedBack.setText(ratingData.Feedback);

        try {
            holder.mRatingValue.setRating(Float.parseFloat(ratingData.Stars));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String profilePic;
        if (title == TabsFragment.RECEIVED_RATINGS) {
            profilePic = ratingData.ProviderProfilePic;
            holder.mRatingUserName.setText("By " + ratingData.ProviderName);
        } else {
            profilePic = ratingData.ReceiverProfilePic;
            holder.mRatingUserName.setText("to " + ratingData.ReceiverName);
        }
        holder.mRatingDate.setText(ratingData.RatingGivenDate);

        if (profilePic != null && !profilePic.isEmpty() && !profilePic.startsWith("/")) {
            Uri uri = Uri.parse(profilePic);
            holder.simpleDraweeView.setImageURI(uri);
        } else {
            holder.simpleDraweeView.setImageURI(Constants.getDefaultPicURI());
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
        public final TextView mFeedBack;
        public final RatingBar mRatingValue;
        public final TextView mRatingUserName;
        public final TextView mRatingDate;
        private final SimpleDraweeView simpleDraweeView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFeedBack = (TextView) view.findViewById(R.id.feedBack);
            mRatingValue = (RatingBar) view.findViewById(R.id.ratingBar);
            mRatingUserName = (TextView) view.findViewById(R.id.ratingBy);
            mRatingDate = (TextView) view.findViewById(R.id.ratingTime);
            simpleDraweeView  = (SimpleDraweeView) view.findViewById(R.id.im_drawee_rating_provider);
        }
    }
}
