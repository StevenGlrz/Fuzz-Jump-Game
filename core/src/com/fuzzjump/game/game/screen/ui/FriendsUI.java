package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import com.fuzzjump.game.game.screen.component.FuzzDialog;
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
import static com.fuzzjump.game.game.Assets.createInputDialogStyle;
import static com.fuzzjump.game.game.Assets.createPlusImageBtnStyle;
import static com.fuzzjump.game.game.Assets.createSmallTBStyle;

public class FriendsUI extends StageUI {

    private static final int FRIEND_DISPLAY_VIEW = 0;
    private static final int LOADING_DISPLAY_VIEW = 1;
    private static final int NO_FRIENDS_VIEW = 2;
    private static final int NETWORK_ERROR_VIEW = 3;

    private final MenuUI parent;
    private final Profile profile;
    private final IFriendService friendService;
    private final GraphicsScheduler scheduler;

    private Table friendsList;
    private ScrollPane friendsScroller;
    private ActorSwitcher friendsListSwitcher;
    private ActorSwitcher addFriendProgressSwitcher;
    private ImageButton addFriendCloseButton;

    private TextField searchField;
    // From parent UI
    private Label mProgressLabel;
    private Image mProgressImage;
    private Button mCloseButton;

    private Label errorAddLabel;

    private Dialog mProgressDialog;
    // Calculated values for spacing
    private Value friendSquareWidth;
    private Value friendSquareHeight;
    private Value friendSquarePadBottom;
    private Value friendSquarePadSides;

    private final Map<String, FriendProfile> profileMap = new HashMap<>();

    public FriendsUI(MenuUI parent, GraphicsScheduler scheduler, IFriendService friendService) {
        super(parent.getTextures(), parent.getGameSkin());
        this.stageScreen = parent.getStageScreen();
        this.parent = parent;
        this.profile = parent.getProfile();
        this.friendService = friendService;
        this.scheduler = scheduler;
    }

    @Override
    public void init() {
        Table innerTable = new Table();

        Table infoTable = new Table();
        infoTable.setBackground(textures.getTextureRegionDrawable("ui-panel-character4"));

        Table friendTable = new Table();
        friendTable.setBackground(textures.getTextureRegionDrawable("ui-panel-lobby1"));

        Table buttonTable = new Table();
        buttonTable.setBackground(textures.getTextureRegionDrawable("ui-panel-lobby"));


        add(innerTable).width(Value.percentWidth(.95f, this)).height(Value.percentHeight(.95f, this)).expand();
        innerTable.add(infoTable).size(Value.percentWidth(1f, innerTable), Value.percentWidth(0.19139831558315f, innerTable)).row();
        innerTable.add(friendTable).size(Value.percentWidth(1f, innerTable), Value.percentWidth(0.8860788066866949f, innerTable)).row();
        innerTable.add(buttonTable).size(Value.percentWidth(1f, innerTable), Value.percentWidth(0.17139831558315f, innerTable)).row();

        initInfoTable(infoTable);
        initFriendTable(friendTable);
        initButtonTable(buttonTable);

        mProgressLabel = parent.actor(Assets.MenuUI.PROGRESS_LABEL);
        mProgressImage = parent.actor(Assets.MenuUI.PROGRESS_IMAGE);
        mCloseButton = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
        mProgressDialog = parent.actor(Assets.MenuUI.PROGRESS_DIALOG);

        friendSquareWidth = Value.percentWidth(.3f, friendsScroller);
        friendSquareHeight = Value.percentWidth(.4f, friendsScroller);
        friendSquarePadBottom = Value.percentWidth(.033f, friendsScroller);
        friendSquarePadSides = Value.percentWidth(.025f, friendsScroller);

        initAddDialog();
    }

