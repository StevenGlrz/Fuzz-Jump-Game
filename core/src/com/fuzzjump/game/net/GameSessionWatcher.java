package com.fuzzjump.game.net;

public interface GameSessionWatcher {
	
	void onConnect();

	void onDisconnect();
	
	void onTimeout();

	void authenticated();

	void onTransferred();

}
