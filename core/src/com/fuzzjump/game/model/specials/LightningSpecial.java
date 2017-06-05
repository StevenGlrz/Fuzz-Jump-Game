package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.game.ingame.actors.LightningActor;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.Textures;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/6/2015.
 */
public class LightningSpecial extends Special {

    private Animation lightningAnimation;

    @Override
    public void init() {
        super.init();
        lightningAnimation = new Animation(.025f, Textures.findAnimationFrames(atlas, "lightning-bolt-V1", 1, 11));
    }

    @Override
    public void activate(Player player) {
        IngameStage stage = (IngameStage) player.getStage();
        Player chosen = null;
        for (int i = 0, n = stage.getScreen().getPlayers().size(); i < n; i++) {
            Player check = stage.getScreen().getPlayers().valueAt(i);
            if (check == player)
                continue;
            if (chosen == null)
                chosen = check;
            if (check.getY() > chosen.getY())
                chosen = check;
        }
        if (chosen == null) return;
        LightningActor actor = new LightningActor(chosen, lightningAnimation);
        actor.setX(chosen.getX() + chosen.getWidth() / 2 - lightningAnimation.getKeyFrames()[0].getRegionWidth() / 2);
        actor.setY(chosen.getY() + 1280);
    }

    @Override
    public void deactivate(Player player) {

    }

    @Override
    public void onPreDraw(Player player, Batch batch, float time) {

    }

    @Override
    public void onPostDraw(Player player, Batch batch, float time) {

    }

    @Override
    public void act(IngameStage stage, Player player, float delta, boolean addParticle) {

    }

    @Override
    public void onInputEvent(Player player, InputEvent event) {

    }

    @Override
    public long getDuration() {
        return 0;
    }

}
