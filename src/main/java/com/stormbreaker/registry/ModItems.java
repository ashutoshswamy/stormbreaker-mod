package com.stormbreaker.registry;

import com.stormbreaker.StormbreakerMod;
import com.stormbreaker.item.StormbreakerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StormbreakerMod.MODID);

    public static final RegistryObject<Item> STORMBREAKER = ITEMS.register("stormbreaker", StormbreakerItem::new);

    private ModItems() {
    }
}
