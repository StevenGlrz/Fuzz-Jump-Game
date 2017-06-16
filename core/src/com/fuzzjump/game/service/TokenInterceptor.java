package com.fuzzjump.game.service;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by steve on 6/15/2017.
 */
public class TokenInterceptor implements Interceptor {

    private String token;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (token == null) {
            return chain.proceed(chain.request());
        }
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Authorization", "Bearer " + token);
        return chain.proceed(builder.build());
    }

    public void setToken(String token) {
        this.token = token;
    }
}
