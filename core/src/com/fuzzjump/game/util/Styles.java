package com.fuzzjump.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.fuzzjump.game.game.StageUI;

public class Styles {

    private static final String BUTTON_UP = "ui-button";
    private static final String BUTTON_DOWN = "ui-button-p";
    private static final String FB_SQ_BUTTON_UP = "ui-facebook-sq-btn";
    private static final String FB_SQ_BUTTON_DOWN = "ui-facebook-sq-btn-p";
    private static final String GPLUS_SQ_BUTTON_UP = "ui-gplus-sq-btn";
    private static final String GPLUS_SQ_BUTTON_DOWN = "ui-gplus-sq-btn-p";
    private static final String PLUS_SQ_BUTTON_UP = "ui-plus-sq-btn";
    private static final String PLUS_SQ_BUTTON_DOWN = "ui-plus-sq-btn-p";
    private static final String EMAIL_SQ_BUTTON_UP = "ui-email-sq-btn";
    private static final String EMAIL_SQ_BUTTON_DOWN = "ui-email-sq-btn-p";
    private static final String X_SQ_BUTTON_UP = "ui-x-sq-btn";
    private static final String X_SQ_BUTTON_DOWN = "ui-x-sq-btn-p";
    private static final String PLAY_SQ_BUTTON_UP = "ui-play-sq-btn";
    private static final String PLAY_SQ_BUTTON_DOWN = "ui-play-sq-btn-p";
    private static final String BUTTON_ARROW_LEFT = "ui-arrow-left";
    private static final String BUTTON_ARROW_RIGHT = "ui-arrow-right";
    private static final String BUTTON_ARROW_LEFT_DOWN = "ui-arrow-left-p";
    private static final String BUTTON_ARROW_RIGHT_DOWN = "ui-arrow-right-p";
    private static final String BUTTON_HELP = "ui-help";
    private static final String BUTTON_HELP_DOWN = "ui-help-p";
    private static final String BUTTON_LOGIN = "ui-login";
    private static final String BUTTON_LOGIN_DOWN = "ui-login-p";

    private static final String GPLUS = "ui-gplus-btn";
    private static final String GPLUS_DOWN = "ui-gplus-btn-p";
    private static final String FB = "ui-facebook-btn";
    private static final String FB_DOWN = "ui-facebook-btn-p";
    private static final String TWITTER = "ui-twitter-button";
    private static final String TWITTER_DOWN = "ui-twitter-button-p";
    private static final String EMAIL = "ui-email-button";
    private static final String EMAIL_DOWN = "ui-email-button-p";
    private static final String SETTINGS = "ui-settings";
    private static final String SETTINGS_DOWN = "ui-settings-p";
    private static final String PROFILE = "ui-profile";
    private static final String PROFILE_DOWN = "ui-profile-p";
    private static final String PLAY = "ui-login";
    private static final String PLAY_DOWN = "ui-login-p";
    private static final String FRIENDS = "ui-friends";
    private static final String FRIENDS_DOWN = "ui-friends-p";
    private static final String CHECK = "ui-check";
    private static final String CHECK_DOWN = "ui-check-p";
    private static final String CLOSE = "ui-close";
    private static final String CLOSE_DOWN = "ui-close-p";
    private static final String STORE = "ui-store";
    private static final String STORE_DOWN = "ui-store-p";
    private static final String ADD = "ui-add-friend";
    private static final String ADD_DOWN = "ui-add-friend-p";

    private static final String LEADERBOARD = "ui-leaderboard";
    private static final String LEADERBOARD_DOWN = "ui-leaderboard-p";

    private static final String CHECKBOX_ON = "ui-checkbox-checked";
    private static final String CHECKBOX_OFF = "ui-checkbox-unchecked";

    private static final String PANEL = "panel";
    private static final String BANNER = "ui-banner";

    private static final String INPUT = "ui-inputfield";
    private static final String INPUT_CURSOR = "cursor";
    private static final String INPUT_SELECTION = "selection";
    private static final String INPUT_ERROR = "ui-close";

    private static final String FUZZ_YELLOW = "fuzzyellow";
    private static final String WHITE = "white";
    private static final String RED = "red";

    private static final String DEFAULT_FONT = "default-font";
    private static final String PROFILE_FONT = "profile-font";
    private static final String INGAME_FONT = "ingame-font";

