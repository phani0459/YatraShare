package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 17/4/16.
 */
public class Countries implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<CountryData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;
}
