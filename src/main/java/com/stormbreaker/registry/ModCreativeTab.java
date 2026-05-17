package com.stormbreaker.registry;

import com.stormbreaker.StormbreakerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StormbreakerMod.MODID);

    public static final RegistryObject<CreativeModeTab> STORMBREAKER_TAB =
            CREATIVE_MODE_TABS.register("stormbreaker_tab",
                    () -> CreativeModeTab.builder()
                            .icon(() -> ModItems.STORMBREAKER.get().getDefaultInstance())
                            .title(Component.translatable("itemGroup.stormbreaker"))
                            .displayItems((params, output) -> output.accept(ModItems.STORMBREAKER.get()))
                            .build());

    private ModCreativeTab() {
    }
}