    public static TextButton.TextButtonStyle createDefaultTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(BUTTON_UP),
                                              ui.getTextureRegionDrawable(BUTTON_DOWN),
                                              null,
                                              ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createFbTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(FB_SQ_BUTTON_UP),
                ui.getTextureRegionDrawable(FB_SQ_BUTTON_DOWN),
                null,
                ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createGPlusTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(GPLUS_SQ_BUTTON_UP),
                ui.getTextureRegionDrawable(GPLUS_SQ_BUTTON_DOWN),
                null,
                ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }


    public static TextButton.TextButtonStyle createEmailTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(EMAIL_SQ_BUTTON_UP),
                ui.getTextureRegionDrawable(EMAIL_SQ_BUTTON_DOWN),
                null,
                ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }


    public static TextButton.TextButtonStyle createPlusTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(PLUS_SQ_BUTTON_UP),
                ui.getTextureRegionDrawable(PLUS_SQ_BUTTON_DOWN),
                null,
                ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }


    public static TextButton.TextButtonStyle createXTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(X_SQ_BUTTON_UP),
                ui.getTextureRegionDrawable(X_SQ_BUTTON_DOWN),
                null,
                ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createPlayTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(PLAY_SQ_BUTTON_UP),
                ui.getTextureRegionDrawable(PLAY_SQ_BUTTON_DOWN),
                null,
                ui.getSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createSmallTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(BUTTON_UP),
                ui.getTextureRegionDrawable(BUTTON_DOWN),
                null,
                ui.getSkin().getFont(PROFILE_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createToggleTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextureRegionDrawable(BUTTON_UP),
                ui.getTextureRegionDrawable(BUTTON_DOWN),
                ui.getTextureRegionDrawable(BUTTON_DOWN),
                ui.getSkin().getFont(PROFILE_FONT));
        style.fontColor = ui.getSkin().getColor(WHITE);
        style.downFontColor = ui.getSkin().getColor(RED);
        return style;
    }

    public static Button.ButtonStyle createDefaultBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextureRegionDrawable(BUTTON_UP),
                ui.getTextureRegionDrawable(BUTTON_DOWN),
                null);
        return style;
    }

    public static Button.ButtonStyle createToggleBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextureRegionDrawable(BUTTON_UP),
                ui.getTextureRegionDrawable(BUTTON_DOWN),
                ui.getTextureRegionDrawable(BUTTON_DOWN));
        return style;
    }

    public static Button.ButtonStyle createQuestionBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextureRegionDrawable(BUTTON_HELP),
                ui.getTextureRegionDrawable(BUTTON_HELP_DOWN),
                null);
        return style;
    }

    public static Button.ButtonStyle createLoginBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextureRegionDrawable(BUTTON_LOGIN),
                ui.getTextureRegionDrawable(BUTTON_LOGIN_DOWN),
                null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createGoogleBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(GPLUS),
                ui.getTextureRegionDrawable(GPLUS_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createFbBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(FB),
                ui.getTextureRegionDrawable(FB_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createTwitterBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(TWITTER),
                ui.getTextureRegionDrawable(TWITTER_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createEmailBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(EMAIL),
                ui.getTextureRegionDrawable(EMAIL_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createPlusImageBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable("ui-btn-sq-add"),
                ui.getTextureRegionDrawable("ui-btn-sq-add-p"),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createSettingsBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(SETTINGS),
                ui.getTextureRegionDrawable(SETTINGS_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createPlayBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(PLAY),
                ui.getTextureRegionDrawable(PLAY_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createFriendsBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(FRIENDS),
                ui.getTextureRegionDrawable(FRIENDS_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createProfileBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(PROFILE),
                ui.getTextureRegionDrawable(PROFILE_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createLeftBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(BUTTON_ARROW_LEFT),
                ui.getTextureRegionDrawable(BUTTON_ARROW_LEFT_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createRightBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(BUTTON_ARROW_RIGHT),
                ui.getTextureRegionDrawable(BUTTON_ARROW_RIGHT_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createCheckBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(CHECK),
                ui.getTextureRegionDrawable(CHECK_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createCloseBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(CLOSE),
                ui.getTextureRegionDrawable(CLOSE_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createStoreBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(STORE),
                ui.getTextureRegionDrawable(STORE_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createLeaderboardBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(LEADERBOARD),
                ui.getTextureRegionDrawable(LEADERBOARD_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createAddBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(ADD),
                ui.getTextureRegionDrawable(ADD_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createMailBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextureRegionDrawable(EMAIL),
                ui.getTextureRegionDrawable(EMAIL_DOWN),
                null, null, null, null);
        return style;
    }

    public static CheckBox.CheckBoxStyle createCheckBoxStyle(StageUI ui) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle(ui.getTextureRegionDrawable(CHECKBOX_OFF),
                ui.getTextureRegionDrawable(CHECKBOX_ON), ui.getSkin().getFont(DEFAULT_FONT), ui.getSkin().getColor(FUZZ_YELLOW));
        return style;
    }

    public static CheckBox.CheckBoxStyle createCFrameStyle(StageUI ui) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle(ui.getTextureRegionDrawable("ui-frame-itemslot"),
                ui.getTextureRegionDrawable("ui-frame-itemslot-s"), ui.getSkin().getFont(DEFAULT_FONT), ui.getSkin().getColor(FUZZ_YELLOW));
        return style;
    }

    public static TextField.TextFieldStyle createETxtFieldStyle(StageUI ui) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(ui.getSkin().getFont(PROFILE_FONT),
                ui.getSkin().getColor(WHITE),
                ui.getSkin().getDrawable(INPUT_CURSOR),
                ui.getSkin().getDrawable(INPUT_SELECTION),
                ui.getTextureRegionDrawable(INPUT));
        return style;
    }

    public static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle createTxtFieldStyle(StageUI ui) {
        com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle style = new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle(ui.getSkin().getFont(DEFAULT_FONT),
                ui.getSkin().getColor(WHITE),
                ui.getSkin().getDrawable(INPUT_CURSOR),
                ui.getSkin().getDrawable(INPUT_SELECTION),
                ui.getTextureRegionDrawable(INPUT));
        return style;
    }

    public static Window.WindowStyle createWindowStyle(StageUI ui) {
        Window.WindowStyle style = new Window.WindowStyle(ui.getSkin().getFont(DEFAULT_FONT),
                ui.getSkin().getColor(FUZZ_YELLOW),
                ui.getTextureRegionDrawable(PANEL));
        style.stageBackground = ui.getSkin().getDrawable("dialogDim");
        return style;
    }

    public static Window.WindowStyle createDialogStyle(StageUI ui) {
        Window.WindowStyle style = new Window.WindowStyle(ui.getSkin().getFont(DEFAULT_FONT),
                ui.getSkin().getColor(FUZZ_YELLOW),
                ui.getTextureRegionDrawable("ui-panel-login"));
        style.stageBackground = ui.getSkin().getDrawable("dialogDim");
        return style;
    }
}
