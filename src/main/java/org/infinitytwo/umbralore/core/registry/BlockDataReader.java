package org.infinitytwo.umbralore.core.registry;

import org.infinitytwo.umbralore.block.BlockType;
import org.infinitytwo.umbralore.core.exception.IllegalDataTypeException;
import org.infinitytwo.umbralore.core.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Set;

public final class BlockDataReader {
    private final Logger logger = new Logger("BlockDataReader");
    private final BlockRegistry registry;

    public BlockDataReader(BlockRegistry registry) {
        this.registry = registry;
    }

    public <T extends BlockType> T fillData(T blockType, byte[] data, int id) {
        BlockRegistry.DataSchematic schematic = registry.getDataSchematicOf(id);
        if (schematic == null) throw new IllegalArgumentException(
                "The BlockType \"" + blockType.getName() + "\" is not a dynamic block."
        );

        ByteBuffer buffer = ByteBuffer.wrap(data);

        for (String fieldName : schematic.getIds()) {
            Object o = switch (schematic.get(fieldName)) {
                case INT -> buffer.getInt();
                case BYTE -> buffer.get();
                case LONG -> buffer.getLong();
                case FLOAT -> buffer.getFloat();
                case SHORT -> buffer.getShort();
                case DOUBLE -> buffer.getDouble();
                default -> null;
            };

            try {
                Field field = blockType.getClass().getField(fieldName);
                field.set(blockType, o);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return blockType;
    }

    public <T extends BlockType> byte[] serialize(T blockType, int id) {
        BlockRegistry.DataSchematic schematic = registry.getDataSchematicOf(id);

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteOutStream);

        for (String fieldName : schematic.getIds()) {
            try {
                Field field = blockType.getClass().getField(fieldName);

                switch (schematic.get(fieldName)) {
                    case SHORT -> outputStream.writeShort(field.getShort(blockType));
                    case DOUBLE -> outputStream.writeDouble(field.getDouble(blockType));
                    case FLOAT -> outputStream.writeFloat(field.getFloat(blockType));
                    case LONG -> outputStream.writeLong(field.getLong(blockType));
                    case BYTE -> outputStream.write(field.getByte(blockType));
                    case INT -> outputStream.writeInt(field.getInt(blockType));
                    default -> throw new IllegalStateException("Schematic seems to be corrupted. Could not continue!");
                }

            } catch (NoSuchFieldException | IOException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return byteOutStream.toByteArray();
    }

    public void setData(int id, byte[] dataset, String field, Object target) {
        if (field.isEmpty()) return;

        BlockRegistry.DataSchematic schematic = registry.getDataSchematicOf(id);
        Set<String> ids = schematic.getIds();
        int offset = 0;
        ByteBuffer buffer = ByteBuffer.wrap(dataset);

        for (String Id : ids) {
            try {
                switch (schematic.get(Id)) {
                    case INT -> {
                        if (Id.equals(field)) buffer.putInt(offset, (Integer) target);
                        offset += Integer.BYTES;
                    }
                    case BYTE -> {
                        if (Id.equals(field)) buffer.put(offset, (Byte) target);
                        offset += Byte.BYTES;
                    }
                    case LONG -> {
                        if (Id.equals(field)) buffer.putLong(offset, (Long) target);
                        offset += Long.BYTES;
                    }
                    case FLOAT -> {
                        if (Id.equals(field)) buffer.putFloat(offset, (Float) target);
                        offset += Float.BYTES;
                    }
                    case SHORT -> {
                        if (Id.equals(field)) buffer.putShort(offset, (Short) target);
                        offset += Short.BYTES;
                    }
                    case DOUBLE -> {
                        if (Id.equals(field)) buffer.putDouble(offset, (Double) target);
                        offset += Double.BYTES;
                    }
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("There is a mismatch datatypes between inputted data and the field \""+field+".\"");
            }
        }
    }

    public Data getData(int id, byte[] dataset, String field) throws IllegalDataTypeException {
        if (field.isEmpty()) return null;

        BlockRegistry.DataSchematic schematic = registry.getDataSchematicOf(id);
        Set<String> ids = schematic.getIds();
        int offset = 0;
        ByteBuffer buffer = ByteBuffer.wrap(dataset);

        for (String Id : ids) {
            switch (schematic.get(Id)) {
                case INT -> {
                    if (Id.equals(field)) return new Data(buffer.getInt(offset), BlockRegistry.Primitive.INT);
                    offset += Integer.BYTES;
                }
                case BYTE -> {
                    if (Id.equals(field)) return new Data(buffer.get(offset), BlockRegistry.Primitive.BYTE);
                    offset += Byte.BYTES;
                }
                case LONG -> {
                    if (Id.equals(field)) return new Data(buffer.getLong(offset), BlockRegistry.Primitive.LONG);
                    offset += Long.BYTES;
                }
                case FLOAT -> {
                    if (Id.equals(field)) return new Data(buffer.getFloat(offset), BlockRegistry.Primitive.FLOAT);
                    offset += Float.BYTES;
                }
                case SHORT -> {
                    if (Id.equals(field)) return new Data(buffer.getShort(offset), BlockRegistry.Primitive.SHORT);
                    offset += Short.BYTES;
                }
                case DOUBLE -> {
                    if (Id.equals(field)) return new Data(buffer.getDouble(offset), BlockRegistry.Primitive.DOUBLE);
                    offset += Double.BYTES;
                }
                case NULL -> {
                    logger.warn("field \""+field+"\" has undefined primitive!");
                    throw new IllegalDataTypeException("field \""+field+"\" has undefined primitive!");
                }
            }
        }
        return new Data(null, BlockRegistry.Primitive.NULL);
    }

    public static class Data {
        public Object value;
        public BlockRegistry.Primitive type;

        public Data(Object o, BlockRegistry.Primitive type) {
            this.type = type;
            value = o;
        }
    }
}
