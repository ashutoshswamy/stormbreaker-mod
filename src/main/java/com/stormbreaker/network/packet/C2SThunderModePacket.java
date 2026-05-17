package com.stormbreaker.network.packet;

import com.stormbreaker.registry.ModItems;
import com.stormbreaker.util.StormbreakerAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SThunderModePacket {
    public static void encode(C2SThunderModePacket msg, FriendlyByteBuf buf) {
    }

    public static C2SThunderModePacket decode(FriendlyByteBuf buf) {
        return new C2SThunderModePacket();
    }

    public static void handle(C2SThunderModePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (main.is(ModItems.STORMBREAKER.get()) || off.is(ModItems.STORMBREAKER.get())) {
                StormbreakerAbilities.activateThunderMode(player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
