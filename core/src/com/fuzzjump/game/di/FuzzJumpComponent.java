package com.fuzzjump.game.di;

import com.fuzzjump.api.ServiceModule;
import com.fuzzjump.game.game.di.FuzzJumpGameComponent;
import com.fuzzjump.game.game.di.FuzzJumpGameModule;
import com.fuzzjump.game.platform.PlatformModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                PlatformModule.class,
                ServiceModule.class,
                FuzzJumpModule.class
        }
)
public interface FuzzJumpComponent {

    FuzzJumpGameComponent gameComponent(FuzzJumpGameModule gameModule);

}
