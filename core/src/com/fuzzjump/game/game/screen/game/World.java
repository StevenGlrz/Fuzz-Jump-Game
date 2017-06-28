package com.fuzzjump.game.game.screen.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fuzzjump.game.game.screen.GameScreen;
import com.fuzzjump.game.game.screen.game.actors.GamePlatform;
import com.fuzzjump.game.game.screen.game.actors.GamePlayer;
import com.fuzzjump.game.game.screen.game.actors.PhysicActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    private static final float FRAME_DT = .01f;

    private final GameScreen screen;

    private final List<PhysicActor> physicsActors = new ArrayList<>();
	private Random random;

	private float width;
	private float height;

	public World(GameScreen screen, String seed, float width, float height) {
		this.width = width;
		this.height = height;
        this.screen = screen;
		this.random = new Random(seed.hashCode());
	}

    float t = 0.0f;

    public long frame = 0;
    long currentTime = System.currentTimeMillis();
    float accumulator = 0.0f;

    private ArrayList<PhysicActor> remove = new ArrayList<PhysicActor>();

	public void act(Stage worldStage) {

        long newTime = System.currentTimeMillis();
        float frameTime = (newTime - currentTime) / 1000f;

        if (frameTime > .25f)
            frameTime = .25f;
        currentTime = newTime;

        accumulator += frameTime;

        while(accumulator >= FRAME_DT) {
            for (int i = 0, n = physicsActors.size(); i < n; i++) {
                PhysicActor actor = physicsActors.get(i);
                if (actor.velocityModifier.x == 0 && actor.velocityModifier.y == 0) {
                    continue;
                }
                actor.previousPosition.x = actor.position.x;
                actor.previousPosition.y = actor.position.y;
                simulatePlayer(actor.position, actor.velocity, t, FRAME_DT, actor.velocityModifier);
            }
            frame++;
            t += FRAME_DT;
            accumulator -= FRAME_DT;
        }

        float alpha = accumulator / FRAME_DT;
        float invAlpha = 1 - alpha;
        for (int i = 0, n = physicsActors.size(); i < n; i++) {
            PhysicActor actor = physicsActors.get(i);

            if (actor.velocityModifier.x != 0 || actor.velocityModifier.y != 0) {
                float visibleX = actor.position.x * alpha + actor.previousPosition.x * invAlpha;
                float visibleY = actor.position.y * alpha + actor.previousPosition.y * invAlpha;
                actor.setPosition(visibleX, visibleY);
            }

            intersection(actor);

            if (actor.removed) {
                remove.add(actor);
            }
        }

        for (int i = 0, n = remove.size(); i < n; i++) {
            physicsActors.remove(remove.get(i));
        }
        remove.clear();

        worldStage.act(FRAME_DT);
	}

    private float[] a = new float[4], b = new float[4], c = new float [4], d = new float[4];

    private void simulatePlayer(Vector2 position, Vector2 velocity, float t, float dt, Vector2 mod) {
        eval(a, position, velocity, t, 0, 0, 0, 0, 0, mod);
        eval(b, position, velocity, t, dt * .5f, a[0], a[1], a[2], a[3], mod);
        eval(c, position, velocity, t, dt * .5f, b[0], b[1], b[2], b[3], mod);
        eval(d, position, velocity, t, dt, c[0], c[1], c[2], c[3], mod);

        float dPositionxDt = (1.0f / 6.0f) * (a[0] + 2.0f * (b[0] + c[0]) + d[0]);
        float dPositionyDt = (1.0f / 6.0f) * (a[1] + 2.0f * (b[1] + c[1]) + d[1]);

        float dVelocityxDt = (1.0f / 6.0f) * (a[2] + 2.0f * (b[2] + c[2]) + d[2]);
        float dVelocityyDt = (1.0f / 6.0f) * (a[3] + 2.0f * (b[3] + c[3]) + d[3]);

        position.x = position.x + dPositionxDt * dt;
        position.y = position.y + dPositionyDt * dt;

        velocity.x = velocity.x + dVelocityxDt * dt;
        velocity.y = velocity.y + dVelocityyDt * dt;
    }

    //returns an array, [0, 1] = velocity x/y [2,3] = acceleration x/y
    public void eval(float[] array, Vector2 initialPosition, Vector2 initialVelocity, float t, float dt, float dVX, float dVY,  float dAX, float dAY, Vector2 mod) {
        array[0] = initialVelocity.x + (dAX * dt);
        array[1] = initialVelocity.y + (dAY * dt);
        array[2] = 25 * mod.x;
        array[3] = -2000 * mod.y;
    }

    public boolean intersection(PhysicActor physicActor) {
		for(int i = 0; i < physicsActors.size(); i++) {
            PhysicActor check = physicsActors.get(i);
            if (check == physicActor || !physicActor.interested(check)) continue;
            //we can probably abstract this out but for now it's w/e
            if (check instanceof GamePlatform && physicActor instanceof GamePlayer) {
                GamePlayer player = (GamePlayer) physicActor;
                if (check.hitbox.overlaps(player.getPlatformHitbox()) || check.hitbox.contains(player.getPlatformHitbox())) {
                    check.hit(physicActor);
                    if (((GamePlatform) check).isWinner()) {
                        screen.playerFinished(player);
                    }
                }
            } else if (physicActor.hitbox.overlaps(check.hitbox) || physicActor.hitbox.contains(check.hitbox)) {
                physicActor.hit(check);
            }
		}
		return false;
	}

    public List<PhysicActor> getPhysicsActors() {
        return physicsActors;
    }

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public Random getRandom() {
		return random;
	}

    public GameScreen getScreen() {
        return screen;
    }
}
