package com.snd.handystarters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * Hopper-style, one-item-at-a-time container transfer, reusing vanilla's own
 * {@link HopperBlockEntity#getContainerAt} / {@link HopperBlockEntity#addItem}
 * helpers so hoppers and other mods' pipes see our blocks as ordinary
 * inventories. The one addition is {@link #isOwnBlock}, which is checked on
 * the far side of every transfer so our own blocks never chain into each
 * other (feeder-feeder, extractor-extractor, feeder-extractor).
 */
final class ContainerTransfer {
    static boolean isOwnBlock(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof WoodenFeederBlock || block instanceof WoodenExtractorBlock;
    }

    private static int[] getSlots(Container container, Direction direction) {
        if (container instanceof WorldlyContainer worldly) {
            return worldly.getSlotsForFace(direction);
        }
        int[] slots = new int[container.getContainerSize()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    private static boolean canTakeItemFromContainer(Container destination, Container source, ItemStack stack, int slot, Direction direction) {
        if (!source.canTakeItem(destination, slot, stack)) {
            return false;
        }
        if (source instanceof WorldlyContainer worldly) {
            return worldly.canTakeItemThroughFace(slot, stack, direction);
        }
        return true;
    }

    /**
     * Tries to push one item out of {@code from} (positioned at {@code fromPos}) into
     * whatever container sits in {@code direction}. Mirrors HopperBlockEntity's own
     * ejectItems: try every non-empty slot in order until one is accepted.
     */
    static boolean pushOneItem(Level level, Container from, BlockPos fromPos, Direction direction) {
        BlockPos targetPos = fromPos.relative(direction);
        if (isOwnBlock(level, targetPos)) {
            return false;
        }
        Container destination = HopperBlockEntity.getContainerAt(level, targetPos);
        if (destination == null) {
            return false;
        }
        Direction insertDirection = direction.getOpposite();
        for (int i = 0; i < from.getContainerSize(); i++) {
            ItemStack stack = from.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack original = stack.copy();
            ItemStack moving = from.removeItem(i, 1);
            ItemStack leftover = HopperBlockEntity.addItem(from, destination, moving, insertDirection);
            if (leftover.isEmpty()) {
                // Both sides, because addItem only calls setItem() when the target slot was
                // empty: merging into an existing stack mutates it in place, so without this
                // the receiving chunk is never flagged for saving and the item is lost on
                // reload. Vanilla's ejectItems marks the destination here for the same reason.
                from.setChanged();
                destination.setChanged();
                return true;
            }
            from.setItem(i, original);
        }
        return false;
    }

    /**
     * Tries to pull one item into {@code into} (positioned at {@code intoPos}) from
     * whatever container sits directly above. Unlike a hopper, this never looks at
     * floating item entities: {@link HopperBlockEntity#getContainerAt} only ever
     * resolves block-backed containers.
     */
    static boolean pullOneItemFromAbove(Level level, Container into, BlockPos intoPos) {
        BlockPos abovePos = intoPos.above();
        if (isOwnBlock(level, abovePos)) {
            return false;
        }
        Container source = HopperBlockEntity.getContainerAt(level, abovePos);
        if (source == null) {
            return false;
        }
        Direction direction = Direction.DOWN;
        for (int slot : getSlots(source, direction)) {
            ItemStack stack = source.getItem(slot);
            if (stack.isEmpty() || !canTakeItemFromContainer(into, source, stack, slot, direction)) {
                continue;
            }
            ItemStack original = stack.copy();
            ItemStack moving = source.removeItem(slot, 1);
            ItemStack leftover = HopperBlockEntity.addItem(source, into, moving, null);
            if (leftover.isEmpty()) {
                source.setChanged();
                into.setChanged();
                return true;
            }
            source.setItem(slot, original);
        }
        return false;
    }

    private ContainerTransfer() {
    }
}
