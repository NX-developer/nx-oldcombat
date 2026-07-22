package com.nx.oldcombat.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttackMixin {

    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void nx$removeAttackCooldown(float baseTime, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.0F);
    }

    @Redirect(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"
        )
    )
    private List<Entity> nx$removeSweepAttack(World world, Entity except, Box box, Predicate<Entity> predicate) {
        return Collections.emptyList();
    }
}
