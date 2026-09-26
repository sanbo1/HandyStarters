package com.snd.handystarters.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The pole saw's attack behaviour. Later versions express this with the
 * {@code minecraft:weapon} data component, which 1.21.1 doesn't have; a plain
 * {@link Item} neither loses durability on hit nor counts as "used" for the
 * statistic, so both are restored here the same way 1.21.1's DiggerItem does.
 *
 * <p>Matches vanilla hoes: 2 durability per attack, and (like a hoe, unlike an
 * axe) it doesn't disable shield blocking, which 1.21.1 only grants to AxeItem.
 */
public final class PoleSawItem extends Item {
    private static final int ATTACK_DURABILITY_COST = 2;

    public PoleSawItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(ATTACK_DURABILITY_COST, attacker, EquipmentSlot.MAINHAND);
    }
}
