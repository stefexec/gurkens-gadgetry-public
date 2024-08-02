package de.gurkenwerfer.gurkensgadgetry.modules;

import com.mojang.logging.LogUtils;
import de.gurkenwerfer.gurkensgadgetry.GurkensGadgetry;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PacketLogger extends Module{
    public PacketLogger() {
        super(GurkensGadgetry.CATEGORY, "Packet-Logger", "A very basic packet logger.");
    }
    List<String> ignoredPackets = new ArrayList<String>();
    public SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> antispam = sgGeneral.add(new BoolSetting.Builder()
        .name("anti-spam")
        .description("Filters out unimportant packets.")
        .defaultValue(true)
        .build()
    );

    @Override
    public void onActivate() {

        assert mc.player != null;
        mc.player.sendMessage(Text.of("Enabling Packet Logger"), false);

        if (antispam.get()) {
            mc.player.sendMessage(Text.of("Filtering Spam!"), false);
            ignoredPackets.add("net.minecraft.class_2726");
            ignoredPackets.add("net.minecraft.class_2743");
            ignoredPackets.add("net.minecraft.class_2684");
            ignoredPackets.add("net.minecraft.class_2672");
            ignoredPackets.add("net.minecraft.class_2676");
            ignoredPackets.add("net.minecraft.class_2761");
            ignoredPackets.add("net.minecraft.class_2684");
            ignoredPackets.add("net.minecraft.class_2684.class_2685");
            ignoredPackets.add("net.minecraft.class_2767");
            ignoredPackets.add("net.minecraft.class_2744");
            ignoredPackets.add("net.minecraft.class_2781");
            ignoredPackets.add("net.minecraft.class_2604");
            ignoredPackets.add("net.minecraft.class_2739");
        }

    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {

        // log all packets to console and chat
        if (!ignoredPackets.contains(event.packet.getClass().getCanonicalName())) {
            LogUtils.getLogger().info("Sent Packet: " + event.packet.toString());
            assert mc.player != null;
            mc.player.sendMessage(Text.of("Sent Packet: " + event.packet.toString()), false);
        }

    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {

        // log all packets to console and chat
        if (!ignoredPackets.contains(event.packet.getClass().getCanonicalName())) {
            LogUtils.getLogger().info("Received Packet: " + event.packet.toString());
            assert mc.player != null;
            mc.player.sendMessage(Text.of("Received Packet: " + event.packet.toString()), false);
        }

    }
}
