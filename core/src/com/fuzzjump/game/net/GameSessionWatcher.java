package com.fuzzjump.game.net;

import org.json.JSONArray;

import java.nio.ByteBuffer;

public interface GameSessionWatcher {
	
	void onConnect();

	void onDisconnect();
	
	void onTimeout();

	void authenticated();

	void onTransferred();

}
