package com.yatrashare.utils;

/**
 * Created by pkandagatla on 6/3/16.
 */
public class APIService {
    private static APIService ourInstance = new APIService();

    public static APIService getInstance() {
        return ourInstance;
    }

    private APIService() {
    }
}
