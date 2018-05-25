package com.fuzzjump.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.FuzzJumpParams;
import com.fuzzjump.game.platform.PlatformModule;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 560;
		config.height = 940;

		//config.addIcon("./icon.png", Files.FileType.Local);

		FuzzJumpParams params = new FuzzJumpParams(arg[0], arg[1], Integer.parseInt(arg[2]));
		FuzzJump fuzzJump = new FuzzJump(params, new PlatformModule(new DesktopGraphicsLoader("")));
		new LwjglApplication(fuzzJump, config);
	}
}
