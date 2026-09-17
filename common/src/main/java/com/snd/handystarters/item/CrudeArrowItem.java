package com.snd.handystarters.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.snd.handystarters.entity.CrudeArrow;

/**
 * A cheap, worse arrow. Both spawn paths are overridden: {@code createArrow} for
 * bows and crossbows, and {@code asProjectile} for dispensers — vanilla's
 * version of the latter also forces pickup back to ALLOWED, so leaving it alone
 * would quietly give dispenser-fired arrows normal behaviour.
 */
public final class CrudeArrowItem extends ArrowItem {
    public CrudeArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, ItemStack weapon) {
        return new CrudeArrow(level, shooter, ammo.copyWithCount(1), weapon);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new CrudeArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
    }
}
