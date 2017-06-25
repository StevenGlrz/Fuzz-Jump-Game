package com.fuzzjump.api.friends;

import com.fuzzjump.api.friends.model.AcceptFriendRequest;
import com.fuzzjump.api.friends.model.FriendRequest;
import com.fuzzjump.api.model.Response;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public class FriendService implements IFriendService {

    private final FriendRestService restService;

    @Inject
    public FriendService(Retrofit retrofit) {
        this.restService = retrofit.create(FriendRestService.class);
    }

    @Override
    public Observable<Response> retrieveFriendList() {
        return restService.retrieveFriendList();
    }

    @Override
    public Observable<Response> sendFriendRequest(FriendRequest request) {
        return restService.sendFriendRequest(request);
    }

    @Override
    public Observable<Response> acceptFriendRequest(AcceptFriendRequest request) {
        return restService.acceptFriendRequest(request);
    }

    private interface FriendRestService {

        @GET("friends")
        Observable<Response> retrieveFriendList();

        @POST("friends")
        Observable<Response> sendFriendRequest(@Body FriendRequest request);

        @PUT("friends")
        Observable<Response> acceptFriendRequest(@Body AcceptFriendRequest request);

    }

}
