package com.badlyac.morefeaturemod.handler.ShowItemDurability;

import com.badlyac.morefeaturemod.KeyBindings.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderOverlayHandler {

    private static boolean showDurability = true;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.INSTANCE.tool_durability_key.consumeClick()) {
            showDurability = !showDurability;
        }
    }

    public static boolean shouldShowDurability() {
        return showDurability;
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (!shouldShowDurability()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null) {
            ItemStack itemStack = player.getMainHandItem();

            if (!itemStack.isEmpty() && itemStack.isDamageableItem()) {
                int currentDurability = itemStack.getMaxDamage() - itemStack.getDamageValue();
                String durabilityText = String.valueOf(currentDurability);

                int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                int x = screenWidth - 22;
                int y = screenHeight - 22;

                event.getGuiGraphics().renderItem(itemStack, x, y);
                event.getGuiGraphics().drawString(minecraft.font, durabilityText, x + 8 - minecraft.font.width(durabilityText) / 2, y + 13, 0xFFFFFF);
            }

            int armorX = minecraft.getWindow().getGuiScaledWidth() - 22;
            int armorY = minecraft.getWindow().getGuiScaledHeight() - 44;

            for (ItemStack armorItem : player.getArmorSlots()) {
                if (!armorItem.isEmpty() && armorItem.isDamageableItem()) {
                    int currentDurability = armorItem.getMaxDamage() - armorItem.getDamageValue();
                    String durabilityText = String.valueOf(currentDurability);

                    event.getGuiGraphics().renderItem(armorItem, armorX, armorY);
                    event.getGuiGraphics().drawString(minecraft.font, durabilityText, armorX + 8 - minecraft.font.width(durabilityText) / 2, armorY + 14, 0xFFFFFF);

                    armorY -= 20;
                }
            }
        }
    }
}
