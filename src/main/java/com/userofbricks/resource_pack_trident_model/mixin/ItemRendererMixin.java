package com.userofbricks.resource_pack_trident_model.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.MatrixUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.client.render.item.ItemRenderer.*;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    @Final
    private ItemModels models;

    @Shadow
    private static boolean usesDynamicDisplay(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }

    @Shadow
    protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);

    //Credit diskree
    @WrapOperation(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/BakedModelManager;getModel(Lnet/minecraft/client/util/ModelIdentifier;)Lnet/minecraft/client/render/model/BakedModel;"
            )
    )
    private BakedModel applyModelOverridesForSpyglassAndTrident(
            BakedModelManager modelManager,
            ModelIdentifier id,
            @NotNull Operation<BakedModel> original,
            @Local(argsOnly = true) ItemStack stack,
            @Local(argsOnly = true) BakedModel model
    ) {
        BakedModel spyglassOrTridentModel = original.call(modelManager, id);
        BakedModel customSpyglassOrTridentModel = spyglassOrTridentModel.getOverrides().apply(
                spyglassOrTridentModel,
                stack,
                null,
                null,
                0
        );
        if (customSpyglassOrTridentModel == null) {
            return models.getModelManager().getMissingModel();
        }
        return customSpyglassOrTridentModel;
    }

    //Credit UserofBricks
    @WrapOperation(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/BuiltinModelItemRenderer;render(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V")
    )
    private void handleNonBuiltinTridentInHandModel(
            BuiltinModelItemRenderer instance, ItemStack stack, ModelTransformationMode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
            Operation<Void> original,
            @Local(argsOnly = true) BakedModel model
    ) {
        if (!model.isBuiltin()) {
            boolean bl2;
            label63:
            {
                if (renderMode != ModelTransformationMode.GUI && !renderMode.isFirstPerson()) {
                    Item var12 = stack.getItem();
                    if (var12 instanceof BlockItem blockItem) {
                        Block block = blockItem.getBlock();
                        bl2 = !(block instanceof TranslucentBlock) && !(block instanceof StainedGlassPaneBlock);
                        break label63;
                    }
                }

                bl2 = true;
            }

            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
            VertexConsumer vertexConsumer;
            if (usesDynamicDisplay(stack) && stack.hasGlint()) {
                MatrixStack.Entry entry = matrices.peek().copy();
                if (renderMode == ModelTransformationMode.GUI) {
                    MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
                } else if (renderMode.isFirstPerson()) {
                    MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
                }

                vertexConsumer = getDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
            } else if (bl2) {
                vertexConsumer = getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
            } else {
                vertexConsumer = getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
            }

            renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
        } else {
            original.call(instance, stack, renderMode, matrices, vertexConsumers, light, overlay);
        }
    }
}