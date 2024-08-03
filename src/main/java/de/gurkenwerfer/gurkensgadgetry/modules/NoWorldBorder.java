package de.gurkenwerfer.gurkensgadgetry.modules;

import de.gurkenwerfer.gurkensgadgetry.GurkensGadgetry;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class NoWorldBorder extends Module {
    public NoWorldBorder() {
        super(GurkensGadgetry.CATEGORY, "no-world-border", "Removes the world border completely.");
    }
}

