package com.snd.handystarters.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.snd.handystarters.ExampleMod;
import com.snd.handystarters.block.WoodenExtractorBlock;
import com.snd.handystarters.block.WoodenExtractorBlockEntity;
import com.snd.handystarters.block.WoodenFeederBlock;
import com.snd.handystarters.block.WoodenFeederBlockEntity;
import com.snd.handystarters.platform.ModPlatform;

public final class ModBlocks {
    private static final ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("functional_blocks"));

    public static Supplier<WoodenFeederBlock> WOODEN_FEEDER;
    public static Supplier<WoodenExtractorBlock> WOODEN_EXTRACTOR;
    public static Supplier<BlockEntityType<WoodenFeederBlockEntity>> WOODEN_FEEDER_ENTITY;
    public static Supplier<BlockEntityType<WoodenExtractorBlockEntity>> WOODEN_EXTRACTOR_ENTITY;

    public static void init(ModPlatform platform) {
        WOODEN_FEEDER = platform.registerBlock("wooden_feeder",
                () -> new WoodenFeederBlock(blockProperties("wooden_feeder")));
        WOODEN_EXTRACTOR = platform.registerBlock("wooden_extractor",
                () -> new WoodenExtractorBlock(blockProperties("wooden_extractor")));

        WOODEN_FEEDER_ENTITY = platform.registerBlockEntityType("wooden_feeder",
                () -> new BlockEntityType<>(WoodenFeederBlockEntity::new, java.util.Set.of(WOODEN_FEEDER.get())));
        WOODEN_EXTRACTOR_ENTITY = platform.registerBlockEntityType("wooden_extractor",
                () -> new BlockEntityType<>(WoodenExtractorBlockEntity::new, java.util.Set.of(WOODEN_EXTRACTOR.get())));

        Supplier<Item> feederItem = platform.registerItem("wooden_feeder",
                () -> new BlockItem(WOODEN_FEEDER.get(), new Item.Properties().setId(itemKey("wooden_feeder"))));
        Supplier<Item> extractorItem = platform.registerItem("wooden_extractor",
                () -> new BlockItem(WOODEN_EXTRACTOR.get(), new Item.Properties().setId(itemKey("wooden_extractor"))));

        platform.addToCreativeTab(FUNCTIONAL_BLOCKS_TAB, feederItem);
        platform.addToCreativeTab(FUNCTIONAL_BLOCKS_TAB, extractorItem);
    }

    private static BlockBehaviour.Properties blockProperties(String path) {
        return BlockBehaviour.Properties.of()
                .setId(blockKey(path))
                .strength(2.5F)
                .sound(SoundType.WOOD)
                // The visual model isn't a full cube (it has an output/intake nub), so
                // occlusion-based face culling must be disabled or neighboring blocks
                // wrongly hide their own faces where our model doesn't cover them.
                .noOcclusion();
    }

    private static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, path));
    }

    private static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, path));
    }

    private ModBlocks() {
    }
}
