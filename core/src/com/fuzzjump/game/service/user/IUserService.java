package com.fuzzjump.game.service.user;


import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.service.model.Response;
import com.fuzzjump.game.service.model.TokenResponse;

import io.reactivex.Observable;

public interface IUserService {

    Observable<Response> login(String username);

    Observable<TokenResponse> retrieveToken(Profile profile, String password);
}
