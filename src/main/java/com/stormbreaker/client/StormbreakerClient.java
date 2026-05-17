package com.stormbreaker.client;

import com.stormbreaker.StormbreakerMod;
import com.stormbreaker.network.StormbreakerNetwork;
import com.stormbreaker.network.packet.C2SCastBifrostPacket;
import com.stormbreaker.network.packet.C2SThunderModePacket;
import com.stormbreaker.registry.ModEntities;
import com.stormbreaker.registry.ModItems;
import com.stormbreaker.registry.ModParticles;
import com.stormbreaker.renderer.StormbreakerProjectileRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StormbreakerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class StormbreakerClient {
    public static void register(IEventBus modBus) {
        modBus.addListener(StormbreakerClient::onRegisterEntityRenderers);
        MinecraftForge.EVENT_BUS.register(ClientForgeEvents.class);
    }

    private static void onRegisterEntityRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STORMBREAKER_PROJECTILE.get(), StormbreakerProjectileRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.THUNDER_MODE);
        event.register(ModKeybinds.BIFROST);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.ELECTRIC_SPARK.get(), com.stormbreaker.particle.ElectricSparkParticle.Provider::new);
        event.registerSpriteSet(ModParticles.BIFROST_GLINT.get(), com.stormbreaker.particle.BifrostGlintParticle.Provider::new);
    }

    private static class ClientForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) {
                return;
            }
            while (ModKeybinds.THUNDER_MODE.consumeClick()) {
                StormbreakerNetwork.CHANNEL.sendToServer(new C2SThunderModePacket());
            }
            while (ModKeybinds.BIFROST.consumeClick()) {
                StormbreakerNetwork.CHANNEL.sendToServer(new C2SCastBifrostPacket());
            }
        }

        @SubscribeEvent
        public static void onGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            ItemStack main = mc.player.getMainHandItem();
            ItemStack off = mc.player.getOffhandItem();
            if (!main.is(ModItems.STORMBREAKER.get()) && !off.is(ModItems.STORMBREAKER.get())) {
                return;
            }

            Font font = mc.font;
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int x = screenWidth / 2 + 95;
            int y = screenHeight - 56;

            event.getGuiGraphics().drawString(font, Component.literal("THR " + ClientStormData.thunderCooldown() / 20 + "s"), x, y, 0x44CCFF);
            event.getGuiGraphics().drawString(font, Component.literal("BFR " + ClientStormData.bifrostCooldown() / 20 + "s"), x, y + 10, 0xFF66FF);
            if (ClientStormData.thunderRemaining() > 0) {
                event.getGuiGraphics().drawString(font, Component.literal("AWAKENED " + ClientStormData.thunderRemaining() / 20 + "s"), x - 40, y - 12, 0x00FFFF);
            }
        }

        @SubscribeEvent
        public static void onRenderSkyHint(RenderLevelStageEvent event) {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
                return;
            }
            if (ClientStormData.thunderRemaining() <= 0 || Minecraft.getInstance().level == null) {
                return;
            }
            Minecraft.getInstance().level.setSkyFlashTime(2);
        }
    }
}
