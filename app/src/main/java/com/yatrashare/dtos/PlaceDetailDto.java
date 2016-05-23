package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 5/4/16.
 */
public class PlaceDetailDto implements Serializable {
    public PlaceDetail result;
    public String status;

    public class PlaceDetail implements Serializable {
        public Geometry geometry;
        public String formatted_address;
        public String id;
        public ArrayList<AddressComponent> address_components;
    }

    public class Geometry implements Serializable {
        public Location location;
        public ViewPort viewport;
    }

    public class Location implements Serializable {
        public double lat;
        public double lng;
    }

    public class ViewPort implements Serializable {
        public Location northeast;
        public Location southwest;
    }

    public class AddressComponent implements Serializable {
        public String long_name;
        public String short_name;
        public ArrayList<String> types;
    }

}
