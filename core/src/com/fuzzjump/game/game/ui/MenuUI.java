package com.fuzzjump.game.game.ui;

import android.app.usage.UsageEvents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.ui.components.FJDragDownBarTable;
import com.fuzzjump.game.game.ui.components.ActorSwitcher;
import com.fuzzjump.game.game.ui.components.Fuzzle;
import com.fuzzjump.game.model.profile.Profile;

import static com.fuzzjump.game.util.Styles.*;

public class MenuUI extends StageUI {

    private ActorSwitcher uiSwitcher;
    private FriendsUI friendsUI;
    private CharacterSelectionUI profileUI;
    private SettingsUI settingsUI;

    private FJDragDownBarTable dropdownTable;

    @Override
	public void init() {
        //MAIN STUFF
        dropdownTable = new FJDragDownBarTable(this, getGame().getProfile());
        uiSwitcher = new ActorSwitcher();

        Label messageLabel = new Label("Loading", game.getSkin(), "default");
        final Image progressImage = new Image(getTextureRegionDrawable("ui-progressspinner"));
        final TextButton progressCloseButton = new TextButton("Close", createDefaultTBStyle(this));
        progressImage.setOrigin(Align.center);
        progressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

        final Dialog progressDialog = new Dialog("", createDialogStyle(this)) {

            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getWidth() * 0.65f;
            }

            @Override
            public float getPrefHeight() {
                return Gdx.graphics.getWidth() * 0.5081829277777778f;
            }


        };
        progressDialog.setModal(true);
        progressDialog.getContentTable().add(messageLabel).padTop(Value.percentHeight(.1f, progressDialog)).row();
        progressDialog.getContentTable().add(progressImage).center().expand().size(Value.percentWidth(.25f, progressDialog));
        progressCloseButton.setVisible(false);
        progressCloseButton.addListener(new ClickListener() {

            public void clicked(InputEvent event, float x, float y) {
                progressDialog.hide();
                progressCloseButton.setVisible(false);
            }

        });
        progressDialog.getButtonTable().add(progressCloseButton).size(Value.percentWidth(.475f, progressDialog), Value.percentWidth(0.1315789473684211f, progressDialog)).padBottom(Value.percentHeight(.035f, progressDialog)).center().expand();

        register(StageIds.MenuUI.PROGRESS_DIALOG, progressDialog);
        register(StageIds.MenuUI.PROGRESS_LABEL, messageLabel);
        register(StageIds.MenuUI.CLOSE_BUTTON, progressCloseButton);
        register(StageIds.MenuUI.PROGRESS_IMAGE, progressImage);

        friendsUI = new FriendsUI(stageScreen, game, this);
        profileUI = new CharacterSelectionUI(stageScreen, game, this);
        settingsUI = new SettingsUI(stageScreen, game, this);
        setFillParent(true);

        Table menuTable = new Table();
        menuTable.setBackground(getTextureRegionDrawable("ui-panel-mainmenu"));

        Value padSides = Value.percentWidth(.05f, menuTable);
        Value padTopBottom = Value.percentHeight(.05f, menuTable);

        Table innerTable = new Table();
        menuTable.add(innerTable).pad(padTopBottom, padSides, padTopBottom, padSides).size(Value.percentWidth(.95f, menuTable), Value.percentHeight(.9f, menuTable)).expand();

        Value topRowHeight = Value.percentHeight(.65f, innerTable);

        Table buttonsTable = new Table();
        TextButton publicGameButton = new TextButton("Find Game", createPlayTBStyle(this));
        publicGameButton.getLabel().setAlignment(Align.right);
        publicGameButton.getLabelCell().padRight(Value.percentWidth(.15f, publicGameButton));
        TextButton privateGameButton = new TextButton("New Game", createPlusTBStyle(this));
        privateGameButton.getLabel().setAlignment(Align.right);
        privateGameButton.getLabelCell().padRight(Value.percentWidth(.05f, privateGameButton));
        TextButton friendsButton = new TextButton("Friends", createDefaultTBStyle(this));
        buttonsTable.defaults().size(Value.percentWidth(.95f, buttonsTable), Value.percentWidth(0.25f, buttonsTable));
        buttonsTable.add(publicGameButton).expand().row();
        buttonsTable.add(privateGameButton).expand().row();
        buttonsTable.add(friendsButton).expand().row();
        innerTable.add(buttonsTable).left().padRight(padSides).expand().size(Value.percentWidth(.55f, innerTable), topRowHeight);

        friendsButton.addListener(new ClickListener() {

            public void clicked(InputEvent event, float x, float y) {
                friendsUI.showing();
                uiSwitcher.setDisplayedChild(1);
            }

        });

