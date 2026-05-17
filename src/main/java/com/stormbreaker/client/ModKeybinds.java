package com.stormbreaker.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeybinds {
    public static final String CATEGORY = "key.category.stormbreaker";
    public static final KeyMapping THUNDER_MODE = new KeyMapping(
            "key.stormbreaker.thunder_mode",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );
    public static final KeyMapping BIFROST = new KeyMapping(
            "key.stormbreaker.bifrost",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );

    private ModKeybinds() {
    }
}
