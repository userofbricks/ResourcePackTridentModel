package com.userofbricks.resource_pack_trident_model;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

public class ResourcePackTridentModel implements ModInitializer {
    public static final TrackedData<ItemStack> ProjectileItemStack = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    @Override
    public void onInitialize() {
    }
}
