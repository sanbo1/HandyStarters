package com.snd.handystarters.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import com.snd.handystarters.HandyStarters;
import com.snd.handystarters.loot.ModCoarseFiberLoot;

public final class HandyStartersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HandyStarters.init(new FabricModPlatform());

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (ModCoarseFiberLoot.isCoarseFiberLootTable(key)) {
                ((FabricLootTableBuilder) tableBuilder).pool(ModCoarseFiberLoot.buildCoarseFiberPool(registries));
            }
        });
    }
}
