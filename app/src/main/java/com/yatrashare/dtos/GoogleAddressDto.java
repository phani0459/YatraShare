package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 5/4/16.
 */
public class GoogleAddressDto implements Serializable {
    public ArrayList<AddressResults> results;
    public String status;

    public class AddressResults implements Serializable {
        public String formatted_address;
        public String place_id;
        public ArrayList<AddressComponents> address_components;
    }

    public class AddressComponents implements Serializable {
        public String long_name;
        public String short_name;
        public ArrayList<String> types;
    }
}
