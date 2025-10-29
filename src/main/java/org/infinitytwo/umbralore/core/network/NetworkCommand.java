package org.infinitytwo.umbralore.core.network;

import org.infinitytwo.umbralore.core.context.CommandContext;

@Deprecated
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
