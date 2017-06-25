package com.fuzzjump.game;

import com.badlogic.gdx.backends.iosmoe.IOSApplication;
import com.badlogic.gdx.backends.iosmoe.IOSApplicationConfiguration;
import org.moe.natj.general.Pointer;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.platform.PlatformModule;

import apple.uikit.c.UIKit;

public class IOSMoeLauncher extends IOSApplication.Delegate {

    protected IOSMoeLauncher(Pointer peer) {
        super(peer);
    }

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useAccelerometer = false;
        //TODO
        FuzzJumpParams params = new FuzzJumpParams("", "", 9);
        FuzzJump fuzzJump = new FuzzJump(params, new PlatformModule(null));
        return new IOSApplication(fuzzJump, config);
    }

    public static void main(String[] argv) {
        UIKit.UIApplicationMain(0, null, null, IOSMoeLauncher.class.getName());
    }
}
