package org.infinitytwo.umbralore.core.network;

import org.infinitytwo.umbralore.core.context.CommandContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public final class NetworkCommandHandler {
    private static final Map<String, NetworkCommand> commands = new ConcurrentHashMap<>();

    public byte[] handleCommand(CommandContext context) throws Exception {
        String cmd = context.getCommandLine()[0];
        if (cmd.isEmpty()) throw new IllegalArgumentException("No command was passed.");
        for (String command : commands.keySet()) {
            if (command.equals(cmd)) {
                return commands.get(command).run(context);
            }
        }
        throw new IllegalArgumentException("No valid command was found");
    }

    public void register(NetworkCommand command) {
        commands.put(command.getCommand(), command);
    }
}