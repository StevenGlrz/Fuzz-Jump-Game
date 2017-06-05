package com.fuzzjump.game.model.map.builder.sections;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.fuzzjump.game.game.StageUI;
import com.fuzzjump.game.game.ingame.actors.Platform;
import com.fuzzjump.game.model.World;
import com.fuzzjump.game.model.map.GameMap;
import com.fuzzjump.game.model.map.GameMapPlatform;
import com.fuzzjump.game.model.map.builder.SectionBuilder;
import com.fuzzjump.game.model.profile.PlayerProfile;

import java.util.Random;

/**
 * Created by Steveadoo on 1/13/2016.
 */
public class RandomPlatformSectionBuilder extends SectionBuilder {

    //size of fuzzles plus a bit
    public static final float MIN_SPACING = 128 * 1.15f;
    public static final float MAX_SPACING = 128 * 3f;

    public static final float Y_SPREAD_BASE_MIN = 300;
    public static final float Y_SPREAD_BASE_MAX = 400;

    public static final float MULTI_Y_CHANCE__MIN = 0;
    public static final float MULTI_Y_CHANCE_MAX = 50;

    public static final float MULTI_Y_COUNT = 2;

    public static final float PLATFORM_WIDTH = 300;

    public RandomPlatformSectionBuilder(TextureAtlas atlas, StageUI ui, World world, GameMap map, Random random, int rank) {
        super(atlas, ui, world, map, random, rank);
    }

    @Override
    protected void platformGenerated(Platform platform) {

    }

    @Override
    public void generateSection(float y, float height) {
        float rankRatio = rank / PlayerProfile.MAX_RANK;
        float diff = MAX_SPACING - MIN_SPACING;
        float minXSpacing = MIN_SPACING + (diff * rankRatio);
        float maxXSpacing = MAX_SPACING - (diff * rankRatio);

        diff = Y_SPREAD_BASE_MAX - Y_SPREAD_BASE_MIN;
        float minYSpread = Y_SPREAD_BASE_MIN + (diff * rankRatio);
        float maxYSpread = Y_SPREAD_BASE_MAX + (diff * rankRatio);
        int spreadDiff = (int) (maxYSpread - minYSpread);

        diff = MULTI_Y_CHANCE_MAX - MULTI_Y_CHANCE__MIN;
        float multiYChanceMin = MULTI_Y_CHANCE__MIN + (diff * rankRatio);
        float multiYChanceMax = MULTI_Y_CHANCE_MAX + (diff * rankRatio);
        float multiYChanceThreshold = multiYChanceMin + ((multiYChanceMax - multiYChanceMin) * rankRatio);
        int multiYMax = Math.max(0, (int)Math.floor(rankRatio * MULTI_Y_COUNT));

        int loops = (int) (height / minYSpread);
        float lastX = 0;
        for(int i = 0; i < loops; i++) {
            float count = random.nextInt((int)multiYChanceMax) < multiYChanceThreshold ? 1 + random.nextInt(multiYMax) : 1;
            float genY = 0;
            for(int f = 0; f < count; f++) {
                genY = y + minYSpread + random.nextInt(spreadDiff) + random.nextFloat();
                float genX = genX(lastX, minXSpacing, maxXSpacing);
                GameMapPlatform randomMapPlatform = randomPlatform();
                TextureRegion region = atlas.findRegion(randomMapPlatform.getName());
                Platform platform = getPlatform(region, genX, genY, region.getRegionWidth(), region.getRegionHeight(), /*randomMapPlatform.getPhysicsY() randomMapPlatform.getPhysicsY()*/region.getRegionHeight(), region.getRegionHeight());
                world.getPhysicsActors().add(platform);
                if(count == 1) {
                    if (10 > 5 + random.nextInt(10)) {
                        platform.setDefaultVelocity();
                    }
                }
                lastX = genX;
            }
            y = genY;
        }
    }

    Rectangle checkRect = new Rectangle();
    Rectangle testRect = new Rectangle();

    public float genX(float lastX, float minXspacing, float maxXspacing) {
        float min = 0;
        float max = world.getWidth() - PLATFORM_WIDTH;
        if (lastX < PLATFORM_WIDTH) {
            min = lastX + PLATFORM_WIDTH + maxXspacing;
        }
        if (lastX > max - PLATFORM_WIDTH) {
            max = lastX - maxXspacing;
        }
        float x = min + random.nextInt((int)max) + random.nextFloat();
        float move = minXspacing + random.nextInt((int) (maxXspacing - minXspacing)) + random.nextFloat();
        checkRect.x = lastX;
        checkRect.y = 0;
        checkRect.width = PLATFORM_WIDTH;
        checkRect.height = 1;
        testRect.x = x;
        testRect.y = 0;
        testRect.width = PLATFORM_WIDTH;
        testRect.height = 2;
        if (testRect.overlaps(checkRect)) {
            float distToRight = Math.abs(testRect.x - (checkRect.x + checkRect.width));
            float distToLeft = Math.abs(testRect.x - checkRect.x);
            if (distToRight > distToLeft) {
                x = checkRect.x - move;
            } else {
                x = checkRect.x + checkRect.width + move;
            }
            if (x + PLATFORM_WIDTH > max) {
                x = x + PLATFORM_WIDTH - max;
            } else if (x < min) {
                x = max - min - x;
            }
        }
        return x;
    }

    @Override
    protected int getPreferredRank() {
        return 0;
    }
}
