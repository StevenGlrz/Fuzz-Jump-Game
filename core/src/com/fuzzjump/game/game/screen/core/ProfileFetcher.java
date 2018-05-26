package com.fuzzjump.game.game.screen.core;

import com.badlogic.gdx.utils.Array;
import com.fuzzjump.api.profile.IProfileService;
import com.fuzzjump.api.profile.model.ProfileDto;
import com.fuzzjump.game.game.player.Profile;
import com.fuzzjump.game.util.GraphicsScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ProfileFetcher {

    private final Profile myProfile;
    private final IProfileService profileService;
    private final GraphicsScheduler scheduler;

    private Disposable profileObservable;

    private boolean waitingToFetch = false;

    public ProfileFetcher(Profile myProfile, IProfileService profileService, GraphicsScheduler scheduler) {
        this.myProfile = myProfile;
        this.profileService = profileService;
        this.scheduler = scheduler;
    }

    public void fetchPlayerProfiles(ProfilesDelegate profilesDelegate, Runnable onComplete) {
        //wait 2 seconds incase another update comes in
        if (profileObservable != null && waitingToFetch) {
            return;
        }
        waitingToFetch = true;
        profileObservable = Observable.timer(1, TimeUnit.SECONDS).flatMap((result) -> {
            List<String> userIds = new ArrayList<>();
            Array<Profile> profiles = profilesDelegate.getProfiles();
            for (Profile profile : profiles) {
                if (!myProfile.getUserId().equals(profile.getUserId())) {
                    userIds.add(profile.getUserId());
                }
            }
            waitingToFetch = false;
            return profileService.getProfiles(userIds.toArray(new String[0]));
        }).observeOn(scheduler).subscribe(profileResponse -> {
            updatePlayerProfiles(profileResponse.getBody(), profilesDelegate);
            onComplete.run();
        });
    }

    private void updatePlayerProfiles(ProfileDto[] profiles, ProfilesDelegate profilesDelegate) {
        for(int i = 0; i < profiles.length; i++) {
            ProfileDto dto = profiles[i];
            Profile profile = findProfile(dto.getUserId(), profilesDelegate); // Java 8, where art thou
            if (profile != null) {
                profile.setDisplayName(dto.getDisplayName());
                profile.setDisplayNameId(dto.getDisplayNameId());
                profile.loadProfile(dto.getProfile());
            }
        }
    }


    private Profile findProfile(String userId, ProfilesDelegate profilesDelegate) {
        Array<Profile> profiles = profilesDelegate.getProfiles();
        for (int i = 0; i < profiles.size; i++) {
            Profile profile = profiles.get(i);
            if (userId.equals(profile.getUserId())) {
                return profile;
            }
        }
        return null;
    }

    @FunctionalInterface
    public interface ProfilesDelegate {
        Array<Profile> getProfiles();
    }

}
