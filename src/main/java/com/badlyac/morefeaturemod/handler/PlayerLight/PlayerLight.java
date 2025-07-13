package com.badlyac.morefeaturemod.handler.PlayerLight;

import com.badlyac.morefeaturemod.Main;
import com.badlyac.morefeaturemod.keybinding.KeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class PlayerLight {
    public static final Map<UUID, Boolean> PLAYER_LIGHT_ENABLED = new HashMap<>();
    private static final Map<UUID, BlockPos> LAST_LIGHT_POS = new HashMap<>();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Main.MODID);
    public static final RegistryObject<Block> INVISIBLE_LIGHT_BLOCK = BLOCKS.register("invisible_light_block", () ->
            new Block(BlockBehaviour.Properties.of()
                    .lightLevel(state -> 15)
                    .noCollission()
                    .instabreak()
                    .noOcclusion()
                    .air())
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) return;

        if (KeyBindings.INSTANCE.toggle_light_key.consumeClick()) {
            UUID uuid = Minecraft.getInstance().player.getUUID();
            boolean current = PLAYER_LIGHT_ENABLED.getOrDefault(uuid, true);
            PLAYER_LIGHT_ENABLED.put(uuid, !current);
            Minecraft.getInstance().player.displayClientMessage(Component.literal("動態光源已 " + (current ? "關閉" : "啟用")).withStyle((current ? ChatFormatting.RED : ChatFormatting.GREEN)), true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        ServerPlayer player = (ServerPlayer) event.player;
        Level level = player.level();
        UUID uuid = player.getUUID();
        BlockPos pos = player.blockPosition();

        boolean holdingLight = isLightItem(player.getMainHandItem()) || isLightItem(player.getOffhandItem());

        BlockPos lastPos = LAST_LIGHT_POS.get(uuid);

        if (!holdingLight) {
            if (lastPos != null && level.getBlockState(lastPos).getBlock() == Blocks.LIGHT) {
                level.setBlockAndUpdate(lastPos, Blocks.AIR.defaultBlockState());
                LAST_LIGHT_POS.remove(uuid);
            }
            return;
        }

        if (lastPos != null && !lastPos.equals(pos)) {
            if (level.getBlockState(lastPos).getBlock() == Blocks.LIGHT) {
                level.setBlockAndUpdate(lastPos, Blocks.AIR.defaultBlockState());
            }
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getBlock() == Blocks.LIGHT) {
            BlockState light = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);
            level.setBlockAndUpdate(pos, light);
            LAST_LIGHT_POS.put(uuid, pos);
        }
    }

    private static boolean isLightItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == Items.TORCH || item == Items.LANTERN || item == Items.GLOWSTONE ||
                item == Items.REDSTONE_TORCH || item == Items.SOUL_TORCH || item == Items.SOUL_LANTERN;
    }
}