package com.stormbreaker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class StormbreakerConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue BASE_DAMAGE;
    public static final ForgeConfigSpec.IntValue LIGHTNING_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue THROW_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue THUNDER_MODE_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue THUNDER_MODE_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BIFROST_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue BIFROST_RANGE;
    public static final ForgeConfigSpec.DoubleValue EXPLOSION_RADIUS;
    public static final ForgeConfigSpec.DoubleValue WEATHER_LIGHTNING_BONUS;
    public static final ForgeConfigSpec.DoubleValue WEATHER_ATTACK_BONUS;
    public static final ForgeConfigSpec.IntValue BIFROST_XP_COST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("stormbreaker");
        BASE_DAMAGE = builder.comment("Base melee attack damage for Stormbreaker.")
                .defineInRange("baseDamage", 22.0D, 1.0D, 100.0D);
        LIGHTNING_COOLDOWN_TICKS = builder.comment("Lightning ability cooldown in ticks.")
                .defineInRange("lightningCooldownTicks", 80, 0, 20 * 60);
        THROW_COOLDOWN_TICKS = builder.comment("Throw ability cooldown in ticks.")
                .defineInRange("throwCooldownTicks", 30, 0, 20 * 60);
        THUNDER_MODE_DURATION_TICKS = builder.comment("Thunder mode active duration in ticks.")
                .defineInRange("thunderModeDurationTicks", 20 * 20, 20, 20 * 120);
        THUNDER_MODE_COOLDOWN_TICKS = builder.comment("Thunder mode cooldown in ticks.")
                .defineInRange("thunderModeCooldownTicks", 20 * 120, 20, 20 * 600);
        BIFROST_COOLDOWN_TICKS = builder.comment("Bifrost cooldown in ticks.")
                .defineInRange("bifrostCooldownTicks", 20 * 90, 20, 20 * 600);
        BIFROST_RANGE = builder.comment("Max Bifrost teleport range.")
                .defineInRange("bifrostRange", 96, 8, 512);
        EXPLOSION_RADIUS = builder.comment("Ability explosion radius.")
                .defineInRange("explosionRadius", 2.5D, 0.0D, 12.0D);
        WEATHER_LIGHTNING_BONUS = builder.comment("Extra lightning AoE damage multiplier in thunderstorms.")
                .defineInRange("weatherLightningBonus", 1.35D, 1.0D, 3.0D);
        WEATHER_ATTACK_BONUS = builder.comment("Extra melee damage in thunderstorms.")
                .defineInRange("weatherAttackBonus", 1.20D, 1.0D, 3.0D);
        BIFROST_XP_COST = builder.comment("XP levels spent when casting Bifrost.")
                .defineInRange("bifrostXpCost", 3, 0, 30);
        builder.pop();
        SPEC = builder.build();
    }

    private StormbreakerConfig() {
    }
}
