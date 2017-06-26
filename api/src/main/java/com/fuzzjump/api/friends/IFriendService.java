package com.fuzzjump.api.friends;


import com.fuzzjump.api.friends.model.GenericFriendRequest;
import com.fuzzjump.api.friends.model.FriendRequest;
import com.fuzzjump.api.friends.model.FriendRequestResponse;
import com.fuzzjump.api.friends.model.FriendRetrieveResponse;
import com.fuzzjump.api.model.response.Response;

import io.reactivex.Observable;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public interface IFriendService {

    Observable<FriendRetrieveResponse> retrieveFriendList();

    Observable<FriendRequestResponse> sendFriendRequest(FriendRequest request);

    Observable<FriendRequestResponse> acceptFriendRequest(GenericFriendRequest request);

    Observable<Response> deleteFriend(GenericFriendRequest request);
}
