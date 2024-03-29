package com.fuzzjump.api.session;


import com.fuzzjump.api.session.model.SessionResponse;
import com.fuzzjump.api.session.model.SessionVerifyResponse;

import io.reactivex.Observable;

public interface ISessionService {

    Observable<SessionResponse> getSessionToken(String feature);
    Observable<SessionResponse> getServerSessionToken(String machineName, String feature);
    Observable<SessionVerifyResponse> verify(String userId, String feature, String token);
    Observable<SessionVerifyResponse> verifyServer(String machineName, String feature, String token);

}
