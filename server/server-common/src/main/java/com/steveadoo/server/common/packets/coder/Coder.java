package com.steveadoo.server.common.packets.coder;

import com.steveadoo.server.common.packets.coder.storage.ArrayHandlerStorage;
import com.steveadoo.server.common.packets.coder.storage.MappedHandlerStorage;

public abstract class Coder<TKey, THandler> {

    private final HandlerStorage<TKey, THandler> storage;

    public Coder(HandlerStorage<TKey, THandler> storage) {
        this.storage = storage;
    }

    public void addHandler(TKey key, THandler handler) {
        this.storage.put(key, handler);
    }

    protected THandler getHandler(TKey key) {
        return storage.get(key);
    }

    public static <TKey, THandler> HandlerStorage<TKey, THandler> getStorageForSize(int size) {
        return size < 15 ? new ArrayHandlerStorage<TKey, THandler>() : new MappedHandlerStorage<TKey, THandler>();
    }
}
