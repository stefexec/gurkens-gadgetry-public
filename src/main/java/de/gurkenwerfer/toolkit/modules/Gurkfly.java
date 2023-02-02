package de.gurkenwerfer.toolkit.modules;

import de.gurkenwerfer.toolkit.GurkensGadgetry;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Gurkfly extends Module {
    double HOVER_SPEED = 0.0012;

    public SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> FLYING_SPEED = sgGeneral.add(new DoubleSetting.Builder()
        .name("flying-speed")
        .description("Take a wild guess.")
        .defaultValue(1.2)
        .min(0)
        .sliderMax(10)
        .build()
    );

    public Gurkfly() {
        super(GurkensGadgetry.CATEGORY, "gurkfly", "Fly like in creative mode. Only works on unprotected servers.");
    }

    @Override
    public void onActivate() {
        assert mc.player != null;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {

        assert mc.player != null;
        // hover and move
        if ((!mc.player.isOnGround() && !mc.options.jumpKey.isPressed()) && !mc.options.sneakKey.isPressed()) {
            if (mc.player != null) {
                // add upward momentum if direction keys are pressed
                if (mc.options.forwardKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(FLYING_SPEED.get()));
                } else if (mc.options.backKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(-FLYING_SPEED.get()));
                } else if (mc.options.leftKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY(45).multiply(FLYING_SPEED.get()));
                } else if (mc.options.rightKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY(-45).multiply(FLYING_SPEED.get()));
                }

                // add upward momentum
                mc.player.setVelocity(mc.player.getVelocity().x, HOVER_SPEED, mc.player.getVelocity().z);

                //fall down every 35 ticks to bypass vanilla fly detection
                if (mc.player.age % 35 == 0) {
                    mc.player.setVelocity(0, -0.048, 0);
                }
            }
        } else if (mc.options.jumpKey.isPressed()) {

            //gain upward momentum
            if (mc.player != null) {

                mc.player.setVelocity(mc.player.getVelocity().add(0, 0.12, 0));
                //fall down every 35 ticks to bypass vanilla fly detection
                if (mc.player.age % 35 == 0) {
                    mc.player.setVelocity(0, -0.048, 0);
                }
            }
        } else if (mc.options.sneakKey.isPressed()) {
            //gain downward momentum
            if (mc.player != null) {
                mc.player.setVelocity(mc.player.getVelocity().add(0, -0.06, 0));
                // no bypass needed here
            }
        }
    }
}
