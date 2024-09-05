package com.userofbricks.resource_pack_trident_model;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.LightBlock;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.client.item.ModelPredicateProviderRegistry.register;

public class ResourcePackTridentModelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerModelPredicateProviders();
    }

    public static void registerModelPredicateProviders() {
        register(Items.TRIDENT, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getActiveItem() != stack ? 0.0F : (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
            }
        });
        register(Items.TRIDENT, Identifier.ofVanilla("brushing"), (stack, world, entity, seed) -> {
            return entity != null && entity.getActiveItem() == stack ? (float) (entity.getItemUseTimeLeft() % 10) / 10.0F : 0.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("pulling"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("filled"), (stack, world, entity, seed) -> {
            return BundleItem.getAmountFilled(stack);
        });
        register(Items.TRIDENT, Identifier.ofVanilla("time"), new ClampedModelPredicateProvider() {
            private double time;
            private double step;
            private long lastTick;

            @Override
            public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
                Entity entity = livingEntity != null ? livingEntity : itemStack.getHolder();
                if (entity == null) {
                    return 0.0F;
                } else {
                    if (clientWorld == null && ((Entity) entity).getWorld() instanceof ClientWorld) {
                        clientWorld = (ClientWorld) ((Entity) entity).getWorld();
                    }

                    if (clientWorld == null) {
                        return 0.0F;
                    } else {
                        double d;
                        if (clientWorld.getDimension().natural()) {
                            d = (double) clientWorld.getSkyAngle(1.0F);
                        } else {
                            d = Math.random();
                        }

                        d = this.getTime(clientWorld, d);
                        return (float) d;
                    }
                }
            }

            private double getTime(World world, double skyAngle) {
                if (world.getTime() != this.lastTick) {
                    this.lastTick = world.getTime();
                    double d = skyAngle - this.time;
                    d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
                    this.step += d * 0.1;
                    this.step *= 0.9;
                    this.time = MathHelper.floorMod(this.time + this.step, 1.0);
                }

                return this.time;
            }
        });
        register(Items.TRIDENT, Identifier.ofVanilla("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
            LodestoneTrackerComponent lodestoneTrackerComponent = (LodestoneTrackerComponent) stack.get(DataComponentTypes.LODESTONE_TRACKER);
            return lodestoneTrackerComponent != null ? (GlobalPos) lodestoneTrackerComponent.target().orElse((GlobalPos) null) : CompassItem.createSpawnPos(world);
        }));
        register(Items.TRIDENT, Identifier.ofVanilla("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
            if (entity instanceof PlayerEntity playerEntity) {
                return (GlobalPos) playerEntity.getLastDeathPos().orElse((GlobalPos) null);
            } else {
                return null;
            }
        }));
        register(Items.TRIDENT, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return CrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / (float) CrossbowItem.getPullTime(stack, entity);
            }
        });
        register(Items.TRIDENT, Identifier.ofVanilla("pulling"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("charged"), (stack, world, entity, seed) -> {
            return CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("firework"), (stack, world, entity, seed) -> {
            ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent) stack.get(DataComponentTypes.CHARGED_PROJECTILES);
            return chargedProjectilesComponent != null && chargedProjectilesComponent.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("broken"), (stack, world, entity, seed) -> {
            return ElytraItem.isUsable(stack) ? 0.0F : 1.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("cast"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                boolean bl = entity.getMainHandStack() == stack;
                boolean bl2 = entity.getOffHandStack() == stack;
                if (entity.getMainHandStack().getItem() instanceof FishingRodItem) {
                    bl2 = false;
                }

                return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity) entity).fishHook != null ? 1.0F : 0.0F;
            }
        });
        register(Items.TRIDENT, Identifier.ofVanilla("blocking"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("level"), (stack, world, entity, seed) -> {
            BlockStateComponent blockStateComponent = (BlockStateComponent) stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
            Integer integer = (Integer) blockStateComponent.getValue(LightBlock.LEVEL_15);
            return integer != null ? (float) integer / 16.0F : 1.0F;
        });
        register(Items.TRIDENT, Identifier.ofVanilla("tooting"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
        });
    }
}
