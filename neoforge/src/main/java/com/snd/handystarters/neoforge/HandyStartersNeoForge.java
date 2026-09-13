package com.snd.handystarters.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import com.snd.handystarters.HandyStarters;
import com.snd.handystarters.loot.ModCoarseFiberLoot;

@Mod(HandyStarters.MOD_ID)
public final class HandyStartersNeoForge {
    public HandyStartersNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        HandyStarters.init(new NeoForgeModPlatform(modEventBus));

        NeoForge.EVENT_BUS.addListener(this::onLootTableLoad);
    }

    private void onLootTableLoad(LootTableLoadEvent event) {
        if (ModCoarseFiberLoot.isCoarseFiberLootTable(event.getKey())) {
            event.getTable().addPool(ModCoarseFiberLoot.buildCoarseFiberPool(event.getRegistries()));
        }
    }
}
