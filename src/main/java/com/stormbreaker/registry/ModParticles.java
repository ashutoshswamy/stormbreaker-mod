package com.stormbreaker.registry;

import com.stormbreaker.StormbreakerMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, StormbreakerMod.MODID);

    public static final RegistryObject<SimpleParticleType> ELECTRIC_SPARK = PARTICLES.register("electric_spark", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BIFROST_GLINT = PARTICLES.register("bifrost_glint", () -> new SimpleParticleType(true));

    private ModParticles() {
    }
}
