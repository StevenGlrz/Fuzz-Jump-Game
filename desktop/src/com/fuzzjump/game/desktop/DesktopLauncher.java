package com.fuzzjump.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.platform.PlatformModule;

import java.util.concurrent.Executors;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		FuzzJump fuzzJump = new FuzzJump(new FuzzJumpParams(), new PlatformModule(new DesktopGraphicsLoader(Executors.newFixedThreadPool(2), "")));
		new LwjglApplication(fuzzJump, config);
	}
}
