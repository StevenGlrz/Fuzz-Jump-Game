package com.fuzzjump.api.friends;


import com.fuzzjump.api.friends.model.AcceptFriendRequest;
import com.fuzzjump.api.friends.model.FriendRequest;
import com.fuzzjump.api.model.Response;

import io.reactivex.Observable;

/**
 * Created by Steven Galarza on 6/24/2017.
 */
public interface IFriendService {

    Observable<Response> retrieveFriendList();

    Observable<Response> sendFriendRequest(FriendRequest request);

    Observable<Response> acceptFriendRequest(AcceptFriendRequest request);
}
