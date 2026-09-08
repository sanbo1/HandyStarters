package com.snd.handystarters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.snd.handystarters.registry.ModBlocks;

public final class WoodenFeederBlockEntity extends AbstractWoodenContainerBlockEntity {
    public WoodenFeederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WOODEN_FEEDER_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WoodenFeederBlockEntity feeder) {
        if (!feeder.tickCooldown() || !state.getValue(WoodenFeederBlock.ENABLED)) {
            return;
        }
        Direction facing = state.getValue(WoodenFeederBlock.FACING);
        ContainerTransfer.pushOneItem(level, feeder, pos, facing);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.handy_starters.wooden_feeder");
    }
}
