package com.fuzzjump.api.user;


import com.fuzzjump.api.model.Response;
import com.fuzzjump.api.model.TokenResponse;

import io.reactivex.Observable;

public interface IUserService {

    Observable<Response> login(String username);

    Observable<TokenResponse> retrieveToken(String password);
}
