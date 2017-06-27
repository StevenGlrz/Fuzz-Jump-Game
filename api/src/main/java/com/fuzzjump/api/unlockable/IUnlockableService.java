package com.fuzzjump.api.unlockable;

import com.fuzzjump.api.unlockable.model.UnlockablePurchaseRequest;
import com.fuzzjump.api.unlockable.model.UnlockableResponse;

import io.reactivex.Observable;

/**
 * Created by Steven Galarza on 6/25/2017.
 */
public interface IUnlockableService {

    Observable<UnlockableResponse> purchaseUnlockable(UnlockablePurchaseRequest request);

}
