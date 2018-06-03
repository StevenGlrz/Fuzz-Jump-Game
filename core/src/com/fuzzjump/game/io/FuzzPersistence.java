package com.fuzzjump.game.io;

import com.badlogic.gdx.Preferences;
import com.fuzzjump.api.model.user.ApiUser;
import com.fuzzjump.game.game.Assets;
import com.fuzzjump.game.game.player.Profile;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Steven Galarza on 6/26/2017.
 */
public class FuzzPersistence {

    private final Preferences preferences;
    private final Gson gson;
    private final Profile profile;
    private final ExecutorService ioService;

    public FuzzPersistence(Preferences preferences, Gson gson, Profile profile) {
        this.preferences = preferences;
        this.gson = gson;
        this.profile = profile;
        this.ioService = Executors.newSingleThreadExecutor();
    }

    public String loadToken() {
        return preferences.getString(Assets.USER_TOKEN, null);
    }

    public boolean loadProfile() {
        String profileData = preferences.getString(Assets.PROFILE_DATA, null);
        if (profileData != null) {
            profile.loadUser(gson.fromJson(profileData, ApiUser.class));
            return true;
        }
        return false;
    }

    public void saveProfile() {
        final ApiUser user = profile.toApiUser();

        // Do in the background for mobile device performance
        ioService.submit(() -> {
            preferences.putString(Assets.PROFILE_DATA, gson.toJson(user));
            preferences.flush();
        });
    }

    public void clearProfile() {
        preferences.remove(Assets.PROFILE_DATA);
        preferences.remove(Assets.USER_TOKEN);

        preferences.flush();
    }

    public void setSoundSetting(boolean soundSetting) {
        preferences.putBoolean(Assets.SOUND_SETTING_PREFERENCE, soundSetting);
        preferences.flush();
    }

    public boolean isSoundSettingSet() {
        return preferences.getBoolean(Assets.SOUND_SETTING_PREFERENCE, false);
    }
}
