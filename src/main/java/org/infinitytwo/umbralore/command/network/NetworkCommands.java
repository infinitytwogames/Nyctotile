package org.infinitytwo.umbralore.command.network;

import org.infinitytwo.umbralore.context.CommandContext;
import org.infinitytwo.umbralore.network.NetworkCommandHandler;
import org.joml.Vector2i;

public class NetworkCommands {
    public static class ChunkCommand extends NetworkCommand {
        private ChunkCommand setup() {
            args = new String[2];
            command = "chunk";
            return this;
        }

        @Override
        public byte[] run(CommandContext context) throws IllegalArgumentException {
            try {
                int x = Integer.parseInt(context.getCommandLine()[1]);
                int y = Integer.parseInt(context.getCommandLine()[2]);

                return context.getServer().getCurrentWorld().getChunkOrGenerate(new Vector2i(x,y)).serialize();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Incorrect number format for position");
            }
        }
    }

    public static void register(NetworkCommandHandler handler) {
        handler.register(new ChunkCommand().setup());
    }
}
