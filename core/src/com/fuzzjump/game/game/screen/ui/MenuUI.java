package com.fuzzjump.game.game.screen.ui;


import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.api.friends.IFriendService;
import com.fuzzjump.api.invite.model.Invite;
import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.api.model.user.Equip;
import com.fuzzjump.api.profile.model.SaveProfileRequest;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Appearance;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.player.unlockable.UnlockableRepositoryService;
import com.fuzzjump.game.game.screen.MenuScreen;
import com.fuzzjump.game.game.screen.component.ActorSwitcher;
import com.fuzzjump.game.game.screen.component.FJDragDownBarTable;
import com.fuzzjump.game.game.screen.component.FuzzDialog;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.io.FuzzPersistence;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.screen.ScreenLoader;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.createCloseBtnStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;
import static com.fuzzjump.game.game.Assets.createDialogStyle;
import static com.fuzzjump.game.game.Assets.createLeaderboardBtnStyle;
import static com.fuzzjump.game.game.Assets.createPlayBtnStyle;
import static com.fuzzjump.game.game.Assets.createPlayTBStyle;
import static com.fuzzjump.game.game.Assets.createPlusTBStyle;
import static com.fuzzjump.game.game.Assets.createSettingsBtnStyle;
import static com.fuzzjump.game.game.Assets.createSmallTBStyle;
import static com.fuzzjump.game.game.Assets.createStoreBtnStyle;

/**
 * Created by Steven Galarza on 6/16/2017.
 */
public class MenuUI extends StageUI {

    private ActorSwitcher uiSwitcher;
    private CharacterSelectionUI profileUI;
    private SettingsUI settingsUI;
    private FriendsUI friendsUI;

    private FJDragDownBarTable dropdownTable;

    private final Profile profile;
    private final UnlockableRepositoryService definitions;
    private final UnlockableColorizer colorizer;
    private final IFriendService friendService;
    private final GraphicsScheduler scheduler;
    private final FuzzPersistence persistence;

    private Label mMessageLabel;
    private Image mProgressImage;
    private Button mMessageCloseBtn;
    private Dialog mProgressDialog;
    private Fuzzle fuzzle;

    private VerticalGroup inviteList;
    private ScrollPane inviteScroller;

    @Inject
    public MenuUI(Textures textures, Skin skin, IFriendService friendService,
                  Profile profile, UnlockableRepositoryService definitions, UnlockableColorizer colorizer,
                  GraphicsScheduler scheduler, FuzzPersistence persistence) {
        super(textures, skin);
        this.friendService = friendService;
        this.profile = profile;
        this.definitions = definitions;
        this.colorizer = colorizer;
        this.scheduler = scheduler;
        this.persistence = persistence;
    }

