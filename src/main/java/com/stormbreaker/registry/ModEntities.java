package com.stormbreaker.registry;

import com.stormbreaker.StormbreakerMod;
import com.stormbreaker.entity.StormbreakerProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StormbreakerMod.MODID);

    public static final RegistryObject<EntityType<StormbreakerProjectileEntity>> STORMBREAKER_PROJECTILE =
            ENTITIES.register("stormbreaker_projectile",
                    () -> EntityType.Builder.<StormbreakerProjectileEntity>of(StormbreakerProjectileEntity::new, MobCategory.MISC)
                            .sized(0.8F, 0.8F)
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .build("stormbreaker_projectile"));

    private ModEntities() {
    }
}
