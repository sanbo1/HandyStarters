package com.snd.handystarters.neoforge;

import net.neoforged.fml.common.Mod;

import com.snd.handystarters.HandyStarters;

@Mod(HandyStarters.MOD_ID)
public final class HandyStartersNeoForge {
    public HandyStartersNeoForge() {
        // Run our common setup.
        HandyStarters.init();
    }
}
