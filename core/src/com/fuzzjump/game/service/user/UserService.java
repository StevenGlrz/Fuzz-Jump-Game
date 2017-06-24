package com.fuzzjump.game.service.user;

import com.badlogic.gdx.Preferences;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.service.TokenInterceptor;
import com.fuzzjump.game.service.model.Response;
import com.fuzzjump.game.service.model.TokenResponse;
import com.fuzzjump.game.service.user.model.RegisterRequest;

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
    private final Preferences preferences;

    @Inject
    public UserService(Retrofit retrofit, TokenInterceptor tokenInterceptor, Preferences preferences) {
        this.restService = retrofit.create(UserRestService.class);
        this.tokenInterceptor = tokenInterceptor;
        this.preferences = preferences;
    }

    @Override
    public Observable<Response> login(String username) {
        return restService.login(new RegisterRequest(username));
    }

    @Override
    public Observable<TokenResponse> retrieveToken(Profile profile, String password) {
        return wrapForIO(restService.retrieveToken(profile.getApiName(), password, "password"))
                .doOnNext(e -> {
                    // Set the token for our interceptor
                    tokenInterceptor.setToken(e.getAccessToken());

                    // Store preferences and flush. Profile data should already be set in preferences.
                    preferences.putString(Assets.USER_TOKEN, e.getAccessToken());
                    preferences.flush();
                });
    }

    private <T> Observable<T> wrapForIO(Observable<T> observable) {
        return observable.doOnError(Throwable::printStackTrace);
    }

    private interface UserRestService {

        @POST("connect/register/")
        Observable<Response> login(@Body RegisterRequest request);

        @FormUrlEncoded
        @POST("connect/token/")
        Observable<TokenResponse> retrieveToken(@Field("username") String username, @Field("password") String password, @Field("grant_type") String grantType);
    }

}
