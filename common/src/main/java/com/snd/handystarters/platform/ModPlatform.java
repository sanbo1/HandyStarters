package com.snd.handystarters.platform;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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

    /**
     * Builds and registers a {@link BlockEntityType}. This goes through the platform
     * seam (rather than letting common code build the type itself, as 26.2 does)
     * because 1.21.11's {@code BlockEntityType} constructor is package-private;
     * only NeoForge's access transformer exposes it, so Fabric needs
     * {@code FabricBlockEntityTypeBuilder} instead.
     *
     * <p>{@code validBlocks} takes suppliers rather than resolved blocks because on
     * NeoForge the block registration is itself deferred: resolving it eagerly here
     * (before the block registry event has fired) throws.
     */
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String path, BiFunction<BlockPos, BlockState, T> factory, Supplier<? extends Block>... validBlocks);

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
