package com.userofbricks.resource_pack_trident_model.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Invoker("scheduleVelocityUpdate")
    public void invokeScheduleVelocityUpdate();
}
