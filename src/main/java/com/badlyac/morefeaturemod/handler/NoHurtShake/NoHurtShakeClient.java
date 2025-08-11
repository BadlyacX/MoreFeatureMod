package com.badlyac.morefeaturemod.handler.NoHurtShake;

import com.badlyac.morefeaturemod.keybinding.KeyBindings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NoHurtShakeClient {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (KeyBindings.INSTANCE.toggle_no_hurt_shake_key.consumeClick()) {
            boolean now = !NoHurtShakeConfig.enabled();
            NoHurtShakeConfig.setEnabled(now);
            toast("NoHurtShake: " + (now ? "已啟用" : "已停用"));
        }
    }

    @SubscribeEvent
    public static void onComputeAngles(ViewportEvent.ComputeCameraAngles e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null) return;

        if (!NoHurtShakeConfig.enabled()) return;

        if (p.hurtTime > 0) {
            float scale = clamp01(NoHurtShakeConfig.intensity());
            e.setRoll(e.getRoll() * scale);
            e.setPitch(e.getPitch() * (0.9f + 0.1f * scale)); // optional, for specific condition
        }
    }

    private static float clamp01(float v) {
        return v < 0f ? 0f : (Math.min(v, 1f));
    }

    private static void toast(String msg) {
        var mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(msg), true);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("nohurtshake")
                .executes(ctx -> {
                    sayStatus();
                    return 1;
                })
                .then(Commands.literal("get").executes(ctx -> {
                    sayStatus();
                    return 1;
                }))
                .then(Commands.literal("toggle").executes(ctx -> {
                    boolean now = !NoHurtShakeConfig.enabled();
                    NoHurtShakeConfig.setEnabled(now);
                    say("NoHurtShake: " + (now ? "已啟用" : "已停用"));
                    return 1;
                }))
                .then(Commands.literal("on").executes(ctx -> {
                    NoHurtShakeConfig.setEnabled(true);
                    say("NoHurtShake: 已啟用");
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                    NoHurtShakeConfig.setEnabled(false);
                    say("NoHurtShake: 已停用");
                    return 1;
                }))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0.0f, 1.0f))
                                .executes(ctx -> {
                                    float v = FloatArgumentType.getFloat(ctx, "value");
                                    NoHurtShakeConfig.setIntensity(v);
                                    say(String.format("NoHurtShake: 強度已設為 %.2f（0=無晃動，1=原版）", v));
                                    return 1;
                                }))));
    }

    private static void sayStatus() {
        say(String.format(
                "NoHurtShake 狀態：%s，強度=%.2f（0=無晃動，1=原版）",
                NoHurtShakeConfig.enabled() ? "啟用" : "停用",
                NoHurtShakeConfig.intensity()
        ));
    }

    private static void say(String msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {

            mc.player.displayClientMessage(Component.literal(msg), false);
        }
    }
}