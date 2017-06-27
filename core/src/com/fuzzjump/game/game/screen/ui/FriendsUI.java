package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.fuzzjump.api.friends.IFriendService;
import com.fuzzjump.api.friends.model.FriendRequest;
import com.fuzzjump.api.friends.model.GenericFriendRequest;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.FriendProfile;
import com.fuzzjump.game.game.player.FriendProfile.FriendStatus;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.component.ActorSwitcher;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.util.GraphicsScheduler;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fuzzjump.game.game.Assets.createCloseBtnStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;
import static com.fuzzjump.game.game.Assets.createETxtFieldStyle;
import static com.fuzzjump.game.game.Assets.createSearchBtnStyle;
import static com.fuzzjump.game.game.Assets.createSmallTBStyle;

public class FriendsUI extends StageUI {

    private static final int FRIEND_DISPLAY_VIEW = 0;
    private static final int LOADING_DISPLAY_VIEW = 1;
    private static final int NO_FRIENDS_VIEW = 2;

    private final MenuUI parent;
    private final Profile profile;
    private final IFriendService friendService;
    private final GraphicsScheduler scheduler;

    private Table friendsList;
    private ScrollPane friendsScroller;
    private ActorSwitcher friendsListSwitcher;

    private TextField searchField;
    // From parent UI
    private Label mProgressLabel;
    private Image mProgressImage;
    private Button mCloseButton;

    private Dialog mProgressDialog;
    // Calculated values for spacing
    private Value friendSquareWidth;
    private Value friendSquareHeight;
    private Value friendSquarePadBottom;
    private Value friendSquarePadSides;

    private final Map<String, FriendProfile> profileMap = new HashMap<>();

    public FriendsUI(MenuUI parent, IFriendService friendService, GraphicsScheduler scheduler) {
        super(parent.getTextures(), parent.getGameSkin());
        this.stageScreen = parent.getStageScreen();
        this.parent = parent;
        this.profile = parent.getProfile();
        this.friendService = friendService;
        this.scheduler = scheduler;
    }

