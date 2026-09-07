package com.snd.handystarters.loot;

import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import com.snd.handystarters.registry.ModItems;

/**
 * Mirrors vanilla's wheat-seed drop from grass (12.5% chance, boosted by Fortune,
 * skipped when broken with shears) so coarse fiber drops from grass, seagrass and
 * dead bushes at exactly the same rate. The two loaders each hook their own
 * loot-table-modification event and call {@link #buildCoarseFiberPool} to get the
 * actual pool to add.
 */
public final class ModCoarseFiberLoot {
    private static final float DROP_CHANCE = 0.125F;
    private static final int FORTUNE_BONUS = 2;

    private static final Block[] SOURCE_BLOCKS = {
            Blocks.SHORT_GRASS, Blocks.TALL_GRASS,
            Blocks.SEAGRASS, Blocks.TALL_SEAGRASS,
            Blocks.DEAD_BUSH,
    };

    public static boolean isCoarseFiberLootTable(ResourceKey<LootTable> key) {
        for (Block block : SOURCE_BLOCKS) {
            if (block.getLootTable().map(key::equals).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    public static LootPool buildCoarseFiberPool(HolderLookup.Provider registries) {
        HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);
        Holder<Enchantment> fortune = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);

        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ModItems.COARSE_FIBER.get())
                        .when(LootItemRandomChanceCondition.randomChance(DROP_CHANCE))
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(items, Items.SHEARS)).invert())
                        .apply(ApplyBonusCount.addUniformBonusCount(fortune, FORTUNE_BONUS)))
                .build();
    }

    private ModCoarseFiberLoot() {
    }
}
