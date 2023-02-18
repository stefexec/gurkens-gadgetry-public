package de.gurkenwerfer.toolkit.modules;

import de.gurkenwerfer.toolkit.GurkensGadgetry;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class FakeCreativeBypass extends Module{
    public FakeCreativeBypass() {
        super(GurkensGadgetry.CATEGORY, "LO-Bypasses", "Bypasses LiveOverflow's Fake Creative Mode/Nag Screens.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    private final Setting<Boolean> credits = sgGeneral.add(new BoolSetting.Builder()
        .name("Cancel Credits")
        .description("Cancel the end screen packet.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> demo = sgGeneral.add(new BoolSetting.Builder()
        .name("Cancel Demo Mode")
        .description("Cancel the demo screen packet.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> creative = sgGeneral.add(new BoolSetting.Builder()
        .name("Cancel Fake Creative Mode")
        .description("Cancel the fake creative mode packet.")
        .defaultValue(true)
        .build()
    );

    public boolean bypassCredits() {
        return this.credits.get();
    }

    public boolean bypassDemo() {
        return this.demo.get();
    }

    public boolean bypassCreative() {
        return this.creative.get();
    }
}
