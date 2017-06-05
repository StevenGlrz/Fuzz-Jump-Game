package com.fuzzjump.game.model;

import com.fuzzjump.game.model.specials.AntiGravSpecial;
import com.fuzzjump.game.model.specials.BalloonsSpecial;
import com.fuzzjump.game.model.specials.BombSpecial;
import com.fuzzjump.game.model.specials.GustSpecial;
import com.fuzzjump.game.model.specials.JetpackSpecial;
import com.fuzzjump.game.model.specials.LightningSpecial;
import com.fuzzjump.game.model.specials.MissileSpecial;
import com.fuzzjump.game.model.specials.ShieldSpecial;
import com.fuzzjump.game.model.specials.Special;

import java.util.Random;

/**
 * Kerpow Games, LLC
 * Created by stephen on 4/6/2015.
 */
public enum SpecialType {
    ANTIGRAV("item-icon-antigravity", new AntiGravSpecial()),
    BALLOONS("item-icon-balloons", new BalloonsSpecial()),
    BOMB("item-icon-bomb", new BombSpecial()),
    JETPACK("item-jetpack", new JetpackSpecial()),
    GUIDED_MISSILE("item-icon-guidedMissle", new MissileSpecial()),
    GUST("item-icon-gust", new GustSpecial()),
    LIGHTNING("item-icon-lightning", new LightningSpecial()),
    SHIELD("item-icon-shield", new ShieldSpecial());

    public final String icon;
    public final Special spec;

    SpecialType(String icon, Special spec) {
        this.icon = icon;
        this.spec = spec;
    }

    public static SpecialType random(Random random) {
        return SpecialType.values()[random.nextInt(SpecialType.values().length)];
    }

    public Special getSpecial() {
        return spec;
    }
}