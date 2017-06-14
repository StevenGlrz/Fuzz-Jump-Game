package com.fuzzjump.game.game.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.ScreenHandler;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.Textures;
import com.fuzzjump.game.game.screens.attachment.MenuScreenAttachment;
import com.fuzzjump.game.game.screens.attachment.WaitingScreenAttachment;
import com.fuzzjump.game.game.ui.CharacterSelectionUI;
import com.fuzzjump.game.game.ui.MenuUI;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.net.GameSession;
import com.fuzzjump.game.net.GameSessionWatcher;
import com.fuzzjump.game.net.requests.PurchaseUnlockableRequest;
import com.fuzzjump.game.net.requests.WebRequestCallback;
import com.google.gson.JsonObject;

import static com.fuzzjump.game.game.StageIds.MenuUI.*;

public class MenuScreen extends StageScreen<MenuScreenAttachment> {

    private MenuUI menuUI;
    private boolean authenticate, showError;

    private GameSession session;

    public MenuScreen(FuzzJump game, Textures textures, ScreenHandler handler) {
        super(game, textures, handler);
    }

    @Override
    public void load(MenuScreenAttachment attachment) {
        authenticate = attachment.isAuthenticate();
        showError = attachment.isError();
    }

    @Override
    public void showScreen() {
        if (showError) {
            Dialog dialog = ui().actor(Dialog.class, StageIds.MenuUI.PROGRESS_DIALOG);
            Image image = ui().actor(Image.class, StageIds.MenuUI.PROGRESS_IMAGE);
            Label status = ui().actor(Label.class, StageIds.MenuUI.PROGRESS_LABEL);
            Button closeButton = ui().actor(Button.class, StageIds.MenuUI.CLOSE_BUTTON);
            image.setVisible(false);
            status.setVisible(true);
            closeButton.setVisible(true);
            status.setText("Lost connection to server");
            dialog.show(game.getStage());
        }
        game.getProfile().raiseEvent();
    }

    @Override
    public StageUI createUI() {
        return menuUI = new MenuUI();
    }

    @Override
    public void clicked(int id, Actor actor) {
        final Dialog progressDialog = ui().actor(Dialog.class, StageIds.MenuUI.PROGRESS_DIALOG);
        final Image image = ui().actor(Image.class, StageIds.MenuUI.PROGRESS_IMAGE);
        final Label status = ui().actor(Label.class, StageIds.MenuUI.PROGRESS_LABEL);
        final Button closeButton = ui().actor(Button.class, StageIds.MenuUI.CLOSE_BUTTON);
        switch (id) {
            case FB_BUTTON:
                game.getContext().openURL("https://www.facebook.com/kerpowgames");
                break;
            case GOOGLE_BUTTON:
                break;
            case TWITTER_BUTTON:
                game.getContext().openURL("https://twitter.com/kerpowgames");
                break;
            case WWW_BUTTON:
                game.getContext().openURL("http://www.kerpowgames.com");
                break;
            case PUBLIC_GAME:
                image.setVisible(true);
                status.setVisible(true);
                closeButton.setVisible(false);
                status.setText("Connecting");
                progressDialog.show(game.getStage());
                session = new GameSession(GameSession.MATCHMAKING_IP, GameSession.MATCHMAKING_PORT, new GameSessionWatcher() {
                    @Override
                    public void onConnect() {
                        status.setText("Authenticating");
                    }

                    @Override
                    public void authenticated() {
                        progressDialog.hide();
                        screenHandler.showScreen(WaitingScreen.class, new WaitingScreenAttachment(session));
                    }

                    @Override
                    public void onTransferred() {
                        status.setText("Transferring");
                    }

                    @Override
                    public void onDisconnect() {
                        image.setVisible(false);
                        status.setText("Error connecting");
                        closeButton.setVisible(true);
                    }

                    @Override
                    public void onTimeout() {
                        onDisconnect();
                    }

                });
                session.connect();
                break;
            case OK_BUTTON:
                ui().actor(Dialog.class, StageIds.MenuUI.CONNECTING_DIALOG).hide();
                break;
            case LOGOUT_BUTTON:
                getGame().getProfile().clear();
                screenHandler.showScreen(LoginScreen.class, null);
                break;
            case CREDITS_BUTTON:
                game.getContext().openURL("http://www.kerpowgames.com/games/fuzzjump/credits");
                break;
            case SOUND_TOGGLE:
                game.toggleSound();
                break;
            case SELECT_BUY_BUTTON:
                final CharacterSelectionUI.UnlockableEntry buyEntry = ui().context(StageIds.MenuUI.SELECTED_UNLOCK);
                if (buyEntry.getUnlockableDefinition().getCost() > getGame().getProfile().getCoins())
                    return;
                final Dialog buyingDialog = ui().actor(Dialog.class, BUYING_DIALOG);
                buyingDialog.hide();
                image.setVisible(true);
                status.setVisible(true);
                closeButton.setVisible(false);
                progressDialog.show(game.getStage());
                status.setText("Purchasing...");
                System.out.println(buyEntry.getUnlockableDefinition().getId());
                PurchaseUnlockableRequest request = new PurchaseUnlockableRequest(game.getProfile(), buyEntry.getUnlockableDefinition().getId());
                request.connect(new WebRequestCallback() {
                    @Override
                    public void onResponse(JsonObject response) {
                        try {
                            progressDialog.hide();
                            if (response.get("Response").getAsInt() == 1) {
                                Unlockable unlockable = game.getProfile().getAppearance().createUnlockable(response.getAsJsonObject("Payload"));
                                if (unlockable != null) {
                                    buyEntry.setSelected(true);
                                    buyEntry.setUnlockable(unlockable);
                                    game.getProfile().setCoins(game.getProfile().getCoins() - buyEntry.getUnlockableDefinition().getCost());
                                    game.getProfile().save();
                                    progressDialog.hide();
                                } else {
                                    //SHOW ERROR
                                    progressDialog.hide();
                                }
                            } else {
                                //SHOW ERROR
                                progressDialog.hide();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

}
