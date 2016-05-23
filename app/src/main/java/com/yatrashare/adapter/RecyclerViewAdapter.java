package com.yatrashare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.dtos.GooglePlacesDto;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SetOnItemClickListener onItemClickListener;
    private ArrayList<GooglePlacesDto.PlaceResults> placeResults;

    public RecyclerViewAdapter(ArrayList<GooglePlacesDto.PlaceResults> placeResults, SetOnItemClickListener onItemClickListener) {
        this.placeResults = placeResults;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new RecyclerViewHolders(layoutView);
    }

    public GooglePlacesDto.PlaceResults getExhibitItem(int position) {
        return placeResults.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        bindItemViewHolder(viewHolder, position);
    }

    private void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerViewHolders holder = (RecyclerViewHolders) viewHolder;
        GooglePlacesDto.PlaceResults exhibit = getExhibitItem(position);
        if (exhibit != null) {
            holder.searchItemDescription.setText(exhibit.description);
        }
    }

    @Override
    public int getItemCount() {
        return placeResults.size();
    }

    class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView searchItemDescription;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            searchItemDescription = (TextView) itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getExhibitItem(getAdapterPosition()));
        }
    }

    public interface SetOnItemClickListener {
        public void onItemClick(GooglePlacesDto.PlaceResults data);
    }
}
