package com.fuzzjump.api.profile;

import com.fuzzjump.api.profile.model.SaveProfileRequest;
import com.fuzzjump.api.profile.model.SaveProfileResponse;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.PUT;

/**
 * Created by Steven Galarza on 6/27/2017.
 */
public class ProfileService implements IProfileService {

    private final ProfileRestService restService;

    @Inject
    public ProfileService(Retrofit retrofit) {
        this.restService = retrofit.create(ProfileRestService.class);
    }

    @Override
    public Observable<SaveProfileResponse> requestProfileSave(SaveProfileRequest request) {
        return restService.requestProfileSave(request);
    }

    private interface ProfileRestService {

        @PUT("profile")
        Observable<SaveProfileResponse> requestProfileSave(@Body SaveProfileRequest request);
    }
}
