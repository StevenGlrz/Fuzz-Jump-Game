package com.fuzzjump.api.friends;

import com.fuzzjump.api.friends.model.FriendRequest;
import com.fuzzjump.api.friends.model.FriendRequestResponse;
import com.fuzzjump.api.friends.model.FriendRetrieveResponse;
import com.fuzzjump.api.friends.model.GenericFriendRequest;
import com.fuzzjump.api.model.response.Response;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
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
    public Observable<FriendRetrieveResponse> retrieveFriendList() {
        return restService.retrieveFriendList();
    }

    @Override
    public Observable<FriendRequestResponse> sendFriendRequest(FriendRequest request) {
        return restService.sendFriendRequest(request);
    }

    @Override
    public Observable<FriendRequestResponse> acceptFriendRequest(GenericFriendRequest request) {
        return restService.acceptFriendRequest(request);
    }

    @Override
    public Observable<Response> deleteFriend(GenericFriendRequest request) {
        return restService.deleteFriend(request);
    }

    private interface FriendRestService {

        @GET("friends")
        Observable<FriendRetrieveResponse> retrieveFriendList();

        @POST("friends")
        Observable<FriendRequestResponse> sendFriendRequest(@Body FriendRequest request);

        @PUT("friends")
        Observable<FriendRequestResponse> acceptFriendRequest(@Body GenericFriendRequest request);

        @HTTP(method = "DELETE", path = "friends", hasBody = true)
        Observable<Response> deleteFriend(@Body GenericFriendRequest request);
    }

}
