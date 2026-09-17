package com.snd.handystarters.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.snd.handystarters.registry.ModItems;

/**
 * The arrow a {@link com.snd.handystarters.item.CrudeArrowItem} fires: half the
 * damage of a normal arrow, can't be picked up again, drops fast and flies wide.
 *
 * <p>It deliberately keeps vanilla's {@code minecraft:arrow} entity type rather
 * than registering its own, which means it renders as an ordinary arrow and
 * still counts for the Power enchantment (whose condition is the entity type
 * tag {@code #minecraft:arrows}). The one cost is that an arrow saved and
 * reloaded mid-flight comes back as a plain vanilla arrow — damage and pickup
 * survive in NBT, but the gravity override does not. That only shows up if a
 * chunk happens to save during the second or so an arrow is airborne.
 */
public final class CrudeArrow extends Arrow {
    /** Vanilla arrows use 2.0. */
    private static final double BASE_DAMAGE = 1.0;

    /** Vanilla arrows use 0.05. Tripling it cuts the flat-shot range to about 68%. */
    private static final double GRAVITY = 0.15;

    /** A bow always passes an inaccuracy of 1.0, which is roughly one degree of spread. */
    private static final float INACCURACY_MULTIPLIER = 3.0F;

    public CrudeArrow(Level level, LivingEntity shooter, ItemStack pickupItem, ItemStack weapon) {
        super(level, shooter, pickupItem, weapon);
        applyCrudeStats();
    }

    public CrudeArrow(Level level, double x, double y, double z, ItemStack pickupItem, ItemStack weapon) {
        super(level, x, y, z, pickupItem, weapon);
        applyCrudeStats();
    }

    private void applyCrudeStats() {
        setBaseDamage(BASE_DAMAGE);
        pickup = Pickup.DISALLOWED;
    }

    @Override
    protected double getDefaultGravity() {
        return GRAVITY;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        // Inaccuracy is chosen by the weapon, not the ammunition, so widening it
        // for this arrow specifically has to happen here.
        super.shoot(x, y, z, velocity, inaccuracy * INACCURACY_MULTIPLIER);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.CRUDE_ARROW.get());
    }
}
