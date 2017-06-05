package com.fuzzjump.game.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageScreen;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.ui.components.ActorSwitcher;
import com.fuzzjump.game.game.ui.components.Fuzzle;
import com.fuzzjump.game.model.character.Unlockable;
import com.fuzzjump.game.model.character.UnlockableDefinition;
import com.fuzzjump.game.model.profile.FriendProfile;
import com.fuzzjump.game.model.profile.PlayerProfile;
import com.fuzzjump.game.model.profile.Profile;
import com.fuzzjump.game.net.requests.FriendWebRequest;
import com.fuzzjump.game.net.requests.GetAppearanceRequest;
import com.fuzzjump.game.net.requests.GetFriendsWebRequest;
import com.fuzzjump.game.net.requests.SearchUsersRequest;
import com.fuzzjump.game.net.requests.WebRequest;
import com.fuzzjump.game.net.requests.WebRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.fuzzjump.game.util.Styles.*;

public class FriendsUI extends StageUI implements WebRequestCallback {

    private MenuUI parent;
    private TextField searchField;
    private Table friendsList;
    private ScrollPane friendsScroller;

    private Value friendSquareWidth;
    private Value friendSquareHeight;
    private Value friendSquarePadBottom;
    private Value friendSquarePadSides;

    private ActorSwitcher friendsListSwitcher;

    public FriendsUI(StageScreen stage, FuzzJump game, MenuUI parent) {
        this.game = game;
        this.stageScreen = stage;
        this.parent = parent;
        init();
    }

    @Override
    public TextureRegionDrawable getTextureRegionDrawable(String name) {
        return parent.getTextureRegionDrawable(name);
    }

    @Override
    public TextureRegion getTexture(String name) {
        return parent.getTexture(name);
    }

    @Override
    public TextureRegion getColored(UnlockableDefinition definition, int colorIndex, boolean hardref) {
        return parent.getColored(definition, colorIndex, hardref);
    }

    @Override
    public TextureRegion getColored(Unlockable unlockable, boolean hardref) {
        return parent.getColored(unlockable, hardref);
    }

