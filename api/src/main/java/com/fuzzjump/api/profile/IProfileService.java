package com.fuzzjump.api.profile;

import com.fuzzjump.api.profile.model.SaveProfileRequest;
import com.fuzzjump.api.profile.model.SaveProfileResponse;

import io.reactivex.Observable;

/**
 * Created by Steven Galarza on 6/27/2017.
 */
public interface IProfileService {

    Observable<SaveProfileResponse> requestProfileSave(SaveProfileRequest request);

}
