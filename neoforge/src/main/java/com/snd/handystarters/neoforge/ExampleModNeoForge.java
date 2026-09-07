package com.snd.handystarters.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import com.snd.handystarters.ExampleMod;
import com.snd.handystarters.loot.ModCoarseFiberLoot;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        ExampleMod.init(new NeoForgeModPlatform(modEventBus));

        NeoForge.EVENT_BUS.addListener(this::onLootTableLoad);
    }

    private void onLootTableLoad(LootTableLoadEvent event) {
        if (ModCoarseFiberLoot.isCoarseFiberLootTable(event.getKey())) {
            event.getTable().addPool(ModCoarseFiberLoot.buildCoarseFiberPool(event.getRegistries()));
        }
    }
}
