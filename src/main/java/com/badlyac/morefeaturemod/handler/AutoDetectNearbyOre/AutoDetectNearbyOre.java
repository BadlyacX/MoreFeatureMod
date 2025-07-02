package com.badlyac.morefeaturemod.handler.AutoDetectNearbyOre;

import com.badlyac.morefeaturemod.keybinding.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class AutoDetectNearbyOre {

    private static final TagKey<Block> ORES_TAG = BlockTags.create(new ResourceLocation("forge", "ores"));
    private static boolean isDetecting = false;
    private static String lastDetectedOre = "";

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent event) {
        if (!isDetecting || lastDetectedOre.isEmpty()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, lastDetectedOre, 5, 5, 0x00FF00, true);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.INSTANCE.auto_Detect_Nearby_Ore_key.consumeClick()) {
            isDetecting = !isDetecting;
            if (Minecraft.getInstance().player == null) return;
            Component message = Component.literal("Ore Detection: " + (isDetecting ? "Enabled" : "Disabled"))
                    .withStyle(isDetecting ? ChatFormatting.GREEN : ChatFormatting.RED);
            Minecraft.getInstance().player.displayClientMessage(message, false);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (isDetecting && event.phase == TickEvent.Phase.END) {
            detectOres(event.player);
        }
    }

    private static void detectOres(Player player) {
        BlockPos playerPos = player.blockPosition();
        Direction direction = player.getDirection();

        for (int i = 1; i <= 30; i++) {
            BlockPos pos = playerPos.relative(direction, i);
            BlockState state = player.level().getBlockState(pos);

            if (isOreBlock(state)) {
                String oreType = getOreType(state);
                lastDetectedOre = "Detected " + oreType + " at " + pos.toShortString();
                return;
            }
        }

        lastDetectedOre = "";
    }

    private static boolean isOreBlock(BlockState state) {
        return state.is(ORES_TAG);
    }

    private static String getOreType(BlockState state) {
        return state.getBlock().getName().getString();
    }
}
