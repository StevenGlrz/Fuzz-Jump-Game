package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Scaling;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.FuzzContext;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.GameMapParser;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.player.unlockable.UnlockableColorizer;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.component.FuzzDialog;
import com.fuzzjump.game.game.screen.component.Fuzzle;
import com.fuzzjump.game.game.screen.component.SnowActor;
import com.fuzzjump.game.game.screen.game.actors.GamePlayer;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.screen.ScreenLoader;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.createDialogStyle;

public class GameUI extends StageUI {

    private final FuzzContext context;
    private final GameMapParser mapParser;
    private final Profile me;
    private final UnlockableColorizer unlockableColorizer;

    private Image profileSquare;
    private long lastSpecialFlip;
    
    private TextureRegion currentSpecial;
    private Image progressImage;
    private Image flagIcon, starIcon;

    private TextureAtlas mapTextures;
    private SnowActor snowActor;

    private Cell<Fuzzle> leaderCell;
    private IntMap<Fuzzle> playerLeaderSquares;
    private IntMap<Fuzzle> playerProgressSquares;

    @Inject
    public GameUI(Textures textures, Skin skin, FuzzContext context, GameMapParser parser, Profile me, UnlockableColorizer unlockableColorizer) {
        super(textures, skin);
        this.context = context;
        this.mapParser = parser;
        this.me = me;
        this.unlockableColorizer = unlockableColorizer;
    }

    @Override
    public void init() {
        setFillParent(true);
        ScreenLoader loader = getStageScreen().getScreenLoader();

        loader.add(() -> {
            Label messageLabel = new Label("Loading", getGameSkin(), "default");
            final Image spinner = new Image(textures.getTextureRegionDrawable(Assets.UI_PROGRESS_SPINNER));
            spinner.setOrigin(Align.center);
            spinner.addAction(Actions.forever(Actions.rotateBy(5f, .01f)));

            final Dialog progressDialog = new FuzzDialog("", createDialogStyle(this), 0.65f, 0.5081829277777778f);
            progressDialog.setModal(true);
            progressDialog.getContentTable().add(messageLabel).padTop(Value.percentHeight(.1f, progressDialog)).row();
            progressDialog.getContentTable().add(spinner).center().expand().size(Value.percentWidth(.25f, progressDialog));

            register(Assets.GameUI.PROGRESS_DIALOG, progressDialog);
            register(Assets.GameUI.PROGRESS_LABEL, messageLabel);
            register(Assets.GameUI.PROGRESS_IMAGE, spinner);
        });

        final GameScreen screen = (GameScreen) getStageScreen();
        final String mapName = GameMap.MAPS[context.getGameMap()];

        screen.getScreenLoader().add(() -> {
            screen.setMap(mapParser.parse(mapName));
            if (screen.getMap().snowing()) {
                snowActor = new SnowActor(this);
                add(snowActor);
            }
        });

        screen.getScreenLoader().add(() -> mapTextures = Textures.atlasFromFolder(Assets.MAP_DIR + GameMap.MAPS[context.getGameMap()] + "/"));

        loader.add(() -> {
            Table leaderPanel = new Table() {
                @Override
                public void layout() {
                    super.layout();

                    //libgdx text center is wonky
                    Actor label = actor(Label.class, Assets.GameUI.HEIGHT_LABEL);
                    label.setY(label.getY() + label.getHeight() * .05f);
                    label.setX(label.getX() - label.getWidth() * .05f);
                }
            };
            Table specialPanel = new Table() {
                @Override
                public void draw(Batch batch, float alpha) {
                    super.draw(batch, alpha);
                    drawSpecial(this, batch, alpha);
                }
            };

            leaderPanel.setBackground(textures.getTextureRegionDrawable("ui-corner-ingame"));
            specialPanel.setBackground(textures.getTextureRegionDrawable("ui-special-corner-ingame"));

            playerLeaderSquares = new IntMap<>();
            playerProgressSquares = new IntMap<>();

            for (GamePlayer player : screen.getPlayers().values()) {
                playerLeaderSquares.put(player.getProfile().getPlayerIndex(), new Fuzzle(this, this.unlockableColorizer, player.getProfile()));
                playerProgressSquares.put(player.getProfile().getPlayerIndex(), new Fuzzle(this, this.unlockableColorizer, player.getProfile()));
            }

            register(Assets.GameUI.HEIGHT_LABEL, new Label("0", getGameSkin(), "default"));

            Label heightLabel = actor(Label.class, Assets.GameUI.HEIGHT_LABEL);
            heightLabel.setAlignment(Align.left);
            leaderPanel.add(heightLabel).expand().left().center().padLeft(Value.percentWidth(.05f, leaderPanel)).size(Value.percentWidth(.5f, leaderPanel));
            leaderCell = leaderPanel.add(playerLeaderSquares.get(me.getPlayerIndex())).right().center().padRight(Value.percentWidth(.05f, leaderPanel)).size(Value.percentHeight(.5f, leaderPanel));

            starIcon = new Image(getTextures().getTextureRegionDrawable("ui-star-icon"), Scaling.fillX, Align.bottom);
            progressImage = new Image(getTextures().getTextureRegionDrawable("ui-game-progress-bar"), Scaling.stretchY) {
                @Override
                public void draw(Batch batch, float alpha) {
                    drawProgress(this, batch, alpha);
                }
            };
            flagIcon = new Image(getTextures().getTextureRegionDrawable("ui-flag-icon"), Scaling.fillX, Align.top);

            Table cornerPanelContainer = new Table();

            add(cornerPanelContainer).expand(true, false).size(Value.percentWidth(1f, this), Value.percentHeight(0.0833f, this)).top().row();

            cornerPanelContainer.add(specialPanel).size(Value.percentHeight(.104125f, this), Value.percentHeight(1f, cornerPanelContainer)).left();
            cornerPanelContainer.add(new Actor()).expand();
            cornerPanelContainer.add(leaderPanel).size(Value.percentHeight(.25f, this), Value.percentHeight(1f, cornerPanelContainer)).right();

            add(starIcon).size(Value.percentWidth(.10f, this), Value.percentWidth(.10f, this)).padTop(Value.percentHeight(.01f, this)).top().right().row();
            add(progressImage).expand().width(Value.percentHeight(.0069659442724458f, this)).padRight(Value.percentWidth(.05f)).center().right().row();
            add(flagIcon).size(Value.percentWidth(.10f, this), Value.percentWidth(.10f, this)).padBottom(Value.percentHeight(.01f, this)).bottom().right();

            starIcon.setZIndex(1);
            progressImage.setZIndex(0);
        });
    }

