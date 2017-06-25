package com.fuzzjump.api;

import com.fuzzjump.api.session.ISessionService;
import com.fuzzjump.api.user.IUserService;

import javax.inject.Inject;

/**
 * Created by Steveadoo on 6/24/2017.
 */

public final class Api {

    private final IUserService userService;
    private final ISessionService sessionService;

    @Inject
    Api(IUserService userService, ISessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public IUserService getUserService() {
        return userService;
    }

    public ISessionService getSessionService() {
        return sessionService;
    }

    public static class Builder {

        private String cachePath;
        private String url;

        public Builder cachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Api build() {
            ApiComponent component = DaggerApiComponent
                    .builder()
                    .retrofitModule(new RetrofitModule(url, cachePath))
                    .build();
            return component.provideApi();
        }

    }

}
