package com.fuzzjump.game.model.specials;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.fuzzjump.game.game.ingame.IngameStage;
import com.fuzzjump.game.model.SpecialType;
import com.fuzzjump.game.game.ingame.actors.Missile;
import com.fuzzjump.game.game.ingame.actors.Player;
import com.fuzzjump.game.game.Textures;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/28/2015.
 */
public class MissileSpecial extends Special {

    private Animation explosionAnimation;
    private Animation missileAnimation;
    private Animation jetpackExhaustAnimation;

    public MissileSpecial() {
        super();
    }

    @Override
    public void init() {
        super.init();
        missileAnimation = new Animation(.05f, atlas.findRegion("item-guidedMissle-frame1"), atlas.findRegion("item-guidedMissle-frame2"));
        missileAnimation.setPlayMode(Animation.PlayMode.LOOP);
        jetpackExhaustAnimation = new Animation(.021f, Textures.findAnimationFrames(SpecialType.JETPACK.getSpecial().getAtlas(), "jetpack-exhaust-sprite", 1, 48));
        explosionAnimation = new Animation(.021f, Textures.findAnimationFrames(SpecialType.BOMB.getSpecial().getAtlas(), "explosion", 1, 9));
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
        Missile missile = new Missile(stage.getScreen().getWorld(), chosen, missileAnimation, explosionAnimation, jetpackExhaustAnimation);
        missile.setPosition(player.getX(), player.getY());
        stage.getScreen().getWorld().getPhysicsActors().add(missile);
        stage.addGameActor(missile);
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
