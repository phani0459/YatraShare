package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 5/4/16.
 */
public class GooglePlacesDto implements Serializable {
    public ArrayList<PlaceResults> predictions;
    public String status;

    public class PlaceResults implements Serializable {
        public String description;
        public String place_id;
        public String id;
    }

}
