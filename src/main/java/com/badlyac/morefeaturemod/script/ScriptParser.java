package com.badlyac.morefeaturemod.script;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\s{4})(\\w+)\\(([^)]*)\\)$");

    public static List<ScriptCommand> parseScript(File scriptFile) throws IOException {
        List<ScriptCommand> commands = new ArrayList<>();
        try (FileReader reader = new FileReader(scriptFile)) {
            List<String> lines = new ArrayList<>();
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                if (ch == '\n') {
                    lines.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append((char) ch);
                }
            }
            if (!sb.isEmpty()) {
                lines.add(sb.toString());
            }

            boolean inLoop = false;
            for (String line : lines) {
                if (line.trim().equals("loop:")) {
                    inLoop = true;
                } else if (inLoop) {
                    Matcher matcher = COMMAND_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        String commandName = matcher.group(2);
                        String[] args = matcher.group(3).split(",");
                        for (int i = 0; i < args.length; i++) {
                            args[i] = args[i].trim();
                        }
                        commands.add(new ScriptCommand(commandName, args));
                    }
                }
            }
        }
        return commands;
    }
}