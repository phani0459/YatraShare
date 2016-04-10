package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pkandagatla on 10/4/16.
 */
public class RegisterVehicle {

    @SerializedName("vehicleInfo")
    VehicleInfo vehicleInfo;

    public RegisterVehicle() {
    }

    public RegisterVehicle(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public class VehicleInfo {
        @SerializedName("VehicleType")
        String mVehicleType;

        @SerializedName("Seats")
        String mSeats;

        @SerializedName("VehicleModelId")
        String mVehicleModelId;

        @SerializedName("Color")
        String mColor;

        @SerializedName("Comfort")
        String mComfort;


        public VehicleInfo(String vehicleType, String seats, String vehicleModelId, String color, String comfort) {
            this.mVehicleType = vehicleType;
            this.mSeats = seats;
            this.mVehicleModelId = vehicleModelId;
            this.mColor = color;
            this.mComfort = comfort;
        }
    }

}
