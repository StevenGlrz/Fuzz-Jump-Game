package com.fuzzjump.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;

/**
 * Created by Steven Galarza on 6/22/2017.
 */
public class GraphicsScheduler extends Scheduler {

    @Override
    public Disposable scheduleDirect(Runnable run) {
        Gdx.app.postRunnable(run);
        return EmptyDisposable.INSTANCE;
    }

    @Override
    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        if (delay == 0L) {
            scheduleDirect(run);
            return EmptyDisposable.INSTANCE;
        }
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                run.run();
            }
        }, unit.toSeconds(delay));
        return EmptyDisposable.INSTANCE;
    }

    @Override
    public Worker createWorker() {
        return new GraphicsWorker();
    }

    private static class GraphicsWorker extends Worker {

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (delay == 0L) {
                Gdx.app.postRunnable(run);
            } else {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        run.run();
                    }
                }, unit.toSeconds(delay));
            }
            return null;
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isDisposed() {
            return true;
        }
    }
}
