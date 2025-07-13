package com.badlyac.morefeaturemod.handler;

import com.badlyac.morefeaturemod.keybinding.KeyBindings;
import com.badlyac.morefeaturemod.Main;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandlers {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.INSTANCE.night_vision_key);
        event.register(KeyBindings.INSTANCE.auto_Detect_Nearby_Ore_key);
        event.register(KeyBindings.INSTANCE.auto_place_key);
        event.register(KeyBindings.INSTANCE.toggle_equip_durability);
        event.register(KeyBindings.INSTANCE.toggle_light_key);
    }
}
