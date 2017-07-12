package com.fuzzjump.api.invite;

import com.fuzzjump.api.invite.model.Invite;
import com.fuzzjump.api.invite.model.InviteDeclineRequest;
import com.fuzzjump.api.invite.model.InviteRequest;
import com.fuzzjump.api.invite.model.GetInviteResponse;
import com.fuzzjump.api.invite.model.InviteResponse;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Steveadoo on 7/11/2017.
 */

public class InviteService implements IInviteService {

    private final InviteService.InviteRestService restService;

    @Inject
    public InviteService(Retrofit retrofit) {
        this.restService = retrofit.create(InviteService.InviteRestService.class);
    }

    @Override
    public Observable<GetInviteResponse> getInvites() {
        return restService.getInvites();
    }

    @Override
    public Observable<InviteResponse> invite(InviteRequest inviteRequest) {
        return restService.invite(inviteRequest);
    }

    @Override
    public Observable<Boolean> declineInvite(InviteDeclineRequest inviteDeclineRequest) {
        return restService.declineInvite(inviteDeclineRequest);
    }

    private interface InviteRestService {

        @GET("invite")
        Observable<GetInviteResponse> getInvites();

        @POST("invite")
        Observable<InviteResponse> invite(@Body InviteRequest request);

        @DELETE("invite")
        Observable<Boolean> declineInvite(@Body InviteDeclineRequest request);

    }

}
