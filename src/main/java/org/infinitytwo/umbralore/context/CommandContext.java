package org.infinitytwo.umbralore.context;

import org.infinitytwo.umbralore.ServerThread;
import org.infinitytwo.umbralore.constants.LogicalSide;
import org.infinitytwo.umbralore.constants.PhysicalSide;
import org.infinitytwo.umbralore.network.NetworkThread;

@Deprecated
public final class CommandContext extends Context {
    private NetworkThread thread;
    private String[] params;

    private CommandContext(PhysicalSide physicalSide) {
        super(LogicalSide.NETWORK, physicalSide);
    }

    public static CommandContext of(NetworkThread thread, String[] params, ServerThread serverThread) {
        CommandContext c = new CommandContext(PhysicalSide.LOCAL);
        c.params = params;
        c.thread = thread;
        c.server = serverThread;
        return c;
    }

    public String[] getCommandLine() {
        return params;
    }
}
