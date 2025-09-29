package org.infinitytwo.umbralore;

import org.infinitytwo.umbralore.block.ServerBlockType;
import org.infinitytwo.umbralore.command.network.NetworkCommands;
import org.infinitytwo.umbralore.event.SubscribeEvent;
import org.infinitytwo.umbralore.event.bus.LocalEventBus;
import org.infinitytwo.umbralore.event.network.PacketReceived;
import org.infinitytwo.umbralore.logging.Logger;
import org.infinitytwo.umbralore.network.NetworkCommandHandler;
import org.infinitytwo.umbralore.network.NetworkHandler;
import org.infinitytwo.umbralore.network.server.ServerNetworkHandler;
import org.infinitytwo.umbralore.network.server.ServerNetworkThread;
import org.infinitytwo.umbralore.registry.BlockRegistry;
import org.infinitytwo.umbralore.world.ServerGridMap;
import org.infinitytwo.umbralore.world.ServerProcedureGridMap;
import org.infinitytwo.umbralore.world.dimension.Dimension;
import org.infinitytwo.umbralore.world.dimension.Overworld;

public final class ServerThread extends Thread {
    private volatile boolean closing = false;
    private volatile boolean ready = false;
    private final LocalEventBus eventBus = new LocalEventBus("Server Network Thread");
    private Dimension overworld;
    private int seed;
    private BlockRegistry registry;
    private ServerBlockType grass;
    private ServerBlockType dirt;
    private ServerBlockType mantle;
    private ServerBlockType stone;
    private ServerNetworkThread networkThread;
    private NetworkHandler handler;
    private NetworkCommandHandler commandHandler;
    private final Logger logger = new Logger("Server");

    public ServerThread(int seed) {
        eventBus.register(this);
        setName("Server Thread");

        this.seed = seed;
    }

    public ServerProcedureGridMap getCurrentWorld() {
        return overworld.getWorld();
    }

    @Override
    public void run() {
        logger.info("Constructing Server...");
        construct();
        logger.info("Initiating Server...");
        init();

        ready = true;
        logger.info("Server is ready");
        while (!closing) {
            tick();
        }
        logger.info("Closing!");
        cleanup();
    }

    private void construct() {
        seed = (int) Math.floor(Math.random() * 1000000);
        registry = new BlockRegistry();
        overworld = new Overworld(seed, registry);

        grass = new ServerBlockType("soil",false,"grass_block");
        dirt = new ServerBlockType("soil",false,"dirt");
        stone = new ServerBlockType("stone",false,"stone");
        mantle = new ServerBlockType("mantle",false,"mantle");

        registry.register(grass);
        registry.register(dirt);
        registry.register(stone);
        registry.register(mantle);

        networkThread = new ServerNetworkThread(eventBus,5555);
        handler = new ServerNetworkHandler(eventBus,networkThread,this);
        commandHandler = new NetworkCommandHandler();

        NetworkCommands.register(commandHandler);
    }

    @SubscribeEvent
    public void messageReceived(PacketReceived e) {
        logger.info(e.packet,e.address.toString());
    }

    private void init() {
        for (int x = -5; x < 5; x++) {
            for (int y = -5; y < 5; y++) {
                overworld.generate(new ServerGridMap.ChunkPos(x,y));
            }
        }

        networkThread.start();
    }

    private void tick() {
        for (int id : registry.getIds()) {
            if (!(registry.get(id) instanceof  ServerBlockType)) throw new RuntimeException("E");
        }
        try {
            Thread.sleep(10); // To prevent high CPU usage
        } catch (InterruptedException e) {
            System.out.println("Hello?");
            cleanup();
        }
    }

    private void cleanup() {
        networkThread.shutdown();
        overworld.cleanup();
    }

    public void shutdown() {
        closing = true;
    }

    public boolean isClosing() {
        return closing;
    }

    public boolean isReady() {
        return ready;
    }

    public NetworkHandler getNetworkHandler() {
        return handler;
    }

    public BlockRegistry getBlockRegistry() {
        return registry;
    }
}
