package org.infinitytwo.umbralore.core.context;

import org.infinitytwo.umbralore.core.ServerThread;
import org.infinitytwo.umbralore.core.constants.LogicalSide;
import org.infinitytwo.umbralore.core.constants.PhysicalSide;
import org.infinitytwo.umbralore.core.network.NetworkThread;

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
