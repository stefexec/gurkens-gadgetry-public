package de.gurkenwerfer.toolkit;

import com.mojang.logging.LogUtils;
import de.gurkenwerfer.toolkit.Commands.VClip;
import de.gurkenwerfer.toolkit.modules.*;
import de.gurkenwerfer.toolkit.modules.BedrockESP.BedrockESP;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class GurkensGadgetry extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Gadgetry");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Gurken's Gadgetry");

        // Modules
        Modules.get().add(new NoChestRender());
        Modules.get().add(new DubCounter());
        Modules.get().add(new AutoItemMove());
        Modules.get().add(new BedrockESP());
        Modules.get().add(new Robowalk());
        Modules.get().add(new Gurkfly());

        // Commands
        Commands.get().add(new VClip());

    }
    @Override
    public void onRegisterCategories() {

        Modules.registerCategory(CATEGORY);
        LOG.info("Registered Category");

    }
    @Override
    public String getPackage() {
        return "de.gurkenwerfer.toolkit";
    }
}
