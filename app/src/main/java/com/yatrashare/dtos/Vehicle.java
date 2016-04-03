package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 1/4/16.
 */
public class Vehicle implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<VehicleData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class VehicleData implements Serializable {
        public String Seats;
        public String Comfort;
        public String Color;
        public String UserGuid;
        public String UserVehicleId;
        public String ComfortRating;
        public String VehiclePicture;
        public String VehicleModelId;
        public String VehicleType;
        public String BrandName;
        public String ModelName;
        public String Brand;
    }

}
