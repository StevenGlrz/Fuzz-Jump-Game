package com.fuzzjump.api.invite;

import com.fuzzjump.api.invite.model.InviteDeclineRequest;
import com.fuzzjump.api.invite.model.InviteRequest;
import com.fuzzjump.api.invite.model.GetInviteResponse;
import com.fuzzjump.api.invite.model.InviteResponse;

import io.reactivex.Observable;

public interface IInviteService {

    Observable<GetInviteResponse> getInvites();

    Observable<InviteResponse> invite(InviteRequest inviteRequest);

    Observable<Boolean> declineInvite(InviteDeclineRequest inviteDeclineRequest);

}
