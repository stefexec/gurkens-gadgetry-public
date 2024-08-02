package de.gurkenwerfer.gurkensgadgetry.modules;

import de.gurkenwerfer.gurkensgadgetry.GurkensGadgetry;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Gurkfly extends Module {

    public SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> FLYING_SPEED = sgGeneral.add(new DoubleSetting.Builder()
        .name("flying-speed")
        .description("Take a wild guess.")
        .defaultValue(2.420)
        .min(0)
        .sliderMax(10)
        .build()
    );

    private final Setting<Double> HOVER_SPEED = sgGeneral.add(new DoubleSetting.Builder()
        .name("hover-speed")
        .description("How far you ascend per tick when hovering.")
        .defaultValue(0.009)
        .min(0)
        .sliderMax(0.02)
        .build()
    );
    private final Setting<Double> BFALL_SPEED = sgGeneral.add(new DoubleSetting.Builder()
        .name("bypass-falldistance")
        .description("How far you fall every 35 ticks to bypass the vanilla anti-cheat.")
        .defaultValue(0.12)
        .min(0)
        .sliderMax(0.5)
        .build()
    );

    private final Setting<Double> ASC_SPEED = sgGeneral.add(new DoubleSetting.Builder()
        .name("ascending-speed")
        .description("How fast you ascend when pressing space.")
        .defaultValue(0.4)
        .min(0)
        .sliderMax(1)
        .build()
    );
    private final Setting<Double> DESC_SPEED = sgGeneral.add(new DoubleSetting.Builder()
        .name("descending-speed")
        .description("How fast you descend when pressing shift.")
        .defaultValue(0.2)
        .min(0)
        .sliderMax(0.5)
        .build()
    );

    private final Setting<Integer> ticks = sgGeneral.add(new IntSetting.Builder()
        .name("Bypass Fall Ticks")
        .description("How many ticks to wait before falling again to bypass the vanilla anti-cheat.")
        .defaultValue(20)
        .min(1)
        .sliderMax(50)
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

        int tick = 0;

        assert mc.player != null;
        // hover and move
        if ((!mc.player.isOnGround() && !mc.options.jumpKey.isPressed()) && !mc.options.sneakKey.isPressed()) {
            if (mc.player != null) {
                // add directional momentum if direction keys are pressed
                if (mc.options.forwardKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(FLYING_SPEED.get()));
                } else if (mc.options.backKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().multiply(-FLYING_SPEED.get()));
                } else if (mc.options.leftKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY((float) Math.PI / 2).multiply(FLYING_SPEED.get()));
                } else if (mc.options.rightKey.isPressed()) {
                    mc.player.setVelocity(mc.player.getRotationVector().rotateY((float)-Math.PI / 2).multiply(FLYING_SPEED.get()));
                } else {
                    mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
                }

                //fall down every 35 ticks to bypass vanilla fly detection
                if (mc.player.age % ticks.get() == 0) {
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() - BFALL_SPEED.get(), mc.player.getZ());

                } else {
                    mc.player.setVelocity(mc.player.getVelocity().x, HOVER_SPEED.get(), mc.player.getVelocity().z);
                }

            }
        } else if (mc.options.jumpKey.isPressed()) {

            //gain upward momentum
            if (mc.player != null) {
                //fall down every 35 ticks to bypass vanilla fly detection
                if (mc.player.age % ticks.get() == 0) {
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() - BFALL_SPEED.get(), mc.player.getZ());
                } else {
                    mc.player.setVelocity(mc.player.getVelocity().add(0, ASC_SPEED.get(), 0));
                }
            }
        } else if (mc.options.sneakKey.isPressed()) {
            //gain downward momentum
            if (mc.player != null) {
                mc.player.setVelocity(mc.player.getVelocity().add(0, -DESC_SPEED.get(), 0));
            }
        }
    }
}
