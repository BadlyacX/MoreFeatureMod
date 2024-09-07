package com.badlyac.morefeaturemod.handler.Effect.NightVision;

import com.badlyac.morefeaturemod.KeyBindings.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NightVision {
    private static boolean nightVisionActive = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (KeyBindings.INSTANCE.night_vision_key.consumeClick()) {
                if (nightVisionActive) {
                    mc.player.removeEffect(MobEffects.NIGHT_VISION);
                    sendChatMessage("Night Vision Mode has been disabled", ChatFormatting.RED);
                    nightVisionActive = false;
                } else {
                    mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                    sendChatMessage("Night Vision Mode has been enabled", ChatFormatting.GREEN);
                    nightVisionActive = true;
                }
            }

            if (nightVisionActive && !mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
                mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            }
        }
    }

    private static void sendChatMessage(String message, ChatFormatting color) {
        MutableComponent text = Component.literal(message).withStyle(color);
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.displayClientMessage(text, false);
    }
}
