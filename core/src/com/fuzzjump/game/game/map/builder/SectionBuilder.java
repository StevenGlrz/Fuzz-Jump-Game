package com.fuzzjump.game.game.map.builder;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.GameMapPlatform;
import com.fuzzjump.game.game.screen.game.World;
import com.fuzzjump.game.game.screen.game.actors.GamePlatform;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import java.util.Random;

/**
 * Created by Steveadoo on 1/13/2016.
 */
public abstract class SectionBuilder {

    protected final GameMap map;
    protected final World world;
    protected final int rank;
    protected final Random random;
    protected final StageUI ui;
    protected final TextureAtlas atlas;

    public SectionBuilder(TextureAtlas atlas, StageUI ui, World world, GameMap map, Random random, int rank) {
        this.atlas = atlas;
        this.ui = ui;
        this.world = world;
        this.random = random;
        this.map = map;
        this.rank = rank;
    }

    public final GameMapPlatform randomPlatform() {
        return map.getPlatforms().get(random.nextInt(map.getPlatforms().size()));
    }

    public final GamePlatform getPlatform(TextureRegion region, float x, float y, float width, float height, float physicsY, float drawHeight) {
        GamePlatform platform = new GamePlatform(world, x, y, width, height, physicsY, drawHeight, region);
        platformGenerated(platform);
        return platform;
    }

    public final boolean use() {
        return true;
    }

    protected abstract void platformGenerated(GamePlatform platform);

    public abstract void generateSection(float y, float height);

    protected abstract int getPreferredRank();

}
