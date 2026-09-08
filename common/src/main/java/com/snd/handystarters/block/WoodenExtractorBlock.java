package com.snd.handystarters.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A lighter, hopper-shaped input-only block: always pulls one item per second
 * from whatever container sits directly above it. Unlike a hopper it never
 * scoops up floating item entities, and never transfers into another feeder
 * or extractor (see {@link ContainerTransfer}). Orientation is fixed, so
 * there's no FACING state.
 */
public final class WoodenExtractorBlock extends BaseEntityBlock {
    public static final MapCodec<WoodenExtractorBlock> CODEC = simpleCodec(WoodenExtractorBlock::new);

    public WoodenExtractorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<WoodenExtractorBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenExtractorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (level2, pos, state2, blockEntity) -> {
            if (blockEntity instanceof WoodenExtractorBlockEntity extractor) {
                WoodenExtractorBlockEntity.serverTick(level2, pos, state2, extractor);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof WoodenExtractorBlockEntity extractor) {
            player.openMenu(extractor);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}