    @Override
    public void draw(Batch batch, float alpha) {
        final GameScreen screen = (GameScreen) getStageScreen();
        GamePlayer leader = null;
        for (GamePlayer player : screen.getPlayers().values()) {
            Fuzzle playerFuzzle = playerProgressSquares.get(player.getProfile().getPlayerIndex());
            if (playerFuzzle == null) {
                playerLeaderSquares.put(player.getProfile().getPlayerIndex(), new Fuzzle(this, this.unlockableColorizer, player.getProfile()));
            }
            if (leader == null || player.getY() > leader.getY())
                leader = player;
        }
        if (leader == null)
            return;
        leaderCell.setActor(playerProgressSquares.get(leader.getProfile().getPlayerIndex()));
        super.draw(batch, alpha);
    }

    private void drawProgress(Image actor, Batch batch, float alpha) {
        final GameScreen screen = (GameScreen) getStageScreen();
        float starY = starIcon.getY() + starIcon.getHeight() / 4;
        float flagY = flagIcon.getY() + flagIcon.getHeight() / 1.5f;
        float actorHeight = starY - flagY;
        actor.getDrawable().draw(batch, actor.getX(), flagY, actor.getImageWidth(), actorHeight);

        IntMap<GamePlayer> players = screen.getPlayers();
        GamePlayer leader = null;
        for (GamePlayer player : players.values()) {
            if (leader == null || player.getY() > leader.getY())
                leader = player;
        }

        GameMap map = screen.getMap();

        for (GamePlayer player : players.values()) {
            Fuzzle progressFuzzle = playerProgressSquares.get(player.getProfile().getPlayerIndex());

            if (progressFuzzle == null) {
                progressFuzzle = new Fuzzle(this, this.unlockableColorizer, player.getProfile());
                playerProgressSquares.put(player.getProfile().getPlayerIndex(), progressFuzzle);
            }

            float fuzzleWidth = actor.getImageWidth() * 4;
            float y = flagY + ((player.getY() - map.getGround().getHeight()) / map.getHeight()) * actorHeight;
            if (y > starIcon.getY()) continue;
            boolean disconnected = screen.getPlayer().getUpdateCounter() != player.getUpdateCounter();

            if (disconnected) {
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0.5f);
            }

            progressFuzzle.setBounds(actor.getX() - fuzzleWidth, y + fuzzleWidth / 2, fuzzleWidth, fuzzleWidth);
            progressFuzzle.draw(batch, 1.0f);
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        }
    }

    private void drawSpecial(Table table, Batch batch, float alpha) {
//        if (me.get.getCurrentSpecial() != null) {
//            if (plr.getSpecials().getCurrentDelay() > .001f) {
//                if (System.currentTimeMillis() - lastSpecialFlip >= 25) {
//                    lastSpecialFlip = System.currentTimeMillis();
//                    SpecialType[] types = SpecialType.values();
//                    //currentSpecial = game.getMapTextures().getTexture(types[screen.getRandom().nextInt(types.length)].icon);
//                }
//            } else {
//                //currentSpecial = game.getMapTextures().getTexture(plr.getSpecials().getCurrentSpecial().icon);
//            }
//        } else {
//            //currentSpecial = game.getMapTextures().getTexture("ui-question-mark");
//        }
//
//        float width = table.getWidth() / 2.5f;
//        float height = width;
//        float x = table.getWidth() / 2.3f - width / 2;
//        float y = table.getHeight() / 1.75f - height / 2;
//        batch.draw(currentSpecial, table.getX() + x, table.getY() + y, width, height);
    }

    public TextureAtlas getMapTextures() {
        return mapTextures;
    }

    public SnowActor getSnowActor() {
        return snowActor;
    }

    public void disposeMaps() {
        if (mapTextures != null) {
            mapTextures.dispose();
        }
    }

    @Override
    public void backPressed() {
    }
}
