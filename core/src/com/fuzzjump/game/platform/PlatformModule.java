package com.fuzzjump.game.platform;

import com.fuzzjump.libgdxscreens.VectorGraphicsLoader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is where we'd have bindings to FB stuff, etc.
 */
@Module
public class PlatformModule {

    private final VectorGraphicsLoader vectorGraphicsLoader;

    public PlatformModule(VectorGraphicsLoader vectorGraphicsLoader) {
        this.vectorGraphicsLoader = vectorGraphicsLoader;
    }

    @Singleton
    @Provides
    public VectorGraphicsLoader provideVectorGraphicsLoader() {
        return vectorGraphicsLoader;
    }

}
