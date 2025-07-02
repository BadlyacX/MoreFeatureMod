package com.badlyac.morefeaturemod.handler.AutoDetectNearbyOre;

import com.badlyac.morefeaturemod.KeyBindings.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class AutoDetectNearbyOre {

    private static final TagKey<Block> ORES_TAG = BlockTags.create(new ResourceLocation("forge", "ores"));
    private static boolean isDetecting = false;

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
                player.displayClientMessage(Component.literal("Detected " + oreType + " ore at: " + pos).setStyle(Style.EMPTY).withStyle(ChatFormatting.GREEN), false);
                break;
            }
        }
    }

    private static boolean isOreBlock(BlockState state) {
        return state.is(ORES_TAG);
    }

//    private static boolean isOreBlock(BlockState state) {
//        return state.is(Blocks.IRON_ORE)
//                || state.is(Blocks.GOLD_ORE)
//                || state.is(Blocks.DIAMOND_ORE)
//                || state.is(Blocks.COAL_ORE)
//                || state.is(Blocks.EMERALD_ORE)
//                || state.is(Blocks.LAPIS_ORE)
//                || state.is(Blocks.REDSTONE_ORE)
//                || state.is(Blocks.ANCIENT_DEBRIS)
//                || state.is(Blocks.DEEPSLATE_IRON_ORE)
//                || state.is(Blocks.DEEPSLATE_GOLD_ORE)
//                || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
//                || state.is(Blocks.DEEPSLATE_COAL_ORE)
//                || state.is(Blocks.DEEPSLATE_EMERALD_ORE)
//                || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
//                || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
//                || state.is(Blocks.NETHER_GOLD_ORE);
//    }

    private static String getOreType(BlockState state) {
        if (state.is(Blocks.IRON_ORE)) return "Iron";
        if (state.is(Blocks.GOLD_ORE)) return "Gold";
        if (state.is(Blocks.DIAMOND_ORE)) return "Diamond";
        if (state.is(Blocks.COAL_ORE)) return "Coal";
        if (state.is(Blocks.EMERALD_ORE)) return "Emerald";
        if (state.is(Blocks.LAPIS_ORE)) return "Lapis";
        if (state.is(Blocks.REDSTONE_ORE)) return "Redstone";
        if (state.is(Blocks.ANCIENT_DEBRIS)) return "Ancient Debris";
        if (state.is(Blocks.DEEPSLATE_IRON_ORE)) return "Deepslate Iron";
        if (state.is(Blocks.DEEPSLATE_GOLD_ORE)) return "Deepslate Gold";
        if (state.is(Blocks.DEEPSLATE_DIAMOND_ORE)) return "Deepslate Diamond";
        if (state.is(Blocks.DEEPSLATE_COAL_ORE)) return "Deepslate Coal";
        if (state.is(Blocks.DEEPSLATE_EMERALD_ORE)) return "Deepslate Emerald";
        if (state.is(Blocks.DEEPSLATE_LAPIS_ORE)) return "Deepslate Lapis";
        if (state.is(Blocks.DEEPSLATE_REDSTONE_ORE)) return "Deepslate Redstone";
        if (state.is(Blocks.NETHER_GOLD_ORE)) return "Nether Gold _Ore";
        return "Unknown";
    }
}
