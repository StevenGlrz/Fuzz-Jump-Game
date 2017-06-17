package com.steveadoo.server.common.packets.coder;

/**
 * Defines methods for getting and storing message handlers
 */
public interface HandlerStorage<TKey, TVal> {

    TVal get(TKey key);
    void put(TKey key, TVal val);

}
