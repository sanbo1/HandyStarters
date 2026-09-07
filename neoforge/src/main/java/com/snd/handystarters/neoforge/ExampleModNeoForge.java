package com.snd.handystarters.neoforge;

import net.neoforged.fml.common.Mod;

import com.snd.handystarters.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
