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
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import com.snd.handystarters.HandyStarters;
import com.snd.handystarters.item.CrudeArrowItem;
import com.snd.handystarters.platform.ModPlatform;

public final class ModItems {
    // Burn times are datapack references rather than raw numbers, because vanilla's
    // cooking/time_* providers aren't plain constants: each one divides its value by 2
    // in a smoker or blast furnace. Passing a constant would silently drop that halving
    // and double the mod's fuel efficiency in those two blocks.
    //
    // A furnace smelts one item per 200 ticks by default, so wood_items_large (200) burns
    // exactly one item per pellet. It is also what vanilla gives every wooden tool, which
    // is the rate the cane is balanced around. dry_plants (100) is what vanilla sticks and
    // saplings burn for, i.e. half a wooden tool.
    private static final ResourceKey<ContextIntProvider> WOOD_PELLET_BURN_TIME =
            ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE;
    private static final ResourceKey<ContextIntProvider> WOODEN_TOOL_BURN_TIME =
            ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE;
    private static final ResourceKey<ContextIntProvider> COARSE_FIBER_BURN_TIME =
            ContextIntProviders.COOKING_TIME_DRY_PLANTS;

    // The cane fights like a wooden hoe: vanilla builds that one as
    // Item.Properties.hoe(ToolMaterial.WOOD, 0.0F, -3.0F), i.e. the displayed
    // 1 damage / 1.0 speed. (26.3 removed HoeItem; the stats moved to Properties.)
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
    private static final ResourceKey<CreativeModeTab> COMBAT_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("combat"));

    public static Supplier<Item> WOODEN_CANE;
    public static Supplier<Item> WOOD_PELLET;
    public static Supplier<Item> COARSE_FIBER;
    public static Supplier<Item> POLE_SAW;
    public static Supplier<Item> CRUDE_ARROW;

    public static void init(ModPlatform platform) {
        WOODEN_CANE = platform.registerItem("wooden_cane",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("wooden_cane"))
                        .stacksTo(1)
                        .cookingFuel(WOODEN_TOOL_BURN_TIME)
                        .attributes(buildWoodenCaneAttributes())));

        WOOD_PELLET = platform.registerItem("wood_pellet",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("wood_pellet"))
                        .cookingFuel(WOOD_PELLET_BURN_TIME)));

        COARSE_FIBER = platform.registerItem("coarse_fiber",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("coarse_fiber"))
                        .cookingFuel(COARSE_FIBER_BURN_TIME)));

        POLE_SAW = platform.registerItem("pole_saw",
                () -> new Item(new Item.Properties()
                        .setId(itemKey("pole_saw"))
                        .stacksTo(1)
                        .durability(ToolMaterial.COPPER.durability())
                        .component(DataComponents.TOOL, buildPoleSawTool())
                        .component(DataComponents.WEAPON, new Weapon(POLE_SAW_ATTACK_DURABILITY_COST, 0.0F))
                        .attributes(buildPoleSawAttributes())));

        CRUDE_ARROW = platform.registerItem("crude_arrow",
                () -> new CrudeArrowItem(new Item.Properties()
                        .setId(itemKey("crude_arrow"))));

        platform.addToCreativeTab(TOOLS_AND_UTILITIES_TAB, WOODEN_CANE);
        platform.addToCreativeTab(COMBAT_TAB, CRUDE_ARROW);
        platform.addToCreativeTab(TOOLS_AND_UTILITIES_TAB, POLE_SAW);
        platform.addToCreativeTab(INGREDIENTS_TAB, WOOD_PELLET);
        platform.addToCreativeTab(INGREDIENTS_TAB, COARSE_FIBER);

        platform.registerDispenserProjectile(CRUDE_ARROW);
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
        return Identifier.fromNamespaceAndPath(HandyStarters.MOD_ID, path);
    }

    private ModItems() {
    }
}
