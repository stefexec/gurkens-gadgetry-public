package de.gurkenwerfer.toolkit.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import de.gurkenwerfer.toolkit.GurkensGadgetry;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

public class WorldGuardBypass extends Module{

    public WorldGuardBypass () {
        super(GurkensGadgetry.CATEGORY, "WGBypass", "Bypasses WorldGuards region protection.");
    }

    @Override
    public void onActivate() {
        assert mc.player != null;
        mc.player.sendMessage(Text.of("Activated WGBypass"), false);
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().flying = true;
    }

    @EventHandler
    private void onTick (TickEvent.Pre event) {

        if (mc.player != null) {
            // tp small distances to avoid firing any movement events
            if (mc.options.forwardKey.isPressed()) {

                //send new movement packet
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 0.06, mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));

            } else if (mc.options.backKey.isPressed()) {

                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() - 0.06, mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));

            } else if (mc.options.leftKey.isPressed()) {

                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ() + 0.06, mc.player.isOnGround()));
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));

            } else if (mc.options.rightKey.isPressed()) {

                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ() - 0.06, mc.player.isOnGround()));
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));

            } else if (mc.options.sneakKey.isPressed()) {

                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.06, mc.player.getZ(), mc.player.isOnGround()));
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));

            } else if (mc.options.jumpKey.isPressed()) {

                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.06, mc.player.getZ(), mc.player.isOnGround()));
                mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));
            }
        }
    }
}
