package com.fuzzjump.libgdxscreens.screen;

import com.badlogic.gdx.Gdx;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Steven Galarza on 6/18/2017.
 */
public class ScreenLoader {

    /**
     * The task queue
     */
    private final Queue<Runnable> tasks = new LinkedList<>();

    /**
     * Callback for when all tasks are done.
     */
    private Runnable onDone;

    /**
     * Processes loading tasks every other frame.
     * NOTE: This function is called on StageSCreen#onPostRender by default
     * @return true if the frame was used for loading a task, false otherwise
     */
    public boolean process() {
        if (isDone()) {
            if (onDone != null) {
                onDone.run();
                onDone = null;
            }
            return false;
        }

        // This can be a bit smarter
        if (Gdx.graphics.getFrameId() % 2 == 0) {
            Runnable loadTask = tasks.poll();
            loadTask.run();
            return true;
        } else {
            // Do load animation ...
            return false;
        }
    }

    public void onDone(Runnable onDone) {
        if (isDone()) {
            onDone.run();
        } else {
            this.onDone = onDone;
        }
    }

    public boolean isDone() {
        return tasks.isEmpty();
    }

    public void add(Runnable task) {
        tasks.add(task);
    }

    public int getTaskSize() {
        return tasks.size();
    }
}
