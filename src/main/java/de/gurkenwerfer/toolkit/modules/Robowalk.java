package de.gurkenwerfer.toolkit.modules;

import de.gurkenwerfer.toolkit.GurkensGadgetry;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Robowalk extends Module {

    public Robowalk() {
        super(GurkensGadgetry.CATEGORY, "robowalk", "Bypasses Liveoverflow's Human-Detection.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        assert mc.player != null;
        double x = Math.round(mc.player.getX() * 100.0) / 100.0;
        double z = Math.round(mc.player.getZ() * 100.0) / 100.0;

        x = Math.nextAfter(x, x + Math.signum(x));
        z = Math.nextAfter(z, z + Math.signum(z));

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, mc.player.getY(), z, mc.player.isOnGround()));
    }
}
