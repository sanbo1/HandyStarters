package com.snd.handystarters.platform;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Thin seam between the loader-agnostic common code and whatever each loader's
 * native modding API calls its equivalent mechanism (Fabric API vs. NeoForge).
 * Each loader module provides its own implementation instead of pulling in a
 * shared cross-platform library, so the mod has no runtime dependency beyond
 * the loader itself (plus Fabric API, which the Fabric side already needs).
 */
public interface ModPlatform {
    <T extends Item> Supplier<T> registerItem(String path, Supplier<T> factory);

    <T extends Block> Supplier<T> registerBlock(String path, Supplier<T> factory);

    <T extends BlockEntityType<?>> Supplier<T> registerBlockEntityType(String path, Supplier<T> factory);

    void addToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item);

    void registerFuel(Supplier<? extends Item> item, int burnTicks);

    /**
     * Makes a dispenser shoot the item instead of dropping it. Implementing
     * {@code ProjectileItem} is not enough on its own: a dispenser looks the
     * behaviour up in {@code DispenserBlock.DISPENSER_REGISTRY}, and the fallback
     * for an unregistered item is to eject it as a dropped entity.
     */
    void registerDispenserProjectile(Supplier<? extends Item> item);

    void onPlayerTick(Consumer<Player> handler);
}
