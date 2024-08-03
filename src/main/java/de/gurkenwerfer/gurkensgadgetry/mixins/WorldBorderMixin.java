/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package de.gurkenwerfer.gurkensgadgetry.mixins;

import de.gurkenwerfer.gurkensgadgetry.modules.NoCollision;
import de.gurkenwerfer.gurkensgadgetry.modules.NoWorldBorder;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.border.WorldBorderStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {
    @Inject(method = "canCollide", at = @At("HEAD"), cancellable = true)
    private void canCollide(CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().get(Collisions.class).ignoreBorder()) info.setReturnValue(false);
    }

    @Inject(method = "contains(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void contains(CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().get(Collisions.class).ignoreBorder()) info.setReturnValue(true);
    }

    @Shadow
    private double centerX;
    @Shadow private double centerZ;

    @Shadow private WorldBorder.Area area;
    @Shadow protected abstract List<WorldBorderListener> getListeners();

    @Inject(method = "asVoxelShape", at = @At("HEAD"), cancellable = true)
    private void onAsVoxelShape(CallbackInfoReturnable<VoxelShape> info) {
        if (Modules.get().isActive(NoWorldBorder.class) || Modules.get().isActive(NoCollision.class) && Modules.get().get(NoCollision.class).shouldCancelBorderCollision()) {
            info.setReturnValue(VoxelShapes.empty());
        }
    }

    @Inject(method = "canCollide", at = @At("HEAD"), cancellable = true)
    private void onCanCollide(Entity entity, Box box, CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().isActive(NoWorldBorder.class) || Modules.get().isActive(NoCollision.class) && Modules.get().get(NoCollision.class).shouldCancelBorderCollision()) {
            info.setReturnValue(false);
        } else if (Modules.get().get(Collisions.class).ignoreBorder()) {
            info.setReturnValue(false);
        }
    }

    // No World Border

    @Inject(method = "getCenterX", at = @At("HEAD"), cancellable = true)
    private void onGetCenterX(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(0.0D);
        }
    }

    @Inject(method = "getCenterZ", at = @At("HEAD"), cancellable = true)
    private void onGetCenterZ(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(0.0D);
        }
    }

    @Inject(method = "setCenter", at = @At("HEAD"), cancellable = true)
    private void onSetCenter(double x, double z, CallbackInfo info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            this.centerX = 0.0D;
            this.centerZ = 0.0D;

            this.area.onCenterChanged();

            for (WorldBorderListener listener : this.getListeners()) {
                listener.onCenterChanged(MinecraftClient.getInstance().world.getWorldBorder(), 0.0D, 0.0D);
            }

            info.cancel();
        }
    }

    // Size & Stage

    @Inject(method = "getSize", at = @At("HEAD"), cancellable = true)
    private void onGetSize(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(Double.MAX_VALUE);
        }
    }

    @Inject(method = "getStage", at = @At("HEAD"), cancellable = true)
    private void onGetStage(CallbackInfoReturnable<WorldBorderStage> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(WorldBorderStage.STATIONARY);
        }
    }

    // Bounds

    @Inject(method = "getBoundWest", at = @At("HEAD"), cancellable = true)
    private void onGetBoundsWest(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(MathHelper.clamp(-Double.MAX_VALUE / 2.0, -Double.MAX_VALUE, Double.MAX_VALUE));
        }
    }

    @Inject(method = "getBoundNorth", at = @At("HEAD"), cancellable = true)
    private void onGetBoundsNorth(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(MathHelper.clamp(-Double.MAX_VALUE / 2.0, -Double.MAX_VALUE, Double.MAX_VALUE));
        }
    }

    @Inject(method = "getBoundEast", at = @At("HEAD"), cancellable = true)
    private void onGetBoundsEast(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(MathHelper.clamp(Double.MAX_VALUE / 2.0, -Double.MAX_VALUE, Double.MAX_VALUE));
        }
    }

    @Inject(method = "getBoundSouth", at = @At("HEAD"), cancellable = true)
    private void onGetBoundsSouth(CallbackInfoReturnable<Double> info) {
        if (Modules.get().isActive(NoWorldBorder.class)) {
            info.setReturnValue(MathHelper.clamp(Double.MAX_VALUE / 2.0, -Double.MAX_VALUE, Double.MAX_VALUE));
        }
    }
}
