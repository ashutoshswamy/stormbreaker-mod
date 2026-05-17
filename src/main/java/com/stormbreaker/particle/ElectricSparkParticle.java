package com.stormbreaker.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class ElectricSparkParticle extends TextureSheetParticle {
    protected ElectricSparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.friction = 0.92F;
        this.gravity = 0.0F;
        this.quadSize = 0.17F;
        this.lifetime = 12 + this.random.nextInt(8);
        this.setSpriteFromAge(sprites);
        this.rCol = 0.4F;
        this.gCol = 0.8F;
        this.bCol = 1.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ElectricSparkParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
