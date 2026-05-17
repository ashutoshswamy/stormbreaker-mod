package com.stormbreaker.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class StormbreakerData {
    public static final String KEY_THUNDER_REMAINING = "stormbreaker_thunder_remaining";
    public static final String KEY_THUNDER_COOLDOWN = "stormbreaker_thunder_cooldown";
    public static final String KEY_BIFROST_COOLDOWN = "stormbreaker_bifrost_cooldown";

    private StormbreakerData() {
    }

    public static int getThunderRemaining(Player player) {
        return player.getPersistentData().getInt(KEY_THUNDER_REMAINING);
    }

    public static int getThunderCooldown(Player player) {
        return player.getPersistentData().getInt(KEY_THUNDER_COOLDOWN);
    }

    public static int getBifrostCooldown(Player player) {
        return player.getPersistentData().getInt(KEY_BIFROST_COOLDOWN);
    }

    public static void setThunderRemaining(Player player, int ticks) {
        player.getPersistentData().putInt(KEY_THUNDER_REMAINING, Math.max(0, ticks));
    }

    public static void setThunderCooldown(Player player, int ticks) {
        player.getPersistentData().putInt(KEY_THUNDER_COOLDOWN, Math.max(0, ticks));
    }

    public static void setBifrostCooldown(Player player, int ticks) {
        player.getPersistentData().putInt(KEY_BIFROST_COOLDOWN, Math.max(0, ticks));
    }

    public static CompoundTag copyStormData(ServerPlayer source) {
        CompoundTag sourceData = source.getPersistentData();
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_THUNDER_REMAINING, sourceData.getInt(KEY_THUNDER_REMAINING));
        tag.putInt(KEY_THUNDER_COOLDOWN, sourceData.getInt(KEY_THUNDER_COOLDOWN));
        tag.putInt(KEY_BIFROST_COOLDOWN, sourceData.getInt(KEY_BIFROST_COOLDOWN));
        return tag;
    }

    public static void applyStormData(Player target, CompoundTag tag) {
        setThunderRemaining(target, tag.getInt(KEY_THUNDER_REMAINING));
        setThunderCooldown(target, tag.getInt(KEY_THUNDER_COOLDOWN));
        setBifrostCooldown(target, tag.getInt(KEY_BIFROST_COOLDOWN));
    }
}
