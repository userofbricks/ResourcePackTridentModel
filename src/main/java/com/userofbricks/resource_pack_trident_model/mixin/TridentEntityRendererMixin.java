package com.userofbricks.resource_pack_trident_model.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.userofbricks.resource_pack_trident_model.ResourcePackTridentModel.ProjectileItemStack;

@Mixin(value = TridentEntityRenderer.class)
public class TridentEntityRendererMixin {

    @WrapOperation(
            method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/TridentEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V")
    )
    public void renderNonBuiltInItem(
            TridentEntityModel instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int overlay,
            Operation<Void> original,
            @Local(argsOnly = true) TridentEntity tridentEntity,
            @Local(argsOnly = true, ordinal = 0) float f,
            @Local(argsOnly = true, ordinal = 1) float g,
            @Local(argsOnly = true) VertexConsumerProvider vertexConsumerProvider
    ) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = tridentEntity.getDataTracker().get(ProjectileItemStack);
        World world = tridentEntity.getWorld();
        int entityId = tridentEntity.getId();

        BakedModel bakedModel = itemRenderer.getModel(itemStack, world, tridentEntity.getOwner() instanceof LivingEntity ? (LivingEntity) tridentEntity.getOwner() : null, entityId);
        if (!bakedModel.isBuiltin()) {
            itemRenderer.renderItem(itemStack, ModelTransformationMode.HEAD, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, world, entityId);
        } else {
            original.call(instance, matrixStack, vertexConsumer, i, overlay);
        }
    }
}
