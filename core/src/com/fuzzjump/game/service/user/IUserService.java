package com.fuzzjump.game.service.user;


import com.fuzzjump.game.service.user.model.LoginResponse;

import io.reactivex.Observable;

public interface IUserService {

    Observable<LoginResponse> login(String username, String password);

}
