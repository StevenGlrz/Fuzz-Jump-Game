package com.fuzzjump.api;

import com.fuzzjump.api.util.HttpLoggingInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * We shouldnt have to touch this.
 */
@Module
public class RetrofitModule {

    private final String apiUrl;

    public RetrofitModule(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Provides
    @Singleton
    Gson gson() {
        return new GsonBuilder().create();
    }


    @Provides
    @Singleton
    TokenInterceptor interceptor() {
        return new TokenInterceptor();
    }

    @Provides
    @Singleton
    OkHttpClient okHttpClient(TokenInterceptor interceptor) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(interceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit retrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .build();
    }

}
