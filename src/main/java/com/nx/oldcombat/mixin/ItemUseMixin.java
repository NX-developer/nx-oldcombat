package com.nx.oldcombat.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemUseMixin {

    @Inject(method = "getUseAction", at = @At("HEAD"), cancellable = true)
    private void nx$getUseAction(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
        if (stack.getItem() instanceof SwordItem) {
            cir.setReturnValue(UseAction.BLOCK);
        } else if (stack.getItem() instanceof ShieldItem) {
            cir.setReturnValue(UseAction.NONE);
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void nx$getMaxUseTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof SwordItem) {
            cir.setReturnValue(72000);
        } else if (stack.getItem() instanceof ShieldItem) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void nx$use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.getItem() instanceof SwordItem) {
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume(stack));
        } else if (stack.getItem() instanceof ShieldItem) {
            cir.setReturnValue(TypedActionResult.pass(stack));
        }
    }
}
