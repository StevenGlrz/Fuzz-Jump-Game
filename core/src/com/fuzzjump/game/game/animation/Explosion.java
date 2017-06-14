package com.fuzzjump.game.game.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.fuzzjump.game.model.SpecialType;
import com.fuzzjump.game.model.World;
import com.fuzzjump.game.game.ingame.actors.Player;

/**
 * Kerpow Games, LLC
 * Created by stephen on 2/28/2015.
 */
public class Explosion extends AnimationActor {

    public static final float SIZE = 512;

    public Explosion(World world, float x, float y, Animation explosion) {
        super(explosion);
        setX(x - SIZE / 2);
        setY(y - SIZE / 2);
        setWidth(SIZE);
        setHeight(SIZE);
        push(world);
    }

    private void push(World world) {
        Vector2 position = new Vector2(getX() + SIZE / 2, getY() + SIZE / 2);
        for (int i = 0; i < world.getScreen().getPlayers().size(); i++) {
            Player player = world.getScreen().getPlayers().get(i);
            if (player.getSpecials().getCurrentSpecial() == SpecialType.SHIELD) {
                player.getSpecials().playShieldAnim();
            } else {
                float dst = position.dst(player.position.x + player.getWidth() / 2, player.position.y + player.getHeight() / 2);
                if (dst < SIZE) {
                    if (dst < SIZE / 2) {
                        //stun and black
                    }
                    //stun for a little
                    player.stun(5f * (dst / 512f));
                    float angle = position.angle(player.position);
                    double radians = Math.toRadians(angle);
                    float offX = (float) Math.cos(radians) * (10000 * (dst / 512f));
                    float offY = (float) Math.sin(radians) * (5000 * (dst / 512f));
                    player.velocity.add(offY, offX);
                }
            }
        }
    }

}