    @Override
    public void init() {
        setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_FRIENDS));

        Label nameLabel = new Label(profile.getDisplayName() + "#" + profile.getDisplayNameId(), getGameSkin(), "default");
        Value nameSpacing = Value.percentHeight(0.015f, this);

        add(nameLabel).center().top().expandX().padTop(nameSpacing).padBottom(nameSpacing).row();

        Table searchTable = new Table();

        ImageButton searchBtn = new ImageButton(createSearchBtnStyle(this));
        searchField = new TextField("", createETxtFieldStyle(this));

        searchField.setMessageText("Search by name#id");
        searchField.getStyle().messageFontColor = Color.WHITE;
        searchField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 35);
        searchField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);

        searchTable.add(searchField).left().size(Value.percentWidth(.8f, this), Value.percentHeight(.15f, this)).padLeft(Value.percentWidth(.05f, this)).padRight(Value.percentWidth(.025f, this));
        searchTable.add(searchBtn).right().size(Value.percentWidth(.085f, this), Value.percentHeight(.085f, this));
        add(searchTable).left().expandX().row();

        friendsList = new Table();
        friendsScroller = new ScrollPane(friendsList);
        friendsScroller.setScrollingDisabled(true, false);
        friendsScroller.layout();
        Table holder = new Table();
        holder.add(friendsScroller).fill().expand().top();
        friendsListSwitcher = new ActorSwitcher();
        friendsListSwitcher.addWidget(holder, 1f, 1f);

        Image progressImage = new Image(textures.getTextureRegionDrawable(Assets.UI_PROGRESS_SPINNER)) {
            @Override
            public void sizeChanged() {
                super.sizeChanged();
                setOrigin(Align.center);
            }
        };
        progressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));
        friendsListSwitcher.addWidget(progressImage, Value.percentWidth(.25f, friendsListSwitcher), Value.percentWidth(.25f, friendsListSwitcher), Align.center);

        add(friendsListSwitcher).expand().fillY().top().width(Value.percentWidth(.95f, this)).padBottom(Value.percentHeight(.015f, this)).row();

        Label noneLabel = new Label("No profiles found", getGameSkin());
        Label noFriendsLabel = new Label("No friends found! :(", getGameSkin());
        noneLabel.setAlignment(Align.center);
        noFriendsLabel.setAlignment(Align.center);

        friendsListSwitcher.addWidget(noneLabel);
        friendsListSwitcher.addWidget(noFriendsLabel);

        TextButton backBtn = new TextButton("Back", createDefaultTBStyle(this));
        add(backBtn).size(Value.percentWidth(0.475f, this), Value.percentWidth(0.0875f, this)).padBottom(Value.percentHeight(.025f, this));


        mProgressLabel = parent.actor(Assets.MenuUI.PROGRESS_LABEL);
        mProgressImage = parent.actor(Assets.MenuUI.PROGRESS_IMAGE);
        mCloseButton = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
        mProgressDialog = parent.actor(Assets.MenuUI.PROGRESS_DIALOG);

        friendSquareWidth = Value.percentWidth(.3f, friendsScroller);
        friendSquareHeight = Value.percentWidth(.4f, friendsScroller);
        friendSquarePadBottom = Value.percentWidth(.033f, friendsScroller);
        friendSquarePadSides = Value.percentWidth(.025f, friendsScroller);

        Helper.addClickAction(searchBtn, (e, x, y) -> searchFriend());
        Helper.addClickAction(backBtn, (e, x, y) -> backPressed());

        refreshDisplayUI();
    }

    public void onShow() {
        refreshFriends();
    }

    private void searchFriend() {
        String[] searchRequest = searchField.getText().split("#");

        if (searchRequest.length != 2 || !Helper.isNumeric(searchRequest[1])) {
            // TODO This is broken
            parent.displayMessage("Please search\n using the\n username#id format!", false);
            return;
        }
        parent.displayMessage("Sending friend request", true);
        friendService.sendFriendRequest(new FriendRequest(searchRequest[0], Integer.parseInt(searchRequest[1])))
                .observeOn(scheduler)
                .subscribe(r -> {
                    if (!r.isSuccess()) {
                        throw new IllegalStateException("Error while sending friend request!");
                    }
                    // TODO Receive friend profile after add
                    closeMessage(true);
                });
    }

    private void acceptFriend(FriendWidget widget) {
        parent.displayMessage("Accepting request", true);
        friendService.acceptFriendRequest(new GenericFriendRequest(widget.profile.getUserId()))
                .observeOn(scheduler)
                .subscribe(r -> {
                    if (r.isSuccess()) {
                        widget.profile.setStatus(FriendStatus.STATUS_ACCEPTED);
                        widget.update();

                        closeMessage(false);
                    }
                });
    }

    private void removeFriend(FriendWidget widget, String message) {
        parent.displayMessage(message, true);
        friendService.deleteFriend(new GenericFriendRequest(widget.profile.getUserId()))
                .observeOn(scheduler)
                .subscribe(r -> {
                    if (r.isSuccess()) {
                        profile.getFriends().remove(widget.profile);
                        widget.remove();

                        // TODO A smart friend refresh system
                        //refreshDisplayUI();

                        closeMessage(false);
                    }
                });
    }

    private void handleFriendAction(FriendWidget widget, boolean decline) {
        switch (widget.status) {
            case STATUS_PENDING://cancel sent request
                removeFriend(widget, "Canceling request");
                break;
            case STATUS_INCOMING://accept incoming
                if (decline) {
                    removeFriend(widget, "Declining request");
                } else {
                    acceptFriend(widget);
                }
                break;
            case STATUS_ACCEPTED://remove friend
                removeFriend(widget, "Removing friend");
                break;
        }
    }

    private void refreshFriends() {
        friendsListSwitcher.setDisplayedChild(FriendsUI.LOADING_DISPLAY_VIEW);
        friendService.retrieveFriendList().observeOn(parent.getScheduler()).subscribe(response -> {
            if (!response.isSuccess()) {
                throw new IllegalStateException("Error while loading friends!");
            }
            profile.loadFriends(response.getBody());
            refreshDisplayUI();
        }, e -> {
            e.printStackTrace();
            // TODO Display error
        });
    }

    private void refreshDisplayUI() {
        List<FriendProfile> profiles = profile.getFriends();

        friendsList.clear();
        int index = profiles.size() == 0 ? FriendsUI.NO_FRIENDS_VIEW : FriendsUI.FRIEND_DISPLAY_VIEW;
        for (int i = 0, n = profiles.size(); i < n; i++) {
            FriendProfile friend = profiles.get(i);
            if (profileMap.containsKey(friend.getUserId())) {
                profiles.set(i, profileMap.get(friend.getUserId()));
            }
        }
        profileMap.clear();
        friendsListSwitcher.setDisplayedChild(index);
        for (int i = 0, n = profiles.size(); i < n; i++) {
            int row = i / 3;
            int column = i % 3;
            if (row > 0 && column == 0) {
                friendsList.row();
            }
            FriendProfile friend = profiles.get(i);
            Cell cell = friendsList.add(new FriendWidget(friend)).center().top().width(friendSquareWidth).height(friendSquareHeight)
                    .padBottom(friendSquarePadBottom)
                    .padTop(friendSquarePadBottom)
                    .expand();
            profileMap.put(friend.getUserId(), friend);
            //if (column == 1)
            //    cell.padLeft(friendSquarePadSides).padRight(friendSquarePadSides);
            if (i == profiles.size() - 1 && column < 2) {
                int fillCount = 2 - column;
                for (int x = 0; x < fillCount; x++) {
                    Actor actor = new Actor();
                    actor.setTouchable(Touchable.disabled);
                    friendsList.add(actor).center().top().width(friendSquareWidth).height(friendSquareHeight)
                            .padBottom(friendSquarePadBottom)
                            .padTop(friendSquarePadBottom)
                            .expand();
                    //if (x == 0 && fillCount == 2)
                    //    cell.padLeft(friendSquarePadSides).padRight(friendSquarePadSides);
                }
            }
        }
    }

    private void closeMessage(boolean resetSearchField) {
        parent.closeMessage();
        if (resetSearchField) {
            searchField.setMessageText("");
            parent.getStageScreen().getStage().setKeyboardFocus(null);
        }
    }

    @Override
    public void backPressed() {
        final Dialog dialog = parent.actor(Dialog.class, Assets.MenuUI.PROGRESS_DIALOG);
        if (dialog.isVisible()) {
            final TextButton closeButton = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
            if (closeButton.isVisible()) {
                for (EventListener listener : closeButton.getListeners()) {
                    if (listener instanceof ClickListener) {
                        ((ClickListener) listener).clicked(new InputEvent(), 0, 0);
                    }
                }
            }
        }
        parent.showMain();
    }

    public class FriendWidget extends Table {

        private final FriendProfile profile;
        private FriendStatus status;
        private TextButton button;
        private Label nameLabel;

        private ImageButton closeButton;

        public FriendWidget(FriendProfile profile) {
            this.profile = profile;
            this.status = profile.getStatus();
            build();
            update();
        }

        private void build() {
            Fuzzle fuzzle = new Fuzzle(parent, parent.getUnlockableColorizer(), parent.getProfile());
            fuzzle.load(parent.getStageScreen().getLoader());

            Table fuzzleTable = new Table();
            nameLabel = new Label(new StringBuilder().append(profile.getDisplayName()).append("#").append(profile.getDisplayNameId()), getGameSkin(), "profile");
            closeButton = new ImageButton(createCloseBtnStyle(parent));

            fuzzleTable.add(fuzzle).width(Value.percentWidth(.75f, fuzzleTable)).height(Value.percentHeight(.75f, fuzzleTable)).center().expand();
            fuzzleTable.setBackground(textures.getTextureRegionDrawable(Assets.UI_FRIEND_FRAME));
            add(fuzzleTable).width(Value.percentWidth(1f, this)).height(Value.percentWidth(1.068903558153523f, this)).padBottom(Value.percentHeight(.025f, this)).expand().row();
            add(nameLabel).width(Value.percentWidth(1f, this)).center().expand().padBottom(Value.percentHeight(.025f, this)).row();
            add(button = new TextButton("Action", createSmallTBStyle(parent))).width(Value.percentWidth(1f, this)).height(Value.percentWidth(0.1842105263157895f, this));
            nameLabel.setAlignment(Align.center);

            addActor(closeButton);
            closeButton.setVisible(false);

            Helper.addClickAction(button, (e, x, y) -> handleFriendAction(this, false));


            //close only visible when incoming
            Helper.addClickAction(closeButton, (e, x, y) -> handleFriendAction(this, true));
        }

        @Override
        public void sizeChanged() {
            super.sizeChanged();
            float closeButtonWidth = getWidth() / 4;
            closeButton.setX(getWidth() - closeButtonWidth);
            closeButton.setY(getHeight() - closeButtonWidth);
            closeButton.setSize(closeButtonWidth, closeButtonWidth);
        }

        public void update() {
            status = profile.getStatus();
            closeButton.setVisible(status == FriendStatus.STATUS_INCOMING);
            button.setText(status.getButtonLabel());
        }

    }


}
