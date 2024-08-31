package com.badlyac.mod.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.List;

public class ScriptExecutor {
    private final Minecraft minecraft = Minecraft.getInstance();

    public void executeCommands(List<ScriptCommand> commands) {
        for (ScriptCommand command : commands) {
            switch (command.getCommandName()) {
                case "attack":
                    attack(command.getArgs()[0]);
                    break;
                case "delay":
                    delay(Integer.parseInt(command.getArgs()[0]));
                    break;
                    
            }
        }
    }

    private void attack(String targetType) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;
    }

    private void delay(int ticks) {

    }
}