    @Override
    public void init() {
        ScreenLoader loader = getStageScreen().getScreenLoader();

        // Load loading dialog
        loader.add(() -> {
            mMessageLabel = new Label("Loading", getGameSkin(), "default");
            mProgressImage = new Image(textures.getTextureRegionDrawable(Assets.UI_PROGRESS_SPINNER));
            mMessageCloseBtn = new TextButton("Close", createDefaultTBStyle(this));
            mProgressImage.setOrigin(Align.center);
            mProgressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

            mProgressDialog = new FuzzDialog("", createDialogStyle(this), 0.65f, 0.5081829277777778f);
            mProgressDialog.setModal(true);
            mProgressDialog.getContentTable().add(mMessageLabel).padTop(Value.percentHeight(.1f, mProgressDialog)).row();
            mProgressDialog.getContentTable().add(mProgressImage).center().expand().size(Value.percentWidth(.25f, mProgressDialog));
            mMessageCloseBtn.setVisible(false);


            mProgressDialog.getButtonTable().add(mMessageCloseBtn).size(Value.percentWidth(.475f, mProgressDialog), Value.percentWidth(0.1315789473684211f, mProgressDialog)).padBottom(Value.percentHeight(.035f, mProgressDialog)).center().expand();

            register(Assets.MenuUI.PROGRESS_DIALOG, mProgressDialog);
            register(Assets.MenuUI.PROGRESS_LABEL, mMessageLabel);
            register(Assets.MenuUI.CLOSE_BUTTON, mMessageCloseBtn);
            register(Assets.MenuUI.PROGRESS_IMAGE, mProgressImage);

            Helper.addClickAction(mMessageCloseBtn, (e, x, y) -> {
                mProgressDialog.hide();
                mMessageCloseBtn.setVisible(false);
            });
        });

        // Doesn't require background loading
        this.profileUI = new CharacterSelectionUI(this, definitions);
        this.settingsUI = new SettingsUI(this, persistence);
        this.friendsUI = new FriendsUI(this, scheduler, friendService);

        setFillParent(true);

        // Load our main elements. THIS SHOULD BE IN ORDER
        loader.add(() -> {
            dropdownTable = new FJDragDownBarTable(this, profile);
            uiSwitcher = new ActorSwitcher();
        });

        loader.add(() -> {
            inviteList = new VerticalGroup();
            inviteList.bottom();
            inviteScroller = new ScrollPane(inviteList);
            inviteScroller.setScrollingDisabled(true, false);
            inviteScroller.layout();

            Value padY = Value.percentHeight(.025f, dropdownTable.getDragDownTable());
            Value padX = Value.percentWidth(.025f, dropdownTable.getDragDownTable());

            Container holder = new Container(inviteScroller);
            holder.fill().pad(padY, padX, padY, padX);

            dropdownTable.getDragDownTable().add(holder).expand().fill();
        });

        loader.add(() -> {
            Table menuTable = new Table();
            menuTable.setBackground(textures.getTextureRegionDrawable(Assets.UI_MAIN_MENU_PANEL));

            Value padSides = Value.percentWidth(.05f, menuTable);
            Value padTopBottom = Value.percentHeight(.15f, menuTable);
            Value padTop = Value.percentHeight(.3f, menuTable);

            Table innerTable = new Table();
            menuTable.add(innerTable).pad(padTop, padSides, padTopBottom, padSides).size(Value.percentWidth(.95f, menuTable), Value.percentHeight(.9f, menuTable)).expand();

            Value topRowHeight = Value.percentHeight(.6f, innerTable);

            Table buttonsTable = new Table();
            TextButton publicGameButton = new TextButton("Find Game", createPlayTBStyle(this));
            publicGameButton.getLabel().setAlignment(Align.right);
            publicGameButton.getLabelCell().padRight(Value.percentWidth(.1f, publicGameButton));
            TextButton privateGameButton = new TextButton("New Game", createPlusTBStyle(this));
            privateGameButton.getLabel().setAlignment(Align.right);
            privateGameButton.getLabelCell().padRight(Value.percentWidth(.05f, privateGameButton));
            TextButton friendsButton = new TextButton("Friends", createDefaultTBStyle(this));
            buttonsTable.defaults().size(Value.percentWidth(.95f, buttonsTable), Value.percentWidth(0.25f, buttonsTable));
            buttonsTable.add(publicGameButton).expand().row();
            buttonsTable.add(privateGameButton).expand().row();
            buttonsTable.add(friendsButton).expand().row();
            innerTable.add(buttonsTable).left().padRight(padSides).expand().size(Value.percentWidth(.55f, innerTable), topRowHeight);


            Table pictureTable = new Table();

            fuzzle = new Fuzzle(this, colorizer, profile).load(loader);

            TextButton profileButton = new TextButton("Customize", createSmallTBStyle(this));
            pictureTable.add(fuzzle).size(Value.percentWidth(0.75f, pictureTable)).expand().row();
            pictureTable.add(profileButton).padBottom(Value.percentHeight(.0416f, pictureTable)).size(Value.percentWidth(.95f, pictureTable), Value.percentWidth(0.25f, pictureTable));
            innerTable.add(pictureTable).right().expand().size(Value.percentHeight(.4f, innerTable), topRowHeight);

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
            btnGroup.add(leaderboardBtn).top().expand();
            btnGroup.add(storeBtn).top().expand().padLeft(btnPad).padRight(btnPad);
            btnGroup.add(settingsBtn).top().expand();

            lowerButtonsTable.add(btnGroup).top().size(Value.percentWidth(.55f, lowerButtonsTable), Value.percentHeight(.75f, lowerButtonsTable));

            innerTable.add(lowerButtonsTable).colspan(2).size(Value.percentWidth(1f, innerTable), Value.percentHeight(.3f, innerTable)).top();

            register(Assets.MenuUI.PUBLIC_GAME, publicGameButton);


            uiSwitcher.addWidget(menuTable, Value.percentWidth(.975f, uiSwitcher), Value.percentWidth(0.8061598235294119f, uiSwitcher));
            uiSwitcher.addWidget(profileUI, Value.percentWidth(.9f, uiSwitcher), Value.percentHeight(.5f, uiSwitcher));
            uiSwitcher.addWidget(friendsUI, Value.percentWidth(.9f, uiSwitcher), Value.percentWidth(0.8288981077080586f, uiSwitcher));
            uiSwitcher.addWidget(settingsUI);

            // Register click listener
            Helper.addClickAction(profileButton, (e, x, y) -> {
                profileUI.onShow();
                uiSwitcher.setDisplayedChild(1);
            });
            Helper.addClickAction(friendsButton, (e, x, y) -> {
                friendsUI.onShow();
                uiSwitcher.setDisplayedChild(2);
            });
            Helper.addClickAction(settingsBtn, (e, x, y) -> uiSwitcher.setDisplayedChild(3));
        });


        // Note expensive operations, but since these are not available immediately then we do this here.
        loader.add(() -> {
            add(dropdownTable).expand().fill();

            Table contentTable = dropdownTable.getContentTable();

            contentTable.add(uiSwitcher).fill().center().expand();
        });

        // Initialize other UI's
        loader.add(() -> profileUI.init());
        loader.add(() -> settingsUI.init());
        loader.add(() -> friendsUI.init());
    }

