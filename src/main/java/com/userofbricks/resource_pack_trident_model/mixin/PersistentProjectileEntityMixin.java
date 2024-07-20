package com.userofbricks.resource_pack_trident_model.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.userofbricks.resource_pack_trident_model.ResourcePackTridentModel.ProjectileItemStack;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {
    @Shadow
    private ItemStack stack;
    @Unique
    private boolean initialStackSynced = false;

    @Shadow
    protected abstract ItemStack getDefaultItemStack();

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    protected void syncStack(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(ProjectileItemStack, stack != null ? stack : getDefaultItemStack());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (!getWorld().isClient && !initialStackSynced) {
            dataTracker.set(ProjectileItemStack, stack.copy());
            initialStackSynced = true;
        }
    }

    @Inject(method = "setStack", at = @At("RETURN"))
    protected void setStack(ItemStack stack, CallbackInfo ci) {
        dataTracker.set(ProjectileItemStack, stack);
    }
}
