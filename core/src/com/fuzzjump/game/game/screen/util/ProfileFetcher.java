package com.fuzzjump.game.game.screen.util;

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
    private final ProfilesDelegate profilesDelegate;

    private Disposable profileObservable;

    private boolean waitingToFetch = false;
    private int updateCount = 0;

    public ProfileFetcher(Profile myProfile,
                          IProfileService profileService,
                          GraphicsScheduler scheduler,
                          ProfilesDelegate profileDelegate) {
        this.myProfile = myProfile;
        this.profileService = profileService;
        this.scheduler = scheduler;
        this.profilesDelegate = profileDelegate;
    }

    public void fetchPlayerProfiles() {
        //wait 2 seconds incase another update comes in
        if (profileObservable != null && waitingToFetch) {
            return;
        }
        waitingToFetch = true;
        final int ourUpdateCount = ++updateCount;
        profileObservable = Observable.timer(2, TimeUnit.SECONDS).flatMap((result) -> {
            List<String> userIds = new ArrayList<>();
            Array<Profile> profiles = profilesDelegate.getProfiles();
            for (Profile profile : profiles) {
                if (myProfile.getUserId().equals(profile.getUserId()))
                    continue;
                userIds.add(profile.getUserId());
            }
            waitingToFetch = false;
            return profileService.getProfiles(userIds.toArray(new String[0]));
        }).observeOn(scheduler).subscribe(profiles -> {
            //another update is coming if the counts arent the same, so ignore the results of this.
            if (ourUpdateCount != updateCount) {
                return;
            }
            updatePlayerProfiles(profiles);
        });

    }

    private void updatePlayerProfiles(ProfileDto[] profiles) {
        for(int i = 0; i < profiles.length; i++) {
            ProfileDto dto = profiles[i];
            Profile profile = findProfile(dto.getUserId());
            if (profile == null) continue;
            profile.setDisplayName(dto.getDisplayName());
            profile.setDisplayNameId(dto.getDisplayNameId());
            profile.loadProfile(dto.getProfile());
        }
    }


    private Profile findProfile(String userId) {
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
