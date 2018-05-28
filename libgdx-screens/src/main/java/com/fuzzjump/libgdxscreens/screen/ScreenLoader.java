package com.fuzzjump.libgdxscreens.screen;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Steven Galarza on 6/18/2017.
 */
public class ScreenLoader {

    /**
     * The task queue
     */
    private final Queue<Runnable> tasks = new ArrayDeque<>();

    /**
     * Callback for when all tasks are done.
     */
    private Runnable onDone;

    /**
     * Flag on whether to skip loading on next frame
     */
    private boolean skipNext;

    /**
     * Processes loading tasks every other frame.
     * NOTE: This function is called on StageScreen#onPostRender by default
     */
    public void process() {
        if (isDone()) {
            if (onDone != null) {
                onDone.run();
                onDone = null;
            }
            skipNext = false;
            return;
        }
        if (!skipNext) {
            long timeTaken = 0L;
            long timeStamp = System.currentTimeMillis();
            Runnable loadTask;

            // 30 ms is enough for 1 frame
            while (timeTaken < 30L && (loadTask = tasks.poll()) != null) {
                loadTask.run();

                long now = System.currentTimeMillis();
                timeTaken += now - timeStamp;
                timeStamp = now;
            }
        }
        skipNext = !skipNext;
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
