package de.gurkenwerfer.gurkensgadgetry;

import com.mojang.logging.LogUtils;

import de.gurkenwerfer.gurkensgadgetry.commands.AutoClip;
import de.gurkenwerfer.gurkensgadgetry.commands.CamClip;
import de.gurkenwerfer.gurkensgadgetry.modules.*;
import de.gurkenwerfer.gurkensgadgetry.modules.BedrockESP.BedrockESP;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
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
        Modules.get().add(new BedrockESP());
        Modules.get().add(new Gurkfly());
        Modules.get().add(new PacketLogger());
        Modules.get().add(new WorldGuardBypass());
        Modules.get().add(new BoomESP());
        Modules.get().add(new BlastGuard());
        Modules.get().add(new NoCollision());
        Modules.get().add(new NoWorldBorder());
        // Commands
        Commands.add(new CamClip());
        Commands.add(new AutoClip());

    }
    @Override
    public void onRegisterCategories() {

        Modules.registerCategory(CATEGORY);
        LOG.info("Registered Category");

    }
    @Override
    public String getPackage() {
        return "de.gurkenwerfer.gurkensgadgetry";
    }
}
