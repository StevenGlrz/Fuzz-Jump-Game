package com.fuzzjump.api.profile.model;

import com.fuzzjump.api.model.unlockable.Unlockable;
import com.fuzzjump.api.model.user.Equip;

/**
 * Created by Steven Galarza on 6/27/2017.
 */
public class SaveProfileRequest {

    private final Equip[] equips;
    private final Unlockable[] unlockables;

    public SaveProfileRequest(Equip[] equips, Unlockable[] unlockables) {
        this.equips = equips;
        this.unlockables = unlockables;
    }

}
