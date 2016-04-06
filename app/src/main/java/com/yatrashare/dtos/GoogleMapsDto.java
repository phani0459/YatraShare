package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 5/4/16.
 */
public class GoogleMapsDto implements Serializable {
    public ArrayList<Routes> routes;
    public String status;

    public class Routes implements Serializable {
        public String copyrights;
        public ArrayList<Leg> legs;
    }

    public class Leg implements Serializable {
        public TextValue distance;
        public TextValue duration;
    }

    public class TextValue implements Serializable {
        public String text;
        public String value;
    }
}
