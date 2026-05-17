package com.stormbreaker.network;

import com.stormbreaker.StormbreakerMod;
import com.stormbreaker.network.packet.C2SCastBifrostPacket;
import com.stormbreaker.network.packet.C2SThunderModePacket;
import com.stormbreaker.network.packet.S2CSyncStormDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class StormbreakerNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(StormbreakerMod.MODID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private StormbreakerNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(packetId++, C2SThunderModePacket.class, C2SThunderModePacket::encode, C2SThunderModePacket::decode, C2SThunderModePacket::handle);
        CHANNEL.registerMessage(packetId++, C2SCastBifrostPacket.class, C2SCastBifrostPacket::encode, C2SCastBifrostPacket::decode, C2SCastBifrostPacket::handle);
        CHANNEL.registerMessage(packetId++, S2CSyncStormDataPacket.class, S2CSyncStormDataPacket::encode, S2CSyncStormDataPacket::decode, S2CSyncStormDataPacket::handle);
    }
}
