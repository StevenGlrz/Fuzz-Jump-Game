package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.FriendProfile;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.component.ActorSwitcher;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.game.screen.component.SearchField;
import com.fuzzjump.game.util.Helper;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fuzzjump.game.game.Assets.createCloseBtnStyle;
import static com.fuzzjump.game.game.Assets.createDefaultTBStyle;
import static com.fuzzjump.game.game.Assets.createETxtFieldStyle;
import static com.fuzzjump.game.game.Assets.createSmallTBStyle;

public class FriendsUI extends StageUI {

    private final MenuUI parent;
    private final Profile profile;

    private TextField searchField;
    private Table friendsList;
    private ScrollPane friendsScroller;

    private Value friendSquareWidth;
    private Value friendSquareHeight;
    private Value friendSquarePadBottom;
    private Value friendSquarePadSides;

    private ActorSwitcher friendsListSwitcher;

    private final Map<Integer, FriendProfile> profileMap = new HashMap<>();

    private Action waitSearchAction = null; // TODO Remove once implemented new API

    public FriendsUI(MenuUI parent) {
        super(parent.getTextures(), parent.getGameSkin());
        this.stageScreen = parent.getStageScreen();
        this.parent = parent;
        this.profile = parent.getProfile();
    }

    @Override
    public void init() {
        setBackground(textures.getTextureRegionDrawable(Assets.UI_PANEL_FRIENDS));

        final Drawable search = textures.getTextureRegionDrawable(Assets.UI_SEARCH);

        searchField = new SearchField(search, "", createETxtFieldStyle(this)) ;
        searchField.setMessageText("Search by name");
        searchField.getStyle().messageFontColor = Color.WHITE;
        searchField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 35);
        searchField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);

        searchField.setTextFieldListener((textField, c) -> updateSearch(textField.getText()));

        add(searchField).top().center().padTop(Value.percentHeight(.025f, this)).padBottom(Value.percentHeight(.015f, this)).size(Value.percentWidth(.95f, this), Value.percentHeight(.15f, this));
        row();

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
        Label nofriendsLabel = new Label("No friends", getGameSkin());
        noneLabel.setAlignment(Align.center);
        nofriendsLabel.setAlignment(Align.center);

        friendsListSwitcher.addWidget(noneLabel);
        friendsListSwitcher.addWidget(nofriendsLabel);

        TextButton backButton = new TextButton("Back", createDefaultTBStyle(this));

        add(backButton).size(Value.percentWidth(0.475f, this), Value.percentWidth(0.0875f, this)).padBottom(Value.percentHeight(.025f, this));


        friendSquareWidth = Value.percentWidth(.3f, friendsScroller);
        friendSquareHeight = Value.percentWidth(.4f, friendsScroller);
        friendSquarePadBottom = Value.percentWidth(.033f, friendsScroller);
        friendSquarePadSides = Value.percentWidth(.025f, friendsScroller);

        Helper.addClickAction(backButton, (e, x, y) -> backPressed());

        refresh(profile.getFriends());
    }

    private void updateSearch(String text) {
        if (text.length() > 0) {
            if (waitSearchAction != null) {
                parent.removeAction(waitSearchAction);
                System.out.println("Removing old search action");
            }
            System.out.println("Adding new action");
            parent.addAction(waitSearchAction = Actions.delay(.15f, Actions.run(this::search)));
        } else {
            refresh(null);
        }
    }

    private void search() {
        /*if (searchRequest != null) {
            searchRequest.cancel();
        }*/
        System.out.println("Searching");
        String username = searchField.getText();
        if (username.length() == 0) {
            refresh(null);
            return;
        }
        friendsListSwitcher.setDisplayedChild(1);
//        searchRequest = new SearchUsersRequest(profile, username);
//        searchRequest.connect(this);
    }


    public void refreshFriends() {
        searchField.setDisabled(true);
        friendsListSwitcher.setDisplayedChild(1);
    }

    public void refresh(List<FriendProfile> profiles) {
        friendsList.clear();
        int index = 0;
        if (profiles == null) {
            profiles = profile.getFriends();
            if (profiles.size() == 0) {
                index = 3;
            }
        } else if (profiles.size() == 0) {
            index = 2;
        }
        for (int i = 0, n = profiles.size(); i < n; i++) {
            FriendProfile friend = profiles.get(i);
            if (profileMap.containsKey(friend.getProfileId())) {
                profiles.set(i, profileMap.get(friend.getProfileId()));
            }
        }
        profileMap.clear();
        friendsListSwitcher.setDisplayedChild(index);
        for (int i = 0, n = profiles.size(); i < n; i++) {
            int row = i / 3;
            int column = i % 3;
            if (row > 0 && column == 0)
                friendsList.row();
            FriendProfile friend = profiles.get(i);
            Cell cell = friendsList.add(new FriendWidget(friend)).center().top().width(friendSquareWidth).height(friendSquareHeight)
                    .padBottom(friendSquarePadBottom)
                    .padTop(friendSquarePadBottom)
                    .expand();
            profileMap.put(friend.getProfileId(), friend);
            //if (column == 1)
            //    cell.padLeft(friendSquarePadSides).padRight(friendSquarePadSides);
            if (i == profiles.size() - 1 && column < 2) {
                int fillCount = (2 - column);
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


    public void changeStatus(final FriendWidget widget, int newStatus) {
        final Dialog dialog = parent.actor(Dialog.class, Assets.MenuUI.PROGRESS_DIALOG);
        final TextButton closeButton = parent.actor(Assets.MenuUI.CLOSE_BUTTON);
        final Image progressImage = parent.actor(Assets.MenuUI.PROGRESS_IMAGE);
        final Label messageLabel = parent.actor(Assets.MenuUI.PROGRESS_LABEL);
        messageLabel.setText(getStatusLabel(newStatus));
        progressImage.setVisible(true);
        closeButton.setVisible(false);
        dialog.show(getStage());
    }

    public String getStatusLabel(int status) {
        switch (status) {
            case FriendProfile.STATUS_NONE:
                return "Removing friend";
            case FriendProfile.STATUS_ACCEPTED:
                return "Accepting friend request";
            case FriendProfile.STATUS_SENT:
                return "Sending friend request";
            default:
                return "Processing";
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
    

    public void onShow() {
        searchField.setText("");
        refreshFriends();
    }

    public class FriendWidget extends Table {

        private final FriendProfile profile;

        private int status;
        private TextButton button;
        private Label nameLabel;
        private ImageButton closeButton;

        public FriendWidget(FriendProfile profile) {
            this.profile = profile;
            this.status = profile.getStatus();
            build();
            profileChanged();
        }

        private void build() {
            Fuzzle fuzzle = new Fuzzle(parent, parent.getUnlockableColorizer(), parent.getProfile());
            // TODO Load fuzzle
            Table fuzzleTable = new Table();
            fuzzleTable.add(fuzzle).width(Value.percentWidth(.75f, fuzzleTable)).height(Value.percentHeight(.75f, fuzzleTable)).center().expand();
            fuzzleTable.setBackground(textures.getTextureRegionDrawable("ui-frame-friend"));
            add(fuzzleTable).width(Value.percentWidth(1f, this)).height(Value.percentWidth(1.068903558153523f, this)).padBottom(Value.percentHeight(.025f, this)).expand().row();
            add(nameLabel = new Label(profile.getDisplayName(), getGameSkin(), "profile")).width(Value.percentWidth(1f, this)).center().expand().padBottom(Value.percentHeight(.025f, this)).row();
            add(button = new TextButton("Action", createSmallTBStyle(parent))).width(Value.percentWidth(1f, this)).height(Value.percentWidth(0.1842105263157895f, this));
            nameLabel.setAlignment(Align.center);

            closeButton = new ImageButton(createCloseBtnStyle(parent));
            addActor(closeButton);
            closeButton.setVisible(false);

            Helper.addClickAction(button, (e, x, y) -> {
                switch (status) {
                    case FriendProfile.STATUS_NONE://send request
                        changeStatus(FriendWidget.this, FriendProfile.STATUS_SENT);
                        break;
                    case FriendProfile.STATUS_SENT://cancel sent request
                        changeStatus(FriendWidget.this, FriendProfile.STATUS_NONE);
                        break;
                    case FriendProfile.STATUS_INCOMING://accept incoming
                        changeStatus(FriendWidget.this, FriendProfile.STATUS_ACCEPTED);
                        break;
                    case FriendProfile.STATUS_ACCEPTED://remove friend
                        changeStatus(FriendWidget.this, FriendProfile.STATUS_NONE);
                        break;
                }
            });



            //close only visible when incoming
            Helper.addClickAction(closeButton, (e, x, y) -> changeStatus(FriendWidget.this, FriendProfile.STATUS_NONE));
        }

        @Override
        public void sizeChanged() {
            super.sizeChanged();
            float closeButtonWidth = getWidth() / 4;
            closeButton.setX(getWidth() - closeButtonWidth);
            closeButton.setY(getHeight() - closeButtonWidth);
            closeButton.setSize(closeButtonWidth, closeButtonWidth);
        }

        // TODO Re-add
        public void profileChanged() {
            boolean refresh = false;
            //check if the status changed from pending to accepted because we will need to relayout then
            if (status < 2 && profile.getStatus() == 2 && searchField.getText().isEmpty()) {
                refresh = true;
            }
            status = profile.getStatus();
            switch (status) {
                case FriendProfile.STATUS_NONE:
                    button.setText("Add");
                    closeButton.setVisible(false);
                    break;
                case FriendProfile.STATUS_SENT:
                    button.setText("Cancel");
                    closeButton.setVisible(false);
                    break;
                case FriendProfile.STATUS_INCOMING:
                    button.setText("Accept");
                    closeButton.setVisible(true);
                    break;
                case FriendProfile.STATUS_ACCEPTED:
                    button.setText("Remove");
                    closeButton.setVisible(false);
                    break;
            }
            if (refresh) {
                refreshFriends();
            }
        }
    }


}
