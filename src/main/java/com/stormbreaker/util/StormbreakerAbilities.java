package com.stormbreaker.util;

import com.stormbreaker.config.StormbreakerConfig;
import com.stormbreaker.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class StormbreakerAbilities {
    private StormbreakerAbilities() {
    }

    public static HitResult raycast(Player player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(range));

        EntityHitResult entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                eye,
                end,
                player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D),
                entity -> entity instanceof LivingEntity && entity != player
        );
        if (entityHit != null) {
            return entityHit;
        }

        return player.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    public static void castLightning(ServerPlayer player, InteractionHand hand) {
        Level level = player.level();
        HitResult hitResult = raycast(player, 64.0D);
        Vec3 target = hitResult.getType() == HitResult.Type.BLOCK
                ? Vec3.atBottomCenterOf(((BlockHitResult) hitResult).getBlockPos())
                : hitResult.getType() == HitResult.Type.ENTITY
                ? ((EntityHitResult) hitResult).getEntity().position()
                : player.position().add(player.getLookAngle().scale(12.0D));

        summonLightningImpact(player, target, true);
        player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), StormbreakerConfig.LIGHTNING_COOLDOWN_TICKS.get());
    }

    public static void summonLightningImpact(ServerPlayer caster, Vec3 target, boolean includeBolt) {
        ServerLevel level = caster.serverLevel();
        if (includeBolt) {
            LightningBolt bolt = new LightningBolt(net.minecraft.world.entity.EntityType.LIGHTNING_BOLT, level);
            bolt.moveTo(target);
            bolt.setCause(caster);
            level.addFreshEntity(bolt);
        }

        float baseDamage = (float) (StormbreakerConfig.BASE_DAMAGE.get() * 0.75F);
        if (level.isThundering()) {
            baseDamage *= StormbreakerConfig.WEATHER_LIGHTNING_BONUS.get().floatValue();
        }

        List<LivingEntity> impacted = level.getEntitiesOfClass(
                LivingEntity.class,
                new net.minecraft.world.phys.AABB(target, target).inflate(4.0D),
                e -> e != caster && e.isAlive()
        );

        DamageSource source = caster.damageSources().indirectMagic(caster, caster);
        for (LivingEntity living : impacted) {
            living.hurt(source, baseDamage);
            living.setSecondsOnFire(4);
        }

        float radius = StormbreakerConfig.EXPLOSION_RADIUS.get().floatValue();
        level.explode(caster, target.x, target.y, target.z, radius, Level.ExplosionInteraction.NONE);
        spawnBurstParticles(level, ModParticles.ELECTRIC_SPARK.get(), target, 50, 1.8D);
        level.playSound(null, BlockPos.containing(target), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.5F, 0.95F);
    }

    public static boolean activateThunderMode(ServerPlayer player) {
        if (StormbreakerData.getThunderCooldown(player) > 0) {
            return false;
        }

        StormbreakerData.setThunderRemaining(player, StormbreakerConfig.THUNDER_MODE_DURATION_TICKS.get());
        StormbreakerData.setThunderCooldown(player, StormbreakerConfig.THUNDER_MODE_COOLDOWN_TICKS.get());

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 20, 4, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 20, 2, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 1, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20 * 20, 1, false, true, true));
        player.level().playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.6F, 1.0F);
        return true;
    }

    public static boolean castBifrost(ServerPlayer player) {
        if (StormbreakerData.getBifrostCooldown(player) > 0) {
            return false;
        }
        int xpCost = StormbreakerConfig.BIFROST_XP_COST.get();
        if (xpCost > 0 && player.experienceLevel < xpCost) {
            return false;
        }

        HitResult hit = raycast(player, StormbreakerConfig.BIFROST_RANGE.get());
        if (!(hit instanceof BlockHitResult blockHit)) {
            return false;
        }
        BlockPos pos = blockHit.getBlockPos();
        if (player.level().getBlockState(pos).is(net.minecraft.world.level.block.Blocks.BEDROCK)) {
            return false;
        }

        Vec3 destination = Vec3.atBottomCenterOf(pos).add(0, 1.0D, 0);
        if (!player.level().noCollision(player, player.getBoundingBox().move(destination.subtract(player.position())))) {
            return false;
        }

        if (xpCost > 0) {
            player.giveExperienceLevels(-xpCost);
        }

        ServerLevel level = player.serverLevel();
        spawnRainbowBeam(level, player.position());
        player.teleportTo(level, destination.x, destination.y, destination.z, player.getYRot(), player.getXRot());
        spawnRainbowBeam(level, destination);

        float radius = StormbreakerConfig.EXPLOSION_RADIUS.get().floatValue() + 1.5F;
        level.explode(player, destination.x, destination.y, destination.z, radius, Level.ExplosionInteraction.NONE);

        List<LivingEntity> impacted = level.getEntitiesOfClass(LivingEntity.class,
                new net.minecraft.world.phys.AABB(destination, destination).inflate(5.0D),
                e -> e != player && e.isAlive());
        for (LivingEntity living : impacted) {
            living.hurt(player.damageSources().playerAttack(player), 16.0F);
        }
        StormbreakerData.setBifrostCooldown(player, StormbreakerConfig.BIFROST_COOLDOWN_TICKS.get());
        return true;
    }

    public static void tickThunderAura(ServerPlayer player) {
        if (StormbreakerData.getThunderRemaining(player) <= 0) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (player.tickCount % 20 == 0) {
            summonLightningImpact(player, player.position(), false);
        }
        spawnBurstParticles(level, ModParticles.ELECTRIC_SPARK.get(), player.position().add(0, 1.0D, 0), 12, 0.8D);
    }

    public static void decrementTimers(ServerPlayer player) {
        int thunder = StormbreakerData.getThunderRemaining(player);
        int thunderCooldown = StormbreakerData.getThunderCooldown(player);
        int bifrostCooldown = StormbreakerData.getBifrostCooldown(player);

        if (thunder > 0) {
            StormbreakerData.setThunderRemaining(player, thunder - 1);
        }

        // Thunderstorms accelerate recovery by +1 tick per second tick.
        boolean stormBonus = player.level().isThundering();
        if (thunderCooldown > 0) {
            StormbreakerData.setThunderCooldown(player, Math.max(0, thunderCooldown - (stormBonus ? 2 : 1)));
        }
        if (bifrostCooldown > 0) {
            StormbreakerData.setBifrostCooldown(player, Math.max(0, bifrostCooldown - (stormBonus ? 2 : 1)));
        }
    }

    public static void spawnRainbowBeam(ServerLevel level, Vec3 center) {
        spawnBurstParticles(level, ModParticles.BIFROST_GLINT.get(), center.add(0, 6.0D, 0), 90, 1.5D);
        level.playSound(null, BlockPos.containing(center), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.2F, 0.8F);
    }

    public static void spawnBurstParticles(ServerLevel level, ParticleOptions particle, Vec3 center, int count, double spread) {
        level.sendParticles(
                particle,
                center.x, center.y, center.z,
                count,
                spread, spread, spread,
                0.01D
        );
    }
}
