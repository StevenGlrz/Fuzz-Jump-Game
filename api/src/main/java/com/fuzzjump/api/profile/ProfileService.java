package com.fuzzjump.api.profile;

import com.fuzzjump.api.profile.model.GetProfileResponse;
import com.fuzzjump.api.profile.model.ProfileDto;
import com.fuzzjump.api.profile.model.SaveProfileRequest;
import com.fuzzjump.api.profile.model.SaveProfileResponse;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

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
    public Observable<SaveProfileResponse> saveProfile(SaveProfileRequest request) {
        return restService.saveProfile(request);
    }

    @Override
    public Observable<GetProfileResponse> getProfiles(String[] userIds) {
        return restService.getProfiles(userIds);
    }

    private interface ProfileRestService {

        @GET("profile")
        Observable<GetProfileResponse> getProfiles(@Query("userIds") String[] userIds);

        @PUT("profile")
        Observable<SaveProfileResponse> saveProfile(@Body SaveProfileRequest request);
    }
}
