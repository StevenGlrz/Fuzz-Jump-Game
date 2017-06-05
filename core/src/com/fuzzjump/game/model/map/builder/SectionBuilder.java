package com.fuzzjump.game.model.map.builder;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.ingame.actors.Platform;
import com.fuzzjump.game.model.World;
import com.fuzzjump.game.model.map.GameMap;
import com.fuzzjump.game.model.map.GameMapPlatform;

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

    public final Platform getPlatform(TextureRegion region, float x, float y, float width, float height, float physicsY, float drawHeight) {
        Platform platform = new Platform(world, x, y, width, height, physicsY, drawHeight, region);
        platformGenerated(platform);
        return platform;
    }

    public final boolean use() {
        boolean use = rank > getPreferredRank();
        if (!use) {
            int distance = Math.min(1, getPreferredRank() - rank);
            use = random.nextInt(distance) < distance / 2;
        }
        return use;
    }

    protected abstract void platformGenerated(Platform platform);

    public abstract void generateSection(float y, float height);

    protected abstract int getPreferredRank();

}
