package com.fuzzjump.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.platform.PlatformModule;

import java.io.File;
import java.util.concurrent.Executors;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 560;
		config.height = 940;

		config.addIcon("./icon.png", Files.FileType.Local);

		FuzzJumpParams params = new FuzzJumpParams(System.getenv("FUZZ_API"), arg[0], Integer.parseInt(arg[1]));
		FuzzJump fuzzJump = new FuzzJump(params, new PlatformModule(new DesktopGraphicsLoader(Executors.newFixedThreadPool(2), "")));
		new LwjglApplication(fuzzJump, config);
	}
}
