package de.gurkenwerfer.gurkensgadgetry.modules;

import de.gurkenwerfer.gurkensgadgetry.GurkensGadgetry;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

public class BlastGuard extends Module {
    public BlastGuard(){
        super(GurkensGadgetry.CATEGORY, "BlastGuard", "Avoid explosions. EXPERIMENTAL!");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Integer> xofftnt = sgGeneral.add(new IntSetting.Builder()
        .name("TNT x offset")
        .description("The offset for the x coordinate when dodging TNT.")
        .defaultValue(3)
        .min(0)
        .sliderMax(9)
        .build()
    );

    private final Setting<Integer> yofftnt = sgGeneral.add(new IntSetting.Builder()
        .name("TNT y offset")
        .description("The offset for the y coordinate when dodging TNT.")
        .defaultValue(2)
        .min(0)
        .sliderMax(9)
        .build()
    );

    private final Setting<Integer> zofftnt = sgGeneral.add(new IntSetting.Builder()
        .name("TNT z offset")
        .description("The offset for the z coordinate when dodging TNT.")
        .defaultValue(3)
        .min(0)
        .sliderMax(9)
        .build()
    );

    private final Setting<Integer> xoffcreep = sgGeneral.add(new IntSetting.Builder()
        .name("Creeper x offset")
        .description("The offset for the x coordinate when dodging creepers.")
        .defaultValue(3)
        .min(0)
        .sliderMax(9)
        .build()
    );

    private final Setting<Integer> yoffcreep = sgGeneral.add(new IntSetting.Builder()
        .name("Creeper y offset")
        .description("The offset for the y coordinate when dodging creepers.")
        .defaultValue(2)
        .min(0)
        .sliderMax(9)
        .build()
    );

    public final Setting<BlastGuard.Mode> mode = sgGeneral.add(new EnumSetting.Builder<BlastGuard.Mode>()
        .name("mode")
        .description("Dodging mode.")
        .defaultValue(BlastGuard.Mode.TP)
        .build()
    );

    private final Setting<Integer> zoffcreep = sgGeneral.add(new IntSetting.Builder()
        .name("Creeper z offset")
        .description("The offset for the z coordinate when dodging creepers.")
        .defaultValue(3)
        .min(0)
        .sliderMax(9)
        .build()
    );

    private final Setting<Integer> clipheight = sgGeneral.add(new IntSetting.Builder()
        .name("Clip height")
        .description("How far to clip up.")
        .defaultValue(200)
        .min(100)
        .sliderMax(225)
        .build()
    );

    private final Setting<Boolean> debugprint = sgGeneral.add(new BoolSetting.Builder()
        .name("debug print")
        .description("Prints debug messages.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> creepers = sgGeneral.add(new BoolSetting.Builder()
        .name("creepers")
        .description("Dodge creepers.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> tnt = sgGeneral.add(new BoolSetting.Builder()
        .name("tnt")
        .description("Dodge TNT.")
        .defaultValue(true)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {

        assert mc.world != null;
        assert mc.player != null;

        // check if there are any lit tnt entities nearby
        if (mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(5, 5, 5)).stream().anyMatch(e -> e instanceof net.minecraft.entity.TntEntity) && tnt.get()) {

            // check for a free 2 block high space 5 blocks horizontally away from the tnt
            net.minecraft.entity.Entity tnt = mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(5, 3, 5)).stream().filter(e -> e instanceof net.minecraft.entity.TntEntity).findFirst().get();

            for (int x = -5; x < 5; x++) {
                for (int z = -5; z < 5; z++) {
                    for (int y = 0; y < 2; y++) {
                        if (mc.world.getBlockState(new BlockPos((int) (tnt.getX() + x), (int) (tnt.getY() + y), (int) (tnt.getZ() + z))).isAir() && mc.world.getBlockState(new BlockPos((int) (tnt.getX() + x), (int) (tnt.getY() + y + 1), (int) (tnt.getZ() + z))).isAir()) {
                            if(debugprint.get()) {
                                warning("TNT detected, moving to safe space...");
                            }
                            // move to the free space
                            if (mode.get() == Mode.Clip) {

                                /*
                                for (int i = 0; i < 12; i++) {
                                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.42, mc.player.getZ(), true));
                                }
                                // send sus
                                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), clipheight.get(), mc.player.getZ(), true));
                                */

                                clip();

                            } else if (mode.get() == Mode.TP) {

                                mc.player.setPosition(mc.player.getX() + x + xofftnt.get(), mc.player.getY() + y + yofftnt.get(), mc.player.getZ() + z + zofftnt.get());

                            }

                            return;
                        }
                    }
                }
            }
            try {
                Thread.sleep(180);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(5, 1, 5)).stream().anyMatch(e -> e instanceof net.minecraft.entity.mob.CreeperEntity) && creepers.get()) {

            // check for a free 2 block high space 5 blocks horizontally away from the tnt
            net.minecraft.entity.Entity creeper = mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(5, 1, 5)).stream().filter(e -> e instanceof net.minecraft.entity.mob.CreeperEntity).findFirst().get();

            for (int x = -5; x < 5; x++) {
                for (int z = -5; z < 5; z++) {
                    for (int y = 0; y < 2; y++) {
                        if (mc.world.getBlockState(new BlockPos((int) (creeper.getX() + x), (int) (creeper.getY() + y), (int) (creeper.getZ() + z))).isAir() && mc.world.getBlockState(new BlockPos((int) (creeper.getX() + x), (int) (creeper.getY() + y + 1), (int) (creeper.getZ() + z))).isAir()) {
                            if(debugprint.get()) {
                                warning("Creeper detected, moving to safe space...");
                            }
                            // move to the free space
                            if (mode.get() == Mode.Clip) {

                                /*
                                for (int i = 0; i < 12; i++) {
                                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.42, mc.player.getZ(), true));
                                }
                                // send sus
                                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), clipheight.get(), mc.player.getZ(), true));
                                 */

                                clip();

                            } else if (mode.get() == Mode.TP) {

                                mc.player.setPosition(mc.player.getX() + x + xoffcreep.get(), mc.player.getY() + y + yoffcreep.get(), mc.player.getZ() + z + zoffcreep.get());

                            }
                            return;
                        }
                    }
                }
            }
            try {
                Thread.sleep(180);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clip() {
        for (int i = 0; i < 12; i++) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.42, mc.player.getZ(), true));
        }
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), clipheight.get(), mc.player.getZ(), true));
    }

    public enum Mode {
        TP,
        Clip
    }
}
