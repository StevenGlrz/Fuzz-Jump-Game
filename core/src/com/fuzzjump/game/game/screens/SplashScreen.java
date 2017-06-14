package com.fuzzjump.game.game.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.ScreenHandler;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.Textures;
import com.fuzzjump.game.game.screens.attachment.GameScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.MenuScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.ScreenAttachment;
import com.fuzzjump.game.game.ui.SplashUI;
import com.fuzzjump.game.net.requests.AuthenticationWebRequest;
import com.fuzzjump.game.net.requests.WebRequest;
import com.fuzzjump.game.net.requests.WebRequestCallback;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by stephen on 8/22/2015.
 */
public class SplashScreen extends StageScreen<ScreenAttachment> {

    public static boolean PASS_THROUGH = false;

    public SplashScreen(FuzzJump game, Textures textures, ScreenHandler handler) {
        super(game, textures, handler);
    }

    @Override
    public void load(ScreenAttachment attachment) {
    }

    @Override
    public void showScreen() {
    }

    @Override
    public void rendered() {
        game.init();
        if (game.getProfile().valid()) {
            AuthenticationWebRequest request = new AuthenticationWebRequest(game.getProfile().getProfileId(), game.getProfile().getSessionKey());
            request.connect(new WebRequestCallback() {
                @Override
                public void onResponse(JsonObject response) {
                    System.out.println(response.toString());
                    try {
                        if (response.get(WebRequest.RESPONSE_KEY).getAsInt() == WebRequest.SUCCESS) {
                            game.getProfile().load(response.getAsJsonObject(WebRequest.PAYLOAD_KEY));
                            game.getProfile().save();
                            if (PASS_THROUGH) {
                                screenHandler.showScreen(GameScreen.class, new GameScreenAttachment(0, 120302303223L));
                            } else {
                                screenHandler.showScreen(MenuScreen.class, new MenuScreenAttachment(false, false));
                            }
                        } else {
                            game.getProfile().clear();
                            screenHandler.showScreen(LoginScreen.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            screenHandler.showScreen(LoginScreen.class, null);
        }
    }

    @Override
    public void clicked(int id, Actor actor) {

    }

    @Override
    public StageUI createUI() {
        return new SplashUI();
    }

}