    @Override
    public void init() {

        setBackground(getTextureRegionDrawable("ui-panel-friends"));

        final Drawable search = getTextureRegionDrawable("ui-search");
        searchField = new TextField("", createETxtFieldStyle(this)) {
            @Override
            public void draw(Batch batch, float alpha) {
                super.draw(batch, alpha);

                float errorDrawableSize = searchField.getHeight() * .5f;
                search.draw(batch, searchField.getX() + searchField.getWidth() - errorDrawableSize - searchField.getStyle().background.getRightWidth(), searchField.getY() + searchField.getHeight() / 2.0f - errorDrawableSize / 2f, errorDrawableSize, errorDrawableSize);
            }
        };
        searchField.setMessageText("Search by email or name");
        searchField.getStyle().messageFontColor = Color.WHITE;

        searchField.getStyle().background.setLeftWidth(Gdx.graphics.getWidth() / 35);
        searchField.getStyle().background.setRightWidth(Gdx.graphics.getWidth() / 35);

        searchField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                updateSearch(textField.getText());
            }
        });

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

        Image progressImage = new Image(getTextureRegionDrawable("ui-progressspinner")) {
            @Override
            public void sizeChanged() {
                super.sizeChanged();
                setOrigin(Align.center);
            }
        };
        progressImage.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));
        friendsListSwitcher.addWidget(progressImage, Value.percentWidth(.25f, friendsListSwitcher), Value.percentWidth(.25f, friendsListSwitcher), Align.center);

        add(friendsListSwitcher).expand().fillY().top().width(Value.percentWidth(.95f, this)).padBottom(Value.percentHeight(.015f, this)).row();

        Label noneLabel = new Label("No profiles found", game.getSkin());
        Label nofriendsLabel = new Label("No friends", game.getSkin());
        noneLabel.setAlignment(Align.center);
        nofriendsLabel.setAlignment(Align.center);

        friendsListSwitcher.addWidget(noneLabel);
        friendsListSwitcher.addWidget(nofriendsLabel);

        TextButton backButton = new TextButton("Back", createDefaultTBStyle(this));

        add(backButton).size(Value.percentWidth(0.475f, this), Value.percentWidth(0.0875f, this)).padBottom(Value.percentHeight(.025f, this));

        backButton.addListener(new ClickListener() {

            public void clicked(InputEvent event, float x, float y) {
                backPressed();
            }

        });

        friendSquareWidth = Value.percentWidth(.3f, friendsScroller);
        friendSquareHeight = Value.percentWidth(.4f, friendsScroller);
        friendSquarePadBottom = Value.percentWidth(.033f, friendsScroller);
        friendSquarePadSides = Value.percentWidth(.025f, friendsScroller);

        refresh(null);
    }

    Runnable updateSearchRunnable = new Runnable() {
        @Override
        public void run() {
            search();
        }
    };
    Action waitSearchAction;
    WebRequest searchRequest;

    private void updateSearch(String text) {
        if (text.length() > 0) {
            if (waitSearchAction != null) {
                parent.removeAction(waitSearchAction);
                System.out.println("Removing old search action");
            }
            System.out.println("Adding new action");
            parent.addAction(waitSearchAction = Actions.delay(.15f, Actions.run(updateSearchRunnable)));
        } else {
            refresh(null);
        }
    }

    private void search() {
        if (searchRequest != null)
            searchRequest.cancel();
        System.out.println("Searching");
        String username = searchField.getText();
        if (username.length() == 0) {
            refresh(null);
            return;
        }
        friendsListSwitcher.setDisplayedChild(1);
        searchRequest = new SearchUsersRequest(game.getProfile(), username);
        searchRequest.connect(this);
    }


    GetAppearanceRequest getAppearanceRequest;
    Runnable downloadAppearanceRunnable = new Runnable() {
        @Override
        public void run() {
            downloadAppearances();
        }
    };
    Action downloadWaitAction = Actions.delay(.5f, Actions.run(downloadAppearanceRunnable));
    HashMap<Long, FriendProfile> profileMap = new HashMap<>();

    public void refreshFriends() {
        searchField.setDisabled(true);
        WebRequest getFriendsRequest = new GetFriendsWebRequest(game.getProfile());
        friendsListSwitcher.setDisplayedChild(1);
        getFriendsRequest.connect(new WebRequestCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null && response.has(WebRequest.RESPONSE_KEY) && response.getInt(WebRequest.RESPONSE_KEY) == WebRequest.SUCCESS) {
                        game.getProfile().getFriends().load(response.getJSONObject(WebRequest.PAYLOAD_KEY));
                        game.getProfile().save();
                        refresh(null);
                        searchField.setDisabled(false);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refresh(null);
                searchField.setDisabled(false);
            }
        });
    }

    private List<FriendProfile> currentList;

    public void refresh(List<FriendProfile> profiles) {
        currentList = profiles;
        friendsList.clear();
        int index = 0;
        if (profiles == null) {
            profiles = game.getProfile().getFriends().getFriends();
            if (profiles.size() == 0)
                index = 3;
        } else if (profiles.size() == 0) {
            index = 2;
        }
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
            if (row > 0 && column == 0)
                friendsList.row();
            FriendProfile friend = profiles.get(i);
            Cell cell = friendsList.add(new FriendWidget(friend)).center().top().width(friendSquareWidth).height(friendSquareHeight)
                    .padBottom(friendSquarePadBottom)
                    .padTop(friendSquarePadBottom)
                    .expand();
            profileMap.put(friend.getUserId(), friend);
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

        appearanceCounter = 0;
        downloadMore = false;
        if (downloadWaitAction != null)
            parent.removeAction(downloadWaitAction);
        parent.addAction(downloadWaitAction = Actions.delay(.5f, Actions.run(downloadAppearanceRunnable)));

    }


    private WebRequestCallback getAppearanceCallback = new WebRequestCallback() {
        @Override
        public void onResponse(JSONObject response) {
            updateAppearances(response);
        }
    };

    private boolean downloadMore = false;
    private int appearanceCounter = 0;

    public void downloadAppearances() {
        if (getAppearanceRequest != null) {
            getAppearanceRequest.cancel();
        }
        SnapshotArray<Actor> friends = friendsList.getChildren();
        long[] ids = new long[5];
        int idCounter = 0;
        for(int i = appearanceCounter; i < friends.size; i++) {
            if (friends.get(i) instanceof FriendWidget) {
                FriendWidget widget = (FriendWidget) friends.get(i);
                if (widget.profile.getAppearance().loaded())
                    continue;
                ids[idCounter++] = widget.profile.getUserId();
                if (idCounter >= 5) {
                    appearanceCounter = i;
                    downloadMore = true;
                    break;
                }
            }
        }
        long[] friendIds = new long[idCounter];
        System.arraycopy(ids, 0, friendIds, 0, idCounter);
        getAppearanceRequest = new GetAppearanceRequest(friendIds, true);
        getAppearanceRequest.connect(getAppearanceCallback);
    }

    public void updateAppearances(JSONObject response) {
        try {
            if (response.has(WebRequest.RESPONSE_KEY) && response.getInt(WebRequest.RESPONSE_KEY) == WebRequest.SUCCESS) {
                JSONArray payload = response.getJSONArray(WebRequest.PAYLOAD_KEY);
                for (int i = 0; i < payload.length(); i++) {
                    JSONObject appearance = payload.getJSONObject(i);
                    long userId = appearance.getLong("UserId");
                    if (!profileMap.containsKey(userId))
                        continue;
                    FriendProfile profile = profileMap.get(userId);
                    profile.setName(appearance.getString("DisplayName"));
                    profile.getAppearance().load(appearance);
                }
                if (downloadMore)
                    downloadAppearances();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeStatus(final FriendWidget widget, int newStatus) {
        final Dialog dialog = parent.actor(Dialog.class, StageIds.MenuUI.PROGRESS_DIALOG);
        final TextButton closeButton = parent.actor(StageIds.MenuUI.CLOSE_BUTTON);
        final Image progressImage = parent.actor(StageIds.MenuUI.PROGRESS_IMAGE);
        final Label messageLabel = parent.actor(StageIds.MenuUI.PROGRESS_LABEL);
        messageLabel.setText(getStatusLabel(newStatus));
        progressImage.setVisible(true);
        closeButton.setVisible(false);
        dialog.show(game.getStage());
        WebRequest request = new FriendWebRequest(game.getProfile(), widget.profile.getUserId(), newStatus);
        request.connect(new WebRequestCallback() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null && response.has(WebRequest.RESPONSE_KEY) && response.getInt(WebRequest.RESPONSE_KEY) == WebRequest.SUCCESS) {
                        List<FriendProfile> list = currentList;
                        int status = response.getJSONObject(WebRequest.PAYLOAD_KEY).getInt("Status");
                        widget.profile.setStatus(status);
                        if (!game.getProfile().getFriends().contains(widget.profile.getUserId()) && status >= FriendProfile.STATUS_SENT) {
                            list.remove(widget.profile);
                            widget.remove();
                            game.getProfile().getFriends().getFriends().add(widget.profile);
                            refresh(list);
                        } else if (game.getProfile().getFriends().contains(widget.profile.getUserId()) && status == FriendProfile.STATUS_NONE) {
                            widget.remove();
                            game.getProfile().getFriends().getFriends().remove(widget.profile);
                            refresh(game.getProfile().getFriends().getFriends());
                        }
                        dialog.hide();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressImage.setVisible(false);
                closeButton.setVisible(true);
                messageLabel.setText("Error processing\nrequest");
            }
        });
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

        final Dialog dialog = parent.actor(Dialog.class, StageIds.MenuUI.PROGRESS_DIALOG);
        if (dialog.isVisible()) {
            final TextButton closeButton = parent.actor(StageIds.MenuUI.CLOSE_BUTTON);
            if (closeButton.isVisible()) {for(EventListener listener : closeButton.getListeners())
                if (listener instanceof ClickListener)
                    ((ClickListener)listener).clicked(new InputEvent(), 0, 0);
            }
        }
        ((MenuUI) parent).showMain();
    }


    @Override
    public void onResponse(JSONObject response) {
        LinkedList<FriendProfile> profiles = new LinkedList<>();
        try {
            if (!response.has(WebRequest.RESPONSE_KEY))
                return;
            if (response.getInt(WebRequest.RESPONSE_KEY) != WebRequest.SUCCESS)
                return;
            JSONArray results = response.getJSONArray(WebRequest.PAYLOAD_KEY);
            SnapshotArray<Actor> inList = friendsList.getChildren();
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                FriendProfile profile = null;
                for(Actor actor : inList) {
                    if (actor instanceof FriendWidget) {
                        FriendWidget friendWidget = (FriendWidget) actor;
                        if (friendWidget.profile.getUserId() == result.getLong("UserId")) {
                            profile = friendWidget.profile;
                            break;
                        }
                    }
                }
                if (profile == null) {
                    profile = new FriendProfile(result.getString("DisplayName"), result.getLong("UserId"));
                }
                if (profile.getUserId() == game.getProfile().getUserId() || game.getProfile().getFriends().contains(profile.getUserId()))
                    continue;
                profile.setStatus(-1);
                profiles.add(profile);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh(profiles);
    }

    public void showing() {
        searchField.setText("");
        refreshFriends();
    }

    public class FriendWidget extends Table implements Profile.ProfileChangeEventListener {

        private final FriendProfile profile;

        private int status;
        private TextButton button;
        private Label nameLabel;
        private ImageButton closeButton;

        public FriendWidget(FriendProfile profile) {
            this.profile = profile;
            this.status = profile.getStatus();
            profile.addChangeListener(this);
            build();
            profileChanged();
        }

        private void build() {
            Fuzzle fuzzle = new Fuzzle(parent, profile);
            Table fuzzleTable = new Table();
            fuzzleTable.add(fuzzle).width(Value.percentWidth(.75f, fuzzleTable)).height(Value.percentHeight(.75f, fuzzleTable)).center().expand();
            fuzzleTable.setBackground(getTextureRegionDrawable("ui-frame-friend"));
            add(fuzzleTable).width(Value.percentWidth(1f, this)).height(Value.percentWidth(1.068903558153523f, this)).padBottom(Value.percentHeight(.025f, this)).expand().row();
            add(nameLabel = new Label(profile.getName(), game.getSkin(), "profile")).width(Value.percentWidth(1f, this)).center().expand().padBottom(Value.percentHeight(.025f, this)).row();
            add(button = new TextButton("Action", createSmallTBStyle(parent))).width(Value.percentWidth(1f, this)).height(Value.percentWidth(0.1842105263157895f, this));
            nameLabel.setAlignment(Align.center);

            closeButton = new ImageButton(createCloseBtnStyle(parent));
            addActor(closeButton);
            closeButton.setVisible(false);

            button.addListener(new ClickListener() {

                public void clicked(InputEvent e, float x, float y) {
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
                }

            });

            //close only visible when incoming
            closeButton.addListener(new ClickListener() {

                public void clicked(InputEvent e, float x, float y) {
                    changeStatus(FriendWidget.this, FriendProfile.STATUS_NONE);
                }

            });
        }

        @Override
        public void sizeChanged() {
            super.sizeChanged();
            float closeButtonWidth = getWidth() / 4;
            closeButton.setX(getWidth() - closeButtonWidth);
            closeButton.setY(getHeight() - closeButtonWidth);
            closeButton.setSize(closeButtonWidth, closeButtonWidth);
        }

        @Override
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
            if (refresh)
                refreshFriends();
        }
    }


}
