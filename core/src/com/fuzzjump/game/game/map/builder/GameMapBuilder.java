package com.fuzzjump.game.game.map.builder;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.fuzzjump.game.game.map.GameMap;
import com.fuzzjump.game.game.map.builder.sections.RandomPlatformSectionBuilder;
import com.fuzzjump.game.game.screen.game.World;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import java.util.Random;

/**
 * Created by Steveadoo on 1/13/2016.
 */
public class GameMapBuilder {

    private static final int MIN_Y_ADVANCE = 1000;
    private static final int MAX_Y_ADVANCE = 10000;

    private final GameMap map;
    private final SectionBuilder[] sectionBuilders;
    private final Random random;

    public GameMapBuilder(TextureAtlas atlas, StageUI ui, World world, Random random, int rank, GameMap map) {
        this.map = map;
        this.random = random;
        this.sectionBuilders = new SectionBuilder[] {
            new RandomPlatformSectionBuilder(atlas, ui, world, map, random, rank)
        };
    }


    public void build() {
        int y = 0;
        while(y < map.getHeight()) {
            SectionBuilder builder = randomBuilder();
            if (!builder.use()) {
                continue;
            }
            float height = Math.min((int) MIN_Y_ADVANCE + random.nextInt(MAX_Y_ADVANCE - MIN_Y_ADVANCE), map.getHeight() - y);
            builder.generateSection(y, height);
            y += height;
        }
    }

    private SectionBuilder randomBuilder() {
        return sectionBuilders[random.nextInt(sectionBuilders.length)];
    }
}
