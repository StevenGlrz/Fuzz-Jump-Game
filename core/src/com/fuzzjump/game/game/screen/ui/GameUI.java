package com.fuzzjump.game.game.screen.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.FuzzContext;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.GameMapParser;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.component.FuzzDialog;
import com.fuzzjump.game.game.screen.component.SnowActor;
import com.fuzzjump.game.game.screen.game.GameStage;
import com.fuzzjump.game.game.screen.game.World;
import com.fuzzjump.libgdxscreens.Textures;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import javax.inject.Inject;

import static com.fuzzjump.game.game.Assets.createDialogStyle;

public class GameUI extends StageUI {

    private final Profile profile;
    private final FuzzContext context;
    private final GameMapParser mapParser;

    private Table uiComponents;

    private TextureAtlas mapTextures;
    private SnowActor snowActor;

    @Inject
    public GameUI(Textures textures, Skin skin, Profile profile, FuzzContext context, GameMapParser parser) {
        super(textures, skin);
        this.profile = profile;
        this.context = context;
        this.mapParser = parser;
    }

    @Override
    public void init() {
        uiComponents = new Table();
        uiComponents.setFillParent(true);

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
        screen.getScreenLoader().add(() -> {
            GameMap map = screen.getMap();
            World world = new World(screen, context.getGameSeed(), map.getWidth(), map.getHeight());
            screen.setWorld(world);

            GameStage worldStage = new GameStage(screen.getStage().getViewport(), screen.getStage().getBatch(), screen, world);
            worldStage.init();
            worldStage.addGameActors(world.getPhysicsActors());
            screen.setWorldStage(worldStage);
        });

        screen.getScreenLoader().add(() -> screen.addPlayer(profile, screen.getWorld().getWidth() / 2));
    }


    /*private void drawSpecial(Table table, Batch batch, float alpha) {
        Player plr = screen.getPlayer();

        if (plr.getSpecials().getCurrentSpecial() != null) {
            if (plr.getSpecials().getCurrentDelay() > .001f) {
                if (System.currentTimeMillis() - lastSpecialFlip >= 25) {
                    lastSpecialFlip = System.currentTimeMillis();
                    SpecialType[] types = SpecialType.values();
                    //currentSpecial = game.getMapTextures().getTexture(types[screen.getRandom().nextInt(types.length)].icon);
                }
            } else {
                //currentSpecial = game.getMapTextures().getTexture(plr.getSpecials().getCurrentSpecial().icon);
            }
        } else {
            //currentSpecial = game.getMapTextures().getTexture("ui-question-mark");
        }

        float width = table.getWidth() / 2.5f;
        float height = width;
        float x = table.getWidth() / 2.3f - width / 2;
        float y = table.getHeight() / 1.75f - height / 2;
        batch.draw(currentSpecial, table.getX() + x, table.getY() + y, width, height);
    }*/

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
