package de.gurkenwerfer.toolkit.mixins;

import de.gurkenwerfer.toolkit.modules.Gurkwalk;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Shadow protected abstract void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks);

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo info) {
        Gurkwalk gw = Modules.get().get(Gurkwalk.class);
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc != null && mc.player != null) {
            if (gw.isActive()) {
                if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround move) {
                    PlayerMoveC2SPacket.PositionAndOnGround modified = new PlayerMoveC2SPacket.PositionAndOnGround(
                        gw.round(move.getX(0)),
                        gw.roundY() ? gw.round(move.getY(0)) : move.getY(0),
                        gw.round(move.getZ(0)),

                        move.isOnGround()
                    );

                    info.cancel();
                    this.sendImmediately(modified, callbacks);

                } else if (packet instanceof PlayerMoveC2SPacket.Full move) {
                    PlayerMoveC2SPacket.Full modified = new PlayerMoveC2SPacket.Full(
                        gw.round(move.getX(0)),
                        gw.roundY() ? gw.round(move.getY(0)) : move.getY(0),
                        gw.round(move.getZ(0)),

                        move.getYaw(mc.player.getYaw()),
                        move.getPitch(mc.player.getPitch()),
                        move.isOnGround()
                    );

                    info.cancel();
                    this.sendImmediately(modified, callbacks);

                } else if (packet instanceof VehicleMoveC2SPacket move) {
                    BoatEntity entity = new BoatEntity(EntityType.BOAT, mc.world);

                    entity.setPos(
                        gw.round(move.getX()),
                        gw.roundY() ? gw.round(move.getY()) : move.getY(),
                        gw.round(move.getZ())
                    );

                    entity.setYaw(move.getYaw());
                    entity.setPitch(move.getPitch());

                    VehicleMoveC2SPacket modified = new VehicleMoveC2SPacket(entity);

                    info.cancel();
                    this.sendImmediately(modified, callbacks);
                }
            }
        }
    }
}
