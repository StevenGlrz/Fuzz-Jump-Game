package com.fuzzjump.game.service.user;

import com.fuzzjump.game.service.TokenInterceptor;
import com.fuzzjump.game.service.user.model.LoginResponse;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
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
    public Observable<LoginResponse> login(String username, String password) {
        return restService.login(username, password)
                .doOnNext(response -> {
                   if (response.isSuccess()) {

                       //TODO set the token
                   }
                });
    }


    //TODO think about this.
    public interface UserRestService {

        @POST
        Observable<LoginResponse> login(String username, String password);

    }

}
