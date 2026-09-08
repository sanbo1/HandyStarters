package com.snd.handystarters.registry;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;

import com.snd.handystarters.ExampleMod;
import com.snd.handystarters.platform.ModPlatform;

public final class ModItems {
    // A furnace smelts one item per 200 ticks by default, so this burns exactly one item per pellet.
    private static final int WOOD_PELLET_BURN_TIME = 200;

    // What vanilla gives every wooden tool (verified against FuelValues).
    private static final int WOODEN_TOOL_BURN_TIME = 200;

    // The cane fights like a wooden hoe: vanilla builds that one as
    // HoeItem(ToolMaterial.WOOD, 0.0F, -3.0F), i.e. the displayed 1 damage / 1.0 speed.
    private static final float WOODEN_CANE_ATTACK_DAMAGE = 1.0F;
    private static final float WOODEN_CANE_ATTACK_SPEED = 1.0F;

    // Pole saw combat/utility stats. Attack speed isn't specified by the design, so it borrows
    // a stone axe's feel (displayed speed 0.8 => modifier of 0.8 - 4.0 base).
    private static final float POLE_SAW_ATTACK_DAMAGE = 3.0F;
    private static final float POLE_SAW_ATTACK_SPEED = 0.8F;
    private static final float POLE_SAW_REACH_BONUS = 3.0F;
    // Matches vanilla hoes: 2 durability per attack, and (like a hoe, unlike an axe) it
    // doesn't disable shield blocking.
    private static final int POLE_SAW_ATTACK_DURABILITY_COST = 2;

    // Vanilla's "Tools & Utilities" creative tab. Referenced by key instead of the
    // (private) CreativeModeTabs constant so we don't need to build our own tab.
    private static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("tools_and_utilities"));
    private static final ResourceKey<CreativeModeTab> INGREDIENTS_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("ingredients"));

    public static Supplier<Item> WOODEN_CANE;
    public static Supplier<Item> WOOD_PELLET;
    public static Supplier<Item> COARSE_FIBER;
    public static Supplier<Item> POLE_SAW;

    public static void init(ModPlatform platform) {
        WOODEN_CANE = platform.registerItem("wooden_cane",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("wooden_cane"))
                        .stacksTo(1)
                        .attributes(buildWoodenCaneAttributes())));

        WOOD_PELLET = platform.registerItem("wood_pellet",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("wood_pellet"))));

        COARSE_FIBER = platform.registerItem("coarse_fiber",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("coarse_fiber"))));

        POLE_SAW = platform.registerItem("pole_saw",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("pole_saw"))
                        .stacksTo(1)
                        .durability(ToolMaterial.COPPER.durability())
                        .component(DataComponents.TOOL, buildPoleSawTool())
                        .component(DataComponents.WEAPON, new Weapon(POLE_SAW_ATTACK_DURABILITY_COST, 0.0F))
                        .attributes(buildPoleSawAttributes())));

        platform.addToCreativeTab(TOOLS_AND_UTILITIES_TAB, WOODEN_CANE);
        platform.addToCreativeTab(TOOLS_AND_UTILITIES_TAB, POLE_SAW);
        platform.addToCreativeTab(INGREDIENTS_TAB, WOOD_PELLET);
        platform.addToCreativeTab(INGREDIENTS_TAB, COARSE_FIBER);

        platform.registerFuel(WOOD_PELLET, WOOD_PELLET_BURN_TIME);
        platform.registerFuel(WOODEN_CANE, WOODEN_TOOL_BURN_TIME);
    }

    private static ItemAttributeModifiers buildWoodenCaneAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, WOODEN_CANE_ATTACK_DAMAGE - 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, WOODEN_CANE_ATTACK_SPEED - 4.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private static Tool buildPoleSawTool() {
        HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        float miningSpeed = ToolMaterial.STONE.speed();
        return new Tool(List.of(
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_AXE), miningSpeed),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_HOE), miningSpeed)),
                1.0F, 1, true);
    }

    private static ItemAttributeModifiers buildPoleSawAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, POLE_SAW_ATTACK_DAMAGE - 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, POLE_SAW_ATTACK_SPEED - 4.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.BLOCK_INTERACTION_RANGE,
                        new AttributeModifier(itemId("pole_saw_block_reach"), POLE_SAW_REACH_BONUS, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(itemId("pole_saw_entity_reach"), POLE_SAW_REACH_BONUS, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, itemId(path));
    }

    private static Identifier itemId(String path) {
        return Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, path);
    }

    private ModItems() {
    }
}
