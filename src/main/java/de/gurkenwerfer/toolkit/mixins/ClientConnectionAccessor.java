package de.gurkenwerfer.toolkit.mixins;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;

import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientConnection.class)
public interface ClientConnectionAccessor {
    @Accessor("channel")
    Channel getChannel();

    @Invoker("sendImmediately")
    void _sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks);
}

