package com.fuzzjump.game.service;

import com.fuzzjump.game.service.user.IUserService;
import com.fuzzjump.game.service.user.UserService;

import dagger.Binds;
import dagger.Module;

/**
 * This is where all the rest services will go
 */
@Module(
        includes = {
                RetrofitModule.class
        }
)
public abstract class ServiceModule {

    @Binds
    public abstract IUserService bindUserService(UserService impl);

}
