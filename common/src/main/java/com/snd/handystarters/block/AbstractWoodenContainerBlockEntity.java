package com.snd.handystarters.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared 5-slot inventory plumbing for the wooden feeder and extractor.
 * Reuses vanilla's own {@link HopperMenu}/HopperScreen for the GUI since it
 * accepts any {@link net.minecraft.world.Container}, not just hoppers.
 */
public abstract class AbstractWoodenContainerBlockEntity extends BaseContainerBlockEntity {
    public static final int CONTAINER_SIZE = 5;

    /** One transfer per second, the rate both blocks are balanced around. */
    protected static final int TRANSFER_COOLDOWN = 20;

    // Same NBT key vanilla hoppers use, so the value reads naturally in NBT viewers.
    private static final String COOLDOWN_TAG = "TransferCooldown";

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private int cooldown = TRANSFER_COOLDOWN;

    protected AbstractWoodenContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Counts down one tick and reports whether this is the tick a transfer may run on.
     * The countdown keeps running even while the block is redstone-disabled, so pulsing
     * it with a redstone clock can't push the transfer rate above one item per second.
     */
    protected boolean tickCooldown() {
        if (--cooldown > 0) {
            return false;
        }
        cooldown = TRANSFER_COOLDOWN;
        return true;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        cooldown = tag.contains(COOLDOWN_TAG, Tag.TAG_INT) ? tag.getInt(COOLDOWN_TAG) : TRANSFER_COOLDOWN;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt(COOLDOWN_TAG, cooldown);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new HopperMenu(containerId, playerInventory, this);
    }
}
