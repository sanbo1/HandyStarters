package com.snd.handystarters.neoforge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.snd.handystarters.ExampleMod;
import com.snd.handystarters.platform.ModPlatform;

public final class NeoForgeModPlatform implements ModPlatform {
    private record FuelEntry(Supplier<? extends Item> item, int burnTicks) {
    }

    private final DeferredRegister.Items items = DeferredRegister.createItems(ExampleMod.MOD_ID);
    private final Map<ResourceKey<CreativeModeTab>, List<Supplier<? extends Item>>> tabEntries = new HashMap<>();
    private final List<FuelEntry> fuelEntries = new ArrayList<>();

    public NeoForgeModPlatform(IEventBus modEventBus) {
        modEventBus.addListener(this::onBuildCreativeModeTabContents);
        NeoForge.EVENT_BUS.addListener(this::onFurnaceFuelBurnTime);
        items.register(modEventBus);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String path, Supplier<T> factory) {
        return items.register(path, factory);
    }

    @Override
    public void addToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {
        tabEntries.computeIfAbsent(tab, key -> new ArrayList<>()).add(item);
    }

    @Override
    public void registerFuel(Supplier<? extends Item> item, int burnTicks) {
        fuelEntries.add(new FuelEntry(item, burnTicks));
    }

    @Override
    public void onPlayerTick(Consumer<Player> handler) {
        NeoForge.EVENT_BUS.addListener((PlayerTickEvent.Post event) -> handler.accept(event.getEntity()));
    }

    private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        List<Supplier<? extends Item>> entries = tabEntries.get(event.getTabKey());
        if (entries != null) {
            for (Supplier<? extends Item> item : entries) {
                event.accept(item.get());
            }
        }
    }

    private void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        for (FuelEntry entry : fuelEntries) {
            if (event.getItemStack().getItem() == entry.item().get()) {
                event.setBurnTime(entry.burnTicks());
                return;
            }
        }
    }
}
