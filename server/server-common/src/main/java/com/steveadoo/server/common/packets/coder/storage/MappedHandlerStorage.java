package com.steveadoo.server.common.packets.coder.storage;

import com.steveadoo.server.common.packets.coder.HandlerStorage;

import java.util.HashMap;

/**
 * a storage storage that uses a map between ids and objects.
 * better for a large list of handlers
 */
public class MappedHandlerStorage<TKey, TVal> implements HandlerStorage<TKey, TVal> {

    private HashMap<TKey, TVal> map = new HashMap<>();

    @Override
    public TVal get(TKey key) {
        return map.get(key);
    }

    @Override
    public void put(TKey key, TVal val) {
        map.put(key, val);
    }

}
