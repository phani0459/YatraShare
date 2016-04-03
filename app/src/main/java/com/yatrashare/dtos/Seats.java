package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pkandagatla on 2/4/16.
 */
public class Seats implements Serializable {
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<String> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;
}
