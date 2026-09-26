package com.snd.handystarters.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import com.snd.handystarters.HandyStarters;

/**
 * Mirrors vanilla's wheat-seed drop from grass (12.5% chance, boosted by Fortune,
 * skipped when broken with shears) so coarse fiber drops from grass, seagrass and
 * dead bushes at exactly the same rate. The two loaders each hook their own
 * loot-table-modification event and call {@link #buildCoarseFiberPool} to get the
 * actual pool to add.
 *
 * <p>The drop itself lives in the data-driven loot table
 * {@code handy_starters:inject/coarse_fiber}; the pool added here only references it.
 * Building the drop in code would need a registry lookup to resolve Fortune, and
 * NeoForge 21.1's {@code LootTableLoadEvent} doesn't provide one.
 */
public final class ModCoarseFiberLoot {
    private static final ResourceKey<LootTable> COARSE_FIBER_TABLE = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(HandyStarters.MOD_ID, "inject/coarse_fiber"));

    private static final Block[] SOURCE_BLOCKS = {
            Blocks.SHORT_GRASS, Blocks.TALL_GRASS,
            Blocks.SEAGRASS, Blocks.TALL_SEAGRASS,
            Blocks.DEAD_BUSH,
    };

    public static boolean isCoarseFiberLootTable(ResourceLocation id) {
        for (Block block : SOURCE_BLOCKS) {
            if (block.getLootTable().location().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static LootPool buildCoarseFiberPool() {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(NestedLootTable.lootTableReference(COARSE_FIBER_TABLE))
                .build();
    }

    private ModCoarseFiberLoot() {
    }
}
