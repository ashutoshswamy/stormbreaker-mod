package com.stormbreaker.entity;

import com.stormbreaker.config.StormbreakerConfig;
import com.stormbreaker.registry.ModEntities;
import com.stormbreaker.registry.ModItems;
import com.stormbreaker.util.StormbreakerAbilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StormbreakerProjectileEntity extends Entity {
    private static final String NBT_ITEM = "StormbreakerItem";
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_RETURNING = "Returning";

    private ItemStack carriedStack = ModItems.STORMBREAKER.get().getDefaultInstance();
    private UUID ownerUuid;
    private boolean returning;
    private int lifeTicks;
    private final Set<Integer> hitTargets = new HashSet<>();

    public StormbreakerProjectileEntity(EntityType<? extends StormbreakerProjectileEntity> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public StormbreakerProjectileEntity(Level level, Player owner, ItemStack stack) {
        this(ModEntities.STORMBREAKER_PROJECTILE.get(), level);
        this.ownerUuid = owner.getUUID();
        this.carriedStack = stack.copy();
        this.setPos(owner.getX(), owner.getEyeY() - 0.15D, owner.getZ());
        this.setDeltaMovement(owner.getLookAngle().scale(2.25D));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;

        if (!level().isClientSide) {
            Player owner = getOwnerPlayer();
            if (owner == null || !owner.isAlive()) {
                dropAsItemAndDiscard();
                return;
            }

            if (!returning && lifeTicks > 25) {
                returning = true;
            }

            if (returning) {
                Vec3 toOwner = owner.getEyePosition().subtract(position());
                Vec3 velocity = toOwner.normalize().scale(1.65D);
                setDeltaMovement(velocity);
                this.noPhysics = true;

                if (toOwner.lengthSqr() < 2.5D) {
                    if (!owner.getInventory().add(carriedStack.copy())) {
                        owner.drop(carriedStack.copy(), false);
                    }
                    discard();
                    return;
                }
            }

            Vec3 nextPos = position().add(getDeltaMovement());
            setPos(nextPos.x, nextPos.y, nextPos.z);
            setYRot(getYRot() + 45.0F);
            setXRot(getXRot() + 45.0F);

            AABB hitbox = getBoundingBox().inflate(0.8D);
            for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, hitbox, this::canHit)) {
                hitTargets.add(target.getId());
                float damage = (float) (StormbreakerConfig.BASE_DAMAGE.get() + 6.0D);
                if (owner.level().isThundering()) {
                    damage *= StormbreakerConfig.WEATHER_ATTACK_BONUS.get().floatValue();
                }
                target.hurt(damageSources().mobProjectile(this, owner), damage);
                target.knockback(1.15F, owner.getX() - target.getX(), owner.getZ() - target.getZ());
                StormbreakerAbilities.spawnBurstParticles((net.minecraft.server.level.ServerLevel) level(), com.stormbreaker.registry.ModParticles.ELECTRIC_SPARK.get(), target.position().add(0, 1.0D, 0), 8, 0.3D);
            }

            if (!level().isEmptyBlock(blockPosition())) {
                returning = true;
            }
        }
    }

    private boolean canHit(LivingEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        Player owner = getOwnerPlayer();
        if (owner != null && entity.getUUID().equals(owner.getUUID())) {
            return false;
        }
        return !hitTargets.contains(entity.getId());
    }

    public ItemStack getCarriedStack() {
        return carriedStack;
    }

    private Player getOwnerPlayer() {
        if (ownerUuid == null || !(level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getServer().getPlayerList().getPlayer(ownerUuid);
    }

    private void dropAsItemAndDiscard() {
        if (!level().isClientSide) {
            ItemEntity drop = new ItemEntity(level(), getX(), getY(), getZ(), carriedStack.copy());
            drop.setUnlimitedLifetime();
            level().addFreshEntity(drop);
        }
        discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(NBT_ITEM)) {
            carriedStack = ItemStack.of(tag.getCompound(NBT_ITEM));
        }
        if (tag.hasUUID(NBT_OWNER)) {
            ownerUuid = tag.getUUID(NBT_OWNER);
        }
        returning = tag.getBoolean(NBT_RETURNING);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put(NBT_ITEM, carriedStack.save(new CompoundTag()));
        if (ownerUuid != null) {
            tag.putUUID(NBT_OWNER, ownerUuid);
        }
        tag.putBoolean(NBT_RETURNING, returning);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
