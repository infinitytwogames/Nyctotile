package org.infinitytwo.umbralore.command.network;

import org.infinitytwo.umbralore.context.CommandContext;

public abstract class NetworkCommand {
    protected String[] args;
    protected String command;

    public String[] getArgs() {
        return args;
    }

    public String getCommand() {
        return command;
    }

    public abstract byte[] run(CommandContext context) throws Exception;
}
