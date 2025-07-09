package com.badlyac.morefeaturemod.handler.InvisibleItemFrame;


import com.badlyac.morefeaturemod.Main;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Main.MODID)
public class InvisibleItemFrame {

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("invisibleitemframe")
                .executes(InvisibleItemFrame::giveInvisibleItemFrame));
        dispatcher.register(Commands.literal("iif")
                .executes(InvisibleItemFrame::giveInvisibleItemFrame));
    }

    private static int giveInvisibleItemFrame(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            ServerPlayer player = source.getPlayerOrException();

            if (!player.server.isSingleplayer()) {
                player.displayClientMessage(Component.literal("這個指令只能在單人世界中使用！")
                        .withStyle(ChatFormatting.RED), false);
                return 0;
            }

            ItemStack itemFrame = new ItemStack(Items.ITEM_FRAME);
            CompoundTag entityTag = new CompoundTag();
            entityTag.putByte("Invisible", (byte) 1);
            CompoundTag displayTag = new CompoundTag();
            displayTag.put("EntityTag", entityTag);
            itemFrame.setTag(displayTag);

            if (player.addItem(itemFrame)) {
                player.displayClientMessage(Component.literal("你獲得了一個隱形展示框！")
                        .withStyle(ChatFormatting.GREEN), false);
            } else {
                player.displayClientMessage(Component.literal("你的背包沒有足夠空間！")
                        .withStyle(ChatFormatting.RED), false);
            }
        } catch (CommandSyntaxException e) {
            System.out.print(e.getMessage());
        }

        return 1;
    }
}

