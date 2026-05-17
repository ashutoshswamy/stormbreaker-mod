package com.stormbreaker.animation;

import com.stormbreaker.StormbreakerMod;
import com.stormbreaker.item.StormbreakerItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StormbreakerItemModel extends GeoModel<StormbreakerItem> {
    @Override
    public ResourceLocation getModelResource(StormbreakerItem animatable) {
        return new ResourceLocation(StormbreakerMod.MODID, "geo/stormbreaker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StormbreakerItem animatable) {
        return new ResourceLocation(StormbreakerMod.MODID, "textures/item/stormbreaker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StormbreakerItem animatable) {
        return new ResourceLocation(StormbreakerMod.MODID, "animations/stormbreaker.animation.json");
    }
}
