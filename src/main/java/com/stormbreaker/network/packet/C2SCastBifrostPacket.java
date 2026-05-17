package com.stormbreaker.network.packet;

import com.stormbreaker.registry.ModItems;
import com.stormbreaker.util.StormbreakerAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SCastBifrostPacket {
    public static void encode(C2SCastBifrostPacket msg, FriendlyByteBuf buf) {
    }

    public static C2SCastBifrostPacket decode(FriendlyByteBuf buf) {
        return new C2SCastBifrostPacket();
    }

    public static void handle(C2SCastBifrostPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (main.is(ModItems.STORMBREAKER.get()) || off.is(ModItems.STORMBREAKER.get())) {
                StormbreakerAbilities.castBifrost(player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
