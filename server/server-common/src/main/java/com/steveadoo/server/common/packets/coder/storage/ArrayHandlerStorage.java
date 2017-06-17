package com.steveadoo.server.common.packets.coder.storage;

import com.steveadoo.server.common.packets.coder.HandlerStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * a storage impl that uses an array. better for a small list of handlers
 */
public class ArrayHandlerStorage<TKey, TVal> implements HandlerStorage<TKey, TVal> {

    private List<Wrapper> handlers = new ArrayList<>();

    @Override
    public TVal get(TKey key) {
        for(Wrapper wrapper : handlers) {
            if (wrapper.key.equals(key)) {
                return wrapper.val;
            }
        }
        return null;
    }

    @Override
    public void put(TKey key, TVal val) {
        handlers.add(new Wrapper(key, val));
        //sort??
    }

    private class Wrapper {

        public final TKey key;
        public final TVal val;

        public Wrapper(TKey key, TVal val) {
            this.key = key;
            this.val = val;
        }

    }

}
