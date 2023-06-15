package de.gurkenwerfer.toolkit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CamClip extends Command {
    public CamClip() {
        super("camclip", "Gets your camera's Y position and tries to clip there vertically.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {

            assert mc.crosshairTarget != null;
            int y = (int) mc.crosshairTarget.getPos().getY();

            assert mc.player != null;
            mc.player.sendMessage(Text.of("Clipping to y = " + y), false);
            for (int i = 0; i < 9; i++) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            }
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), y, mc.player.getZ(), true));

            return SINGLE_SUCCESS;
        });
    }

}
