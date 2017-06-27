package com.fuzzjump.api.unlockable;

import com.fuzzjump.api.unlockable.model.UnlockablePurchaseRequest;
import com.fuzzjump.api.unlockable.model.UnlockableResponse;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Steven Galarza on 6/25/2017.
 */
public class UnlockableService implements IUnlockableService {

    private final UnlockableRestService restService;

    @Inject
    public UnlockableService(Retrofit retrofit) {
        this.restService = retrofit.create(UnlockableRestService.class);
    }

    @Override
    public Observable<UnlockableResponse> purchaseUnlockable(UnlockablePurchaseRequest request) {
        return restService.purchaseUnlockable(request);
    }

    public interface UnlockableRestService {

        @POST("unlockable")
        Observable<UnlockableResponse> purchaseUnlockable(@Body UnlockablePurchaseRequest request);
    }
}
