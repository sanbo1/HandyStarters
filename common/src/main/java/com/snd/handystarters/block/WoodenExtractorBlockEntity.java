package com.snd.handystarters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.snd.handystarters.registry.ModBlocks;

public final class WoodenExtractorBlockEntity extends AbstractWoodenContainerBlockEntity {
    public WoodenExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WOODEN_EXTRACTOR_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WoodenExtractorBlockEntity extractor) {
        if (!extractor.tickCooldown() || !state.getValue(WoodenExtractorBlock.ENABLED)) {
            return;
        }
        ContainerTransfer.pullOneItemFromAbove(level, extractor, pos);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.handy_starters.wooden_extractor");
    }
}
