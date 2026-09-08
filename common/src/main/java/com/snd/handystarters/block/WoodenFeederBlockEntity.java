package com.snd.handystarters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.snd.handystarters.registry.ModBlocks;

public final class WoodenFeederBlockEntity extends AbstractWoodenContainerBlockEntity {
    // ~1 item/sec, matching the design's transfer rate (a vanilla hopper's default is 8 ticks).
    private static final int TRANSFER_COOLDOWN = 20;

    private int cooldown = TRANSFER_COOLDOWN;

    public WoodenFeederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WOODEN_FEEDER_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WoodenFeederBlockEntity feeder) {
        if (--feeder.cooldown > 0) {
            return;
        }
        feeder.cooldown = TRANSFER_COOLDOWN;
        Direction facing = state.getValue(WoodenFeederBlock.FACING);
        ContainerTransfer.pushOneItem(level, feeder, pos, facing);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.handy_starters.wooden_feeder");
    }
}
