package com.badlyac.morefeaturemod.handler.Afk;

import com.badlyac.morefeaturemod.Main;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class Afk {

    private static final long AFK_TIMEOUT = 5 * 60 * 1000;
    private static final Map<UUID, Boolean> afkStatusMap = new HashMap<>();
    private static final Map<UUID, GameType> originalGameModeMap = new HashMap<>();
    private static final Map<UUID, Coordinates> afkCoordinatesMap = new HashMap<>();

    private record Coordinates(double x, double y, double z) {
        boolean isClose(double x, double y, double z) {
            return Math.abs(this.x - x) < 0.1 && Math.abs(this.y - y) < 0.1 && Math.abs(this.z - z) < 0.1;
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("afk")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    UUID playerId = player.getUUID();

                    if (!player.server.isSingleplayer()) {
                        player.sendSystemMessage(Component.literal("這個指令只能在單人世界使用！")
                                .withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    if (!player.hasPermissions(2)) {
                        player.sendSystemMessage(Component.literal("你沒有權限使用 /afk，因為你無法切換遊戲模式。")
                                .withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    if (player.getVehicle() instanceof Boat) {
                        player.sendSystemMessage(Component.literal("你在船上，不能使用 /afk")
                                .withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    if (player.isSleeping()) {
                        player.sendSystemMessage(Component.literal("你正在睡覺，不能使用 /afk")
                                .withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    if (!afkStatusMap.getOrDefault(playerId, false)) {
                        Component afkMessage = Component.literal(player.getName().getString() + " just went AFK...")
                                .withStyle(ChatFormatting.GRAY);
                        for (ServerPlayer p : player.server.getPlayerList().getPlayers()) {
                            p.sendSystemMessage(afkMessage);
                        }

                        originalGameModeMap.put(playerId, player.gameMode.getGameModeForPlayer());
                        player.setGameMode(GameType.SPECTATOR);
                        afkStatusMap.put(playerId, true);
                        afkCoordinatesMap.put(playerId, new Coordinates(player.getX(), player.getY(), player.getZ()));
                    }
                    return 1;
                }));
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        afkStatusMap.clear();
        originalGameModeMap.clear();
        afkCoordinatesMap.clear();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppedEvent event) {
        for (ServerPlayer allPlayer : Objects.requireNonNull(event.getServer().getPlayerList()).getPlayers()) {
            UUID playerId = allPlayer.getUUID();

            originalGameModeMap.getOrDefault(playerId, GameType.SURVIVAL);
            afkStatusMap.remove(playerId);
            afkCoordinatesMap.remove(playerId);
            originalGameModeMap.remove(playerId);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 目前無行為
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (afkStatusMap.containsKey(playerId)) {
            if (player.getUUID().equals(playerId)) {
                player.setGameMode(originalGameModeMap.getOrDefault(playerId, GameType.SURVIVAL));
            }
        }

        afkStatusMap.remove(playerId);
        afkCoordinatesMap.remove(playerId);
        originalGameModeMap.remove(playerId);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        ServerPlayer player = (ServerPlayer) event.player;
        UUID playerId = player.getUUID();
        Coordinates storedCoordinates = afkCoordinatesMap.get(playerId);
        boolean wasAfk = afkStatusMap.getOrDefault(playerId, false);

        if (wasAfk && storedCoordinates != null &&
                !storedCoordinates.isClose(player.getX(), player.getY(), player.getZ())) {
            Component backMessage = Component.literal(player.getName().getString() + " 回來了")
                    .withStyle(ChatFormatting.GRAY);
            for (ServerPlayer p : player.server.getPlayerList().getPlayers()) {
                p.sendSystemMessage(backMessage);
            }

            GameType originalGameMode = originalGameModeMap.get(playerId);
            if (originalGameMode != null) {
                player.setGameMode(originalGameMode);
                originalGameModeMap.remove(playerId);
            }

            afkStatusMap.put(playerId, false);
            afkCoordinatesMap.remove(playerId);
        }
    }
}
