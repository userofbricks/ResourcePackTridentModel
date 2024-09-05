package com.userofbricks.resource_pack_trident_model.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.userofbricks.resource_pack_trident_model.item.components.ItemDataComponents;
import com.userofbricks.resource_pack_trident_model.item.components.UseAnim;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends Item implements ProjectileItem {
    public TridentItemMixin(Item.Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void noVelocityOnClient(PlayerEntity instance, double x, double y, double z, Operation<Void> original) {
        if (!instance.getWorld().isClient) {
            original.call(instance, x, y, z);
            ((EntityInvoker) instance).invokeScheduleVelocityUpdate();
        }
    }

    @Inject(method = "getUseAction", at = @At("RETURN"), cancellable = true)
    public void getUseAction(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
        cir.setReturnValue(stack.getOrDefault(ItemDataComponents.USE_ANIM, UseAnim.SPEAR).getUseAction());
    }
}
