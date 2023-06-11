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
    private void onTick(TickEvent.Pre event) {
        if (mc.player != null) {
            if (mc.options.forwardKey.isPressed()) {
                moveForward();
            } else if (mc.options.backKey.isPressed()) {
                moveBackward();
            } else if (mc.options.leftKey.isPressed()) {
                strafeLeft();
            } else if (mc.options.rightKey.isPressed()) {
                strafeRight();
            } else if (mc.options.jumpKey.isPressed()) {
                moveUp();
            } else if (mc.options.sneakKey.isPressed()) {
                moveDown();
            }
        }
    }

    private void moveForward() {
        assert mc.player != null;
        double yaw = Math.toRadians(mc.player.getYaw());

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        sendMovementPacket(x, 0, z);
    }

    private void moveBackward() {
        assert mc.player != null;
        double yaw = Math.toRadians(mc.player.getYaw());

        double x = Math.sin(yaw);
        double z = -Math.cos(yaw);

        sendMovementPacket(x, 0, z);
    }

    private void strafeLeft() {
        assert mc.player != null;
        double yaw = Math.toRadians(mc.player.getYaw() - 90);

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        sendMovementPacket(x, 0, z);
    }

    private void strafeRight() {
        assert mc.player != null;
        double yaw = Math.toRadians(mc.player.getYaw() + 90);

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        sendMovementPacket(x, 0, z);
    }

    private void moveUp() {
        sendMovementPacket(0, 0.06, 0);
    }

    private void moveDown() {
        sendMovementPacket(0, -0.06, 0);
    }

    private void sendMovementPacket(double x, double y, double z) {
        double speed = 0.06;
        double yspeed = 0.12;
        double xOffset = x * speed;
        double yOffset = y * yspeed;
        double zOffset = z * speed;

        assert mc.player != null;
        mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + xOffset, mc.player.getY() + yOffset, mc.player.getZ() + zOffset, mc.player.isOnGround()));
        mc.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + 420, mc.player.getY(), mc.player.getZ() + 420, mc.player.isOnGround()));
    }
}
