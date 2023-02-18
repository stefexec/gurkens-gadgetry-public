package de.gurkenwerfer.toolkit.modules;

import de.gurkenwerfer.toolkit.GurkensGadgetry;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Gurkwalk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> digits = sgGeneral.add(new IntSetting.Builder()
        .name("digits")
        .description("How many digits to remove.")
        .defaultValue(2)
        .sliderMin(0)
        .sliderMax(5)
        .noSlider()
        .build()
    );

    private final Setting<Boolean> modifyY = sgGeneral.add(new BoolSetting.Builder()
        .name("round-y")
        .description("Rounds your y coordinate.")
        .defaultValue(false)
        .build()
    );

    public Gurkwalk() {
        super(GurkensGadgetry.CATEGORY, "Gurkwalk", "Rounds your coords to bypass LiveOverflow's anti-human movement check.");
    }


    public boolean roundY() {
        return modifyY.get();
    }

    // Utils

    public double round(double value) {
        int digit = (int) Math.pow(10, digits.get());
        double round = ((double) (Math.round(value * digit)) / digit);
        return Math.nextAfter(round, round + Math.signum(round));
    }
}