    public void displayMessage(String message, boolean process) {
        mMessageLabel.setText(message);
        mProgressImage.setVisible(process);
        mMessageCloseBtn.setVisible(!process);

        mProgressDialog.show(getStage());
    }

    public void closeMessage() {
        mProgressDialog.hide();
    }

    public void addInvites(Invite[] invites) {
        if (invites.length == 0) {
            return;
        }
        Value inviteHeight = Value.percentHeight(.2f, inviteScroller);
        Value inviteWidth = Value.percentWidth(1f, inviteScroller);
        for(int i = 0; i < invites.length; i++) {
            inviteList.addActor(new InviteActor(invites[i], inviteWidth, inviteHeight));
        }
    }

    @Override
    public void backPressed() {

    }

    public void showMain() {
        Appearance appearance = profile.getAppearance();
        if (appearance.isTracking()) {
            Equip[] equipChanges = appearance.getEquipChanges();
            Unlockable[] unlockableChanges = appearance.getUnlockableChanges();

            appearance.stopTracking();
            if (equipChanges != null || unlockableChanges != null) {
                displayMessage("Saving profile", true);
                //TODO should be in screen.
                fuzzle.load(getStageScreen().getScreenLoader());

                MenuScreen menuScreen = (MenuScreen) getStageScreen();
                menuScreen.saveProfile(new SaveProfileRequest(equipChanges, unlockableChanges));
            }
        }
        uiSwitcher.setDisplayedChild(0);
    }

    Profile getProfile() {
        return profile;
    }

    GraphicsScheduler getScheduler() {
        return scheduler;
    }

    UnlockableColorizer getUnlockableColorizer() {
        return colorizer;
    }

    private class InviteActor extends Table {

        private final Invite invite;
        private final Value width;
        private final Value height;

        private Fuzzle fuzzle;
        private Label label;

        public InviteActor(Invite invite, Value width, Value height) {
            this.invite = invite;
            this.width = width;
            this.height = height;
            init();
        }

        private void init() {
            setBackground(textures.getTextureRegionDrawable("ui-panel-character1"));

            Value fullHeight = Value.percentHeight(.75f, this);
            Value padTop = Value.percentHeight(.01f, this);
            Value padSides = Value.percentWidth(.01f, this);

            fuzzle = new Fuzzle(MenuUI.this, getUnlockableColorizer());
            label = new Label("Steveadoo invited you to a game.", MenuUI.this.getSkin(), "profile");
            label.setWrap(true);

            ImageButton acceptButton = new ImageButton(createPlayBtnStyle(MenuUI.this));
            ImageButton closeButton = new ImageButton(createCloseBtnStyle(MenuUI.this));

            defaults().height(fullHeight);
            add(fuzzle).width(fullHeight).padLeft(padSides).padRight(padSides).expandY();
            add(label).expandX().fillX().expandY();
            add(closeButton).size(fullHeight).padRight(padSides).expandY();
            add(acceptButton).size(fullHeight).padRight(padSides).expandY();
        }

        @Override
        public float getHeight() {
            return height.get(null);
        }

        @Override
        public float getWidth() {
            return width.get(null);
        }

        @Override
        public float getPrefHeight() {
            return height.get(null);
        }

        @Override
        public float getPrefWidth() {
            return width.get(null);
        }

    }
}
