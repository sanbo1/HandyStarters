package com.snd.handystarters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.snd.handystarters.registry.ModBlocks;

public final class WoodenExtractorBlockEntity extends AbstractWoodenContainerBlockEntity {
    // ~1 item/sec, matching the design's transfer rate (a vanilla hopper's default is 8 ticks).
    private static final int TRANSFER_COOLDOWN = 20;

    private int cooldown = TRANSFER_COOLDOWN;

    public WoodenExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WOODEN_EXTRACTOR_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WoodenExtractorBlockEntity extractor) {
        if (--extractor.cooldown > 0) {
            return;
        }
        extractor.cooldown = TRANSFER_COOLDOWN;
        ContainerTransfer.pullOneItemFromAbove(level, extractor, pos);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.handy_starters.wooden_extractor");
    }
}
