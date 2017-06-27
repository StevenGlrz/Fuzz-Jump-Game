package com.fuzzjump.api.unlockable.model;

import com.fuzzjump.api.model.response.Response;
import com.fuzzjump.api.model.unlockable.UnlockablePurchase;

/**
 * Created by Steven Galarza on 6/25/2017.
 */
public class UnlockableResponse extends Response {

    private UnlockablePurchase body;

    public UnlockablePurchase getBody() {
        return body;
    }

}
