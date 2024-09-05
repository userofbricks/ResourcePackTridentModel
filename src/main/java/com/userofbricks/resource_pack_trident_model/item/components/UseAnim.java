package com.userofbricks.resource_pack_trident_model.item.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.UseAction;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum UseAnim implements StringIdentifiable {
    NONE(0, "none", UseAction.NONE),
    EAT(1, "eat", UseAction.EAT),
    DRINK(2, "drink", UseAction.DRINK),
    BLOCK(3, "block", UseAction.BLOCK),
    BOW(4, "bow", UseAction.BOW),
    SPEAR(5, "spear", UseAction.SPEAR),
    CROSSBOW(6, "crossbow", UseAction.CROSSBOW),
    SPYGLASS(7, "spyglass", UseAction.SPYGLASS),
    TOOT_HORN(8, "toot_horn", UseAction.TOOT_HORN),
    BRUSH(9, "brush", UseAction.BRUSH);

    private static final IntFunction<UseAnim> BY_ID = ValueLists.createIdToValueFunction(UseAnim::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);
    public static final StringIdentifiable.EnumCodec<UseAnim> CODEC = StringIdentifiable.createCodec(UseAnim::values);
    public static final PacketCodec<ByteBuf, UseAnim> PACKET_CODEC = PacketCodecs.indexed(BY_ID, UseAnim::getId);

    private final int id;
    private final String name;
    private final UseAction useAction;

    UseAnim(int id, String name, UseAction useAction) {
        this.id = id;
        this.name = name;
        this.useAction = useAction;
    }

    @Override
    public String asString() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UseAction getUseAction() {
        return useAction;
    }
}
