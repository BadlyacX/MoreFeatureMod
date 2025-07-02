package com.badlyac.morefeaturemod.keybinding;


import com.badlyac.morefeaturemod.Main;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class KeyBindings {
    public static final KeyBindings INSTANCE = new KeyBindings();

    public static final String CATEGORY = "key.category." + Main.MODID;

    public final KeyMapping night_vision_key = new KeyMapping(
            "key." + Main.MODID + ".Night_vision",
            KeyConflictContext.IN_GAME,
            InputConstants.UNKNOWN,
            CATEGORY
    );

    public final KeyMapping auto_Detect_Nearby_Ore_key = new KeyMapping(
            "key." + Main.MODID + ".auto_detect_nearby_ore",
            KeyConflictContext.IN_GAME,
            InputConstants.UNKNOWN,
            CATEGORY
    );

    public final KeyMapping auto_place_key = new KeyMapping(
            "key." + Main.MODID + ".auto_place",
            KeyConflictContext.IN_GAME,
            InputConstants.UNKNOWN,
            CATEGORY
    );

    public final KeyMapping toggle_equip_durability = new KeyMapping(
            "key." + Main.MODID + ".equip_durability",
            KeyConflictContext.IN_GAME,
            InputConstants.UNKNOWN,
            CATEGORY
    );
}
