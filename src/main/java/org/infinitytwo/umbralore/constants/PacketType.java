package org.infinitytwo.umbralore.constants;

public enum PacketType {
    /**
     * Format:<br>
     * <i>(header same as before)</i><br>
     * [ Blocks (byte[]) ]
     */
    CHUNK((byte) 0),
    /**
     * Format:
     * <br>
     * <i>(header same as before)</i><br>
     * [ Command Size (int) ]<br>
     * [ Command (byte[]) ]
     */
    COMMAND((byte) 1),

    /**
     * Format:<br>
     * [ Packet ID (int) ] <i>Same as the resending packet</i><br>
     * ...<br>
     * [ Packet length (short) ]<br>
     * [ Packet indexes (short[]) ]<br>
     */
    NACK((byte) 2),

    /**
     * <h4>Not to be confused by {@code FAILURE}</h4>
     * Used for resending packets, not failures
     * Format: <br>
     * [ Packet ID (int) ] <i>Same as the completed packet</i><br>
     * ...<br>
     * <i>No payload</i>
     */
    ACK((byte) 3),

    /**
     * Format:<br>
     * ...<br>
     * [ TokenId Size (int) ]<br>
     * [ TokenId (byte[] as string) ]<br>
     */
    // TODO: ADD MORE DATA TO AUTHENTICATION
    AUTHENTICATION((byte) 4),

    /**
     * Format:<br>
     * ...<br>
     * [ Command Length (int) ]<br>
     * [ Command (byte[] as string) ]<br>
     * [ Command's Payload (byte[]) ]<br>
     */
    CMD_BYTE_DATA((byte) 5),

    /**
     * Same format as {@code CMD_BYTE_DATA}, but unencrypted.
     */
    UNENCRYPTED((byte) 6),

    /**
     * <h4>Not to be confused by {@code NACK}</h4>
     * Used for failures caused by commands.<br>
     * Format is the same as {@code COMMAND}
     */
    FAILURE((byte) 7),

    /**
     * Primarily for connection.<br>
     * Format:<br>
     * ...<br>
     * [ Public Key ]
     */
    EXCHANGE((byte) 8),

    CONNECTION((byte) 9),
    /**
     * The format is same as {@code ACK}.<br>
     * Primarily for connection
     */
    DISCONNECTION((byte) 10)
    ;

    private final byte id;

    PacketType(byte id) {
        this.id = id;
    }

    public byte getType() {
        return id;
    }

    public static PacketType fromId(byte id) {
        for (PacketType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PacketType ID: " + id);
    }
}
