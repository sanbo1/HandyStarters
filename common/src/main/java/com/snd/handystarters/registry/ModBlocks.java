package com.snd.handystarters.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.snd.handystarters.block.WoodenExtractorBlock;
import com.snd.handystarters.block.WoodenExtractorBlockEntity;
import com.snd.handystarters.block.WoodenFeederBlock;
import com.snd.handystarters.block.WoodenFeederBlockEntity;
import com.snd.handystarters.platform.ModPlatform;

public final class ModBlocks {
    private static final ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace("functional_blocks"));

    public static Supplier<WoodenFeederBlock> WOODEN_FEEDER;
    public static Supplier<WoodenExtractorBlock> WOODEN_EXTRACTOR;
    public static Supplier<BlockEntityType<WoodenFeederBlockEntity>> WOODEN_FEEDER_ENTITY;
    public static Supplier<BlockEntityType<WoodenExtractorBlockEntity>> WOODEN_EXTRACTOR_ENTITY;

    public static void init(ModPlatform platform) {
        WOODEN_FEEDER = platform.registerBlock("wooden_feeder",
                () -> new WoodenFeederBlock(blockProperties()));
        WOODEN_EXTRACTOR = platform.registerBlock("wooden_extractor",
                () -> new WoodenExtractorBlock(blockProperties()));

        WOODEN_FEEDER_ENTITY = platform.registerBlockEntityType("wooden_feeder",
                WoodenFeederBlockEntity::new, WOODEN_FEEDER);
        WOODEN_EXTRACTOR_ENTITY = platform.registerBlockEntityType("wooden_extractor",
                WoodenExtractorBlockEntity::new, WOODEN_EXTRACTOR);

        Supplier<Item> feederItem = platform.registerItem("wooden_feeder",
                () -> new BlockItem(WOODEN_FEEDER.get(), new Item.Properties()));
        Supplier<Item> extractorItem = platform.registerItem("wooden_extractor",
                () -> new BlockItem(WOODEN_EXTRACTOR.get(), new Item.Properties()));

        platform.addToCreativeTab(FUNCTIONAL_BLOCKS_TAB, feederItem);
        platform.addToCreativeTab(FUNCTIONAL_BLOCKS_TAB, extractorItem);
    }

    private static BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.of()
                .strength(2.5F)
                .sound(SoundType.WOOD)
                // The visual model isn't a full cube (it has an output/intake nub), so
                // occlusion-based face culling must be disabled or neighboring blocks
                // wrongly hide their own faces where our model doesn't cover them.
                .noOcclusion();
    }

    private ModBlocks() {
    }
}
