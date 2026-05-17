package com.stormbreaker.network.packet;

import com.stormbreaker.client.ClientStormData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncStormDataPacket {
    private final int thunderRemaining;
    private final int thunderCooldown;
    private final int bifrostCooldown;

    public S2CSyncStormDataPacket(int thunderRemaining, int thunderCooldown, int bifrostCooldown) {
        this.thunderRemaining = thunderRemaining;
        this.thunderCooldown = thunderCooldown;
        this.bifrostCooldown = bifrostCooldown;
    }

    public static void encode(S2CSyncStormDataPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.thunderRemaining);
        buf.writeVarInt(msg.thunderCooldown);
        buf.writeVarInt(msg.bifrostCooldown);
    }

    public static S2CSyncStormDataPacket decode(FriendlyByteBuf buf) {
        return new S2CSyncStormDataPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(S2CSyncStormDataPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                ClientStormData.update(msg.thunderRemaining, msg.thunderCooldown, msg.bifrostCooldown);
            }
        });
        ctx.setPacketHandled(true);
    }
}
