package com.userofbricks.resource_pack_trident_model.item.components;

import com.userofbricks.resource_pack_trident_model.ResourcePackTridentModel;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class ItemDataComponents {
    public static final ComponentType<UseAnim> USE_ANIM = register("use_anim", (builder) -> builder.codec(UseAnim.CODEC).packetCodec(UseAnim.PACKET_CODEC));


    public static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        Identifier componentID = Identifier.of(ResourcePackTridentModel.MOD_ID, id);
        return Registry.register(Registries.DATA_COMPONENT_TYPE, componentID, builderOperator.apply(ComponentType.builder()).build());
    }

    public static void initialize() {
    }
}
