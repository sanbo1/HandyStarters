package com.snd.handystarters;

import com.snd.handystarters.platform.ModPlatform;
import com.snd.handystarters.registry.ModItems;

public final class ExampleMod {
    public static final String MOD_ID = "handy_starters";

    public static void init(ModPlatform platform) {
        ModItems.init(platform);
        WoodenCaneStepAssist.init(platform);
    }
}
