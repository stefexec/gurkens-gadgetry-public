/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package de.gurkenwerfer.gurkensgadgetry.modules;

import de.gurkenwerfer.gurkensgadgetry.GurkensGadgetry;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.math.BlockPos;

public class BoomESP extends Module {

    public BoomESP() {
        super(GurkensGadgetry.CATEGORY, "BoomESP", "Renders the hitbox of TNT and Creepers.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Creepers, TNT, or Both.")
        .defaultValue(Mode.Both)
        .build()
    );

    private final Setting<ShapeMode> renderType = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("render-type")
        .defaultValue(ShapeMode.Lines)
        .build()
    );

    private final Setting<SettingColor> renderColor = sgGeneral.add(new ColorSetting.Builder()
        .name("Color")
        .defaultValue(new SettingColor(255, 170, 0, 255))
        .build()
    );

    @EventHandler
    public void onRender3dEvent(Render3DEvent event) {
        if (mode.get() == Mode.TNT || mode.get() == Mode.Both) {
            for (Entity tnt : mc.world.getEntities()) {
                if (tnt instanceof TntEntity tntEntity) {
                    BlockPos tntPosition = tntEntity.getBlockPos();
                    event.renderer.box(tntPosition.getX() - 5.2, tntPosition.getY() - 5.2, tntPosition.getZ() - 5.2, tntPosition.getX() + 5.2, tntPosition.getY() + 5.2, tntPosition.getZ() + 5.2, new Color(renderColor.get().r, renderColor.get().g, renderColor.get().b, renderColor.get().a), new Color(renderColor.get().r, renderColor.get().g, renderColor.get().b, renderColor.get().a), renderType.get(), 0);
                }
            }
        }
        if (mode.get() == Mode.Creeper || mode.get() == Mode.Both) {
            for (Entity creeper : mc.world.getEntities()) {
                if (creeper instanceof CreeperEntity creeperEntity) {
                    // check if creeper is ignited
                    if (creeperEntity.getFuseSpeed() < 0) continue;
                    BlockPos creeperPosition = creeperEntity.getBlockPos();
                    event.renderer.box(creeperPosition.getX() - 5.2, creeperPosition.getY() - 5.2, creeperPosition.getZ() - 5.2, creeperPosition.getX() + 5.2, creeperPosition.getY() + 5.2, creeperPosition.getZ() + 5.2, new Color(renderColor.get().r, renderColor.get().g, renderColor.get().b, renderColor.get().a), new Color(renderColor.get().r, renderColor.get().g, renderColor.get().b, renderColor.get().a), renderType.get(), 0);
                }
            }
        }
    }

    public enum Mode {
        TNT,
        Creeper,
        Both
    }
}
