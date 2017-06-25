package com.fuzzjump.api.user;

import com.fuzzjump.api.model.response.TokenResponse;
import com.fuzzjump.api.user.model.RegisterResponse;

import io.reactivex.Observable;

public interface IUserService {

    Observable<RegisterResponse> register(String username);

    Observable<TokenResponse> retrieveToken(String username, String password);
}
