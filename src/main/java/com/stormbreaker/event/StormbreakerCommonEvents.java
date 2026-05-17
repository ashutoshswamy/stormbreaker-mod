package com.stormbreaker.event;

import com.stormbreaker.config.StormbreakerConfig;
import com.stormbreaker.network.StormbreakerNetwork;
import com.stormbreaker.network.packet.S2CSyncStormDataPacket;
import com.stormbreaker.registry.ModItems;
import com.stormbreaker.util.StormbreakerAbilities;
import com.stormbreaker.util.StormbreakerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class StormbreakerCommonEvents {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        StormbreakerAbilities.decrementTimers(player);
        StormbreakerAbilities.tickThunderAura(player);
        if (player.tickCount % 20 == 0) {
            syncStormData(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncStormData(player);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer) || !(event.getOriginal() instanceof ServerPlayer oldPlayer)) {
            return;
        }
        CompoundTag copied = StormbreakerData.copyStormData(oldPlayer);
        StormbreakerData.applyStormData(newPlayer, copied);
        syncStormData(newPlayer);
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isThundering()
                && player.getItemBySlot(EquipmentSlot.MAINHAND).is(ModItems.STORMBREAKER.get())) {
            event.setAmount((float) (event.getAmount() * StormbreakerConfig.WEATHER_ATTACK_BONUS.get()));
        }
    }

    private static void syncStormData(ServerPlayer player) {
        StormbreakerNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new S2CSyncStormDataPacket(
                        StormbreakerData.getThunderRemaining(player),
                        StormbreakerData.getThunderCooldown(player),
                        StormbreakerData.getBifrostCooldown(player)
                )
        );
    }
}
