package com.nx.oldcombat.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class WeaponDamageMixin {

    private static final float NX_SWORD_MULTIPLIER = 1.3F;
    private static final float NX_AXE_MULTIPLIER = 0.6F;

    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    private void nx$rebalanceWeaponDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        ItemStack weapon = source.getWeaponStack();
        if (weapon == null || weapon.isEmpty()) {
            return;
        }

        float result = cir.getReturnValue();
        if (weapon.getItem() instanceof SwordItem) {
            cir.setReturnValue(result * NX_SWORD_MULTIPLIER);
        } else if (weapon.getItem() instanceof AxeItem) {
            cir.setReturnValue(result * NX_AXE_MULTIPLIER);
        }
    }
}
