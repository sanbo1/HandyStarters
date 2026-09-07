package com.snd.handystarters;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.snd.handystarters.platform.ModPlatform;
import com.snd.handystarters.registry.ModItems;

/**
 * While a player holds the wooden cane in either hand, boosts their step height
 * enough to walk up a full 1-block step without jumping.
 */
public final class WoodenCaneStepAssist {
    private static final Identifier STEP_HEIGHT_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "wooden_cane_step_height");
    private static final double STEP_HEIGHT_BONUS = 1.0;

    public static void init(ModPlatform platform) {
        platform.onPlayerTick(WoodenCaneStepAssist::onPlayerTick);
    }

    private static void onPlayerTick(Player player) {
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight == null) {
            return;
        }

        boolean holdingCane = isWoodenCane(player.getMainHandItem()) || isWoodenCane(player.getOffhandItem());
        boolean hasModifier = stepHeight.hasModifier(STEP_HEIGHT_MODIFIER_ID);

        if (holdingCane && !hasModifier) {
            stepHeight.addTransientModifier(
                    new AttributeModifier(STEP_HEIGHT_MODIFIER_ID, STEP_HEIGHT_BONUS, AttributeModifier.Operation.ADD_VALUE));
        } else if (!holdingCane && hasModifier) {
            stepHeight.removeModifier(STEP_HEIGHT_MODIFIER_ID);
        }
    }

    private static boolean isWoodenCane(ItemStack stack) {
        return stack.getItem() == ModItems.WOODEN_CANE.get();
    }

    private WoodenCaneStepAssist() {
    }
}
