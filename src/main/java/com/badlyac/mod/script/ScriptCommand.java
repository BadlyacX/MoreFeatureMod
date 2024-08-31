package com.badlyac.mod.script;

public class ScriptCommand {

    private final String commandName;
    private final String[] args;

    public ScriptCommand(String commandName, String[] args) {
        this.commandName = commandName;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}
