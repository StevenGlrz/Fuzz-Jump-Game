package com.fuzzjump.game.game.ui;

import android.util.SparseArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.common.Lobby;
import com.fuzzjump.game.game.StageIds;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.ui.components.FJDragDownBarTable;
import com.fuzzjump.game.game.ui.components.Fuzzle;
import com.fuzzjump.game.game.ui.components.ActorSwitcher;
import com.fuzzjump.game.model.map.GameMap;
import com.fuzzjump.game.model.profile.PlayerProfile;
import com.fuzzjump.game.model.profile.Profile;

import java.util.List;

import static com.fuzzjump.game.util.Styles.*;

public class WaitingUI extends StageUI {

    private FJDragDownBarTable dropdownTable;

    private PlayerSlot[] playerSlots = new PlayerSlot[4];
    private MapSlot[] mapSlots = new MapSlot[3];

    private Label timeLabel;
    private TextButton readyButton;
    private MapSlot selectedSlot;

    @Override
    public void init() {
        dropdownTable = new FJDragDownBarTable(this, getGame().getProfile());
        setFillParent(true);

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
                progressImage.setVisible(true);
            }

        });
        progressDialog.getButtonTable().add(progressCloseButton).size(Value.percentWidth(.475f, progressDialog), Value.percentWidth(0.1315789473684211f, progressDialog)).padBottom(Value.percentHeight(.035f, progressDialog)).center().expand();

        register(StageIds.WaitingUI.PROGRESS_DIALOG, progressDialog);
        register(StageIds.WaitingUI.PROGRESS_LABEL, messageLabel);
        register(StageIds.WaitingUI.CLOSE_BUTTON, progressCloseButton);
        register(StageIds.WaitingUI.PROGRESS_IMAGE, progressImage);

        Table contentTable = dropdownTable.getContentTable();
        Table innerTable = new Table();

        Table playerTable = new Table();
        playerTable.setBackground(getTextureRegionDrawable("ui-panel-lobby"));

        Table mapTable = new Table();
        mapTable.setBackground(getTextureRegionDrawable("ui-panel-lobby1"));

        contentTable.add(innerTable).width(Value.percentWidth(.95f, contentTable)).height(Value.percentHeight(.95f, contentTable)).expand();
        innerTable.add(playerTable).size(Value.percentWidth(1f, innerTable), Value.percentWidth(0.3276619875464073f, innerTable)).row();
        innerTable.add(mapTable).size(Value.percentWidth(1f, innerTable), Value.percentWidth(0.8860788066866949f, innerTable)).row();

        Value width = Value.percentWidth(.2f, playerTable);
        Value height = Value.percentHeight(.8f, playerTable);

        for(int i = 0; i < playerSlots.length; i++) {
            playerTable.add(playerSlots[i] = new PlayerSlot()).size(width, height).center().expand();
        }

        playerSlots[0].setPlayer(game.getProfile());

        Label selectLevelLabel = new Label("Select Level", game.getSkin(), "default");
        selectLevelLabel.setAlignment(Align.topLeft | Align.center);
        mapTable.add(selectLevelLabel).expand(true, false).padLeft(Value.percentWidth(.025f, mapTable)).left();

        timeLabel = new Label("Waiting", game.getSkin(), "default");
        register(StageIds.WaitingUI.TIME_LABEL, timeLabel);
        selectLevelLabel.setAlignment(Align.topRight | Align.center);
        mapTable.add(timeLabel).expand(true, false).padRight(Value.percentWidth(.025f, mapTable)).right();

        mapTable.row();

        Value size = Value.percentWidth(.275f, mapTable);

        Table mapSlotTable = new Table();

        mapTable.add(mapSlotTable).width(Value.percentHeight(1f, mapTable)).colspan(2).expand();

        for(int i = 0; i < mapSlots.length; i++) {
            final MapSlot slot = mapSlots[i] = new MapSlot();
            mapSlotTable.add(slot).size(size).center().expand();
            slot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedSlot != null) {
                        selectedSlot.selected = false;
                    }
                    stageScreen.clicked(StageIds.WaitingUI.MAP_BUTTON, slot);
                    slot.selected = true;
                    selectedSlot = slot;
                }
            });
        }
        mapTable.row();

        Value padBottom = Value.percentHeight(.05f, mapTable);
        TextButton cancelBtn = new TextButton("Cancel", createXTBStyle(this));
        readyButton = new TextButton("Ready", createDefaultTBStyle(this));
        register(StageIds.WaitingUI.CANCEL_BUTTON, cancelBtn);
        register(StageIds.WaitingUI.READY_BUTTON, readyButton);
        //mapTable.add(new Actor()).center().expand().row();

        mapTable.add(cancelBtn).padBottom(padBottom).center().expand(true, false).size(Value.percentWidth(.45f, mapTable), Value.percentWidth(0.15957446808510638297872340425532f, mapTable));
        mapTable.add(readyButton).padBottom(padBottom).center().expand(true, false).size(Value.percentWidth(.45f, mapTable), Value.percentWidth(0.15957446808510638297872340425532f, mapTable));

        add(dropdownTable).expand().fill();
    }

    public void setPlayers(SparseArray<PlayerProfile> players) {
        for(int i = 0; i < 4; i++) {
            PlayerProfile profile = players.valueAt(i);
            playerSlots[i].setPlayer(profile);
            if (profile != null) {
                if (profile == game.getProfile()) {
                    readyButton.setText(profile.isReady() ? "Unready" : "Ready");
                }
            }
        }
    }

    public void setMapSlots(List<Lobby.MapSlot> slots) {
        for(int i = 0; i < slots.size(); i++) {
            Lobby.MapSlot slot = slots.get(i);
            MapSlot clientSlot = mapSlots[i];
            if (slot.getMapId() != -1) {
                clientSlot.setDrawable(getTextureRegionAbsolutePathDrawable(String.format("data/maps/%s/preview.png", GameMap.MAPS[slot.getMapId()])));
            } else {
                clientSlot.setDrawable(this.getTextureRegionDrawable("ui-question-mark"));
            }
            clientSlot.setUserObject(slot.getMapId());
            clientSlot.votes = slot.getVotes();
        }
    }


    @Override
    public void backPressed() {

    }

    public class PlayerSlot extends Table implements Profile.ProfileChangeEventListener {

        private final TextureRegionDrawable checkDrawable;
        private final TextureRegionDrawable xDrawable;
        private ActorSwitcher switcher;
        private PlayerProfile profile;

        private Fuzzle fuzzle;
        private Label nameLabel;
        private Image statusImage;

        public PlayerSlot() {
            this.xDrawable = WaitingUI.this.getTextureRegionDrawable("ui-close");
            this.checkDrawable = WaitingUI.this.getTextureRegionDrawable("ui-check");
            initSlot();
        }

        private void initSlot() {
            Table fuzzleTable = new Table();
            fuzzleTable.add(switcher = new ActorSwitcher()).width(Value.percentWidth(.75f, fuzzleTable)).height(Value.percentWidth(.75f, fuzzleTable)).center().expand();
            fuzzleTable.setBackground(getTextureRegionDrawable("ui-frame-friend"));
            add(fuzzleTable).width(Value.percentWidth(1f, this)).height(Value.percentWidth(1.068903558153523f, this)).padBottom(Value.percentHeight(.025f, this)).expand().row();
            add(nameLabel = new Label("Invite", game.getSkin(), "profile")).width(Value.percentWidth(1f, this)).center().expand().padBottom(Value.percentHeight(.025f, this)).row();
            nameLabel.setAlignment(Align.center);

            statusImage = new Image(xDrawable);
            addActor(statusImage);

            this.fuzzle = new Fuzzle(WaitingUI.this);
            ImageButton addButton = new ImageButton(createPlusImageBtnStyle(WaitingUI.this));
            addButton.addListener(new ClickListener() {

                public void clicked(InputEvent event, float x, float y) {
                    //show friends dialog
                }

            });

            switcher.addWidget(addButton);
            switcher.addWidget(fuzzle);

            setPlayer(null);
        }

        public void setPlayer(PlayerProfile newProfile) {
            if (newProfile == null) {
                statusImage.setVisible(false);
                switcher.setDisplayedChild(0);
                nameLabel.setText("Invite");
                return;
            }
            if (newProfile == profile)
                return;
            switcher.setDisplayedChild(1);
            statusImage.setVisible(true);
            if (this.profile != null)
                this.profile.removeChangeListener(this);
            this.profile = newProfile;
            this.fuzzle.setProfile(newProfile);
            newProfile.addChangeListener(this);

            profileChanged();
        }

        @Override
        public void profileChanged() {
            nameLabel.setText(profile.getName());
            statusImage.setDrawable(profile.isReady() ? checkDrawable : xDrawable);
        }

        @Override
        public void sizeChanged() {
            super.sizeChanged();
            float closeButtonWidth = getWidth() / 2.5f;
            statusImage.setX(getWidth() - (closeButtonWidth / 1.5f));
            statusImage.setY(getHeight() - (closeButtonWidth / 1.5f));
            statusImage.setSize(closeButtonWidth, closeButtonWidth);
        }
    }

    public class MapSlot extends Image {

        private final BitmapFont font;
        private final Drawable selectedDrawable, unselectedDrawable;

        public boolean selected;
        public int votes = 0;

        public MapSlot() {
            super(WaitingUI.this.getTextureRegionDrawable("ui-question-mark"), Scaling.fillX);
            this.font = WaitingUI.this.game.getSkin().getFont("profile-font");
            this.selectedDrawable = getTextureRegionDrawable("ui-map-selected");
            this.unselectedDrawable = getTextureRegionDrawable("ui-map-unselected");
        }

        private GlyphLayout votesLayout = new GlyphLayout();

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            float size = getWidth() / 8;
            String votesString = Integer.toString(votes);
            votesLayout.setText(font, votesString);
            font.setColor(game.getSkin().getColor("fuzzyellow"));
            batch.setColor(game.getSkin().getColor("fuzzyellow"));

            Drawable outline = selected ? selectedDrawable : unselectedDrawable;

            float drawX = getImageX() - 5;
            float drawY = getImageY() - 5;
            float width = getImageWidth() + 10;
            float height = getImageHeight() + 10;

            outline.draw(batch, getX() + drawX, getY() + drawY, width, height);
            font.draw(batch, votesLayout, getX() + getImageX() + ((size / 2) + (votesLayout.width / 2)), getY() + getImageY() + size * 1.65f);
        }



    }


}
