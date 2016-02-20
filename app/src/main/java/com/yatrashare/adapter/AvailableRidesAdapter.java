package com.yatrashare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.dtos.SearchRides;

import java.util.ArrayList;

/**
 * Created by Krishna on 10-01-2016.
 */
public class AvailableRidesAdapter extends RecyclerView.Adapter<AvailableRidesAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<SearchRides.SearchData> dataSearchArray;

    public AvailableRidesAdapter(Context mContext, ArrayList<SearchRides.SearchData> arrayList){
        this.mContext = mContext;
        this.dataSearchArray = arrayList;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return dataSearchArray.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.available_ride_item,null);
        MyViewHolder holder = new MyViewHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String string = dataSearchArray.get(position).RideDate;
//        String string1 = string.substring(string.lastIndexOf(",",0));
//        String string2 = string.substring(string.lastIndexOf("-"));

            holder.rideDayText.setText(dataSearchArray.get(position).RideDate);
            holder.rideFareText.setText("" + mContext.getResources().getString(R.string.Rs) + " " + dataSearchArray.get(position).RoutePrice);
            holder.rideFromText.setText(dataSearchArray.get(position).DepartureCity);
            holder.rideToText.setText(dataSearchArray.get(position).ArrivalCity);
            holder.rideVehicleText.setText(dataSearchArray.get(position).VehicleModel);
            holder.availableSeatText.setText(dataSearchArray.get(position).RemainingSeats);
            holder.userNameText.setText(dataSearchArray.get(position).UserName);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameText, rideFareText, availableSeatText, rideFromText, rideToText, rideDayText, rideTimeText, rideVehicleText;

        public MyViewHolder(View itemView) {
            super(itemView);

            userNameText = (TextView) itemView.findViewById(R.id.user_name_text);
            rideFareText = (TextView) itemView.findViewById(R.id.ride_fare_text);
            rideFromText = (TextView) itemView.findViewById(R.id.ride_from_text);
            rideToText = (TextView) itemView.findViewById(R.id.ride_to_text);
            rideDayText  = (TextView) itemView.findViewById(R.id.ride_day_text);
            rideVehicleText = (TextView) itemView.findViewById(R.id.ride_car_text);
            availableSeatText = (TextView) itemView.findViewById(R.id.ride_available_text);
        }
    }
}
