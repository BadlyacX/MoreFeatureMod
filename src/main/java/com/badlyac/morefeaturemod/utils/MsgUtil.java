package com.badlyac.morefeaturemod.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Forge 1.20.1 - Client Msg Util
 * usage：
 *   MsgUtil.sendMessage2Client("hey hey, green string", ChatFormatting.GREEN);
 *   MsgUtil.sendMessage2Client("custom color #55AAFF", 0x55AAFF);
 *   MsgUtil.sendActionBar2Client("Action Bar prompt", ChatFormatting.YELLOW);
 *   MsgUtil.sendMessage2Client(Component.literal("already a component message").withStyle(ChatFormatting.AQUA));
 */
public final class MsgUtil {

    private MsgUtil() {}

    public static boolean sendMessage2Client(Component component) {
        return sendInternal(component, false);
    }

    public static boolean sendMessage2Client(String msg) {
        return sendInternal(Component.literal(msg), false);
    }

    public static boolean sendMessage2Client(String msg, ChatFormatting... formats) {
        Style s = Style.EMPTY;
        if (formats != null) {
            for (ChatFormatting f : formats) {
                s = s.applyFormat(f);
            }
        }
        return sendInternal(Component.literal(msg).setStyle(s), false);
    }

    public static boolean sendMessage2Client(String msg, int rgb) {
        Style s = Style.EMPTY.withColor(TextColor.fromRgb(rgb));
        return sendInternal(Component.literal(msg).setStyle(s), false);
    }

    public static boolean sendActionBar2Client(Component component) {
        return sendInternal(component, true);
    }

    public static boolean sendActionBar2Client(String msg, ChatFormatting... formats) {
        Style s = Style.EMPTY;
        if (formats != null) {
            for (ChatFormatting f : formats) s = s.applyFormat(f);
        }
        return sendInternal(Component.literal(msg).setStyle(s), true);
    }

    public static boolean sendActionBar2Client(String msg, int rgb) {
        Style s = Style.EMPTY.withColor(TextColor.fromRgb(rgb));
        return sendInternal(Component.literal(msg).setStyle(s), true);
    }

    private static boolean sendInternal(Component component, boolean actionBar) {
        if (FMLEnvironment.dist != Dist.CLIENT) return false;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return false;

        player.displayClientMessage(component, actionBar);
        return true;
    }

    public static boolean sendAmpColorMessage(String msgWithAmp) {
        ChatFormatting color = ampCodeToFormatting(msgWithAmp);
        String plain = stripLeadingAmpColor(msgWithAmp);
        return sendMessage2Client(plain, color != null ? new ChatFormatting[]{color} : new ChatFormatting[0]);
    }

    private static ChatFormatting ampCodeToFormatting(String s) {
        if (s == null || s.length() < 2 || s.charAt(0) != '&') return null;
        char c = Character.toLowerCase(s.charAt(1));

        return switch (c) {
            case '0' -> ChatFormatting.BLACK;
            case '1' -> ChatFormatting.DARK_BLUE;
            case '2' -> ChatFormatting.DARK_GREEN;
            case '3' -> ChatFormatting.DARK_AQUA;
            case '4' -> ChatFormatting.DARK_RED;
            case '5' -> ChatFormatting.DARK_PURPLE;
            case '6' -> ChatFormatting.GOLD;
            case '7' -> ChatFormatting.GRAY;
            case '8' -> ChatFormatting.DARK_GRAY;
            case '9' -> ChatFormatting.BLUE;
            case 'a' -> ChatFormatting.GREEN;
            case 'b' -> ChatFormatting.AQUA;
            case 'c' -> ChatFormatting.RED;
            case 'd' -> ChatFormatting.LIGHT_PURPLE;
            case 'e' -> ChatFormatting.YELLOW;
            case 'f' -> ChatFormatting.WHITE;
            default -> null;
        };
    }

    private static String stripLeadingAmpColor(String s) {
        if (s != null && s.length() >= 2 && s.charAt(0) == '&') {
            return s.substring(2).trim();
        }
        return s;
    }
}
