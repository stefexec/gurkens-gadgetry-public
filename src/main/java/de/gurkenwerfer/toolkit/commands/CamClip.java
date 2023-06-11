package de.gurkenwerfer.toolkit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.gurkenwerfer.toolkit.mixins.ClientConnectionAccessor;
import de.gurkenwerfer.toolkit.modules.Gurkwalk;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CamClip extends Command {
    public CamClip() {
        super("CamClip", "Gets your camera's Y position and tries to clip there vertically.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            // int y = (int) mc.player.getY();

            //int y = (int) mc.cameraEntity.getY();
            assert mc.crosshairTarget != null;
            int y = (int) mc.crosshairTarget.getPos().getY();

            assert mc.player != null;
            mc.player.sendMessage(Text.of("Clipping to y = " + y), false);
            for (int i = 0; i < 13; i++) {
                sendPositionPacket(mc.player.getPos());
            }
            sendPositionPacket(new Vec3d(mc.player.getX(), y, mc.player.getZ()));

            return SINGLE_SUCCESS;
        });
    }

    public static void sendPositionPacket(Vec3d pos) {

        MinecraftClient mc = MinecraftClient.getInstance();
        // Check if coords should be rounded
        Gurkwalk gw = Modules.get().get(Gurkwalk.class);
        if (gw.isActive()) {
            Packet<?> positionPacket = new PlayerMoveC2SPacket.PositionAndOnGround(gw.round(pos.getX()), pos.getY(), gw.round(pos.getZ()), true);
            ((ClientConnectionAccessor) mc.getNetworkHandler().getConnection())._sendImmediately(positionPacket, null);
        } else {
            Packet<?> positionPacket = new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), true);
            ((ClientConnectionAccessor) mc.getNetworkHandler().getConnection())._sendImmediately(positionPacket, null);
        }
    }
}
