package com.stormbreaker.renderer;

import com.stormbreaker.animation.StormbreakerItemModel;
import com.stormbreaker.item.StormbreakerItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class StormbreakerItemRenderer extends GeoItemRenderer<StormbreakerItem> {
    public StormbreakerItemRenderer() {
        super(new StormbreakerItemModel());
    }
}
