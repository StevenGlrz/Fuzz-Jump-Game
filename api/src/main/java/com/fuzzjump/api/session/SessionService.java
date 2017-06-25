package com.fuzzjump.api.session;

import com.fuzzjump.api.TokenInterceptor;
import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.response.TokenResponse;
import com.fuzzjump.api.session.model.SessionResponse;
import com.fuzzjump.api.session.model.SessionVerifyRequest;
import com.fuzzjump.api.session.model.SessionVerifyResponse;
import com.fuzzjump.api.user.model.RegisterRequest;
import com.fuzzjump.api.user.model.RegisterResponse;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class SessionService implements ISessionService {

    private final SessionRestService restService;

    @Inject
    public SessionService(Retrofit retrofit) {
        this.restService = retrofit.create(SessionRestService.class);
    }

    @Override
    public Observable<SessionResponse> getSessionToken(String feature) {
        return restService.get(feature);
    }

    @Override
    public Observable<SessionVerifyResponse> verify(String userId, String feature, String token) {
        return restService.verify(new SessionVerifyRequest(userId, feature, token));
    }

    private interface SessionRestService {

        @GET("session")
        Observable<SessionResponse> get(@Query("feature") String feature);

        @POST("session")
        Observable<SessionVerifyResponse> verify(@Body SessionVerifyRequest request);

    }

}
