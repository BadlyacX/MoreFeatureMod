package com.badlyac.morefeaturemod.handler.AutoPlace;

import com.badlyac.morefeaturemod.KeyBindings.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class AutoPlace {

    static boolean active = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.INSTANCE.auto_place_key.consumeClick()) {
            active = !active;
            if (active) sendChatMessage("Auto Place Mode has been enabled", ChatFormatting.GREEN);
            if (!active) sendChatMessage("Auto Place Mode has been disabled", ChatFormatting.RED);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (active) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null && mc.options.keyUse.isDown()) {
                if (player.getMainHandItem().getItem() instanceof BlockItem) {
                    BlockPos blockPos = player.blockPosition().below();

                    if (mc.level == null) return;
                    if (mc.level.isEmptyBlock(blockPos)) {
                        rightClickBlock(mc, blockPos);
                    }
                }
            }
        }
    }

    private static void rightClickBlock(Minecraft mc, BlockPos pos) {
        LocalPlayer player = mc.player;
        MultiPlayerGameMode gameMode = mc.gameMode;
        if (player != null && gameMode != null) {
            BlockHitResult blockHitResult = new BlockHitResult(
                    new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5),
                    player.getDirection(),
                    pos,
                    false
            );
            gameMode.useItemOn(player, InteractionHand.MAIN_HAND, blockHitResult);
        }
    }

    private static void sendChatMessage(String message, ChatFormatting color) {
        MutableComponent text = Component.literal(message).withStyle(color);
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.displayClientMessage(text, false);
    }
}
