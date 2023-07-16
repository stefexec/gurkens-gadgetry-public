package de.gurkenwerfer.toolkit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.gurkenwerfer.toolkit.mixins.ClientConnectionAccessor;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class AutoClip extends Command {
    public AutoClip() {
        super("autoclip", "Tries to clip to the first available position below you.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {

            BlockPos playerPos = mc.player.getBlockPos();
            int y = playerPos.getY();
            BlockPos blockBelowPos = findFirstAirBlockBelowPlayer(playerPos);

            if (blockBelowPos != null) {
                // get block below the first air block
                y = blockBelowPos.down().getY();
                mc.player.sendMessage(Text.of("Found first air block below you at y = " + y), false);
            } else {
                mc.player.sendMessage(Text.of("No air blocks found below you."), false);
                return SINGLE_SUCCESS;
            }

            int blocks = playerPos.getY() - y;
            int packetsRequired = (int) Math.ceil(Math.abs(blocks / 10));

            assert mc.player != null;
            mc.player.sendMessage(Text.of("Clipping to y = " + y), false);

            if (mc.player.hasVehicle()) {

                Entity boat = mc.player.getVehicle();

                for (int packetNumber = 0; packetNumber < (packetsRequired - 1); packetNumber++) {
                    Packet<?> positionPacket = new VehicleMoveC2SPacket(boat);
                    ((ClientConnectionAccessor) mc.getNetworkHandler().getConnection())._sendImmediately(positionPacket, null);
                }

                boat.updatePosition(boat.getX(), y, boat.getZ());
                Packet<?> positionPacket = new VehicleMoveC2SPacket(boat);
                ((ClientConnectionAccessor) mc.getNetworkHandler().getConnection())._sendImmediately(positionPacket, null);

            } else {

                // it wont work if you dont send a position packet before the onground packet for whatever fucking reason
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));

                // taken from meteor
                for (int packetNumber = 0; packetNumber < (packetsRequired - 1); packetNumber++) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
                }
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), y, mc.player.getZ(), true));
            }
            return SINGLE_SUCCESS;
        });
    }

    // function that finds the first available position below the player that consists of two air blocks, if player stands on ground, go down until two air blocks are found
    public BlockPos findFirstAirBlockBelowPlayer(BlockPos playerPos) {

        BlockPos currentPos = playerPos.down();

        while (currentPos.getY() > -60) {
            if (mc.world.getBlockState(currentPos).isAir() && mc.world.getBlockState(currentPos.down()).isAir()) {
                return currentPos;
            }
            currentPos = currentPos.down();
        }
        return null;
    }
}
