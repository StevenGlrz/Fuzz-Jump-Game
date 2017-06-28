package com.fuzzjump.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fuzzjump.game.game.screen.game.actors.SnowActor;
import com.fuzzjump.libgdxscreens.screen.ScreenHandler;
import com.fuzzjump.libgdxscreens.screen.StageScreen;
import com.fuzzjump.libgdxscreens.screen.StageUI;

import java.util.Calendar;

public abstract class SnowScreen<TUI extends StageUI> extends StageScreen<TUI> {

    protected static SnowActor snowActor;

    public SnowScreen(TUI ui) {
        super(ui);
    }

    public final void init(Stage stage, ScreenHandler handler) {
        super.init(stage, handler);
        if (snowActor == null) {
            snowActor = new SnowActor(ui().getTextures().getTextures(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        snowActor.setEnabled(Calendar.getInstance().get(Calendar.MONTH) == 11);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        snowActor.draw(getStage().getBatch(), 1f, snowVelocity());
    }

    public float snowVelocity() {
        return 1f;
    }

}
