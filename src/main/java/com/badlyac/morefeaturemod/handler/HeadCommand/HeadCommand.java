package com.badlyac.morefeaturemod.handler.HeadCommand;

import com.badlyac.morefeaturemod.Main;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Main.MODID)
public class HeadCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("head")
                        .then(Commands.argument("itemId", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    if (!player.server.isSingleplayer()) {
                                        player.displayClientMessage(Component.literal("這個指令只能在單人世界中使用！")
                                                .withStyle(ChatFormatting.RED), false);
                                        return 0;
                                    }

                                    String itemId = StringArgumentType.getString(context, "itemId");
                                    if ("null".equals(itemId)) {
                                        replaceWithHeldItem(player);
                                    } else {
                                        replaceWithItem(player, itemId);
                                    }
                                    return 1;
                                }))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            // ✅ 單人世界檢查
                            if (!player.server.isSingleplayer()) {
                                player.displayClientMessage(Component.literal("這個指令只能在單人世界中使用！")
                                        .withStyle(ChatFormatting.RED), false);
                                return 0;
                            }

                            replaceWithHeldItem(player);
                            return 1;
                        })
        );
    }

    private static void replaceWithItem(ServerPlayer player, String itemId) {
        ItemStack currentHeadItem = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!currentHeadItem.isEmpty() && !isCursedAndNonArmor(currentHeadItem)) {
            player.spawnAtLocation(currentHeadItem);
        }

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item != null) {
            ItemStack itemStack = new ItemStack(item);
            itemStack.enchant(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE, 1);
            player.setItemSlot(EquipmentSlot.HEAD, itemStack);
            player.getPersistentData().putBoolean("customHeadItem", true);
        } else {
            player.displayClientMessage(Component.literal("找不到物品！")
                    .withStyle(ChatFormatting.RED), false);
        }
    }

    private static void replaceWithHeldItem(ServerPlayer player) {
        ItemStack currentHeadItem = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack heldItem = player.getMainHandItem();

        if (!heldItem.isEmpty()) {
            if (!currentHeadItem.isEmpty() && !isCursedAndNonArmor(currentHeadItem)) {
                player.spawnAtLocation(currentHeadItem);
            }
            player.setItemSlot(EquipmentSlot.HEAD, heldItem);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            player.getPersistentData().putBoolean("customHeadItem", true);
        } else {
            if (currentHeadItem.isEmpty()) {

                player.displayClientMessage(Component.literal("你沒有拿著物品，頭上也沒有任何東西。")
                        .withStyle(ChatFormatting.RED), false);
            } else {
                player.displayClientMessage(Component.literal("你已經有東西在頭上了。")
                        .withStyle(ChatFormatting.RED), false);
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack oldItem = event.getFrom();
            if (event.getSlot() == EquipmentSlot.HEAD && !oldItem.isEmpty()) {
                if (player.getPersistentData().getBoolean("customHeadItem")) {
                    if (isCursedAndNonArmor(oldItem)) {
                        oldItem.setCount(0);
                    }
                    player.getPersistentData().remove("customHeadItem");
                }
            }
        }
    }

    private static boolean isCursedAndNonArmor(ItemStack itemStack) {
        return EnchantmentHelper.hasBindingCurse(itemStack) && !(itemStack.getItem() instanceof ArmorItem);
    }
}
