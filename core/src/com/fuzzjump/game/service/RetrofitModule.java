package com.fuzzjump.game.service;

import com.badlogic.gdx.Gdx;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * We shouldnt have to touch this.
 */
@Module
public class RetrofitModule {

    @Provides
    @Singleton
    Cache okHttpCache() {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(Gdx.files.getLocalStoragePath()), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }


    @Provides
    @Singleton
    TokenInterceptor authenticator() {
        return new TokenInterceptor();
    }

    @Provides
    @Singleton
    OkHttpClient okHttpClient(Cache cache, TokenInterceptor authenticator) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(authenticator)
                //TODO interceptor
                .build();
        return client;
    }

    @Provides
    @Singleton
    Retrofit retrofit(Gson gson, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://www.fuzzjumpapi.com")//not even used atm.
                .client(okHttpClient)
                .build();
        return retrofit;
    }

}