        Table pictureTable = new Table();
        Fuzzle image = new Fuzzle(this, getGame().getProfile());
        TextButton profileButton = new TextButton("Customize", createDefaultTBStyle(this));
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileUI.showing();
                uiSwitcher.setDisplayedChild(2);
            }
        });
        pictureTable.add(image).size(Value.percentWidth(0.75f, pictureTable)).expand().row();
        pictureTable.add(profileButton).padBottom(Value.percentHeight(.0416f, pictureTable)).size(Value.percentWidth(.95f, pictureTable), Value.percentWidth(0.25f, pictureTable));
        innerTable.add(pictureTable).right().expand().size(Value.percentWidth(.4f, innerTable), topRowHeight);

        innerTable.row();

        Table lowerButtonsTable = new Table();
        Table btnGroup = new Table();

        ImageButton leaderboardBtn = new ImageButton(createLeaderboardBtnStyle(this));
        ImageButton storeBtn = new ImageButton(createStoreBtnStyle(this));
        ImageButton settingsBtn = new ImageButton(createSettingsBtnStyle(this));

        Value btnHeight = Value.percentHeight(1f, btnGroup);
        Value btnWidth = btnHeight;
        Value btnPad = Value.percentWidth(.025f, btnGroup);
        btnGroup.defaults().size(btnWidth, btnHeight);
        btnGroup.add(leaderboardBtn).expand();
        btnGroup.add(storeBtn).expand().padLeft(btnPad).padRight(btnPad);
        btnGroup.add(settingsBtn).expand();

        lowerButtonsTable.add(btnGroup).size(Value.percentWidth(.55f, lowerButtonsTable), Value.percentHeight(.75f, lowerButtonsTable));

        innerTable.add(lowerButtonsTable).colspan(2).size(Value.percentWidth(1f, innerTable), Value.percentHeight(.3f, innerTable)).center().expand();

        //innerTable.pack();

        register(StageIds.MenuUI.PUBLIC_GAME, publicGameButton);


        uiSwitcher.addWidget(menuTable, Value.percentWidth(.975f, uiSwitcher), Value.percentWidth(0.8061598235294119f, uiSwitcher));
        uiSwitcher.addWidget(friendsUI, Value.percentWidth(.9f, uiSwitcher), Value.percentWidth(0.8288981077080586f, uiSwitcher));
        uiSwitcher.addWidget(profileUI, Value.percentWidth(.9f, uiSwitcher), Value.percentHeight(.5f, uiSwitcher));
        uiSwitcher.addWidget(settingsUI);


        add(dropdownTable).expand().fill();

        Table contentTable = dropdownTable.getContentTable();

        contentTable.add(uiSwitcher).fill().center().expand();

	}

    public FriendsUI getFriendsUI() {
        return friendsUI;
    }


	@Override
	public void backPressed() {
        Dialog progressDialog = actor(StageIds.MenuUI.PROGRESS_DIALOG);
        if (progressDialog.isVisible()) {
            TextButton button = actor(StageIds.MenuUI.CLOSE_BUTTON);
            for(EventListener listener : button.getListeners())
                if (listener instanceof ClickListener)
                    ((ClickListener)listener).clicked(new InputEvent(), 0, 0);
        }
//        if (actor(Dialog.class, StageIds.MenuUI.CONNECTING_DIALOG).isVisible()) {
//            actor(Dialog.class, StageIds.MenuUI.CONNECTING_DIALOG).hide();
//        } else if (actor(Dialog.class, StageIds.MenuUI.STORE_DIALOG).isVisible()) {
//            actor(Dialog.class, StageIds.MenuUI.STORE_DIALOG).hide();
//        } else
        if (uiSwitcher.getCurrentIdx() > 0) {
            ((StageUI)uiSwitcher.getActor(uiSwitcher.getCurrentIdx())).backPressed();
        }
	}

    public void showMain() {
        uiSwitcher.setDisplayedChild(0);
    }

    public class StoreEntry extends Actor {

        private GlyphLayout layout = new GlyphLayout();

        private final Drawable bg;
        private final Drawable coinIcon;
        private final BitmapFont coinsFont, costFont;

        private int storeId, coins;
        private float cost;

        public StoreEntry(int storeId, int coins, float cost) {
            this.bg = MenuUI.this.getTextureRegionDrawable("ui-wait-square");
            this.coinIcon = MenuUI.this.getTextureRegionDrawable("kerpow-coin");
            this.coinsFont = game.getSkin().getFont("profile-font");
            this.costFont = game.getSkin().getFont("default-font");
            this.storeId = storeId;
            this.coins = coins;
            this.cost = cost;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            bg.draw(batch, getX(), getY(), getWidth(), getHeight());
            float coinSize = getWidth() / 4.85f;
            String coinsStr = Integer.toString(coins);
            layout.setText(coinsFont, coinsStr);
            float totalCoinsWidth = coinSize + 2 + layout.width;
            float coinX = getX() + getWidth() / 2 - totalCoinsWidth / 2;
            coinIcon.draw(batch, coinX, getY() + getHeight() - coinSize * 2f, coinSize, coinSize);
            batch.setColor(game.getSkin().getColor("fuzzyellow"));
            coinsFont.setColor(game.getSkin().getColor("fuzzyellow"));
            coinsFont.draw(batch, coinsStr, coinX + coinSize + 2, getY() + getHeight() - coinSize * 2f + layout.height * 1.25f);

            String costStr = "$" + Float.toString(cost);
            layout.setText(coinsFont, costStr);
            float totalCostWidth = layout.width;
            float costX = getX() + getWidth() / 2 - totalCostWidth / 2;
            batch.setColor(game.getSkin().getColor("money_green"));
            costFont.setColor(game.getSkin().getColor("money_green"));
            costFont.draw(batch, costStr, costX, getY() + (layout.height * 2f));
            batch.setColor(Color.WHITE);
        }
    }
}
