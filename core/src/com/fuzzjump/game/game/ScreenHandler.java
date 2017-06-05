package com.fuzzjump.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fuzzjump.game.FuzzJump;
import com.fuzzjump.game.game.screens.attachment.ScreenAttachment;

import java.lang.reflect.InvocationTargetException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Kerpow Games, LLC
 * Created by stephen on 3/27/2015.
 */
public class ScreenHandler {

    private final FuzzJump game;

    private final LinkedHashMap<String, ScreenInfo> screens;
    private final ArrayList<StageScreen> cachedScreens;
    private final Textures textures;

    private StageScreen currentScreen;

    public ScreenHandler(FuzzJump game, Textures textures) {
        this.game = game;
        this.textures = textures;
        this.screens = new LinkedHashMap<>();
        this.cachedScreens = new ArrayList<>();
    }

    /**
     * need to rework cacheDistance. should use something where you can check what
     * the next screen is and decide to cache off of that.
     */
    public void addScreen(Class<? extends StageScreen> type, int cacheDistance) {
        ScreenInfo info = new ScreenInfo(type, cacheDistance, screens.size());
        screens.put(type.getName(), info);
    }

    public <T extends StageScreen> T showScreen(Class<T> clazz, final ScreenAttachment attachment) {
        StageScreen show = cached(clazz.getName());
        if (show == null) {
            try {
                HashMap<String, Integer> referenceCounts = null;
                if (currentScreen != null) {
                    referenceCounts = new HashMap<>();
                    for (Map.Entry<String, StageUI.TextureReferenceCounter> entry : currentScreen.ui().getTextures().entrySet()) {
                        referenceCounts.put(entry.getKey(), entry.getValue().references);
                    }
                }
                show = initScreen(clazz.getName());
                if (currentScreen != null) {
                    //pass the loaded textures to the new ui
                    currentScreen.ui().getTextures().putAll(currentScreen.ui().getTextures());
                }
                show.init();
                if (currentScreen != null) {
                    Map<String, StageUI.TextureReferenceCounter> newTextures = show.ui().getTextures();
                    for (Map.Entry<String, Integer> entry : referenceCounts.entrySet()) {
                        //the loaded texture isn't used on this screen so remove it from the screens map
                        StageUI.TextureReferenceCounter referenceCounter = newTextures.get(entry.getKey());
                        if (referenceCounter != null && referenceCounter.references == entry.getValue()) {
                            newTextures.remove(entry.getKey());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            show.initCache();
        }
        final StageScreen screen = show;
        //update the screen on the next draw
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(screen);
                currentScreen = screen;
                checkCache();
                screen.load(attachment);
            }
        });
        return clazz.cast(show);
    }

    private StageScreen initScreen(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return screens.get(name).type.getDeclaredConstructor(FuzzJump.class, Textures.class, ScreenHandler.class).newInstance(game, textures, this);
    }

    private StageScreen cached(String name) {
        if (cachedScreens.isEmpty()) return null;
        ScreenInfo info = screens.get(name);
        for (StageScreen screen : cachedScreens) {
            if (info.type.isInstance(screen))
                return screen;
        }
        return null;
    }

    private void checkCache() {
        boolean shittyPhone = false; //TODO add actual logic for this later
        boolean addToCache = true;
        ScreenInfo current = screens.get(currentScreen.getClass().getName());
        Map<String, StageUI.TextureReferenceCounter> disposing = new HashMap<>();
        Map<String, StageUI.TextureReferenceCounter> loadedTextures = new HashMap<>();
        loadedTextures.putAll(currentScreen.ui().getTextures());
        if (shittyPhone) {
            for (StageScreen screen : cachedScreens) {
                screen.dispose();
                cachedScreens.remove(screen);
            }
        } else {
            for (StageScreen screen : cachedScreens) {
                if (screen.getClass().getName().equals(current.type.getName())) {
                    addToCache = false;
                    continue;
                }
                if (remove(current, screens.get(screen.getClass().getName()))) {
                    disposing.putAll(screen.ui().getTextures());
                    screen.dispose();
                    cachedScreens.remove(screen);
                } else {
                    loadedTextures.putAll(screen.ui().getTextures());
                }
            }
        }
        for (Map.Entry<String, StageUI.TextureReferenceCounter> entry : disposing.entrySet()) {
            if (!loadedTextures.containsKey(entry.getKey())) {
                TextureRegion region = entry.getValue().region.get();
                if (region != null) {
                    region.getTexture().dispose();
                }
            }
        }
        if (addToCache)
            cachedScreens.add((StageScreen) game.getScreen());
    }

    private boolean remove(ScreenInfo current, ScreenInfo check) {
        return Math.abs(current.index - check.index) > check.cacheDistance;
    }

    private class ScreenInfo {

        public final Class<? extends StageScreen> type;
        public final int cacheDistance;
        private final int index;

        public ScreenInfo(Class<? extends StageScreen> type, int cacheDistance, int index) {
            this.type = type;
            this.cacheDistance = cacheDistance;
            this.index = index;
        }
    }
}
