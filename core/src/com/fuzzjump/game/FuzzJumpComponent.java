package com.fuzzjump.game;

import com.fuzzjump.game.game.FuzzJumpGameComponent;
import com.fuzzjump.game.game.FuzzJumpGameModule;
import com.fuzzjump.game.platform.PlatformModule;
import com.fuzzjump.game.service.ServiceModule;

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
