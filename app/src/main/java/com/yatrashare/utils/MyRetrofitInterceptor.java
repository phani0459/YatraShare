package com.yatrashare.utils;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MyRetrofitInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = "";

        Request newRequest = originalRequest.newBuilder()
                .header("X-Authorization", token)
                .build();

        return chain.proceed(newRequest);
    }
}