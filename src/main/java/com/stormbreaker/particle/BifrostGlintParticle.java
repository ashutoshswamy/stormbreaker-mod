package com.stormbreaker.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BifrostGlintParticle extends TextureSheetParticle {
    protected BifrostGlintParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.friction = 0.88F;
        this.gravity = -0.01F;
        this.quadSize = 0.22F;
        this.lifetime = 18 + this.random.nextInt(10);
        this.setSpriteFromAge(sprites);
        this.rCol = 1.0F;
        this.gCol = 0.8F;
        this.bCol = 0.95F;
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
            return new BifrostGlintParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
