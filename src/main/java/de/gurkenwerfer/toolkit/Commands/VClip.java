package de.gurkenwerfer.toolkit.Commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class VClip extends Command {
    public VClip() {
        // Thanks to 0xEnjoy for the original vClip.
        // idk why he removed the repo, but I still had it saved.
        super("vclip", "Clips up or down through walls up to 10 blocks away.", "vClip");
    }

    private Block getBlock(BlockPos pos) {
        return mc.player.world.getBlockState(pos).getBlock();
    }

    private boolean doAutoClipDown(float incr) {
        BlockPos pos = mc.player.getBlockPos();
        //      -1            -10          -1
        ClientPlayerEntity player = mc.player;
        assert player != null;
        if(incr == 0) incr = 1;
        for(float i = incr; incr > 0 ? i <= 10 : i >= -10; i += incr) {
            if(getBlock(pos.add(0, i, 0)) == Blocks.AIR && getBlock(pos.add(0, i - 1, 0)) == Blocks.AIR) {
                ChatUtils.info("Found clip block " + i + " blocks " + (incr > 0 ? "up" : "down") + ".");
                if (player.hasVehicle()) {
                    Entity vehicle = player.getVehicle();
                    vehicle.setPosition(vehicle.getX(), vehicle.getY() + i -1, vehicle.getZ());
                }
                player.setPosition(player.getX(), player.getY() + i -1, player.getZ());
                return true;
            }
        }
        ChatUtils.error("Unable to clip.");
        return false;
    }

    private boolean doAutoClipUp(float incr) {
        BlockPos pos = mc.player.getBlockPos();
        //      -1            -10          -1
        ClientPlayerEntity player = mc.player;
        assert player != null;
        if(incr == 0) incr = 1;
        for(float i = incr; incr > 0 ? i <= 10 : i >= -10; i += incr) {
            if(getBlock(pos.add(0, i, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 1, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 2, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 3, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 4, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 5, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 6, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 7, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 8, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 9, 0)) == Blocks.AIR && getBlock(pos.add(0, i + 10, 0)) == Blocks.AIR) {
                ChatUtils.info("Found clip block " + i + " blocks " + (incr > 0 ? "up" : "down") + ".");
                if (player.hasVehicle()) {
                    Entity vehicle = player.getVehicle();
                    vehicle.setPosition(vehicle.getX(), vehicle.getY() + i, vehicle.getZ());
                }
                player.setPosition(player.getX(), player.getY() + i, player.getZ());
                return true;
            }
        }
        ChatUtils.error("Unable to clip.");
        return false;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("down").executes(c -> {
            doAutoClipDown(-1);
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("up").executes(c -> {
            doAutoClipUp(1);
            return SINGLE_SUCCESS;
        }));
    }
}
