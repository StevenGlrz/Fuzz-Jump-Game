package com.fuzzjump.api.unlockable.model;

/**
 * Created by Steven Galarza on 6/25/2017.
 */
public class UnlockablePurchaseRequest {

    private final int unlockableDefinitionId;

    public UnlockablePurchaseRequest(int unlockableDefinitionId) {
        this.unlockableDefinitionId = unlockableDefinitionId;
    }

}
