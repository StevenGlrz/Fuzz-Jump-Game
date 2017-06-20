package com.fuzzjump.libgdxscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScreenHandler {

    private final Map<String, ScreenSession> screens;
    private final List<StageScreen> cachedScreens;
    private final Textures textures;
    private final Stage stage;
    private final ScreenResolver screenResolver;

    private StageScreen currentScreen;
    private ScreenChangeHandler screenChangeHandler;

    public ScreenHandler(Stage stage, Textures textures, ScreenResolver screenResolver) {
        this.stage = stage;
        this.textures = textures;
        this.screenResolver = screenResolver;
        this.screens = new LinkedHashMap<>();
        this.cachedScreens = new ArrayList<>();
    }

    /**
     * need to rework cacheDistance. should use something where you can check what
     * the next screen is and decide to cache off of that.
     */
    public void addScreen(Class<? extends StageScreen> type, int cacheDistance) {
        ScreenSession info = new ScreenSession(type, cacheDistance, screens.size());
        screens.put(type.getName(), info);
    }

    public <T extends StageScreen> void showScreenOnThread(final Class<T> clazz) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                showScreen(clazz);
            }
        });
    }

    public <T extends StageScreen> void loadScreen(Class<T> clazz) {

    }

    public <T extends StageScreen> T showScreen(Class<T> clazz) {
        StageScreen show = cached(clazz.getName());
        boolean fromCache = true;
        boolean currentScreenExists = currentScreen != null;
        if (show == null) {
            fromCache = false;
            try {
                HashMap<String, Integer> referenceCounts = null;
                if (currentScreenExists) {
                    referenceCounts = new HashMap<>();
                    for (Map.Entry<String, StageUITextures.TextureReferenceCounter> entry : currentScreen.getUI().getTextures().getTextures().entrySet()) {
                        referenceCounts.put(entry.getKey(), entry.getValue().references);
                    }
                }
                show = initScreen(clazz.getName());
                if (currentScreenExists) {
                    // pass the loaded textures to the new ui
                    currentScreen.getUI().getTextures().getTextures().putAll(currentScreen.getUI().getTextures().getTextures());
                }
                show.init(stage, this);
                if (currentScreenExists) {
                    Map<String, StageUITextures.TextureReferenceCounter> newTextures = show.getUI().getTextures().getTextures();
                    for (Map.Entry<String, Integer> entry : referenceCounts.entrySet()) {
                        //the loaded texture isn't used on this screen so remove it from the screens map
                        StageUITextures.TextureReferenceCounter referenceCounter = newTextures.get(entry.getKey());
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
        final boolean fFromCache = fromCache;
        final StageScreen screen = show;
        //update the screen on the next draw
        if (screen != null) {
            if (currentScreenExists) {
                currentScreen.setLoader(screen.getLoader());
            }
            screen.getLoader().onDone(new Runnable() {
                @Override
                public void run() {
                    if (!fFromCache) {
                        screen.onReady();
                    }
                    screen.showing();
                    stage.addActor(screen.getUI());
                    if (screenChangeHandler != null) {
                        screenChangeHandler.changeScreen(screen);
                    }
                    currentScreen = screen;
                    //checkCache();
                }
            });
        }
        return clazz.cast(show);
    }

    private StageScreen initScreen(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return screenResolver.resolveScreen(screens.get(name).type);
    }

    private StageScreen cached(String name) {
        if (cachedScreens.isEmpty()) {
            return null;
        }
        ScreenSession info = screens.get(name);
        if (info == null) {
            throw new RuntimeException("Screen " + name + " does not exist");
        }
        for (int i = 0, n = cachedScreens.size(); i < n; i++) {
            StageScreen screen = cachedScreens.get(i);
            if (info.type.isInstance(screen)) {
                return screen;
            }
        }
        return null;
    }

    private void checkCache() {
        if (currentScreen == null) {
            return;
        }
        boolean addToCache = true;
        ScreenSession current = screens.get(currentScreen.getClass().getName());
        Map<String, StageUITextures.TextureReferenceCounter> disposing = new HashMap<>();
        Map<String, StageUITextures.TextureReferenceCounter> loadedTextures = new HashMap<>();
        loadedTextures.putAll(currentScreen.getUI().getTextures().getTextures());

        for (StageScreen screen : new ArrayList<>(cachedScreens)) {
            if (screen.getClass().getName().equals(current.type.getName())) {
                addToCache = false;
                continue;
            }
            if (remove(current, screens.get(screen.getClass().getName()))) {
                disposing.putAll(screen.getUI().getTextures().getTextures());
                screen.dispose();
                cachedScreens.remove(screen);
            } else {
                loadedTextures.putAll(screen.getUI().getTextures().getTextures());
            }
        }
        for (Map.Entry<String, StageUITextures.TextureReferenceCounter> entry : disposing.entrySet()) {
            if (!loadedTextures.containsKey(entry.getKey())) {
                TextureRegion region = entry.getValue().region.get();
                if (region != null) {
                    region.getTexture().dispose();
                }
            }
        }
        if (addToCache)
            cachedScreens.add(currentScreen);
    }

    private boolean remove(ScreenSession current, ScreenSession check) {
        return Math.abs(current.index - check.index) > check.cacheDistance;
    }

    public StageScreen getCurrentScreen() {
        return currentScreen;
    }

    public void setScreenChangeHandler(ScreenChangeHandler screenChangeHandler) {
        this.screenChangeHandler = screenChangeHandler;
    }

    public interface ScreenChangeHandler {

        void changeScreen(Screen screen);

    }

    private class ScreenSession {

        public final Class<? extends StageScreen> type;
        public final int cacheDistance;
        private final int index;

        public ScreenSession(Class<? extends StageScreen> type, int cacheDistance, int index) {
            this.type = type;
            this.cacheDistance = cacheDistance;
            this.index = index;
        }
    }
}
