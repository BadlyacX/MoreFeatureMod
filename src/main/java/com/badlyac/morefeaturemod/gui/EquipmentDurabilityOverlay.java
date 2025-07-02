package com.badlyac.morefeaturemod.gui;

import com.badlyac.morefeaturemod.keybinding.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EquipmentDurabilityOverlay {

    private static boolean isEnabled = true;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (KeyBindings.INSTANCE.toggle_equip_durability.consumeClick()) {
            isEnabled = !isEnabled;
            Objects.requireNonNull(Minecraft.getInstance().player).sendSystemMessage(
                    Component.literal("Equipment Durability: " + (isEnabled ? "has Enabled" : "has Disabled"))
                            .withStyle(isEnabled ? ChatFormatting.GREEN : ChatFormatting.RED)
            );
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!isEnabled) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        GuiGraphics graphics = event.getGuiGraphics();

        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int spacing = 24;
        int startY = screenHeight - 10;
        int iconSize = 16;

        int x = screenWidth - iconSize - 10;

        ItemStack[] all = new ItemStack[]{
                player.getInventory().armor.get(0),  // 鞋
                player.getInventory().armor.get(1),  // 腿
                player.getInventory().armor.get(2),  // 胸
                player.getInventory().armor.get(3),  // 頭
                player.getOffhandItem(),
                player.getMainHandItem()
        };

        for (ItemStack stack : all) {
            if (!stack.isEmpty()) {
                int y = startY - spacing;

                graphics.renderItemDecorations(mc.font, stack, x, y);
                graphics.renderItem(stack, x, y);
                if (stack.isDamageableItem()) {
                    int max = stack.getMaxDamage();
                    int current = max - stack.getDamageValue();
                    int color = getDurabilityColor(current, max);
                    int textWidth = mc.font.width(String.valueOf(current));
                    graphics.drawString(mc.font, String.valueOf(current), x + iconSize / 2 - textWidth / 2, y + 18, color, true);
                }

                startY -= spacing;
            }
        }
    }

    private static int getDurabilityColor(int current, int max) {
        float ratio = (float) current / max;
        int red = (int) ((1.0f - ratio) * 255);
        int green = (int) (ratio * 255);
        return (red << 16) | (green << 8);
    }
}