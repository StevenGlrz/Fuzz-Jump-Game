package com.fuzzjump.api;


import com.fuzzjump.api.user.IUserService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ServiceModule.class
        }
)
interface ApiComponent {

        Api provideApi();

}
