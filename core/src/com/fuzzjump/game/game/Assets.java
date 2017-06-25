package com.fuzzjump.game.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.fuzzjump.libgdxscreens.screen.StageUI;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class Assets {

    public static final boolean DEBUG = false;

    public static final String PREFERENCES_NAME = "FuzzJump";

    public static final String USER_TOKEN = "FuzzUserToken";
    public static final String PROFILE_DATA = "FuzzProfile";

    // Resources
    public static final String ATLAS = "main.pack";
    public static final String SKIN = "uiskin.json";

    public static final String SVG_DIR = "data/svg/";
    public static final String MAP_DIR = "data/maps/";
    public static final String UNLOCKABLES_DIR = SVG_DIR + "unlockables/";
    public static final String SUNNY_DAY_DIR = MAP_DIR + "sunny_day_map/";

    public static final String SVG_RESOURCE = "svgs.json";

    public static final String LOGO = SVG_DIR + "logo.svg";

    public static final String SUNNY_DAY_SKY = SUNNY_DAY_DIR + "bg-sky.png";
    public static final String SUNNY_DAY_GROUND = SUNNY_DAY_DIR + "ground.png";

    // Fonts
    public static final String LARGE_FONT = "large-font";
    public static final String BIG_FONT = "big-font";
    public static final String DEFAULT_FONT = "default-font";
    public static final String PROFILE_FONT = "profile-font";
    public static final String INGAME_FONT = "ingame-font";
    public static final String SMALL_FONT = "small-font";
    public static final String SMALL_INGAME_FONT = "ingame-small-font";


    // UI
    public static final String UI_BACKGROUND = "ui_background";
    public static final String UI_GROUND = "ui_ground";
    public static final String UI_LOGO = "logo";
    public static final String UI_PROGRESS_SPINNER = "ui-progressspinner";
    public static final String UI_PANEL_WELCOME = "ui-panel-welcome";
    public static final String UI_SEARCH = "ui-search";
    public static final String UI_PANEL_FRIENDS = "ui-panel-friends";

    // Maps
    public final static String[] MAPS = {
            "sunny_day_map",
            "snow_map",
            "jungle_map",
            "city_map",
            "desert_map"
    };
    public static final int FUZZLE_COUNT = 6;

    // Loaded resources
    public static BitmapFont DEBUG_FONT = null;

    public class MainUI {

        //LOGIN
        public static final int LOGIN_USER_FIELD = 0;
        public static final int START_BUTTON = 2;
        public static final int LOGIN_WAITING_MESSAGE_DIALOG = 3;
        public static final int LOGIN_DIALOG_OK = 4;
        public static final int LOGIN_DIALOG_MESSAGE = 5;
        public static final int LOGIN_REGISTER_DIALOG = 6;
        public static final int REGISTER_FACEBOOK = 7;
    }

    public class MenuUI {
        public static final int PUBLIC_GAME = 0;
        public static final int CONNECTING_DIALOG = 1;
        public static final int CONNECTING_LABEL = 2;
        public static final int OK_BUTTON = 3;
        public static final int FB_BUTTON = 4;
        public static final int TWITTER_BUTTON = 5;
        public static final int GOOGLE_BUTTON = 6;
        public static final int WWW_BUTTON = 7;
        public static final int SOUND_TOGGLE = 8;
        public static final int LOGOUT_BUTTON = 9;
        public static final int CREDITS_BUTTON = 10;
        public static final int LEADERBOARD_BUTTON = 11;
        public static final int STORE_BUTTON = 12;
        public static final int STORE_DIALOG = 13;
        public static final int PROFILE_PANEL = 14;
        public static final int SELECT_UNLOCKABLE_BUTTON = 15;
        public static final int SELECT_BUY_BUTTON = 16;
        public static final int COLOR_SELECTED = 17;
        public static final int BUYING_DIALOG = 18;
        public static final int CONFIRM_LABEL = 19;
        public static final int CONFIRM_PURCHASE_BUTTON = 20;
        public static final int PROGRESS_DIALOG = 21;
        public static final int PREVIEW_TABLE = 22;
        public static final int PROGRESS_LABEL = 23;
        public static final int SELECTED_UNLOCK = 24;
        public static final int CLOSE_BUTTON = 25;
        public static final int PROGRESS_IMAGE = 26;
    }

    public class WaitingUI {
        public static final int TIME_LABEL = 5;
        public static final int READY_BUTTON = 6;
        public static final int CANCEL_BUTTON = 7;
        public static final int MAP_BUTTON = 8;

        public static final int CONNECTING_DIALOG = 9;
        public static final int CONNECTING_MESSAGE = 10;
        public static final int CONNECTING_BUTTON = 11;
        public static final int MAP_TABLE = 12;
        public static final int CONNECTING_PROGRESS = 13;
    }

    public class GameUI {

        public static final int HEIGHT_LABEL = 0;

        public static final int PROGRESS_DIALOG = 4;
        public static final int PROGRESS_IMAGE = 5;
        public static final int PROGRESS_LABEL = 6;
    }



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

    public static TextButton.TextButtonStyle createDefaultTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createFbTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(FB_SQ_BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(FB_SQ_BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createGPlusTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(GPLUS_SQ_BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(GPLUS_SQ_BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }


    public static TextButton.TextButtonStyle createEmailTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(EMAIL_SQ_BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(EMAIL_SQ_BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }


    public static TextButton.TextButtonStyle createPlusTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(PLUS_SQ_BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(PLUS_SQ_BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }


    public static TextButton.TextButtonStyle createXTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(X_SQ_BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(X_SQ_BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createPlayTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(PLAY_SQ_BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(PLAY_SQ_BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(DEFAULT_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createSmallTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN),
                null,
                ui.getGameSkin().getFont(PROFILE_FONT));
        style.fontColor = Color.WHITE;
        return style;
    }

    public static TextButton.TextButtonStyle createToggleTBStyle(StageUI ui) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN),
                ui.getGameSkin().getFont(PROFILE_FONT));
        style.fontColor = ui.getGameSkin().getColor(WHITE);
        style.downFontColor = ui.getGameSkin().getColor(RED);
        return style;
    }

    public static Button.ButtonStyle createDefaultBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN),
                null);
        return style;
    }

    public static Button.ButtonStyle createToggleBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_UP),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN),
                ui.getTextures().getTextureRegionDrawable(BUTTON_DOWN));
        return style;
    }

    public static Button.ButtonStyle createQuestionBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_HELP),
                ui.getTextures().getTextureRegionDrawable(BUTTON_HELP_DOWN),
                null);
        return style;
    }

    public static Button.ButtonStyle createLoginBtnStyle(StageUI ui) {
        Button.ButtonStyle style = new Button.ButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_LOGIN),
                ui.getTextures().getTextureRegionDrawable(BUTTON_LOGIN_DOWN),
                null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createGoogleBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(GPLUS),
                ui.getTextures().getTextureRegionDrawable(GPLUS_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createFbBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(FB),
                ui.getTextures().getTextureRegionDrawable(FB_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createTwitterBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(TWITTER),
                ui.getTextures().getTextureRegionDrawable(TWITTER_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createEmailBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(EMAIL),
                ui.getTextures().getTextureRegionDrawable(EMAIL_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createPlusImageBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable("ui-btn-sq-add"),
                ui.getTextures().getTextureRegionDrawable("ui-btn-sq-add-p"),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createSettingsBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(SETTINGS),
                ui.getTextures().getTextureRegionDrawable(SETTINGS_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createPlayBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(PLAY),
                ui.getTextures().getTextureRegionDrawable(PLAY_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createFriendsBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(FRIENDS),
                ui.getTextures().getTextureRegionDrawable(FRIENDS_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createProfileBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(PROFILE),
                ui.getTextures().getTextureRegionDrawable(PROFILE_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createLeftBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_ARROW_LEFT),
                ui.getTextures().getTextureRegionDrawable(BUTTON_ARROW_LEFT_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createRightBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(BUTTON_ARROW_RIGHT),
                ui.getTextures().getTextureRegionDrawable(BUTTON_ARROW_RIGHT_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createCheckBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(CHECK),
                ui.getTextures().getTextureRegionDrawable(CHECK_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createCloseBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(CLOSE),
                ui.getTextures().getTextureRegionDrawable(CLOSE_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createStoreBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(STORE),
                ui.getTextures().getTextureRegionDrawable(STORE_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createLeaderboardBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(LEADERBOARD),
                ui.getTextures().getTextureRegionDrawable(LEADERBOARD_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createAddBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(ADD),
                ui.getTextures().getTextureRegionDrawable(ADD_DOWN),
                null, null, null, null);
        return style;
    }

    public static ImageButton.ImageButtonStyle createMailBtnStyle(StageUI ui) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(ui.getTextures().getTextureRegionDrawable(EMAIL),
                ui.getTextures().getTextureRegionDrawable(EMAIL_DOWN),
                null, null, null, null);
        return style;
    }

    public static CheckBox.CheckBoxStyle createCheckBoxStyle(StageUI ui) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle(ui.getTextures().getTextureRegionDrawable(CHECKBOX_OFF),
                ui.getTextures().getTextureRegionDrawable(CHECKBOX_ON), ui.getGameSkin().getFont(DEFAULT_FONT), ui.getGameSkin().getColor(FUZZ_YELLOW));
        return style;
    }

    public static CheckBox.CheckBoxStyle createCFrameStyle(StageUI ui) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle(ui.getTextures().getTextureRegionDrawable("ui-frame-itemslot"),
                ui.getTextures().getTextureRegionDrawable("ui-frame-itemslot-s"), ui.getGameSkin().getFont(DEFAULT_FONT), ui.getGameSkin().getColor(FUZZ_YELLOW));
        return style;
    }

    public static TextField.TextFieldStyle createETxtFieldStyle(StageUI ui) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(ui.getGameSkin().getFont(PROFILE_FONT),
                ui.getGameSkin().getColor(WHITE),
                ui.getGameSkin().getDrawable(INPUT_CURSOR),
                ui.getGameSkin().getDrawable(INPUT_SELECTION),
                ui.getTextures().getTextureRegionDrawable(INPUT));
        return style;
    }

    public static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle createTxtFieldStyle(StageUI ui) {
        com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle style = new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle(ui.getGameSkin().getFont(DEFAULT_FONT),
                ui.getGameSkin().getColor(WHITE),
                ui.getGameSkin().getDrawable(INPUT_CURSOR),
                ui.getGameSkin().getDrawable(INPUT_SELECTION),
                ui.getTextures().getTextureRegionDrawable(INPUT));
        return style;
    }

    public static Window.WindowStyle createWindowStyle(StageUI ui) {
        Window.WindowStyle style = new Window.WindowStyle(ui.getGameSkin().getFont(DEFAULT_FONT),
                ui.getGameSkin().getColor(FUZZ_YELLOW),
                ui.getTextures().getTextureRegionDrawable(PANEL));
        style.stageBackground = ui.getGameSkin().getDrawable("dialogDim");
        return style;
    }

    public static Window.WindowStyle createDialogStyle(StageUI ui) {
        Window.WindowStyle style = new Window.WindowStyle(ui.getGameSkin().getFont(DEFAULT_FONT),
                ui.getGameSkin().getColor(FUZZ_YELLOW),
                ui.getTextures().getTextureRegionDrawable("ui-panel-login"));
        style.stageBackground = ui.getGameSkin().getDrawable("dialogDim");
        return style;
    }

}
