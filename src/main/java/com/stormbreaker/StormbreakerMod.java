package com.stormbreaker;

import com.stormbreaker.client.StormbreakerClient;
import com.stormbreaker.config.StormbreakerConfig;
import com.stormbreaker.event.StormbreakerCommonEvents;
import com.stormbreaker.network.StormbreakerNetwork;
import com.stormbreaker.registry.ModCreativeTab;
import com.stormbreaker.registry.ModEntities;
import com.stormbreaker.registry.ModItems;
import com.stormbreaker.registry.ModParticles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(StormbreakerMod.MODID)
public class StormbreakerMod {
    public static final String MODID = "stormbreaker";

    public StormbreakerMod() {
        GeckoLib.initialize();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modBus);
        DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> StormbreakerClient.register(modBus));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StormbreakerConfig.SPEC);
        StormbreakerNetwork.register();
        MinecraftForge.EVENT_BUS.register(new StormbreakerCommonEvents());
    }
}
