package com.yatrashare.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pkandagatla on 9/4/16.
 */
public class OfferRide {
    @SerializedName("rideInfo")
    RideInfo rideInfo;

    public OfferRide(RideInfo rideInfo) {
        this.rideInfo = rideInfo;
    }
}
