package com.snd.handystarters.fabric;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.registry.FuelValueEvents;

import com.snd.handystarters.HandyStarters;
import com.snd.handystarters.platform.ModPlatform;

public final class FabricModPlatform implements ModPlatform {
    @Override
    public <T extends Item> Supplier<T> registerItem(String path, Supplier<T> factory) {
        T item = factory.get();
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(HandyStarters.MOD_ID, path), item);
        return () -> item;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String path, Supplier<T> factory) {
        T block = factory.get();
        Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(HandyStarters.MOD_ID, path), block);
        return () -> block;
    }

    @Override
    public <T extends BlockEntityType<?>> Supplier<T> registerBlockEntityType(String path, Supplier<T> factory) {
        T type = factory.get();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(HandyStarters.MOD_ID, path), type);
        return () -> type;
    }

    @Override
    public void addToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {
        CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> output.accept(item.get()));
    }

    @Override
    public void registerFuel(Supplier<? extends Item> item, int burnTicks) {
        FuelValueEvents.BUILD.register((builder, context) -> builder.add(item.get(), burnTicks));
    }

    @Override
    public void registerDispenserProjectile(Supplier<? extends Item> item) {
        // Items are registered eagerly on Fabric and vanilla has already run its
        // bootstrap by the time a mod initialises, so this can be applied directly.
        Item resolved = item.get();
        DispenserBlock.registerBehavior(resolved, new ProjectileDispenseBehavior(resolved));
    }

    @Override
    public void onPlayerTick(Consumer<Player> handler) {
        // The integrated server (singleplayer) and dedicated server both go through this,
        // and attribute changes sync to clients automatically, so no client-side handler
        // is needed. Registering one here would also crash a dedicated server: this method
        // runs from the shared ModInitializer, and merely referencing a client-only event
        // class (even without calling it) triggers loading net.minecraft.client.Minecraft,
        // which doesn't exist on a server-only environment.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                handler.accept(player);
            }
        });
    }
}