    private void initAddDialog() {
        addFriendCloseButton = new ImageButton(createCloseBtnStyle(this));
        Dialog addDialog = new FuzzDialog("", createInputDialogStyle(this), 0.9f, 0.3548957887917666f) {
            @Override
            public void result(Object obj) {
                cancel();
            }

            @Override
            public void sizeChanged() {
                super.sizeChanged();
                float closeButtonWidth = getWidth() / 10f;
                addFriendCloseButton.setX(getWidth() - (closeButtonWidth / 1.5f));
                addFriendCloseButton.setY(getHeight() - (closeButtonWidth / 1.5f));
                addFriendCloseButton.setSize(closeButtonWidth, closeButtonWidth);
            }
        };

        addDialog.setClip(false);
        addDialog.addActor(addFriendCloseButton);

        Helper.addClickAction(addFriendCloseButton, (evt, x, y) -> backPressed());

        Table contentTable = addDialog.getContentTable();

        Value padSides = Value.percentWidth(.015f, contentTable);
        Value padTop = Value.percentHeight(.025f, contentTable);

        Label addLabel = new Label("Add friend", getGameSkin(), "default");
        addLabel.setAlignment(Align.center);
        contentTable.add(addLabel).expand().fill().padLeft(padSides).padTop(padTop).padRight(padSides).colspan(2).row();

        searchField = new TextField("", createETxtFieldStyle(this));
        searchField.getStyle().messageFontColor = Color.WHITE;
        searchField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 35);
        searchField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);
        searchField.setMessageText("Enter friend code (username#id)");

        contentTable.add(searchField).padLeft(padSides).size(Value.percentWidth(.8f, contentTable), Value.percentWidth(.15f, contentTable)).expand();

        ImageButton addButton = new ImageButton(createPlusImageBtnStyle(this));

        Image progressImage = new Image(textures.getTextureRegionDrawable(Assets.UI_PROGRESS_SPINNER)) {
            @Override
            public void sizeChanged() {
                super.sizeChanged();
                setOrigin(Align.center);
            }
        };
        progressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

        addFriendProgressSwitcher = new ActorSwitcher();
        addFriendProgressSwitcher.addWidget(addButton);
        addFriendProgressSwitcher.addWidget(progressImage);

        contentTable.add(addFriendProgressSwitcher).padRight(padSides).size(Value.percentWidth(.15f, contentTable)).center().expandY().row();

        addFriendProgressSwitcher.setDisplayedChild(0);

        Helper.addClickAction(addButton, (evt, x, y) -> sendFriendRequest());

        padSides = Value.percentWidth(.025f, contentTable);
        padTop = Value.percentHeight(.05f, contentTable);

        errorAddLabel = new Label("Error!", getGameSkin(), "profile");
        errorAddLabel.setColor(Color.RED);
        errorAddLabel.setAlignment(Align.top | Align.center);
        contentTable.add(errorAddLabel).fill().padLeft(padSides).padBottom(padTop).colspan(2);
        errorAddLabel.setVisible(false);

        register(Assets.MenuUI.FRIEND_ADD_DIALOG, addDialog);
    }

    private void initInfoTable(Table infoTable) {
        Label nameLabel = new Label("You: \n" + profile.getDisplayName() + "#" + profile.getDisplayNameId(), getGameSkin(), "default");
        nameLabel.setAlignment(Align.center);
        infoTable.add(nameLabel).center().top().expandX().row();
    }

    private void initFriendTable(Table friendTable) {
        friendsList = new Table();
        friendsScroller = new ScrollPane(friendsList);
        friendsScroller.setScrollingDisabled(true, false);
        friendsScroller.layout();
        Table holder = new Table();
        Value padY = Value.percentHeight(.025f, holder);
        Value padX = Value.percentWidth(.025f, holder);
        holder.add(friendsScroller).pad(padY, padX, padY, padX).fill().expand().top();
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

        friendTable.add(friendsListSwitcher).expand().fillY().top().width(Value.percentWidth(.95f, friendTable)).padBottom(Value.percentHeight(.015f, friendTable)).row();

        Label noneLabel = new Label("You have no friends.", getGameSkin());
        noneLabel.setAlignment(Align.center);

        Table networkErrorTable = new Table();

        Label networkErrorLabel = new Label("Network error.", getGameSkin());
        networkErrorLabel.setAlignment(Align.center);
        networkErrorTable.add(networkErrorLabel).expand().center().bottom().row();

        TextButton retryButton = new TextButton("Retry", createDefaultTBStyle(this));
        networkErrorTable.add(retryButton).expand().size(Value.percentWidth(.45f, networkErrorTable), Value.percentWidth(.12888888f, networkErrorTable)).center().top();

        Helper.addClickAction(retryButton, (evt, x, y) -> refreshFriends());

        friendsListSwitcher.addWidget(noneLabel);
        friendsListSwitcher.addWidget(networkErrorTable);
    }

    private void initButtonTable(Table buttonTable) {
        TextButton backButton = new TextButton("Back", createDefaultTBStyle(parent));
        TextButton addButton = new TextButton("Add friend", createDefaultTBStyle(parent));

        Helper.addClickAction(backButton, (e, x, y) -> backPressed());
        Helper.addClickAction(addButton, (e, x, y) -> actor(Dialog.class, Assets.MenuUI.FRIEND_ADD_DIALOG).show(getStage()));

        Value outerPad = Value.percentWidth(.025f, buttonTable);
        Value upperPad = Value.percentHeight(.025f, buttonTable);
        Value width = Value.percentWidth(.45f, buttonTable);
        Value height = Value.percentWidth(.12888888f, buttonTable);

        buttonTable.add(backButton).size(width, height).pad(upperPad, outerPad, upperPad, outerPad).expand();
        buttonTable.add(addButton).size(width, height).pad(upperPad, outerPad, upperPad, outerPad).expand();
    }

    public void onShow() {
        refreshFriends();
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
            friendsListSwitcher.setDisplayedChild(NETWORK_ERROR_VIEW);
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
            friendsList.add(new FriendWidget(friend)).center().top().width(friendSquareWidth).height(friendSquareHeight)
                    .padBottom(friendSquarePadBottom)
                    .padTop(friendSquarePadBottom)
                    .expand();
            profileMap.put(friend.getUserId(), friend);
            //if (column == 1)
            //    cell.padLeft(friendSquarePadSides).padRight(friendSquarePadSides);
            //fills in the rest of the row with a dummy actor.
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

    private void sendFriendRequest() {
        errorAddLabel.setVisible(false);
        String[] searchRequest = searchField.getText().split("#");
        if (searchRequest.length != 2 || !Helper.isNumeric(searchRequest[1])) {
            errorAddLabel.setText("Please use the format username#id");
            errorAddLabel.setVisible(true);
            return;
        }
        addFriendCloseButton.setVisible(false);
        addFriendProgressSwitcher.setDisplayedChild(1);
        friendService.sendFriendRequest(new FriendRequest(searchRequest[0], Integer.parseInt(searchRequest[1])))
                .observeOn(scheduler)
                .subscribe(r -> {
                    if (!r.isSuccess()) {
                        errorAddLabel.setText("Error adding friend.");
                        errorAddLabel.setVisible(true);
                    }
                    errorAddLabel.setVisible(false);
                    actor(Dialog.class, Assets.MenuUI.FRIEND_ADD_DIALOG).hide();
                    addFriendCloseButton.setVisible(true);
                    addFriendProgressSwitcher.setDisplayedChild(0);
                    refreshFriends();
                }, err -> {
                    errorAddLabel.setText("Error adding friend.");
                    errorAddLabel.setVisible(true);
                    addFriendCloseButton.setVisible(true);
                    addFriendProgressSwitcher.setDisplayedChild(0);
                });
    }

    private void acceptFriend(FriendWidget widget) {
        displayMessage("Accepting request", true);
        friendService.acceptFriendRequest(new GenericFriendRequest(widget.profile.getUserId()))
                .observeOn(scheduler)
                .subscribe(r -> {
                    if (r.isSuccess()) {
                        widget.profile.setStatus(FriendStatus.STATUS_ACCEPTED);
                        widget.update();
                        closeMessage(false);
                    }
                }, err -> {
                    displayMessage("Error accepting request", false);
                });
    }

    private void removeFriend(FriendWidget widget, String message) {
        displayMessage(message, true);
        friendService.deleteFriend(new GenericFriendRequest(widget.profile.getUserId()))
                .observeOn(scheduler)
                .subscribe(r -> {
                    if (r.isSuccess()) {
                        profile.getFriends().remove(widget.profile);
                        widget.remove();

                        // TODO A smart friend refresh system
                        refreshDisplayUI();

                        closeMessage(false);
                    }
                }, err -> {
                    displayMessage("Error removing friend", false);
                });
    }

    private void handleFriendAction(FriendWidget widget, boolean decline) {
        switch (widget.status) {
            case STATUS_PENDING://cancel sent request
                removeFriend(widget, "Cancelling request");
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

    private void displayMessage(String message, boolean process) {
        mProgressLabel.setText(message);
        mProgressImage.setVisible(process);
        mCloseButton.setVisible(!process);
        mProgressDialog.show(getStage());
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
        final Dialog friendDialog = actor(Dialog.class, Assets.MenuUI.FRIEND_ADD_DIALOG);
        final TextButton closeButton = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
        if (dialog != null && dialog.hasParent() && dialog.isVisible() && closeButton.isVisible()) {
            for (EventListener listener : closeButton.getListeners()) {
                if (listener instanceof ClickListener) {
                    ((ClickListener) listener).clicked(new InputEvent(), 0, 0);
                }
            }
        } else if (friendDialog != null && friendDialog.hasParent() && friendDialog.isVisible() && addFriendCloseButton.isVisible()) {
            friendDialog.hide();
            searchField.setText("");
            errorAddLabel.setText("");
        } else {
            parent.showMain();
        }
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
            Fuzzle fuzzle = new Fuzzle(parent, parent.getUnlockableColorizer(), profile).load(parent.getStageScreen().getLoader());

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
