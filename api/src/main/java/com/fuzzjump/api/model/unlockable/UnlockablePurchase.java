package com.fuzzjump.api.model.unlockable;

/**
 * Created by Steven Galarza on 6/25/2017.
 */
public class UnlockablePurchase {

    public static final int PURCHASE_FAILED = 0;
    public static final int PURCHASE_NOT_ENOUGH_COINS = 1;
    public static final int PURCHASE_ALREADY_PURCHASED = 2;
    public static final int PURCHASE_SUCCESS = 3;

    private int purchaseStatus;
    private Unlockable unlockable;

    public int getPurchaseStatus() {
        return purchaseStatus;
    }

    public Unlockable getUnlockable() {
        return unlockable;
    }
}
