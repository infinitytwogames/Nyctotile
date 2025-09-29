package org.infinitytwo.umbralore;

public class ClientState {
    public enum State {
        MENU,
        WORLD,
        GENERATING,
        CONNECTING,
        INITIAL,
        MOD_LOADING,
    }

    private static boolean isLocalServer;

    public static boolean isLocalServer() {
        return isLocalServer;
    }

    public static void setUsingLocalServer(boolean b) {
        ClientState.isLocalServer = b;
    }
}
