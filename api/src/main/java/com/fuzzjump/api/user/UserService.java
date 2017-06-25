package com.fuzzjump.api.user;

import com.fuzzjump.api.TokenInterceptor;
import com.fuzzjump.api.model.response.TokenResponse;
import com.fuzzjump.api.user.model.RegisterResponse;
import com.fuzzjump.api.user.model.RegisterRequest;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * We really dont need all our services to implement an interface because i dont plan on writing tests...
 */
public class UserService implements IUserService {

    private final UserRestService restService;
    private final TokenInterceptor tokenInterceptor;

    @Inject
    public UserService(Retrofit retrofit, TokenInterceptor tokenInterceptor) {
        this.restService = retrofit.create(UserRestService.class);
        this.tokenInterceptor = tokenInterceptor;
    }

    @Override
    public Observable<RegisterResponse> register(String username) {
        return restService.register(new RegisterRequest(username));
    }

    @Override
    public Observable<TokenResponse> retrieveToken(String username, String password) {
        return restService.retrieveToken(username, password, "password").doOnNext(e -> {
            // Set the token for our interceptor
            tokenInterceptor.setToken(e.getAccessToken());
        });
    }

    private interface UserRestService {

        @POST("connect/register/")
        Observable<RegisterResponse> register(@Body RegisterRequest request);

        @FormUrlEncoded
        @POST("connect/token/")
        Observable<TokenResponse> retrieveToken(@Field("username") String username, @Field("password") String password, @Field("grant_type") String grantType);

    }

}
