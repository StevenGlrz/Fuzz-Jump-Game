package com.fuzzjump.libgdxscreens.screen;

/**
 * Created by Steveadoo on 6/14/2017.
 */

public interface ScreenResolver {

    public <T extends StageScreen> T resolveScreen(Class<T> screenClazz);

}
