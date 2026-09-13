package com.snd.handystarters.neoforge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.snd.handystarters.HandyStarters;
import com.snd.handystarters.platform.ModPlatform;

public final class NeoForgeModPlatform implements ModPlatform {
    private record FuelEntry(Supplier<? extends Item> item, int burnTicks) {
    }

    private final DeferredRegister.Items items = DeferredRegister.createItems(HandyStarters.MOD_ID);
    private final DeferredRegister<Block> blocks = DeferredRegister.create(Registries.BLOCK, HandyStarters.MOD_ID);
    private final DeferredRegister<BlockEntityType<?>> blockEntityTypes =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HandyStarters.MOD_ID);
    private final Map<ResourceKey<CreativeModeTab>, List<Supplier<? extends Item>>> tabEntries = new HashMap<>();
    private final List<FuelEntry> fuelEntries = new ArrayList<>();
    private final List<Supplier<? extends Item>> dispenserProjectiles = new ArrayList<>();

    public NeoForgeModPlatform(IEventBus modEventBus) {
        modEventBus.addListener(this::onBuildCreativeModeTabContents);
        modEventBus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(this::onFurnaceFuelBurnTime);
        items.register(modEventBus);
        blocks.register(modEventBus);
        blockEntityTypes.register(modEventBus);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String path, Supplier<T> factory) {
        return items.register(path, factory);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String path, Supplier<T> factory) {
        return blocks.register(path, factory);
    }

    @Override
    public <T extends BlockEntityType<?>> Supplier<T> registerBlockEntityType(String path, Supplier<T> factory) {
        return blockEntityTypes.register(path, factory);
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
    public void registerDispenserProjectile(Supplier<? extends Item> item) {
        dispenserProjectiles.add(item);
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

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Deferred twice over: items aren't resolvable until registration has run,
        // and DISPENSER_REGISTRY is a plain map that mod setup would otherwise be
        // writing to from several threads at once.
        event.enqueueWork(() -> {
            for (Supplier<? extends Item> entry : dispenserProjectiles) {
                Item item = entry.get();
                DispenserBlock.registerBehavior(item, new ProjectileDispenseBehavior(item));
            }
        });
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
